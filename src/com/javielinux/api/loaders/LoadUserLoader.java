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
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.CacheData;
import twitter4j.TwitterException;
import twitter4j.User;

public class LoadUserLoader extends AsynchronousLoader<BaseResponse> {

    private LoadUserRequest request;

    public LoadUserLoader(Context context, LoadUserRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            LoadUserResponse response = new LoadUserResponse();

			ConnectionManager.getInstance().open(getContext());

			User user_data = ConnectionManager.getInstance().getUserForSearchesTwitter().showUser(request.getUser());

            InfoUsers infoUsers = new InfoUsers(user_data);

            response.setInfoUsers(infoUsers);

            CacheData.getInstance().addCacheUsers(infoUsers);

            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		} catch (NullPointerException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		} catch (Exception e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		}
    }
}
