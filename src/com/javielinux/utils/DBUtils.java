package com.javielinux.utils;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.database.EntityTweetUser;

public class DBUtils {

    public static long getIdFromUserName(String name) {
        long id = 0;
        Entity entity = DataFramework.getInstance().getTopEntity("users", "name = '"+name+"'", "");
        if (entity!=null) {
            id = entity.getId();
        }
        return id;
    }

    public static int getUnreadTweetsUser(int column, long id) {
        return new EntityTweetUser(id, convertColumnInType(column)).getValueNewCount();
    }

    public static int getUnreadTweetsSearch(long id) {
        return new EntitySearch(id).getValueNewCount();
    }

    public static int convertColumnInType(int column) {
        int type = 0;
        switch (column) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
                type = TweetTopicsUtils.TWEET_TYPE_TIMELINE;
            break;
            case TweetTopicsUtils.COLUMN_MENTIONS:
                type = TweetTopicsUtils.TWEET_TYPE_MENTIONS;
            break;
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                type = TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES;
            break;
            case TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES:
                type = TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES;
                break;
            case TweetTopicsUtils.COLUMN_FAVORITES:
                type = TweetTopicsUtils.TWEET_TYPE_FAVORITES;
                break;
        }
        return type;
    }

    public static int nextPositionColumn() {
        int pos = -1;
        for (Entity column : DataFramework.getInstance().getEntityList("columns")) {
            if (column.getInt("position") > pos) {
                pos = column.getInt("position");
            }
        }
        return pos + 1;
    }

}
