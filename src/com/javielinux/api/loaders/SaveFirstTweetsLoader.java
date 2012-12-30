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
import com.javielinux.api.request.SaveFirstTweetsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SaveFirstTweetsResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoSaveTweets;

public class SaveFirstTweetsLoader extends AsynchronousLoader<BaseResponse> {

    private long id = 0;
    private Context context;

    public SaveFirstTweetsLoader(Context context, SaveFirstTweetsRequest request) {
        super(context);
        this.context = context;
        this.id = request.getId();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SaveFirstTweetsResponse response = new SaveFirstTweetsResponse();

		    EntitySearch entitySearch = new EntitySearch(id);
            InfoSaveTweets infoSaveTweets = entitySearch.saveTweets(context, false, -1);

            response.setInfoSaveTweets(infoSaveTweets);
            return response;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(exception, exception.getMessage());
            return response;
        }
    }
}
