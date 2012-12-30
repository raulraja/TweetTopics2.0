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
import com.javielinux.api.request.CheckConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.CheckConversationResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class CheckConversationLoader extends AsynchronousLoader<BaseResponse> {

    private CheckConversationRequest request;
    
    public CheckConversationLoader(Context context, CheckConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            CheckConversationResponse response = new CheckConversationResponse();
            ConnectionManager.getInstance().open(getContext());
            if (request.getFrom() == InfoTweet.FROM_STATUS) {
                response.setStatus(ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()));
            } else {
                long i = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()).getInReplyToStatusId();
                if (i>0) {
                    response.setStatus(ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(i));
                }
            }
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
