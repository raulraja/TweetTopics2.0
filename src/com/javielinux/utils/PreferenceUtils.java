package com.javielinux.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoSubMenuTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.widget.ServiceWidgetTweets4x2;

import java.util.ArrayList;
import java.util.Locale;

public class PreferenceUtils {

    private static final String WORK_ALARM_KEY = "work_alarm"; // usado para saber si la alarma esta trabajando
    private static final String WORK_APP_KEY = "work_app"; // usado para saber si la app esta abierta
    private static final String NOTIFICATIONS_KEY = "notifications_app"; // usado cuando sales de la aplicacion
    private static final String TRANSLATION_LANGUAGE_KEY = "translation_language"; // usado cuando sales de la aplicacion

    public static boolean getFinishForceClose(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("force_close")) {
            return prefs.getBoolean("force_close", false);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("force_close", false);
            editor.commit();
            return false;
        }
    }

    public static void setFinishForceClose(Context cnt, boolean value) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("force_close", value);
        editor.commit();
    }

    public static boolean getGeo(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("geo")) {
            return prefs.getBoolean("geo", false);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("geo", false);
            editor.commit();
            return false;
        }
    }

    public static void setGeo(Context cnt, boolean value) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("geo", value);
        editor.commit();
    }

    public static boolean getSubMenuTweet(Context cnt, String submenu) {
        String name = "submenutweet_"+submenu;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(name)) {
            return prefs.getBoolean(name, false);
        } else {
            boolean def = false;
            if (submenu.equals(TweetActions.TWEET_ACTION_REPLY) || submenu.equals(TweetActions.TWEET_ACTION_LAST_READ)
                    || submenu.equals(TweetActions.TWEET_ACTION_READ_AFTER)) {
                def = true;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(name, def);
            editor.commit();
            return def;
        }
    }

    public static void setSubMenuTweet(Context cnt, String submenu, boolean value) {
        String name = "submenutweet_"+submenu;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static ArrayList<String> getArraySubMenuTweet(Context cnt) {
        ArrayList<String> ar = new ArrayList<String>();
        for (int i=0; i< InfoSubMenuTweet.codesSubMenuTweets.length; i++) {
            if (getSubMenuTweet(cnt, InfoSubMenuTweet.codesSubMenuTweets[i])) {
                ar.add(InfoSubMenuTweet.codesSubMenuTweets[i]);
            }
        }
        return ar;
    }

    public static int getColorMentions(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("color_pos_mentions")) {
            return prefs.getInt("color_pos_mentions", 1);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("color_pos_mentions", 1);
            editor.commit();
            return 1;
        }
    }

    public static void setColorMentions(Context cnt, int pos) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("color_pos_mentions", pos);
        editor.commit();
    }

    public static int getColorFavorited(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("color_pos_favorites")) {
            return prefs.getInt("color_pos_favorites", 0);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("color_pos_favorites", 0);
            editor.commit();
            return 0;
        }
    }

    public static void setColorFavorited(Context cnt, int pos) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("color_pos_favorites", pos);
        editor.commit();
    }

    public static String getDefaultTextInTweet(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("default_text")) {
            return prefs.getString("default_text", "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("default_text", "");
            editor.commit();
            return "";
        }
    }

    public static void setDefaultTextInTweet(Context cnt, String text) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("default_text", text);
        editor.commit();
    }

    public static String getUsernameBitly(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("username_bitly")) {
            return prefs.getString("username_bitly", "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username_bitly", "");
            editor.commit();
            return "";
        }
    }

    public static void setUsernameBitly(Context cnt, String username) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username_bitly", username);
        editor.commit();
    }

    public static String getKeyBitly(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("key_bitly")) {
            return prefs.getString("key_bitly", "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key_bitly", "");
            editor.commit();
            return "";
        }
    }

    public static void setKeyBitly(Context cnt, String key) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key_bitly", key);
        editor.commit();
    }

    public static String getUsernameKarmacracy(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("username_karmacracy")) {
            return prefs.getString("username_karmacracy", "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username_karmacracy", "");
            editor.commit();
            return "";
        }
    }

    public static void setUsernameKarmacracy(Context cnt, String username) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username_karmacracy", username);
        editor.commit();
    }

    public static String getKeyKarmacracy(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("key_karmacracy")) {
            return prefs.getString("key_karmacracy", "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("key_karmacracy", "");
            editor.commit();
            return "";
        }
    }

    public static void setKeyKarmacracy(Context cnt, String key) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key_karmacracy", key);
        editor.commit();
    }
    /*
    public static long getDateDeleteAvatars(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("date_detele_avatar")) {
            return prefs.getLong("date_detele_avatar", 14);
        } else {
            Editor editor = prefs.edit();
               editor.putLong("date_detele_avatar", -1);
            editor.commit();
            return -1;
        }
    }

    public static void setDateDeleteAvatars(Context cnt, long time) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong("date_detele_avatar", time);
        editor.commit();
    }
    */
    public static long getDateApiConfiguration(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("date_api_conf")) {
            return prefs.getLong("date_api_conf", 14);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("date_api_conf", -1);
            editor.commit();
            return -1;
        }
    }

    public static void setDateApiConfiguration(Context cnt, long time) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("date_api_conf", time);
        editor.commit();
    }

    public static int getShortURLLength(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("short_url_length")) {
            return prefs.getInt("short_url_length", 20);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("short_url_length", 20);
            editor.commit();
            return 0;
        }
    }

    public static void setShortURLLength(Context cnt, int shortURLLength) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("short_url_length", shortURLLength);
        editor.commit();
    }

    public static int getShortURLLengthHttps(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("short_url_length_https")) {
            return prefs.getInt("short_url_length_https", 21);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("short_url_length_https", 21);
            editor.commit();
            return 0;
        }
    }

    public static void setShortURLLengthHttps(Context cnt, int shortURLLengthHttps) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("short_url_length_https", shortURLLengthHttps);
        editor.commit();
    }

    public static int getWoeidTT(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("woeid")) {
            return prefs.getInt("woeid", 0);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("woeid", 0);
            editor.commit();
            return 0;
        }
    }

    public static void setWoeidTT(Context cnt, int woeid) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("woeid", woeid);
        editor.commit();
    }

    public static int getSizeTitles(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("size_titles")) {
            return prefs.getInt("size_titles", (int)cnt.getResources().getDimension(R.dimen.size_header_tweet));
        } else {
            int dimenDefault = (int)cnt.getResources().getDimension(R.dimen.size_header_tweet);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("size_titles", dimenDefault);
            editor.commit();
            return dimenDefault;
        }
    }

    public static void setSizeTitles(Context cnt, int size) {
        if (size<6) size = 6;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size_titles", size);
        editor.commit();
    }

    public static int getSizeTextNewStatus(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("size_text_new_status")) {
            return prefs.getInt("size_text_new_status", (int)cnt.getResources().getDimension(R.dimen.size_text_tweet));
        } else {
            int dimenDefault = (int)cnt.getResources().getDimension(R.dimen.size_text_tweet);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("size_text_new_status", dimenDefault);
            editor.commit();
            return dimenDefault;
        }
    }

    public static void setSizeTextNewStatus(Context cnt, int size) {
        if (size<10) size = 10;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size_text_new_status", size);
        editor.commit();
    }

    public static int getSizeText(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("size_text")) {
            return prefs.getInt("size_text", (int)cnt.getResources().getDimension(R.dimen.size_text_tweet));
        } else {
            int dimenDefault = (int)cnt.getResources().getDimension(R.dimen.size_text_tweet);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("size_text", dimenDefault);
            editor.commit();
            return dimenDefault;
        }
    }

    public static void setSizeText(Context cnt, int size) {
        if (size<6) size = 6;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size_text", size);
        editor.commit();
    }

    public static int getApplicationAccessCount(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("application_access_count")) {
            return prefs.getInt("application_access_count", 1);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("application_access_count", 1);
            editor.commit();
            return 1;
        }
    }

    public static void setApplicationAccessCount(Context cnt, int access_count) {
        if (access_count>20) access_count = 21;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("application_access_count", access_count);
        editor.commit();
    }

    public static long getWidgetColumn(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("widget_column")) {
            return prefs.getLong("widget_column", 1);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("widget_column", 1);
            editor.commit();
            return 1;
        }
    }

    public static void setWidgetColumn(Context cnt, long column) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("widget_column", column);
        editor.commit();
    }

    public static int getTypeWidget(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("type_widget")) {
            return prefs.getInt("type_widget", ServiceWidgetTweets4x2.TIMELINE);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("type_widget", ServiceWidgetTweets4x2.TIMELINE);
            editor.commit();
            return ServiceWidgetTweets4x2.TIMELINE;
        }
    }

    public static void setTypeWidget(Context cnt, int type) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("type_widget", type);
        editor.commit();
    }

    public static long getIdSearchWidget(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("search_widget")) {
            return prefs.getLong("search_widget", 0);
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("search_widget", 0);
            editor.commit();
            return Long.parseLong("0");
        }
    }

    public static void setIdSearchWidget(Context cnt, long search) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("search_widget", search);
        editor.commit();
    }


    public static boolean getShowHelp(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("show_help")) {
            return false;
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("show_help", false);
            editor.commit();
            return true;
        }
    }

    public static void showChangeLog(Context cnt) {
        boolean showChangeLog = false;
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains("version")) {
            if (!prefs.getString("version", Utils.VERSION).equals(Utils.VERSION)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("version", Utils.VERSION);
                editor.commit();
                showChangeLog = true;
            }
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("version", Utils.VERSION);
            editor.commit();
            showChangeLog = true;
        }

        if (showChangeLog) {
            OnChageVersion(cnt);
            String file = "changelog_en.txt";
            if (Locale.getDefault().getLanguage().equals("es")) {
                file = "changelog_es.txt";
            }

            try {
                AlertDialog builder = DialogUtils.PersonalDialogBuilder.create(cnt, cnt.getString(R.string.changelog), file);
                builder.show();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void OnChageVersion(Context cnt) {
        if (Utils.VERSION.equals("1.32")) {
            ArrayList<String> colorsThemeWhite = new ArrayList<String>();
            colorsThemeWhite.add("#e0c8ce");
            colorsThemeWhite.add("#9be4e5");
            colorsThemeWhite.add("#cdf3be");
            colorsThemeWhite.add("#e1b8e3");
            colorsThemeWhite.add("#f8da88");
            colorsThemeWhite.add("#e3c2a7");
            colorsThemeWhite.add("#e4e58f");
            colorsThemeWhite.add("#b8c4e3");

            ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("type_colors");
            for (Entity ent : ents) {
                if (!ent.getString("color").equals("")) {
                    int pos = colorsThemeWhite.indexOf(ent.getString("color"));
                    ent.setValue("color", "");
                    ent.setValue("pos", pos);
                    ent.save();
                }
            }
        }

        if (Utils.VERSION.equals("1.61")) {
            ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets", "favorite=1");
            for (Entity ent : ents) {
                Entity newent = new Entity("saved_tweets");
                newent.setValue("url_avatar", ent.getString("url_avatar"));
                newent.setValue("username", ent.getString("username"));
                newent.setValue("user_id", ent.getString("user_id"));
                newent.setValue("tweet_id", ent.getString("tweet_id"));
                newent.setValue("text", ent.getString("text"));
                newent.setValue("source", ent.getString("source"));
                newent.setValue("to_username", ent.getString("to_username"));
                newent.setValue("to_user_id", ent.getString("to_user_id"));
                newent.setValue("date", ent.getString("date"));
                newent.setValue("latitude", ent.getString("latitude"));
                newent.setValue("longitude", ent.getString("longitude"));
                newent.save();
                ent.delete();
            }
        }
    }

    public static boolean getStatusWorkAlarm(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(WORK_ALARM_KEY)) {
            return prefs.getBoolean(WORK_ALARM_KEY, false);
        }
        return false;
    }

    public static void saveStatusWorkAlarm(Context cnt, boolean work) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(WORK_ALARM_KEY, work);
        editor.commit();
    }

    public static boolean getStatusWorkApp(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(WORK_APP_KEY)) {
            return prefs.getBoolean(WORK_APP_KEY, false);
        }
        return false;
    }

    public static void saveStatusWorkApp(Context cnt, boolean work) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(WORK_APP_KEY, work);
        editor.commit();
    }

    public static boolean getNotificationsApp(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(NOTIFICATIONS_KEY)) {
            return prefs.getBoolean(NOTIFICATIONS_KEY, true);
        }
        return true;
    }

    public static void saveNotificationsApp(Context cnt, boolean work) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(NOTIFICATIONS_KEY, work);
        editor.commit();
    }

    public static String getTraslationDefaultLanguage(Context cnt) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(TRANSLATION_LANGUAGE_KEY)) {
            return prefs.getString(TRANSLATION_LANGUAGE_KEY, "");
        }
        return "";
    }

    public static void saveTraslationDefaultLanguage(Context cnt, String language) {
        SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TRANSLATION_LANGUAGE_KEY, language);
        editor.commit();
    }
}
