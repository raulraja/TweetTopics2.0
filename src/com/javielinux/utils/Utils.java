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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoLink;
import com.javielinux.preferences.ColorsApp;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.twitter.ConnectionManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import twitter4j.MediaEntity;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.URLEntity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String KEY_ACTIVITY_ANIMATION = "KEY_ACTIVITY_ANIMATION";
    public static final String KEY_ACTIVITY_USER_ACTIVE = "KEY_ACTIVITY_USER_ACTIVE";
    public static final String KEY_EXTRAS_INFO = "KEY_EXTRAS_INFO";

    public static final int ACTIVITY_ANIMATION_RIGHT = 0;
    public static final int ACTIVITY_ANIMATION_LEFT = 1;

    public static final int NETWORK_TWITTER = 0;
    public static final int NETWORK_FACEBOOK = 1;

    public static String ACTION_WIDGET_CONTROL = "com.javielinux.tweettopics2.WIDGET_CONTROL";
    public static final String URI_SCHEME = "tweettopics2_widget_tweets";

    public static final String SEP_BLOCK = "\n";
    public static final String SEP_VALUES = "\t";

    public static final String APPLICATION_PREFERENCES = "app_prefs";

    public static final String URL_QR = "http://chart.apis.google.com/chart?cht=qr&chs=300x300&chl=tweettopics%%qr";

    public static final String URL_SHARE_THEME_QR = "http://chart.apis.google.com/chart?cht=qr&chs=300x300&chl=tweettopics%%theme";

    public static final String TAG = "TweetTopics2Dev";
    public static final String TAG_ALARM = "TweetTopics2Alarm";
    public static final String TAG_WIDGET = "TweetTopics2Widget";

    public static final String HASHTAG_SHARE = "#TweetTopicsQR";
    public static final String HASHTAG_SHARE_THEME = "#TweetTopicsTheme";

    public static final int NUMBERS_ZEROS_IN_LONG = 24;

    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_RECTANGLE = 1;
    public static final int TYPE_BUBBLE = 2;

    public static final int TYPE_LINK_IMAGE = 0;
    public static final int TYPE_LINK_VIDEO = 1;
    public static final int TYPE_LINK_GENERAL = 2;
    public static final int TYPE_LINK_TWEETOPICS_QR = 3;
    public static final int TYPE_LINK_TWEETOPICS_THEME = 4;
    public static final int TYPE_LINK_TWEET = 5;

    public static final int NOERROR = 1;
    public static final int UNKNOWN_ERROR = 2;
    public static final int LIMIT_ERROR = 3;

    public static final int MAX_NOTIFICATIONS = 50;
    public static final int MAX_NOTIFICATIONS_LITE = 1;

    public static final int HEIGHT_SEARCH_ICON = 48;
    public static final int HEIGHT_SEARCH_ICON_SMALL = 30;
    public static final int HEIGHT_IMAGELINK = 60;
    public static final int HEIGHT_THUMB = 45;
    public static final int HEIGHT_THUMB_NEWSTATUS = 60;
    public static final int HEIGHT_IMAGE = 250;
    public static final int HEIGHT_VIDEO = 150;

    public static final int HEIGHT_PHOTO_SIZE_SMALL = 480;
    public static final int HEIGHT_PHOTO_SIZE_MIDDLE = 768;
    public static final int HEIGHT_PHOTO_SIZE_LARGE = 1600;

    public static final int MAX_ROW_BYSEARCH = 500;
    public static final int MAX_ROW_BYSEARCH_FORCE = 1200;

    static public int AVATAR_SMALL = 30;
    static public int AVATAR_MEDIUM = 36;
    static public int AVATAR_LARGE = 48;
    static public int AVATAR_XLARGE = 54;

    static public String appDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tweettopics2/";
    static public String appIconsDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tweettopics2/icons/";
    static public String appUploadImageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tweettopics2/uploadimage/";
    static public String packageName = "com.javielinux.tweettopics2";
    static public String packageNamePRO = "com.javielinux.tweettopics.pro";

    //static public String filesDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tweettopics/avatars/";

    static public int dip2px(Context cnt, float dip) {
        final float scale = cnt.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dip * scale + 0.5f);
    }


    static public SharedPreferences getPreference(Context cnt) {
        PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
        return PreferenceManager.getDefaultSharedPreferences(cnt);
    }

    static public boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return false;
        }
    }


    public static int getLenghtTweet(String text, int shortURLLength, int shortURLLengthHttps) {
        int length = text.length();
        ArrayList<String> links = LinksUtils.pullLinksHTTP(text);
        for (String link : links) {
            if (link.contains("http:") && link.length() > shortURLLength) {
                length -= link.length();
                length += shortURLLength;
            }
            if (link.contains("https:") && link.length() > shortURLLengthHttps) {
                length -= link.length();
                length += shortURLLengthHttps;
            }
        }
        return length;
    }


    public static String toCapitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static ArrayList<String> getDivide140(String text, String reply) {
        ArrayList<String> ar = new ArrayList<String>();

        int max = 134;
        if (!reply.equals("")) {
            max = 134 - reply.length();
            if (text.startsWith(reply)) {
                text = text.substring(text.indexOf(" ")).trim();
            }
            reply += " ";
        }

        boolean todo = true;
        while (todo) {
            if (text.length() > max) {
                String t1 = text.substring(0, max - 1);
                int pos = t1.lastIndexOf(" ");
                ar.add(reply + text.substring(0, pos));
                text = text.substring(pos).trim();
            } else {
                ar.add(reply + text);
                todo = false;
            }
        }

        ArrayList<String> out = new ArrayList<String>();
        int count = 1;
        for (String a : ar) {
            out.add(a + " (" + count + "/" + ar.size() + ")");
            //Log.d(Utils.TAG, a + " (" + count + "/" + ar.size() + ")");
            count++;
        }

        return out;
    }

    static public String getIconGeneric(Context cnt, String name) {
        if (name.startsWith("#")) {
            return "drawable/letter_hash";
        }
        if (name.startsWith("@")) {
            return "drawable/letter_user";
        }
        int id = 0;
        String c = null;
        try {
            c = name.toLowerCase().substring(0, 1);
            Log.d(Utils.TAG, "es: " + c);
            id = cnt.getResources().getIdentifier(Utils.packageName + ":drawable/letter_" + c, null, null);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id > 0 && c != null) {
            return "drawable/letter_" + c;
        } else {
            return "drawable/letter_az";
        }
    }

    static public String createIconForSearch(Bitmap bmp) {
        int count = 1;
        String tokenFile = "search_1";
        String file = Utils.appDirectory + tokenFile + ".png";
        File f = new File(file);
        while (f.exists()) {
            count++;
            tokenFile = "search_" + count;
            file = Utils.appDirectory + tokenFile + ".png";
            f = new File(file);
        }

        Bitmap avatarBig = Bitmap.createScaledBitmap(bmp, Utils.HEIGHT_SEARCH_ICON, Utils.HEIGHT_SEARCH_ICON, true);
        Bitmap avatarSmall = Bitmap.createScaledBitmap(bmp, Utils.HEIGHT_SEARCH_ICON_SMALL, Utils.HEIGHT_SEARCH_ICON_SMALL, true);

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            avatarBig.compress(Bitmap.CompressFormat.PNG, 90, out);

            String fileSmall = Utils.appDirectory + tokenFile + "_small.png";
            FileOutputStream outSmall = new FileOutputStream(fileSmall);
            avatarSmall.compress(Bitmap.CompressFormat.PNG, 90, outSmall);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tokenFile;
    }

    static public String fillZeros(String number) {
        int fill = NUMBERS_ZEROS_IN_LONG - number.length();
        String out = "";
        for (int i = 0; i < fill; i++) {
            out += "0";
        }
        return out + number;
    }

    static public void createDirectoriesIfIsNecessary() {
        try {
            File dir = new File(appDirectory);
            if (!dir.exists()) dir.mkdir();

            File fileNomedia = new File(appDirectory + ".nomedia");
            if (!fileNomedia.exists()) fileNomedia.createNewFile();

            File dirIcons = new File(appIconsDirectory);
            if (!dirIcons.exists()) dirIcons.mkdir();

            File fileNomediaIcons = new File(appIconsDirectory + ".nomedia");
            if (!fileNomediaIcons.exists()) fileNomediaIcons.createNewFile();

            File dirUploadImageDirectory = new File(appUploadImageDirectory);
            if (!dirUploadImageDirectory.exists()) dirUploadImageDirectory.mkdir();

            File fileUploadImageDirectory = new File(appUploadImageDirectory + ".nomedia");
            if (!fileUploadImageDirectory.exists()) fileUploadImageDirectory.createNewFile();
            /*
            File dirAvatars = new File(filesDirPath);
            if (!dirAvatars.exists()) dirAvatars.mkdir();

            File avatarsNomedia = new File(filesDirPath+".nomedia");
            if (!avatarsNomedia.exists()) avatarsNomedia.createNewFile();
            deleteAvatars(cnt);
            */
            //filesDirPath = cnt.getCacheDir().getPath();


        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

    }

    /*
     static public void deleteAvatars(Context cnt) {
         boolean todo = false;
         long time = getDateDeleteAvatars(cnt);
         if (time<0) {
             setDateDeleteAvatars(cnt, new Date().getTime());
         } else {
             Date dateToday = new Date();
             Date dateRefresh = new Date(time);
             if (dateToday.after(dateRefresh)) {
                 todo = true;
                 Calendar calToday = Calendar.getInstance();
                 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m");
                 String tomorrow = Utils.getTomorrow(calToday.get(Calendar.YEAR), (calToday.get(Calendar.MONTH)+1), calToday.get(Calendar.DATE));
                 try {
                     Date nextDate = format.parse(tomorrow + " 10:00");
                     setDateDeleteAvatars(cnt, nextDate.getTime());
                 } catch (ParseException e) {
                     e.printStackTrace();
                 }

             }
         }

         if (todo) {

             File dir = new File(filesDirPath);
             File[] files = dir.listFiles();
             int max = 300;
             if (files.length>max) {
                 Log.d(TAG, "Borramos " + (files.length-max) + " avatars");
                 Arrays.sort(files, new Comparator<File>(){
                     public int compare(File f1, File f2) {
                         return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                     }
                 });
                 for (int i=0; i<files.length-max; i++) {
                     files[i].delete();
                 }
             }

         }
     }
         */
    static public void saveApiConfiguration(Context cnt) {
        boolean todo = false;
        long time = PreferenceUtils.getDateApiConfiguration(cnt);
        if (time < 0) {
            PreferenceUtils.setDateApiConfiguration(cnt, new Date().getTime());
            todo = true;
        } else {
            Date dateToday = new Date();
            Date dateRefresh = new Date(time);
            if (dateToday.after(dateRefresh)) {
                todo = true;
                Calendar calToday = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m");
                String tomorrow = Utils.getTomorrow(calToday.get(Calendar.YEAR), (calToday.get(Calendar.MONTH) + 1), calToday.get(Calendar.DATE));
                try {
                    Date nextDate = format.parse(tomorrow + " 10:00");
                    PreferenceUtils.setDateApiConfiguration(cnt, nextDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        if (todo) {

            Log.d(Utils.TAG, "Cargar configuración");

            ConnectionManager.getInstance().open(cnt);

            try {
                TwitterAPIConfiguration api = ConnectionManager.getInstance().getUserForSearchesTwitter().getAPIConfiguration();
                PreferenceUtils.setShortURLLength(cnt, api.getShortURLLength());
                PreferenceUtils.setShortURLLengthHttps(cnt, api.getShortURLLengthHttps());
            } catch (TwitterException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    static public String now() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        return formatter.format(currentTime);
    }

    static public void showMessage(Context context, String msg) {
        Toast.makeText(context,
                msg,
                Toast.LENGTH_LONG).show();
    }

    static public void showMessage(Context context, int resmsg) {
        Toast.makeText(context,
                context.getString(resmsg),
                Toast.LENGTH_LONG).show();
    }

    static public void showShortMessage(Context context, String msg) {
        Toast.makeText(context,
                msg,
                Toast.LENGTH_SHORT).show();
    }

    static public void showShortMessage(Context context, int resmsg) {
        Toast.makeText(context,
                context.getString(resmsg),
                Toast.LENGTH_SHORT).show();
    }

    static public String seconds2Time(int seconds) {
        return seconds2Time(seconds, true);
    }

    static public String seconds2Time(int seconds, boolean hasHours) {
        int h = 0, m = 0, s = seconds;
        if (s > 60) {
            m = s / 60;
            s = s % 60;
            if (m > 60) {
                h = m / 60;
            }
        }
        if (s == 60) {
            s = 0;
            m++;
        }
        if (m == 60) {
            m = 0;
            h++;
        }
        if (m == 61) {
            m = 1;
            h++;
        }
        if (!hasHours) {
            m += h * 60;
        }
        String hs = "" + h, ms = "" + m, ss = "" + s;
        if (h < 10) {
            hs = "0" + hs;
        }
        if (m < 10) {
            ms = "0" + ms;
        }
        if (s < 10) {
            ss = "0" + ss;
        }
        if (hasHours) {
            return hs + ":" + ms + ":" + ss;
        } else {
            return ms + ":" + ss;
        }
    }

    static public String getTextURLs(twitter4j.Status st) {
        String out = "";
        URLEntity[] urls = st.getURLEntities();

        if (urls != null && urls.length > 0) {
            for (URLEntity url : urls) {
                if (url.getDisplayURL() != null && !url.getDisplayURL().equals("")) {
                    out += url.getURL().toString() + SEP_VALUES + url.getDisplayURL()
                            + SEP_VALUES + url.getExpandedURL().toString() + SEP_BLOCK;
                }
            }
        }

        MediaEntity[] medias = st.getMediaEntities();

        if (medias != null && medias.length > 0) {
            for (MediaEntity media : medias) {
                if (media.getDisplayURL() != null && !media.getDisplayURL().equals("")) {
                    out += media.getURL().toString() + SEP_VALUES + media.getDisplayURL()
                            + SEP_VALUES + media.getExpandedURL().toString()
                            + SEP_VALUES + media.getMediaURL().toString() + ":thumb"
                            + SEP_VALUES + media.getMediaURL().toString() + ":medium"
                            + SEP_BLOCK;
                }
            }
        }

        return out;
    }
    /*
    static public class InfoURLEntity {
    	String url;
    	String urlDisplay;
    	String urlExpanded;
    }
*/

    static public class URLContent {
        public String normal;
        public String display;
        public String expanded;
        public String linkMediaThumb = null;
        public String linkMediaLarge = null;
    }

    static public URLContent searchContent(ArrayList<URLContent> urls, String url) {
        for (URLContent u : urls) {
            if (u.normal.equals(url) || u.display.equals(url) || u.expanded.equals(url)) {
                return u;
            }
        }
        return null;
    }

    static public ArrayList<URLContent> urls2content(String urls) {
        /*Log.d(Utils.TAG, "urls: " + urls);
          urls = urls.replace("--", SEP_BLOCK);
          urls = urls.replace(";;", SEP_VALUES);
          Log.d(Utils.TAG, "urls: " + urls);*/
        ArrayList<URLContent> out = new ArrayList<URLContent>();

        StringTokenizer tokens = new StringTokenizer(urls, SEP_BLOCK);

        while (tokens.hasMoreTokens()) {
            try {
                String token = tokens.nextToken();
                StringTokenizer hash = new StringTokenizer(token, SEP_VALUES);
                if (hash.countTokens() == 3 || hash.countTokens() == 5) {
                    URLContent u = new URLContent();
                    u.normal = hash.nextToken();
                    u.display = hash.nextToken();
                    u.expanded = hash.nextToken();
                    if (hash.hasMoreTokens()) u.linkMediaThumb = hash.nextToken();
                    if (hash.hasMoreTokens()) u.linkMediaLarge = hash.nextToken();
                    if (u.linkMediaThumb != null && !u.linkMediaThumb.equals("")) {
                        CacheData.getInstance().putURLMedia(u.expanded, u);
                    }
                    out.add(u);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    static public String[] toHTMLTyped(Context cnt, String text, String urls) {
        return toHTMLTyped(cnt, text, urls, "");
    }

    static public String[] toHTMLTyped(Context cnt, String text, String urls, String underline) {

        // 1 normal
        // 2 display
        // 3 Expanded
        int type = Integer.parseInt(Utils.getPreference(cnt).getString("prf_show_links", "2"));

        String[] out = new String[2];

        if (urls == null || urls.equals("") || type == 1) {
            out[0] = text;
            out[1] = Utils.toHTML(cnt, text);
            return out;
        }

        //ArrayList<InfoURLEntity> ents = new ArrayList<InfoURLEntity>();

        String normalText = text;
        String htmlText = Utils.toHTML(cnt, text, underline);

        StringTokenizer tokens = new StringTokenizer(urls, SEP_BLOCK);

        while (tokens.hasMoreTokens()) {
            try {
                String token = tokens.nextToken();
                StringTokenizer hash = new StringTokenizer(token, SEP_VALUES);
                if (hash.countTokens() == 3 || hash.countTokens() == 5) {
                    //InfoURLEntity e = new InfoURLEntity();
                    String url = hash.nextToken();
                    String urlDisplay = hash.nextToken();
                    String urlExpanded = hash.nextToken();
                    if (type == 2) {
                        normalText = normalText.replace(url, urlDisplay);
                        htmlText = htmlText.replace(url, urlDisplay);
                    } else {
                        normalText = normalText.replace(url, urlExpanded);
                        htmlText = htmlText.replace(url, urlExpanded);
                    }
                    //ents.add(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out[0] = normalText;
        out[1] = htmlText;


        return out;
    }

    static public String toHTML(Context context, String text) {
        return toHTML(context, text, "");
    }

    static public String toHTML(Context context, String text, String underline) {
        String out = text.replace("<", "&lt;");
        //out = out.replace(">", "&gt;");
        ArrayList<Integer> valStart = new ArrayList<Integer>();
        ArrayList<Integer> valEnd = new ArrayList<Integer>();

        Comparator<Integer> comparator = Collections.reverseOrder();

        // enlaces

        String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        // hashtag

        regex = "(#[\\w-]+)";
        p = Pattern.compile(regex);
        m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        // usuarios twitter

        regex = "(@[\\w-]+)";
        p = Pattern.compile(regex);
        m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        ThemeManager theme = new ThemeManager(context);

        String tweet_color_link = theme.getStringColor("tweet_color_link");
        String tweet_color_hashtag = theme.getStringColor("tweet_color_hashtag");
        String tweet_color_user = theme.getStringColor("tweet_color_user");

        Collections.sort(valStart, comparator);
        Collections.sort(valEnd, comparator);

        for (int i = 0; i < valStart.size(); i++) {
            int s = valStart.get(i);
            int e = valEnd.get(i);
            String link = out.substring(s, e);
            if (link.equals(underline)) {
                link = "<u>" + out.substring(s, e) + "</u>";
            }
            if (out.substring(s, s + 1).equals("#")) {
                out = out.substring(0, s) + "<font color=\"#" + tweet_color_hashtag + "\">" + link + "</font>" + out.substring(e, out.length());
            } else if (out.substring(s, s + 1).equals("@")) {
                out = out.substring(0, s) + "<font color=\"#" + tweet_color_user + "\">" + link + "</font>" + out.substring(e, out.length());
            } else {
                out = out.substring(0, s) + "<font color=\"#" + tweet_color_link + "\">" + link + "</font>" + out.substring(e, out.length());
            }
        }

        return out;
    }

    static public String toExportHTML(Context context, String text) {
        String out = text.replace("<", "&lt;");
        //out = out.replace(">", "&gt;");
        ArrayList<Integer> valStart = new ArrayList<Integer>();
        ArrayList<Integer> valEnd = new ArrayList<Integer>();

        Comparator<Integer> comparator = Collections.reverseOrder();

        // enlaces

        String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        // hashtag

        regex = "(#[\\w-]+)";
        p = Pattern.compile(regex);
        m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        // usuarios twitter

        regex = "(@[\\w-]+)";
        p = Pattern.compile(regex);
        m = p.matcher(out);
        while (m.find()) {
            valStart.add(m.start());
            valEnd.add(m.end());
        }

        Collections.sort(valStart, comparator);
        Collections.sort(valEnd, comparator);

        for (int i = 0; i < valStart.size(); i++) {
            int s = valStart.get(i);
            int e = valEnd.get(i);
            String link = out.substring(s, e);

            if (out.substring(s, s + 1).equals("#")) {
                out = out.substring(0, s) + "<a href=\"http://twitter.com/#!/search/" + link + "\" class=\"hashtag\">" + link + "</a>" + out.substring(e, out.length());
            } else if (out.substring(s, s + 1).equals("@")) {
                out = out.substring(0, s) + "<a href=\"http://twitter.com/#!/" + link + "\" class=\"user\">" + link + "</a>" + out.substring(e, out.length());
            } else {
                out = out.substring(0, s) + "<a href=\"" + link + "\" class=\"link\">" + link + "</a>" + out.substring(e, out.length());
            }
        }

        return out;
    }

    static public String getQuotedText(String text, String operator, boolean startWithOperator) {
        String query = "";
        if (startWithOperator) query += " " + operator;
        Matcher matcher = Pattern.compile("\\s*\"([^\"]+)\"").matcher(text);
        boolean sw = true;
        while (matcher.find()) {
            String f = matcher.group().trim();
            if (sw) {
                sw = false;
                if (!startWithOperator) query += " ";
            } else {
                query += " " + operator;
            }
            query += f;
            text = text.replace(f, "");
        }
        text = text.trim().replace("  ", " ");
        text = text.replace("  ", " ");

        if (!text.equals("")) { // si queda texto sin estar entre comillas lo ponemos
            if (sw) { // no tiene textos entre comillas
                if (!startWithOperator) query += " ";
            } else { // si tiene textos entre comillas
                query += " " + operator;
            }
            query += text.replace(" ", " " + operator);
        }

        return query;
    }


    static public String getTwitLoger(twitter4j.Status st) {
        String out = "";
        String link = "";
        if (st.getText().contains("(cont) http://t.co/")) {
            URLEntity[] urls = st.getURLEntities();
            if (urls == null || urls.length <= 0) return out;
            for (URLEntity url : urls) {
                if (url.getDisplayURL() != null) {
                    if (url.getDisplayURL().contains("tl.gd")) {
                        link = url.getDisplayURL();
                    }
                }
            }
            if (!link.equals("")) {

                String id = link.substring(link.lastIndexOf("/") + 1);
                String strURL = "http://www.twitlonger.com/api_read/" + id;
                Document doc = null;
                try {
                    URL url;
                    URLConnection urlConn = null;
                    url = new URL(strURL);
                    urlConn = url.openConnection();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    doc = db.parse(urlConn.getInputStream());
                } catch (IOException ioe) {
                } catch (ParserConfigurationException pce) {
                } catch (SAXException se) {
                }
                if (doc != null) {
                    try {
                        //String content = doc.getElementsByTagName("content").item(0).getChildNodes().getLength()+"";//.getFirstChild().getNodeValue();

                        String content = "";
                        NodeList nodes = doc.getElementsByTagName("content").item(0).getChildNodes();
                        for (int i = 0; i < nodes.getLength(); i++) {
                            content += nodes.item(i).getNodeValue();
                        }
                        if (!content.equals("")) {
                            return content;
                        }
                    } catch (Exception e) {
                    }
                }

            }
        }
        return out;
    }

    static public ArrayList<InfoLink> getThumbsTweet(String text) {
        ArrayList<String> links = LinksUtils.pullLinks(text);
        ArrayList<InfoLink> images = new ArrayList<InfoLink>();
        for (int i = 0; i < links.size(); i++) {
            String link = links.get(i);
            if ((!link.startsWith("#")) && (!link.startsWith("@"))) {
                images.add(LinksUtils.getInfoTweet(link));
            }
        }
        return images;
    }

    private static final char[] BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            .toCharArray();

    public static String numberToAlpha(long number) {
        char[] buffer = new char[20];
        int index = 0;
        do {
            buffer[index++] = BASE58_CHARS[(int) (number % BASE58_CHARS.length)];
            number = number / BASE58_CHARS.length;
        } while (number > 0);
        return new String(buffer, 0, index);
    }

    public static long alphaToNumber(String text) {
        char[] chars = text.toCharArray();
        long result = 0;
        long multiplier = 1;
        for (int index = 0; index < chars.length; index++) {
            char c = chars[index];
            int digit;
            if (c >= '1' && c <= '9') {
                digit = c - '1';
            } else if (c >= 'A' && c < 'I') {
                digit = (c - 'A') + 9;
            } else if (c > 'I' && c < 'O') {
                digit = (c - 'J') + 17;
            } else if (c > 'O' && c <= 'Z') {
                digit = (c - 'P') + 22;
            } else if (c >= 'a' && c < 'l') {
                digit = (c - 'a') + 33;
            } else if (c > 'l' && c <= 'z') {
                digit = (c - 'l') + 43;
            } else {
                throw new IllegalArgumentException("Illegal character found: '"
                        + c + "'");
            }

            result += digit * multiplier;
            multiplier = multiplier * BASE58_CHARS.length;
        }
        return result;
    }

    static public int base58_decode(String snipcode) {
        String alphabet = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        int num = snipcode.length();
        int decoded = 0;
        int multi = 1;
        for (int i = (num - 1); i >= 0; i--) {
            decoded = decoded + multi * alphabet.indexOf(snipcode.substring(i, i + 1));
            multi = multi * alphabet.length();
        }
        return decoded;
    }

    static public String getFileFromURL(String url) {
        String[] c = url.split("/");
        return c[c.length - 1];
    }

    public static String diffDate(Date first, Date second) {

        if (second.after(first)) {
            return "0s";
        }

        long diff = ((first.getTime() - second.getTime())) / 1000;

        if (diff < 60) {
            return String.valueOf(diff) + "s";
        } else {
            // minutos
            long sec = diff % 60;
            diff = diff / 60;
            if (diff < 60) {
                return String.valueOf(diff) + "m " + sec + "s";
            } else {
                // horas
                long min = diff % 60;
                diff = diff / 60;
                if (diff < 24) {
                    return String.valueOf(diff) + "h " + min + "m";
                } else {
                    // dias
                    long hours = diff % 24;
                    diff = diff / 24;
                    return String.valueOf(diff) + "d " + hours + "h";
                }
            }
        }

    }

    public static String timeFromTweet(Context cnt, Date timeTweet) {

        if (Integer.parseInt(Utils.getPreference(cnt).getString("prf_date_format", "1")) == 1) {
            return diffDate(new Date(), timeTweet);
        } else {
            Date now = new Date();
            if (now.getDay() == timeTweet.getDay() && now.getMonth() == timeTweet.getMonth()
                    && now.getYear() == timeTweet.getYear()) {
                return DateFormat.getTimeInstance().format(timeTweet);
            } else {
                return DateFormat.getDateInstance().format(timeTweet);
            }
        }

    }

    public static String timeFromTweetExtended(Context cnt, Date timeTweet) {

        if (Integer.parseInt(Utils.getPreference(cnt).getString("prf_date_format", "1")) == 1) {
            if (timeTweet != null) {
                Date now = new Date();

                long diff = ((now.getTime() - timeTweet.getTime())) / 1000;
                String out = "";

                if (diff < 60) {
                    out = String.valueOf(diff) + " " + cnt.getString(R.string.seconds);
                } else {
                    // minutos
                    long sec = diff % 60;
                    diff = diff / 60;
                    if (diff < 60) {
                        out = String.valueOf(diff) + " " + cnt.getString(R.string.minutes) + " " + cnt.getString(R.string.and) + " " + sec + " " + cnt.getString(R.string.seconds);
                    } else {
                        // horas
                        long min = diff % 60;
                        diff = diff / 60;
                        if (diff < 24) {
                            out = String.valueOf(diff) + " " + cnt.getString(R.string.hours) + " " + cnt.getString(R.string.and) + " " + min + " " + cnt.getString(R.string.minutes);
                        } else {
                            // dias
                            long hours = diff % 24;
                            diff = diff / 24;
                            out = String.valueOf(diff) + " " + cnt.getString(R.string.days) + " " + cnt.getString(R.string.and) + " " + hours + " " + cnt.getString(R.string.hours);
                        }
                    }
                }

                return cnt.getString(R.string.date_long, out);
            }
        } else {
            return DateFormat.getDateTimeInstance().format(timeTweet);
        }
        return "";
    }

    public static String getTomorrow(int year, int month, int day) {
        int lastDayMonth = 0;
        if ((month == 1) && (month == 3) && (month == 5) && (month == 7) && (month == 8) && (month == 10) && (month == 12)) {
            lastDayMonth = 31;
        } else if (month == 2) {
            if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
                lastDayMonth = 29;
            else
                lastDayMonth = 28;
        } else {
            lastDayMonth = 30;
        }
        String out = "";
        if (day + 1 > lastDayMonth) {
            if (month + 1 > 12) {
                out = (year + 1) + "-1-1";
            } else {
                out = year + "-" + (month + 1) + "-1";
            }
        } else {
            out = year + "-" + month + "-" + (day + 1);
        }

        return out;
    }

    public static Drawable getDrawable(Context ctx, String text) {
        if (text.startsWith("drawable")) {
            return ctx.getResources().getDrawable(ctx.getResources().getIdentifier(Utils.packageName + ":" + text, null, null));
        }
        if (text.startsWith("file")) {
            return Drawable.createFromPath(Utils.appDirectory + text.substring(5));
        }
        return null;
    }

    public static String getHashFromFile(String file) {
        return String.valueOf(Math.abs(file.hashCode()));
    }

    public static File getFileForSaveURL(Context cnt, String file) {
        return new File(cnt.getCacheDir(), getHashFromFile(file));
    }

    /*
    public static String extractFileFromAvatar(String url) {
        String[] s = url.split("/");
        if (s.length>1) {
            return s[s.length-2] + "_" + s[s.length-1];
        }
        return "";
    }
    */
    public static String getAsset(Context cnt, String file) {
        AssetManager assetManager = cnt.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(file);
        } catch (IOException e) {
            Log.d(TAG, "No se ha podido cargar el fichero " + file);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toString();
    }

    public static boolean importTheme(Context cnt, String text) {

        try {
            text = text.replace(Utils.URL_SHARE_THEME_QR, "");

            Log.d(Utils.TAG, text);

            ColorsApp.loadTheme(cnt, text);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean importSearch(Context cnt, String text) {

        try {
            if (text.startsWith("http://bit.ly")) {
                String url = "http://www.longurlplease.com/api/v1.1?q=" + text;

                HttpGet request = new HttpGet(url);
                HttpClient client = new DefaultHttpClient();
                HttpResponse httpResponse;
                try {
                    httpResponse = client.execute(request);
                    String xml = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = new JSONObject(xml);
                    String t = jsonObject.getString(text);
                    if ((t != null) && t != "") text = t;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            text = text.replace(Utils.URL_QR, "");

            StringTokenizer tokens = new StringTokenizer(text, "||");

            Entity ent = new Entity("search");
            ent.setValue("date_create", Utils.now());
            ent.setValue("last_modified", Utils.now());
            ent.setValue("use_count", 0);

            long icon_id = -1;

            while (tokens.hasMoreTokens()) {

                StringTokenizer hash = new StringTokenizer(tokens.nextToken(), "%%");

                if (hash.countTokens() == 2) {
                    String key = hash.nextToken();
                    String value = hash.nextToken();
                    if (key.equals("name")) {
                        ent.setValue("name", value);
                    } else if (key.equals("and")) {
                        ent.setValue("words_and", value);
                    } else if (key.equals("or")) {
                        ent.setValue("words_or", value);
                    } else if (key.equals("not")) {
                        ent.setValue("words_not", value);
                    } else if (key.equals("from")) {
                        ent.setValue("from_user", value);
                    } else if (key.equals("to")) {
                        ent.setValue("to_user", value);
                    } else if (key.equals("lang")) {
                        ent.setValue("lang", value);
                    } else if (key.equals("att")) {
                        ent.setValue("attitude", value);
                    } else if (key.equals("filt")) {
                        ent.setValue("filter", value);
                    } else if (key.equals("rt")) {
                        ent.setValue("no_retweet", value);
                    } else if (key.equals("geo")) {
                        ent.setValue("use_geo", value);
                    } else if (key.equals("tgeo")) {
                        ent.setValue("type_geo", value);
                    } else if (key.equals("lat")) {
                        ent.setValue("latitude", value);
                    } else if (key.equals("long")) {
                        ent.setValue("longitude", value);
                    } else if (key.equals("tdist")) {
                        ent.setValue("type_distance", value);
                    } else if (key.equals("dist")) {
                        ent.setValue("distance", value);
                    } else if (key.equals("icon")) {
                        icon_id = Long.parseLong(value);
                    }
                }
            }

            if (icon_id > 1) {
                Entity icon = new Entity("icons", icon_id);
                ent.setValue("icon_big", "drawable/" + icon.getValue("icon"));
                ent.setValue("icon_small", "drawable/" + icon.getValue("icon_small"));
            } else {
                String c = ent.getString("name").substring(0, 1).toLowerCase();
                int id = cnt.getResources().getIdentifier(Utils.packageName + ":drawable/letter_" + c, null, null);
                if (id > 0) {
                    ent.setValue("icon_big", "drawable/letter_" + c);
                    ent.setValue("icon_small", "drawable/letter_" + c + "_small");
                } else {
                    ent.setValue("icon_big", "drawable/letter_az");
                    ent.setValue("icon_small", "drawable/letter_az_small");
                }
            }

            ent.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String exportSearch(Context context, long id) {

        String url = URL_QR;

        Entity search = new Entity("search", id);

        url += "||name%%" + search.getString("name");
        url += "||icon%%" + search.getString("icon_id");
        url += "||and%%" + search.getString("words_and");
        url += "||or%%" + search.getString("words_or");
        url += "||not%%" + search.getString("words_not");
        url += "||from%%" + search.getString("from_user");
        url += "||to%%" + search.getString("to_user");
        url += "||lang%%" + search.getString("lang");
        url += "||att%%" + search.getString("attitude");
        url += "||filt%%" + search.getString("filter");
        url += "||rt%%" + search.getString("no_retweet");
        url += "||geo%%" + search.getString("use_geo");
        url += "||tgeo%%" + search.getString("type_geo");
        url += "||lat%%" + search.getString("latitude");
        url += "||long%%" + search.getString("longitude");
        url += "||tdist%%" + search.getString("type_distance");
        url += "||dist%%" + search.getString("distance");

        return LinksUtils.shortURL(context, url);

    }

    public static void sendLastCrash(Activity cnt) {
        try {
            Intent gmail = new Intent(Intent.ACTION_VIEW);
            gmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            gmail.putExtra(Intent.EXTRA_EMAIL, new String[]{cnt.getString(R.string.email_send_errors)});
            gmail.setData(Uri.parse(cnt.getString(R.string.email_send_errors)));
            gmail.putExtra(Intent.EXTRA_SUBJECT, "TweetTopics crash");
            gmail.setType("plain/text");
            gmail.putExtra(Intent.EXTRA_TEXT, ErrorReporter.getErrors(cnt));
            cnt.startActivity(gmail);
        } catch (ActivityNotFoundException e) {
            Intent msg=new Intent(Intent.ACTION_SEND);
            msg.putExtra(Intent.EXTRA_EMAIL, new String[]{cnt.getString(R.string.email_send_errors)});
            msg.putExtra(Intent.EXTRA_SUBJECT, "TweetTopics crash");
            msg.setType("plain/text");
            msg.putExtra(Intent.EXTRA_TEXT, ErrorReporter.getErrors(cnt));
            cnt.startActivity(msg);
        }
    }

    public static boolean isLite(Context context) {
        String pName = context.getString(R.string.package_verified_premium);
        boolean isLite = true;
        PackageManager packageManager = context.getPackageManager();
        try {
            String installPM = packageManager.getInstallerPackageName(pName);
            if (installPM == null) {
                isLite = true;
            } else if (installPM.equals("com.google.android.feedback")) {
                isLite = false;
            } else if (installPM.equals("com.android.vending")) {
                isLite = false;
            }
        } catch (IllegalArgumentException e) {
        }

        return isLite;
    }

    public static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    public static class SlideDrawable extends ShapeDrawable {
        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int mColorStroke;

        public SlideDrawable(Shape s, int c) {
            super(s);
            init(s, c, 255);
        }

        public SlideDrawable(Shape s, int c, int alpha) {
            super(s);
            init(s, c, alpha);

        }

        private void init(Shape s, int c, int alpha) {
            float[] hsv = new float[3];
            Color.colorToHSV(c, hsv);
            if (hsv[2] - .08f > 0) hsv[2] = hsv[2] - .08f;
            mColorStroke = Color.HSVToColor(hsv);

            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(3);
            mStrokePaint.setColor(mColorStroke);
            mStrokePaint.setAlpha(alpha);

            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setColor(c);
            mFillPaint.setAlpha(alpha);
        }

        public int getColorStroke() {
            return mColorStroke;
        }


        @Override
        protected void onDraw(Shape s, Canvas c, Paint p) {
            s.draw(c, p);
            s.draw(c, mFillPaint);
            s.draw(c, mStrokePaint);
        }
    }


}