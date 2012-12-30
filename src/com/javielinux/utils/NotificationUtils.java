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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import com.android.dataframework.Entity;
import com.javielinux.notifications.SearchNotifications;
import com.javielinux.notifications.UserNotifications;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.updatestatus.LaunchServiceUpdateStatus;

import java.util.List;

public class NotificationUtils {

    private static int MAX_MESSAGES_INBOX = 8;

    private static int ID_NOTIFICATION_UPDATES = 151515;
    private static int ID_NOTIFICATION_SEARCHES = 161616;

    public static int getUniqueId() {
         return (int) (System.currentTimeMillis() & 0xfffffff);
    }

    public static void cancelNotification(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID_NOTIFICATION_UPDATES);
    }

    public static void cancelUserNotification(Context context, long id) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int) id);
    }

    public static void cancelSearchNotification(Context context, long id) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID_NOTIFICATION_SEARCHES);
    }

    public static void sendNotification(Context context, String title, String text, String info, boolean tryAgain, boolean feedback) {

        Intent intent = new Intent();
        if (tryAgain) intent = new Intent(context, LaunchServiceUpdateStatus.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, getUniqueId(), intent, 0);

        Notification notification = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            notification = new Notification(R.drawable.ic_stat_send_tweet, text, System.currentTimeMillis());
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.setLatestEventInfo(context, title, text, contentIntent);
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            builder
                    .setSmallIcon(R.drawable.ic_stat_send_tweet)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentInfo(info)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
                    .setContentIntent(contentIntent);
            notification = builder.getNotification();
        }

        if (notification != null) {
            if (feedback) {
                addFeedback(context, notification);
            }
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(ID_NOTIFICATION_UPDATES, notification);
        }

    }

    public static void sendUserNotification(Context context, UserNotifications userNotifications) {

        if (userNotifications.getCount() > 0) {
            Intent intent = new Intent(context, TweetTopicsActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_USER, userNotifications.getId());
            int typeColumn = TweetTopicsUtils.COLUMN_TIMELINE;
            if (userNotifications.getCountMentions() > 0) {
                typeColumn = TweetTopicsUtils.COLUMN_MENTIONS;
            }
            if (userNotifications.getCountDMs() > 0) {
                typeColumn = TweetTopicsUtils.COLUMN_DIRECT_MESSAGES;
            }
            intent.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, typeColumn);
            PendingIntent contentIntent = PendingIntent.getActivity(context, getUniqueId(), intent, 0);

            Notification notification = null;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                String title = "";
                String text = "";

                if (userNotifications.getCount() == 1) {
                    Entity tweet = new Entity("tweets_user", userNotifications.getFirstId());
                    title = tweet.getString("username");
                    text = tweet.getString("text");
                } else {
                    title = userNotifications.getName();
                    if (userNotifications.getCountTimeline() > 0) {
                        text += context.getString(R.string.notif_timeline) + ": " + userNotifications.getCountTimeline() + " ";
                    }
                    if (userNotifications.getCountMentions() > 0) {
                        text += context.getString(R.string.notif_mentions) + ": " + userNotifications.getCountMentions() + " ";
                    }
                    if (userNotifications.getCountDMs() > 0) {
                        text += context.getString(R.string.notif_directs) + ": " + userNotifications.getCountDMs() + " ";
                    }
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    notification = new Notification(R.drawable.ic_stat_notification, text, System.currentTimeMillis());
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    notification.setLatestEventInfo(context, title, text, contentIntent);
                } else {
                    Bitmap bitmapLargeIcon = ImageUtils.getBitmapAvatar(userNotifications.getId(), Utils.AVATAR_LARGE);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setTicker(text)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(title)
                            .setContentText(text)
                            .setLargeIcon(bitmapLargeIcon)
                            .setContentIntent(contentIntent);

                    notification = builder.getNotification();
                }
            } else {

                if (userNotifications.getCount() == 1) {
                    Bitmap bitmapLargeIcon = ImageUtils.getBitmapAvatar(userNotifications.getId(), Utils.AVATAR_LARGE);

                    Entity tweet = new Entity("tweets_user", userNotifications.getFirstId());
                    int type = tweet.getInt("type_id");
                    String shortTitle = context.getString(R.string.messageTo, "@" + userNotifications.getName());
                    String shortMessage = "";
                    if (type == TweetTopicsUtils.TWEET_TYPE_TIMELINE) {
                        shortMessage = context.getString(R.string.timelineFrom, "@" + tweet.getString("username"));
                    } else if (type == TweetTopicsUtils.TWEET_TYPE_MENTIONS) {
                        shortMessage = context.getString(R.string.mentionFrom, "@" + tweet.getString("username"));
                    } else if (type == TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES) {
                        shortMessage = context.getString(R.string.dmFrom, "@" + tweet.getString("username"));
                    }

                    Notification.Builder builder = new Notification.Builder(context);
                    builder
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setTicker(shortMessage)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(shortTitle)
                            .setContentText(shortMessage)
                            .setLargeIcon(bitmapLargeIcon)
                            .setContentIntent(contentIntent);

                    Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(builder)
                            .bigText(tweet.getString("text"))
                            .setBigContentTitle(shortTitle)
                            .setSummaryText(shortMessage);

                    notification = bigTextStyle.build();
                } else {
                    Bitmap bitmapLargeIcon = ImageUtils.getBitmapAvatar(userNotifications.getId(), Utils.AVATAR_LARGE);

                    String shortTitle = context.getString(R.string.messagesTo, userNotifications.getCount(), "@" + userNotifications.getName());
                    String shortMessage = "";
                    if (userNotifications.getCountTimeline() > 0) {
                        shortMessage += context.getString(R.string.notif_timeline) + ": " + userNotifications.getCountTimeline() + " ";
                    }
                    if (userNotifications.getCountMentions() > 0) {
                        shortMessage += context.getString(R.string.notif_mentions) + ": " + userNotifications.getCountMentions() + " ";
                    }
                    if (userNotifications.getCountDMs() > 0) {
                        shortMessage += context.getString(R.string.notif_directs) + ": " + userNotifications.getCountDMs() + " ";
                    }

                    Notification.Builder builder = new Notification.Builder(context);
                    builder
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setTicker(shortMessage)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(shortTitle)
                            .setContentText(shortMessage)
                            .setLargeIcon(bitmapLargeIcon)
                            .setContentIntent(contentIntent);

                    Notification.InboxStyle n = new Notification.InboxStyle(builder)
                            .setBigContentTitle(shortTitle)
                            .setSummaryText(shortMessage);

                    int count = 0;
                    for (long id : userNotifications.getIdsMentions()) {
                        if (count < MAX_MESSAGES_INBOX) {
                            Entity tweet = new Entity("tweets_user", id);
                            n.addLine(context.getString(R.string.mentioned) + " @" + tweet.getString("username") + ": " + tweet.getString("text"));
                        }
                        count++;
                    }
                    for (long id : userNotifications.getIdsDMs()) {
                        if (count < MAX_MESSAGES_INBOX) {
                            Entity tweet = new Entity("tweets_user", id);
                            n.addLine(context.getString(R.string.dm) + " @" + tweet.getString("username") + ": " + tweet.getString("text"));
                        }
                        count++;
                    }
                    for (long id : userNotifications.getIdsTimeline()) {
                        if (count < MAX_MESSAGES_INBOX) {
                            Entity tweet = new Entity("tweets_user", id);
                            n.addLine(context.getString(R.string.timeline) + " @" + tweet.getString("username") + ": " + tweet.getString("text"));
                        }
                        count++;
                    }

                    notification = n.build();
                }

            }


            if (notification != null) {
                addFeedback(context, notification);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int) userNotifications.getId(), notification);
            }

        }
    }

    public static void sendSearchNotification(Context context, List<SearchNotifications> list) {

        if (list.size() > 0) {

            Intent intent = new Intent(context, TweetTopicsActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            PendingIntent contentIntent = PendingIntent.getActivity(context, getUniqueId(), intent, 0);

            Notification notification = null;

            int countMessages = 0;
            for (SearchNotifications s : list) {
                countMessages += s.getTotal();
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || list.size() == 1) {

                String title = context.getString(R.string.counterSearches, countMessages);
                String text = "";
                String info = "";

                for (SearchNotifications s : list) {
                    if (s.getTotal() > 0) {
                        if (!text.isEmpty()) text += ", ";
                        text += s.getName() + " (" + s.getTotal() + ")";
                    }
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    notification = new Notification(R.drawable.ic_stat_notification, text, System.currentTimeMillis());
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    notification.setLatestEventInfo(context, title, text, contentIntent);
                } else {
                    Bitmap bitmapLargeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setTicker(text)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(title)
                            .setContentText(text)
                            .setContentInfo(info)
                            .setLargeIcon(bitmapLargeIcon)
                            .setContentIntent(contentIntent);

                    notification = builder.getNotification();
                }

            } else {

                Bitmap bitmapLargeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

                String shortTitle = context.getString(R.string.searches);
                String shortMessage = context.getString(R.string.counterSearches, countMessages);

                Notification.Builder builder = new Notification.Builder(context);
                builder
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setTicker(shortMessage)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(shortTitle)
                        .setContentText(shortMessage)
                        .setLargeIcon(bitmapLargeIcon)
                        .setContentIntent(contentIntent);

                Notification.InboxStyle n = new Notification.InboxStyle(builder)
                        .setBigContentTitle(shortTitle)
                        .setSummaryText(shortMessage);

                int count = 0;
                for (SearchNotifications s : list) {
                    if (count < MAX_MESSAGES_INBOX) {
                        n.addLine(context.getString(R.string.counterSearch, s.getTotal(), s.getName()));
                    }
                    count++;
                }

                notification = n.build();

            }

            if (notification != null) {
                addFeedback(context, notification);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(ID_NOTIFICATION_SEARCHES, notification);
            }
        }
    }

    public static void addFeedback(Context context, Notification notification) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean led = preferences.getBoolean("prf_led_notifications", true);
        if (led) {
            String color = preferences.getString("prf_led_color", "#FFFF0000");
            notification.ledARGB = Color.parseColor(color);//0xFFff0000;
            notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            notification.ledOnMS = 100;
            notification.ledOffMS = 100;
        }

        boolean vibrate = preferences.getBoolean("prf_vibrate_notifications", true);
        if (vibrate) {
            int mode = Integer.parseInt(preferences.getString("prf_time_vibrate", "3"));
            if (mode == 1) {
                long[] pattern = {500};
                notification.vibrate = pattern;
            }
            if (mode == 2) {
                long[] pattern = {1000};
                notification.vibrate = pattern;
            }
            if (mode == 3) {
                long[] pattern = {0, 500, 200, 500, 200};
                notification.vibrate = pattern;
            }
            if (mode == 4) {
                long[] pattern = {0, 250, 200, 250, 200, 250, 200, 250, 200};
                notification.vibrate = pattern;
            }
        }

        boolean sound = preferences.getBoolean("prf_sound_notifications", true);
        if (sound) {
            String lringtone = preferences.getString("prf_ringtone", "");
            if (lringtone != "") {
                notification.sound = Uri.parse(lringtone);
            } else {
                notification.sound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
            }

        }
    }

}
