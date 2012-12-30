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
import com.javielinux.api.request.ConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ConversationResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class ConversationLoader extends AsynchronousLoader<BaseResponse> {

    private ConversationRequest request;

    public ConversationLoader(Context context, ConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            ConversationResponse response = new ConversationResponse();

            ConnectionManager.getInstance().open(getContext());
            ArrayList<Status> tweets = new ArrayList<twitter4j.Status>();
            twitter4j.Status st = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getId());

            tweets.add(st);
            while (st.getInReplyToStatusId()>0) {
                st = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(st.getInReplyToStatusId());
                tweets.add(st);
            }
            response.setTweets(tweets);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
