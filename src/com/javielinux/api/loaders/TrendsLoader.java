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
import com.javielinux.api.request.TrendsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Trend;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Arrays;

public class TrendsLoader extends AsynchronousLoader<BaseResponse> {

    private int location_id;

    public TrendsLoader(Context context, TrendsRequest request) {
        super(context);

        this.location_id = request.getLocationId();
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            ConnectionManager.getInstance().open(getContext());

            TrendsResponse response = new TrendsResponse();
            Trend[] trends_list = ConnectionManager.getInstance().getUserForSearchesTwitter().getPlaceTrends(location_id).getTrends();

            response.setTrends(new ArrayList<Trend>(Arrays.asList(trends_list)));
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
