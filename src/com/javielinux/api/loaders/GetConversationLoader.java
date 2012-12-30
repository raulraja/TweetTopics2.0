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
import com.javielinux.api.request.GetConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetConversationResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class GetConversationLoader extends AsynchronousLoader<BaseResponse> {

    private GetConversationRequest request;

    public GetConversationLoader(Context context, GetConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            GetConversationResponse response = new GetConversationResponse();

            ConnectionManager.getInstance().open(getContext());
            twitter4j.Status status = ConnectionManager.getInstance().getUserForSearchesTwitter().showStatus(request.getId());

            InfoTweet infoTweet = new InfoTweet(status);
            response.setConversationTweet(infoTweet);

            // TODO : ver como funciona el publish progress en los loaders
            /*
            publishProgress(result);

            while (status.getInReplyToStatusId() > 0) {
                status = ConnectionManager.getInstance().getTwitter().showStatus(status.getInReplyToStatusId());

                result.conversation_status = status;
                publishProgress(result);
            }             */

            return response;
        } catch (TwitterException exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        }

    }

}
