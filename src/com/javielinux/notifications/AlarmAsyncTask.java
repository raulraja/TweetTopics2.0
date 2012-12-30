/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.preferences.IntegrationADW;
import com.javielinux.preferences.IntegrationADWAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.NotificationUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import com.javielinux.widget.WidgetCounters2x1;
import com.javielinux.widget.WidgetCounters4x1;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class AlarmAsyncTask extends AsyncTask<Void, Void, Void> {

    private boolean showSearchNotifications = false;

    private Twitter twitter;

    private Context context;
    SharedPreferences preferences;

    private int type;

    // variables para mostrar la notificacion en android

    private ArrayList<UserNotifications> mUserNotifications = new ArrayList<UserNotifications>();
    private ArrayList<SearchNotifications> mSearchNotifications = new ArrayList<SearchNotifications>();

    // variables para ADW Launcher

    private int totalTimelineADW = 0;
    private  int totalMentionsADW = 0;
    private int totalDMsAWD = 0;
    private int totalSearchesAWD = 0;

    public interface AlarmAsyncTaskResponder {
        public void alarmLoading();

        public void alarmCancelled();

        public void alarmLoaded(Void trends);
    }

    private AlarmAsyncTaskResponder responder;

    public AlarmAsyncTask(AlarmAsyncTaskResponder responder, Context context, int type) {
        this.responder = responder;
        this.type = type;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... args) {
        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionManager.getInstance().open(context);

        twitter = ConnectionManager.getInstance().getUserForSearchesTwitter();

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            if (!PreferenceUtils.getStatusWorkApp(context)) {
                searchUser();
            }
            if (!PreferenceUtils.getStatusWorkApp(context)) {
                searchNotifications();
            }
            if (!PreferenceUtils.getStatusWorkApp(context)) {
                writeADWLauncher();
            }

            if (!PreferenceUtils.getStatusWorkApp(context)) {
                shouldSendNotificationAndroid();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PreferenceUtils.saveStatusWorkAlarm(context, false);
        } finally {
            PreferenceUtils.saveStatusWorkAlarm(context, false);
        }

        DataFramework.getInstance().close();

        PreferenceUtils.saveStatusWorkAlarm(context, false);

        WidgetCounters2x1.updateAll(context);
        WidgetCounters4x1.updateAll(context);

        Log.d(Utils.TAG_ALARM, "Finalizado notificaciones en background");

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        responder.alarmLoading();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        responder.alarmCancelled();
    }

    @Override
    protected void onPostExecute(Void trends) {
        super.onPostExecute(trends);
        responder.alarmLoaded(trends);
    }

    public void loadUser(long id) {
        twitter = ConnectionManager.getInstance().getTwitter(id);
    }

    public void searchUser() {

        List<Entity> users = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        boolean timeline = preferences.getBoolean("prf_notif_in_timeline", false);
        boolean mentions = preferences.getBoolean("prf_notif_in_mentions", true);
        boolean dms = preferences.getBoolean("prf_notif_in_direct", true);

        for (int i = 0; i < users.size(); i++) {
            try {

                if (!PreferenceUtils.getStatusWorkApp(context)) {
                    loadUser(users.get(i).getId());
                    Log.d(Utils.TAG_ALARM, "Cargar en background usuario " + twitter.getScreenName());
                }

                UserNotifications userNotification = new UserNotifications();

                userNotification.setName(users.get(i).getString("name"));
                userNotification.setId(users.get(i).getId());

                // TIMELINE

                if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_TIMELINE)) {
                    EntityTweetUser etuTimeline = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_TIMELINE);
                    if (!PreferenceUtils.getStatusWorkApp(context) && type != OnAlarmReceiver.ALARM_ONLY_OTHERS) {
                        InfoSaveTweets info = etuTimeline.saveTweets(context, twitter);
                        if (info.getNewMessages() > 0 && timeline) {
                            userNotification.setIdsTimeline(info.getIds());
                        }
                    }
                    totalTimelineADW += etuTimeline.getValueNewCount();

                }

                // MENTIONS

                if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_MENTIONS)) {
                    EntityTweetUser etuMentions = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_MENTIONS);
                    if (!PreferenceUtils.getStatusWorkApp(context) && type != OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
                        InfoSaveTweets info = etuMentions.saveTweets(context, twitter);
                        if (info.getNewMessages() > 0 && mentions) {
                            userNotification.setIdsMentions(info.getIds());
                        }
                    }

                    totalMentionsADW += etuMentions.getValueNewCount();
                }

                // DIRECTOS

                if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_DIRECT_MESSAGES)) {
                    EntityTweetUser etuDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES);
                    if (!PreferenceUtils.getStatusWorkApp(context) && type != OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
                        InfoSaveTweets info = etuDMs.saveTweets(context, twitter);
                        if (info.getNewMessages() > 0 && dms) {
                            userNotification.setIdsDMs(info.getIds());
                        }
                    }
                    totalDMsAWD += etuDMs.getValueNewCount();
                }

                // DIRECTOS ENVIADOS

                if (!PreferenceUtils.getStatusWorkApp(context) && type != OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
                    EntityTweetUser etuSentDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES);
                    etuSentDMs.saveTweets(context, twitter);
                }

                mUserNotifications.add(userNotification);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void searchNotifications() {


        List<Entity> searchs = DataFramework.getInstance().getEntityList("search");

        for (int i = 0; i < searchs.size(); i++) {
            if (searchs.get(i).getInt("notifications") == 1 && !PreferenceUtils.getStatusWorkApp(context)) {
                EntitySearch es = new EntitySearch(searchs.get(i).getId());

                if (type != OnAlarmReceiver.ALARM_ONLY_OTHERS) {
                    InfoSaveTweets info = es.saveTweets(context, true, -1);
                    if (info.getNewMessages() > 0 && searchs.get(i).getInt("notifications_bar") == 1) {
                        showSearchNotifications = true;
                    }
                }

                int count = es.getValueNewCount();

                totalSearchesAWD += count;

                if (searchs.get(i).getInt("notifications_bar") == 1) {
                    SearchNotifications sn = new SearchNotifications();
                    sn.setTotal(count);
                    sn.setName(searchs.get(i).getString("name"));
                    mSearchNotifications.add(sn);

                }

            }
        }


    }

    public void writeADWLauncher() {

        if (!PreferenceUtils.getStatusWorkApp(context)) {
            boolean noread_adw = preferences.getBoolean("prf_no_read_adw", true);

            if (noread_adw) {
                IntegrationADW.createPreferences(context);
                IntegrationADW.verifyPreferences();

                String color = "";
                int number = 0;

                for (int i = 1; i <= 4; i++) {
                    String pref = IntegrationADW.getPreference(i);
                    if (pref.equals(IntegrationADWAdapter.PREFERENCES_SEARCH)) {
                        int mTotalSumSearches = 0;
                        for (SearchNotifications s : mSearchNotifications) {
                            mTotalSumSearches += s.getTotal();
                        }
                        if (mTotalSumSearches > 0) {
                            color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_SEARCH);
                            number = mTotalSumSearches;
                            i = 4;
                            Log.d(Utils.TAG_ALARM, number + " nuevos en busqueda. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
                        }
                    }
                    if (pref.equals(IntegrationADWAdapter.PREFERENCES_TIMELINE)) {
                        if (totalTimelineADW > 0) {
                            color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_TIMELINE);
                            number = totalTimelineADW;
                            i = 4;
                            Log.d(Utils.TAG_ALARM, number + " nuevos en timeline. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
                        }
                    }
                    if (pref.equals(IntegrationADWAdapter.PREFERENCES_MENTIONS)) {
                        if (totalMentionsADW > 0) {
                            color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_MENTIONS);
                            number = totalMentionsADW;
                            i = 4;
                            Log.d(Utils.TAG_ALARM, number + " nuevos en menciones. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
                        }
                    }
                    if (pref.equals(IntegrationADWAdapter.PREFERENCES_DIRECTS)) {
                        if (totalDMsAWD > 0) {
                            color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_DIRECTS);
                            number = totalDMsAWD;
                            i = 4;
                            Log.d(Utils.TAG_ALARM, number + " nuevos en directos. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
                        }
                    }
                }

                if (number > 0) {
                    Intent intent = new Intent();
                    intent.setAction("org.adw.launcher.counter.SEND");
                    intent.putExtra("PNAME", Utils.packageName);
                    intent.putExtra("COUNT", number);
                    if (!color.equals("")) intent.putExtra("COLOR", Color.parseColor(color));
                    context.sendBroadcast(intent);
                }


            }


        }

    }

    public void shouldSendNotificationAndroid() {

        for (UserNotifications userNotification : mUserNotifications) {

            if (!PreferenceUtils.getStatusWorkApp(context)) {
                NotificationUtils.sendUserNotification(context, userNotification);
            }

        }

        if (showSearchNotifications && !PreferenceUtils.getStatusWorkApp(context)) {
            NotificationUtils.sendSearchNotification(context, mSearchNotifications);
        }

    }

}
