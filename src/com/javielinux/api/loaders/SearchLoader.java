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
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;

import java.util.ArrayList;

public class SearchLoader extends AsynchronousLoader<BaseResponse> {

    private EntitySearch entitySearch;
    private long since_id;

    public SearchLoader(Context context, SearchRequest request) {
        super(context);

        this.entitySearch = request.getEntitySearch();
        this.since_id = request.getSinceId();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SearchResponse response = new SearchResponse();

            ConnectionManager.getInstance().open(getContext());

            if (entitySearch.getInt("notifications")==1) {
                response.setInfoSaveTweets(entitySearch.saveTweets(getContext(), false, since_id));
            } else {
                response.setInfoSaveTweets(null);

                ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

                if (entitySearch.isUser()) {
                    // La búsqueda es de un usuario, así que buscamos en twitter directamente
                    ResponseList<Status> statuses = ConnectionManager.getInstance().getUserForSearchesTwitter().getUserTimeline(entitySearch.getString("from_user"));
                    for (twitter4j.Status status : statuses) {
                        infoTweets.add(new InfoTweet(status));
                    }
                } else {
                    Query query = entitySearch.getQuery(getContext());
                    if (since_id != -1)
                        query.setSinceId(since_id);
                    QueryResult result = ConnectionManager.getInstance().getUserForSearchesTwitter().search(query);
                    ArrayList<Status> tweets = (ArrayList<Status>)result.getTweets();
                    for (Status tweet : tweets) {
                        infoTweets.add(new InfoTweet(tweet));
                    }
                }

                response.setInfoTweets(infoTweets);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
