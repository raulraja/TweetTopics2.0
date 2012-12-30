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

package com.javielinux.api.loaders;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.TwitterUserDBRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.TwitterUserDBResponse;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoTweet;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TwitterUserDBLoader extends AsynchronousLoader<BaseResponse> {

    private TwitterUserDBRequest request;

    public TwitterUserDBLoader(Context context, TwitterUserDBRequest request) {
        super(context);

        this.request = request;

    }


    @Override
    public BaseResponse loadInBackground() {

        TwitterUserDBResponse response = new TwitterUserDBResponse();

        ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

        if (request.getColumn() == TweetTopicsUtils.COLUMN_SEARCH) {
            ArrayList<Entity> tweets;

            try {
                tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + request.getSearchEntity().getId(), "date desc");
            } catch (Exception exception) {
                tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + request.getSearchEntity().getId(), "date desc", "0," + Utils.MAX_ROW_BYSEARCH);
            }

            int pos = 0;
            int count = 0;
            boolean found = false;
            int countHide = 0;

            for (int i = 0; i < tweets.size(); i++) {

                boolean delete_tweet = false;

                if (i > 0) {
                    if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                        delete_tweet = true;
                    }
                }
                if (delete_tweet) {
                    try {
                        tweets.get(i).delete();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));
                    if (!found && request.getSearchEntity().getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                        infoTweet.setLastRead(true);
                        pos = count;
                        found = true;
                    }


                    if (i >= tweets.size() - 1 && !found) {
                        infoTweet.setLastRead(true);
                        pos = count;
                        found = true;
                    }

                    infoTweet.setRead(found);

                    try {
                        infoTweets.add(infoTweet);
                        count++;
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }
            }

            response.setInfoTweets(infoTweets);
            response.setPosition(pos);
            response.setCountHide(countHide);
        } else if (request.getColumn() == TweetTopicsUtils.COLUMN_SAVED_TWEETS) {
            ArrayList<Entity> tweets;

            try {
                tweets = DataFramework.getInstance().getEntityList("saved_tweets", "", "date desc");
            } catch (Exception exception) {
                tweets = DataFramework.getInstance().getEntityList("saved_tweets", "", "date desc", "0," + Utils.MAX_ROW_BYSEARCH);
            }

            for (int i = 0; i < tweets.size(); i++) {

                boolean delete_tweet = false;

                if (i > 0) {
                    if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                        delete_tweet = true;
                    }
                }
                if (delete_tweet) {
                    try {
                        tweets.get(i).delete();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));

                    try {
                        infoTweets.add(infoTweet);
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }
            }

            response.setInfoTweets(infoTweets);
            response.setPosition(0);
            response.setCountHide(0);
        } else {
            try {
                EntityTweetUser entityTweetUser = new EntityTweetUser(request.getUserId(), request.getTypeUserColumn());

                String whereType = "";

                switch (request.getColumn()) {
                    case TweetTopicsUtils.COLUMN_TIMELINE:
                        whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE;
                        break;
                    case TweetTopicsUtils.COLUMN_MENTIONS:
                        whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS;
                        break;
                    case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                        whereType = " AND (type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " OR type_id = " + TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES + ")";
                        break;
                }

                ArrayList<Entity> tweets;

                try {
                    tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + request.getUserId() + whereType, "date desc, has_more_tweets_down asc");
                } catch (Exception exception) {
                    tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + request.getUserId() + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
                }

                int pos = 0;
                int count = 0;
                boolean found = false;
                int countHide = 0;

                for (int i = 0; i < tweets.size(); i++) {

                    boolean delete_tweet = false;

                    if (i > 0) {
                        if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                            delete_tweet = true;
                        }
                    }
                    if (delete_tweet) {
                        try {
                            tweets.get(i).delete();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else {
                        boolean is_timeline = request.getColumn() ==  TweetTopicsUtils.COLUMN_TIMELINE;

                        if (is_timeline && CacheData.getInstance().isHideUserInText(tweets.get(i).getString("username").toLowerCase())) { // usuario
                            countHide++;
                        } else if (is_timeline && CacheData.getInstance().isHideWordInText(tweets.get(i).getString("text").toLowerCase())) { // palabra
                            countHide++;
                        } else if (is_timeline && CacheData.getInstance().isHideSourceInText(tweets.get(i).getString("source").toLowerCase())) { // fuente
                            countHide++;
                        } else {
                            InfoTweet infoTweet = new InfoTweet(tweets.get(i));
                            if (!found && entityTweetUser.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                                infoTweet.setLastRead(true);
                                pos = count;
                                found = true;
                            }

                            if (i >= tweets.size() - 1 && !found) {
                                infoTweet.setLastRead(true);
                                pos = count;
                                found = true;
                            }

                            infoTweet.setRead(found);

                            try {
                                infoTweets.add(infoTweet);
                                /*if (r.hasMoreTweetDown()) {
                                    response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                                }*/
                                count++;
                            } catch (OutOfMemoryError er) {
                                i = tweets.size();
                            }
                        }
                    }

                }

                response.setInfoTweets(infoTweets);
                response.setPosition(pos);
                response.setCountHide(countHide);
            } catch (CursorIndexOutOfBoundsException e) {

            }
        }

        return response;

    }
}
