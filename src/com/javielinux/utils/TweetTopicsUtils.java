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
    public static final int TWEET_TYPE_USER_LIST = 5;

    public static boolean hasColumn(long userId, int column) {
        for (Entity entityColumn : DataFramework.getInstance().getEntityList("columns")) {
            if (entityColumn.getLong("user_id") == userId && entityColumn.getInt("type_id")==column) {
                return true;
            }
        }
        return false;
    }

}
