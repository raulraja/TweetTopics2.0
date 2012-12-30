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


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.preference.PreferenceManager;
import com.android.dataframework.DataFramework;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadMoreTweetDownRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadMoreTweetDownResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class LoadMoreTweetDownLoader extends AsynchronousLoader<BaseResponse> {

    private LoadMoreTweetDownRequest request;

    public LoadMoreTweetDownLoader(Context context, LoadMoreTweetDownRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            LoadMoreTweetDownResponse response = new LoadMoreTweetDownResponse();

            PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            int maxDownloadTweet = Integer.parseInt(pref.getString("prf_n_max_download", "60"));
            if (maxDownloadTweet <= 0) maxDownloadTweet = 60;

            ConnectionManager.getInstance().open(getContext());

            Twitter twitter = ConnectionManager.getInstance().getTwitter(request.getUserId());

            Paging p = new Paging();
            p.setCount(maxDownloadTweet);
            p.setSinceId(request.getSinceId());
            p.setMaxId(request.getMaxId());

            ResponseList<Status> statii = null;

            try {
                statii = twitter.getHomeTimeline(p);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            boolean breakTimeline = false;

            if (statii != null && statii.size() >= maxDownloadTweet - 10) {
                p = new Paging(1, 10);
                p.setSinceId(request.getSinceId());
                p.setMaxId(statii.get(statii.size() - 1).getId());
                if (twitter.getHomeTimeline().size() > 0) {
                    breakTimeline = true;
                    response.setHasMoreTweets(true);
                }
            }

            if (statii != null) {

                if (statii.size() > 0) {

                    try {
                        DataFramework.getInstance().open(getContext(), Utils.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    List<InfoTweet> tweets = new ArrayList<InfoTweet>();
                    for (Status status : statii) {
                        tweets.add(new InfoTweet(status));
                    }
                    response.setTweets(tweets);

                    long nextId = 1;
                    Cursor c = DataFramework.getInstance().getCursor("tweets_user", new String[]{DataFramework.KEY_ID},
                            null, null, null, null, DataFramework.KEY_ID + " desc", "1");
                    if (!c.moveToFirst()) {
                        c.close();
                        nextId = 1;
                    } else {
                        long Id = c.getInt(0) + 1;
                        c.close();
                        nextId = Id;
                    }

                    try {
                        boolean isFirst = true;
                        for (int i = statii.size() - 1; i >= 0; i--) {
                            User u = statii.get(i).getUser();
                            if (u != null) {
                                ContentValues args = new ContentValues();
                                args.put(DataFramework.KEY_ID, "" + nextId);
                                args.put("type_id", TweetTopicsUtils.TWEET_TYPE_TIMELINE);
                                args.put("user_tt_id", "" + request.getUserId());
                                if (u.getProfileImageURL() != null) {
                                    args.put("url_avatar", u.getProfileImageURL().toString());
                                } else {
                                    args.put("url_avatar", "");
                                }
                                args.put("username", u.getScreenName());
                                args.put("fullname", u.getName());
                                args.put("user_id", "" + u.getId());
                                args.put("tweet_id", Utils.fillZeros("" + statii.get(i).getId()));
                                args.put("source", statii.get(i).getSource());
                                args.put("to_username", statii.get(i).getInReplyToScreenName());
                                args.put("to_user_id", "" + statii.get(i).getInReplyToUserId());
                                args.put("date", String.valueOf(statii.get(i).getCreatedAt().getTime()));
                                if (statii.get(i).getRetweetedStatus() != null) {
                                    args.put("is_retweet", 1);
                                    args.put("retweet_url_avatar", statii.get(i).getRetweetedStatus().getUser().getProfileImageURL().toString());
                                    args.put("retweet_username", statii.get(i).getRetweetedStatus().getUser().getScreenName());
                                    args.put("retweet_source", statii.get(i).getRetweetedStatus().getSource());
                                    String t = Utils.getTwitLoger(statii.get(i).getRetweetedStatus());
                                    if (t.equals("")) {
                                        args.put("text", statii.get(i).getRetweetedStatus().getText());
                                        args.put("text_urls", Utils.getTextURLs(statii.get(i).getRetweetedStatus()));
                                    } else {
                                        args.put("text", t);
                                    }
                                    args.put("is_favorite", 0);
                                } else {
                                    String t = Utils.getTwitLoger(statii.get(i));
                                    if (t.equals("")) {
                                        args.put("text", statii.get(i).getText());
                                        args.put("text_urls", Utils.getTextURLs(statii.get(i)));
                                    } else {
                                        args.put("text", t);
                                    }

                                    if (statii.get(i).isFavorited()) {
                                        args.put("is_favorite", 1);
                                    }
                                }

                                if (statii.get(i).getGeoLocation() != null) {
                                    args.put("latitude", statii.get(i).getGeoLocation().getLatitude());
                                    args.put("longitude", statii.get(i).getGeoLocation().getLongitude());
                                }
                                args.put("reply_tweet_id", statii.get(i).getInReplyToStatusId());

                                if (breakTimeline && isFirst) args.put("has_more_tweets_down", 1);

                                DataFramework.getInstance().getDB().insert("tweets_user", null, args);

                                nextId++;

                                if (isFirst) isFirst = false;
                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    DataFramework.getInstance().close();

                }

            }

            return response;

        } catch (TwitterException twitterException) {
            twitterException.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(twitterException, twitterException.getMessage());
            return errorResponse;
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        }

    }

}
