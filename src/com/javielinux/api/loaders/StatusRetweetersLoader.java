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
import com.javielinux.api.request.StatusRetweetersRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.StatusRetweetersResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatusRetweetersLoader extends AsynchronousLoader<BaseResponse> {

    private StatusRetweetersRequest request;

    public StatusRetweetersLoader(Context context, StatusRetweetersRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            // TODO retweet
			ConnectionManager.getInstance().open(getContext());
            ResponseList<Status> retweeters_list = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweets(request.getId());

            StatusRetweetersResponse response = new StatusRetweetersResponse();
            //response.setUserList(retweeters_list);
            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
