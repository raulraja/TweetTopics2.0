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
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.TwitterUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TwitterUserResponse;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

public class TwitterUserLoader extends AsynchronousLoader<BaseResponse> {

    private TwitterUserRequest request;

    public TwitterUserLoader(Context context, TwitterUserRequest request) {
        super(context);

        this.request = request;

    }

    private InfoSaveTweets saveTimeline(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_TIMELINE);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(request.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                infoSaveTweets = new InfoSaveTweets();
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;
    }

    private InfoSaveTweets saveMentions(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_MENTIONS);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(request.getUserId()));

        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                InfoSaveTweets info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;
    }

    private InfoSaveTweets saveDirects(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            // TODO: Comprobar este código
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(request.getUserId()));

            EntityTweetUser entityTweetUser_send = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser_send.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(request.getUserId()));

        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                InfoSaveTweets info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;

    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            TwitterUserResponse response = new TwitterUserResponse();

            ConnectionManager.getInstance().open(getContext());

            response.setUserId(request.getUserId());
            response.setColumn(request.getColumn());

            if (request.getColumn() == TweetTopicsUtils.COLUMN_TIMELINE) {
                InfoSaveTweets infoSaveTweets = saveTimeline(response.getUserId());
                if (infoSaveTweets.getError() == Utils.LIMIT_ERROR) {
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setTypeError(Utils.LIMIT_ERROR);
                    errorResponse.setRateError(infoSaveTweets.getRate());
                    return errorResponse;
                } else {
                    response.setInfo(infoSaveTweets);
                }
            }
            if (request.getColumn() == TweetTopicsUtils.COLUMN_MENTIONS) {
                InfoSaveTweets infoSaveTweets = saveMentions(response.getUserId());
                if (infoSaveTweets.getError() == Utils.LIMIT_ERROR) {
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setTypeError(Utils.LIMIT_ERROR);
                    errorResponse.setRateError(infoSaveTweets.getRate());
                    return errorResponse;
                } else {
                    response.setInfo(infoSaveTweets);
                }
            }
            if (request.getColumn() == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
                InfoSaveTweets infoSaveTweets = saveDirects(response.getUserId());
                if (infoSaveTweets.getError() == Utils.LIMIT_ERROR) {
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setTypeError(Utils.LIMIT_ERROR);
                    errorResponse.setRateError(infoSaveTweets.getRate());
                    return errorResponse;
                } else {
                    response.setInfo(infoSaveTweets);
                }
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }
    }
}
