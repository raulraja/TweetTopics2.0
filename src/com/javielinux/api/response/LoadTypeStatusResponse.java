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

package com.javielinux.api.response;

import com.javielinux.infos.InfoTweet;

import java.util.ArrayList;

public class LoadTypeStatusResponse implements BaseResponse {

    private ArrayList<InfoTweet> infoTweetArrayList;

    public ArrayList<InfoTweet> getInfoTweets() {
        return infoTweetArrayList;
    }
    public void setInfoTweets(ArrayList<InfoTweet> list) {
        this.infoTweetArrayList = list;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
