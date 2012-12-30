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
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.SearchContentInDBRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchContentInDBResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.utils.LinksUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchContentInDBLoader extends AsynchronousLoader<BaseResponse> {

    private static final int MAX_RESULTS = 5;

    private SearchContentInDBRequest request;

    public SearchContentInDBLoader(Context context, SearchContentInDBRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            SearchContentInDBResponse response = new SearchContentInDBResponse();
            List<Object> list = new ArrayList<Object>();

            int count = 0;
            List<String> names = new ArrayList<String>();

            if (request.getType().equals(SearchContentInDBRequest.TypeContent.USERS)) {
                List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "username like '" + request.getSearch() + "%'", "username asc");
                for (int i = 0; i < ents.size() && count < MAX_RESULTS; i++) {
                    if (!names.contains(ents.get(i).getString("username"))) {
                        InfoUsers iu = new InfoUsers();
                        iu.setName(ents.get(i).getString("username"));
                        iu.setUrlAvatar(ents.get(i).getString("url_avatar"));
                        names.add(ents.get(i).getString("username"));
                        list.add(iu);
                        count++;
                    }
                }

            } else {
                List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "text like '%#%'", "");
                for (int i = 0; i < ents.size() && count < MAX_RESULTS; i++) {
                    ArrayList<String> hashs = LinksUtils.pullLinksHashTags(ents.get(i).getString("text"));
                    for (String h : hashs) {
                        h = h.replace("#", "");
                        if (!names.contains(h) && h.startsWith(request.getSearch())) {
                            names.add(h);
                            list.add(h);
                            count++;
                        }
                    }
                }
            }

            response.setObjectList(list);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
