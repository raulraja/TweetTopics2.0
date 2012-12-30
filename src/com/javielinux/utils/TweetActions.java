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

package com.javielinux.utils;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.IconAndTextSimpleAdapter;
import com.javielinux.adapters.UsersAdapter;
import com.javielinux.dialogs.OnSelectedIconAndText;
import com.javielinux.dialogs.TwitterUsersConnectedDialogFragment;
import com.javielinux.infos.InfoTweet;
import com.javielinux.preferences.RetweetsTypes;
import com.javielinux.tweettopics2.MapSearch;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.updatestatus.ServiceUpdateStatus;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

public class TweetActions {

    public static String TWEET_ACTION_REPLY = "reply";
    public static String TWEET_ACTION_RETWEET = "retweet";
    public static String TWEET_ACTION_LAST_READ = "lastread";
    public static String TWEET_ACTION_READ_AFTER = "readafter";
    public static String TWEET_ACTION_FAVORITE = "favorite";
    public static String TWEET_ACTION_MAP = "map";
    public static String TWEET_ACTION_SHARE = "share";
    public static String TWEET_ACTION_MENTION = "mention";
    public static String TWEET_ACTION_CLIPBOARD = "clipboard";
    public static String TWEET_ACTION_SEND_DM = "send_dm";
    public static String TWEET_ACTION_DELETE_TWEET = "delete_tweet";
    public static String TWEET_ACTION_DELETE_UP_TWEET = "delete_up_tweets";

    public static void execByCode(String code, FragmentActivity activity, long fromUser, InfoTweet infoTweet) {
        execByCode(code, activity, fromUser, infoTweet, null);
    }

    public static void execByCode(String code, FragmentActivity activity, long fromUser, InfoTweet infoTweet, Object extra) {
        if (code.equals(TWEET_ACTION_REPLY)) {
            goToReply(activity, fromUser, infoTweet);
        } else if (code.equals(TWEET_ACTION_RETWEET)) {
            showDialogRetweet(activity, fromUser, infoTweet);
        } else if (code.equals(TWEET_ACTION_LAST_READ)) {
            if (extra instanceof ListFragmentListener) {
                ((ListFragmentListener) extra).onMarkPositionLastReadAsLastReadId(ListFragmentListener.FORCE_FIRST_VISIBLE);
            }
        } else if (code.equals(TWEET_ACTION_READ_AFTER)) {
            saveTweet(activity, infoTweet);
        } else if (code.equals(TWEET_ACTION_FAVORITE)) {
            goToFavorite(activity, infoTweet);
        } else if (code.equals(TWEET_ACTION_SHARE)) {
            goToShare(activity, infoTweet);
        } else if (code.equals(TWEET_ACTION_MENTION)) {
            goToMention(activity, fromUser, infoTweet);
        } else if (code.equals(TWEET_ACTION_CLIPBOARD)) {
            copyToClipboard(activity, infoTweet);
        } else if (code.equals(TWEET_ACTION_SEND_DM)) {
            directMessage(activity, fromUser, infoTweet.getUsername());
        } else if (code.equals(TWEET_ACTION_MAP)) {
            goToMap(activity, infoTweet);
        } else if (code.equals(TWEET_ACTION_DELETE_TWEET)) {
            //this.goToDeleteTweet(mTweetTopicsCore);
        } else if (code.equals(TWEET_ACTION_DELETE_UP_TWEET)) { // opción sólo para desarrollo
            goToDeleteTop(activity, fromUser, infoTweet);
        }
    }

    private static void goToDeleteTop(FragmentActivity activity, long fromUser, InfoTweet infoTweet) {
        if (infoTweet.isTimeline()) {
            Entity ent = new Entity("tweets_user", infoTweet.getIdDB());
            String date = ent.getString("date");
            String sqlDelete = "DELETE FROM tweets_user WHERE type_id= " + TweetTopicsUtils.TWEET_TYPE_TIMELINE + " and user_tt_id=" + fromUser + " AND date > '" + date + "'";
            DataFramework.getInstance().getDB().execSQL(sqlDelete);
        }
        if (infoTweet.isMention()) {
            Entity ent = new Entity("tweets_user", infoTweet.getIdDB());
            String date = ent.getString("date");
            String sqlDelete = "DELETE FROM tweets_user WHERE type_id= " + TweetTopicsUtils.TWEET_TYPE_MENTIONS + " and user_tt_id=" + fromUser + " AND date > '" + date + "'";
            DataFramework.getInstance().getDB().execSQL(sqlDelete);
        }
    }

    public static void goToFavorite(final FragmentActivity activity, final InfoTweet infoTweet) {
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");
        if (ents.size() == 1) {
            createFavorite(activity, infoTweet, ents.get(0).getId());
        } else {
            TwitterUsersConnectedDialogFragment frag = new TwitterUsersConnectedDialogFragment(new OnSelectedIconAndText() {
                @Override
                public void OnSelectedItem(IconAndTextSimpleAdapter.IconAndText item) {
                    createFavorite(activity, infoTweet, Long.parseLong(item.extra.toString()));
                }
            });
            frag.show(activity.getSupportFragmentManager(), "dialog");
        }
    }

    public static boolean createFavorite(FragmentActivity activity, final InfoTweet infoTweet, final long id) {
        ConnectionManager.getInstance().open(activity);


        if (infoTweet.getTypeFrom() == InfoTweet.FROM_STATUS && infoTweet.getIdDB() > 0) {
            try {
                Entity ent = new Entity("tweets_user", infoTweet.getIdDB());
                ent.setValue("is_favorite", 1);
                ent.save();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        Utils.showMessage(activity, activity.getString(R.string.favorite_save));

        infoTweet.setFavorited(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionManager.getInstance().getTwitter(id).createFavorite(infoTweet.getId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;

    }

    public static void copyToClipboard(FragmentActivity activity, InfoTweet infoTweet) {
        try {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(infoTweet.getText());
            Utils.showMessage(activity, activity.getString(R.string.copied_to_clipboard));
        } catch (NoClassDefFoundError e) {
            Utils.showMessage(activity, activity.getString(R.string.error_general));
            e.printStackTrace();
        }
    }

    public static void goToMention(FragmentActivity activity, long fromUser, InfoTweet infoTweet) {
        updateStatus(activity, fromUser, NewStatusActivity.TYPE_NORMAL, "@" + infoTweet.getUsername(), infoTweet);
    }

    public static void goToMention(FragmentActivity activity, long fromUser, String username) {
        updateStatus(activity, fromUser, NewStatusActivity.TYPE_NORMAL, "@" + username, null);
    }

    public static void goToShare(FragmentActivity activity, InfoTweet infoTweet) {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT, infoTweet.getUsername() + ": " + infoTweet.getText());
        msg.setType("text/plain");
        activity.startActivity(msg);
    }

    public static void saveTweet(FragmentActivity activity, InfoTweet infoTweet) {
        try {
            if (infoTweet.isSavedTweet()) {
                Entity ent = new Entity("saved_tweets", infoTweet.getIdDB());
                ent.delete();
                // TODO borrar registro de la pantalla
                Utils.showMessage(activity, activity.getString(R.string.favorite_delete));
            } else {
                Entity ent = new Entity("saved_tweets");
                ent.setValue("url_avatar", infoTweet.getUrlAvatar());
                ent.setValue("username", infoTweet.getUsername());
                ent.setValue("user_id", infoTweet.getUserId());
                ent.setValue("tweet_id", infoTweet.getId() + "");
                ent.setValue("text", infoTweet.getText());
                ent.setValue("text_urls", infoTweet.getTextURLs());
//                ent.setValue("source", infoTweet.getSource());
                ent.setValue("to_username", infoTweet.getToUsername());
                ent.setValue("to_user_id", infoTweet.getToUserId());
                ent.setValue("date", infoTweet.getDate().getTime() + "");
                ent.setValue("latitude", infoTweet.getLatitude());
                ent.setValue("longitude", infoTweet.getLongitude());
                ent.save();
                Utils.showMessage(activity, activity.getString(R.string.favorite_save));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showMessage(activity, activity.getString(R.string.favorite_no_save));
        }
    }

    public static void goToReply(FragmentActivity activity, long fromUser, InfoTweet infoTweet) {
        if (infoTweet.isDm()) {
            directMessage(activity, fromUser, infoTweet.getUsername());
        } else {
            ArrayList<String> users = LinksUtils.pullLinksUsers(infoTweet.getText());
            int count = users.size();
            if (!users.contains("@" + infoTweet.getUsername())) count++;

            if (fromUser > 0) {
                try {
                    Entity e = new Entity("users", fromUser);
                    if (e != null) {
                        if (users.contains("@" + e.getString("name"))) count--;
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

            if (count > 1) {
                showDialogReply(activity, fromUser, infoTweet);
            } else {
                updateStatus(activity, fromUser, NewStatusActivity.TYPE_REPLY, "", infoTweet);
            }
        }

    }

    public static void showDialogReply(final FragmentActivity activity, final long fromUser, final InfoTweet it) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.actions)
                .setItems(R.array.actions_reply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (it != null) {
                                ArrayList<String> users = LinksUtils.pullLinksUsers(it.getText());
                                String text = "";
                                String user = "";
                                Entity e = new Entity("users", fromUser);
                                if (e != null) {
                                    user = e.getString("name");
                                }
                                for (int i = 0; i < users.size(); i++) {
                                    if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                            && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                        text += users.get(i) + " ";
                                    }
                                }
                                updateStatus(activity, fromUser, NewStatusActivity.TYPE_REPLY, text, it);
                            }
                        } else if (which == 1) {
                            if (it != null) {
                                ArrayList<String> users = LinksUtils.pullLinksUsers(it.getText());
                                String text = " //cc ";
                                String user = "";
                                Entity e = new Entity("users", fromUser);
                                if (e != null) {
                                    user = e.getString("name");
                                }
                                for (int i = 0; i < users.size(); i++) {
                                    if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                            && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                        text += users.get(i) + " ";
                                    }
                                }
                                updateStatus(activity, fromUser, NewStatusActivity.TYPE_REPLY_ON_COPY, text, it);
                            }
                        } else if (which == 2) {
                            if (it != null) {
                                updateStatus(activity, fromUser, NewStatusActivity.TYPE_REPLY, "", it);
                            }
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    public static void directMessage(FragmentActivity activity, long fromUser, String username) {
        Intent newstatus = new Intent(activity, NewStatusActivity.class);
        if (fromUser > 0) newstatus.putExtra("start_user_id", fromUser);
        newstatus.putExtra("type", NewStatusActivity.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", username);
        activity.startActivity(newstatus);

    }

    public static void updateStatus(final FragmentActivity activity, final String text) {
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendStatus(activity, ents.get(0).getId() + "", text);

        } else {
            final UsersAdapter adapter = new UsersAdapter(activity, ents);

            AlertDialog builder = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendStatus(activity, adapter.getItem(which).getId() + "", text);
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();
        }
    }

    public static void updateStatus(FragmentActivity activity, long fromUser, int type, String text, InfoTweet tweet) {
        updateStatus(activity, fromUser, type, text, tweet, "");
    }

    private static void updateStatus(FragmentActivity activity, long fromUser, int type, String text, InfoTweet tweet, String prev) {
        Intent newstatus = new Intent(activity, NewStatusActivity.class);
        if (fromUser > 0) newstatus.putExtra("start_user_id", fromUser);
        newstatus.putExtra("text", text);
        newstatus.putExtra("type", type);
        newstatus.putExtra("retweet_prev", prev);
        if (tweet != null) {
            if (type == NewStatusActivity.TYPE_REPLY || type == NewStatusActivity.TYPE_REPLY_ON_COPY) {
                newstatus.putExtra("reply_tweetid", tweet.getId());
            }
            if (tweet.isRetweet()) {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatarRetweet());
                newstatus.putExtra("reply_screenname", tweet.getUsernameRetweet());
                newstatus.putExtra("reply_text", tweet.getTextRetweet());
            } else {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatar());
                newstatus.putExtra("reply_screenname", tweet.getUsername());
                newstatus.putExtra("reply_text", tweet.getText());
            }
        }
        activity.startActivity(newstatus);
    }

    public static void showDialogRetweet(FragmentActivity activity, long fromUser, InfoTweet it) {
        showDialogRetweet(activity, fromUser, it, null);
    }

    public static void showDialogRetweet(final FragmentActivity activity, final long fromUser, final InfoTweet it, final Callable callBack) {
        final ArrayList<String> phrases = new ArrayList<String>();
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("types_retweets");

        CharSequence[] c = new CharSequence[ents.size() + 3];
        c[0] = activity.getString(R.string.retweet_now);
        phrases.add("_RN_");
        c[1] = activity.getString(R.string.edit_message);
        phrases.add("_EM_");
        c[2] = activity.getString(R.string.retweet_url);
        phrases.add("_RU_");
        for (int i = 0; i < ents.size(); i++) {
            c[i + 3] = activity.getString(R.string.retweet) + " \"" + ents.get(i).getString("phrase") + "\" " + activity.getString(R.string.now);
            phrases.add(ents.get(i).getString("phrase"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.retweet);
        builder.setItems(c, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phrase = phrases.get(which);
                if (phrase.equals("_RN_")) {
                    if (it != null) {
                        retweetStatus(activity, it.getId(), callBack);
                    }
                } else if (phrase.equals("_EM_")) {
                    if (it != null) {
                        updateStatus(activity, fromUser, NewStatusActivity.TYPE_RETWEET, it.getText(), it);
                    }
                } else if (phrase.equals("_RU_")) {
                    if (it != null) {
                        updateStatus(activity, fromUser, NewStatusActivity.TYPE_RETWEET, it.getUrlTweet(), it);
                    }
                } else {
                    if (it != null) {
                        String text = phrase + " RT: @" + it.getUsername() + ": " + it.getText();
                        if (text.length() > 140) {
                            updateStatus(activity, fromUser, NewStatusActivity.TYPE_RETWEET, it.getText(), it, phrase);
                        } else {
                            updateStatus(activity, text);
                        }
                    }
                }
            }


        });
        builder.setNeutralButton(R.string.show_retweets, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent newstatus = new Intent(activity, RetweetsTypes.class);
                activity.startActivity(newstatus);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    public static void sendStatus(FragmentActivity activity, String users, String text) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("text", text);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 1);
        ent.setValue("username_direct", "");
        ent.setValue("photos", "");
        ent.setValue("mode_tweetlonger", NewStatusActivity.MODE_TL_NONE);
        ent.setValue("reply_tweet_id", "-1");
        ent.setValue("use_geo", PreferenceUtils.getGeo(activity) ? "1" : "0");
        ent.save();

        activity.startService(new Intent(activity, ServiceUpdateStatus.class));
    }

    public static void sendRetweet(FragmentActivity activity, String users, long tweet_id) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 3);
        ent.setValue("reply_tweet_id", tweet_id);
        ent.save();

        activity.startService(new Intent(activity, ServiceUpdateStatus.class));
    }

    public static void retweetStatus(final FragmentActivity activity, final long tweet_id, final Callable callBack) {

        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendRetweet(activity, ents.get(0).getId() + "", tweet_id);
            try {
                if (callBack != null) callBack.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            final UsersAdapter adapter = new UsersAdapter(activity, ents);

            AlertDialog builder = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRetweet(activity, adapter.getItem(which).getId() + "", tweet_id);
                            try {
                                if (callBack != null) callBack.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();

        }

    }

    public static void showDialogTranslation(final TweetActivity activity) {

        View translate_dialog_footer = View.inflate(activity, R.layout.translate_dialog_footer, null);
        final CheckBox translate_default_language_checkbox = (CheckBox) translate_dialog_footer.findViewById(R.id.translate_default_language_checkbox);

        final ArrayList<String> languages_text = new ArrayList<String>();
        Collections.addAll(languages_text, activity.getResources().getStringArray(R.array.languages_translates));

        final ArrayList<String> languages_values = new ArrayList<String>();
        Collections.addAll(languages_values, activity.getResources().getStringArray(R.array.languages_translates_values));

        CharSequence[] languages_char_sequence = new CharSequence[languages_text.size()];
        for (int i = 0; i < languages_text.size(); i++) {
            languages_char_sequence[i] = languages_text.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.language);
        builder.setView(translate_dialog_footer);

        builder.setItems(languages_char_sequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (translate_default_language_checkbox.isChecked()) {
                    PreferenceUtils.saveTraslationDefaultLanguage(activity, languages_values.get(which));
                    Utils.showMessage(activity, R.string.default_language_setting_message);
                }

                activity.translateTweet(languages_values.get(which));
            }
        });

        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    public static void goToMap(FragmentActivity activity, InfoTweet tweet) {
        if (tweet.hasLocation()) {
            Intent map = new Intent(activity, MapSearch.class);
            map.putExtra("longitude", tweet.getLongitude());
            map.putExtra("latitude", tweet.getLatitude());
            activity.startActivity(map);
        }
    }

}
