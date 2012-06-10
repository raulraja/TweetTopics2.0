package com.javielinux.utils;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class TweetTopicsUtils {

    public static final int COLUMN_TIMELINE = 1;
    public static final int COLUMN_MENTIONS = 2;
    public static final int COLUMN_FAVORITES = 3;
    public static final int COLUMN_DIRECT_MESSAGES = 4;
    public static final int COLUMN_SENT_DIRECT_MESSAGES = 5;
    public static final int COLUMN_LIST_USER = 6;
    public static final int COLUMN_YOUR_TWEETS_RETWEETED = 7;
    public static final int COLUMN_RETWEETS_BY_YOU = 8;
    public static final int COLUMN_RETWEETS_BY_OTHERS = 9;
    public static final int COLUMN_TRENDING_TOPIC = 10;
    public static final int COLUMN_SEARCH = 11;
    public static final int COLUMN_FOLLOWERS = 12;
    public static final int COLUMN_FOLLOWINGS = 13;
    public static final int COLUMN_TIMELINE_OF_USER = 14;
    public static final int COLUMN_SAVED_TWEETS = 15;
    public static final int COLUMN_SAVED_IMAGES = 16;
    public static final int COLUMN_SAVED_LINKS = 17;
    public static final int COLUMN_MY_ACTIVITY = 18;

    public static final int TWEET_TYPE_TIMELINE = 0;
    public static final int TWEET_TYPE_MENTIONS = 1;
    public static final int TWEET_TYPE_FAVORITES = 2;
    public static final int TWEET_TYPE_DIRECTMESSAGES = 3;
    public static final int TWEET_TYPE_SENT_DIRECTMESSAGES = 4;


    public static boolean hasColumn(long userId, int column) {
        for (Entity entityColumn : DataFramework.getInstance().getEntityList("columns")) {
            if (entityColumn.getLong("user_id") == userId && entityColumn.getInt("type_id")==column) {
                return true;
            }
        }
        return false;
    }

}
