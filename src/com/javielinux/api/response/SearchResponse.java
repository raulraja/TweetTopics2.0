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

import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.infos.InfoTweet;

import java.util.ArrayList;

public class SearchResponse implements BaseResponse {

    private InfoSaveTweets infoSaveTweets = null;
    private ArrayList<InfoTweet> infoTweets = null;

    public InfoSaveTweets getInfoSaveTweets() {
        return infoSaveTweets;
    }
    public void  setInfoSaveTweets(InfoSaveTweets infoSaveTweets) {
        this.infoSaveTweets = infoSaveTweets;
    }

    public ArrayList<InfoTweet> getInfoTweets() {
        return infoTweets;
    }
    public void  setInfoTweets(ArrayList<InfoTweet> infoTweets) {
        this.infoTweets = infoTweets;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
