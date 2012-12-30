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
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoWeb;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.LinksUtils;

public class LoadLinkLoader extends AsynchronousLoader<BaseResponse> {

    private LoadLinkRequest request;

    public LoadLinkLoader(Context context, LoadLinkRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        LoadLinkResponse response = new LoadLinkResponse();

        InfoLink il = request.getInfoLink();

        if (il==null) {
            il = LinksUtils.getInfoTweet(request.getLink());
            if (il!=null) {
                CacheData.getInstance().putCacheInfoLinks(request.getLink(), il);
            }
        }

        if (il!=null) {
            if (il.getType()==InfoLink.GENERAL && !il.isExtensiveInfo()) { // es un link
                try {
                    InfoWeb info = new InfoWeb(il.getLink());

                    if (!info.getTitle().equals("")) {
                        il.setTitle(info.getTitle());
                    }
                    if (!info.getDescription().equals("")) {
                        il.setDescription(info.getDescription());
                    }
                    if (!"".equals(info.getImage())) {
                        il.setLinkImageThumb(info.getImage());
                    }

                    il.setExtensiveInfo(true);
                } catch (Exception e) {
                    e.printStackTrace();
//                    ErrorResponse errorResponse = new ErrorResponse();
//                    errorResponse.setError(e, e.getMessage());
//                    return errorResponse;
                }

            }

        }

        if (il!=null) CacheData.getInstance().putCacheInfoLinks(request.getLink(), il);

        response.setInfoLink(il);

        return response;


    }

}
