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

package com.javielinux.api.request;

import twitter4j.Twitter;

public class UploadStatusRequest implements BaseRequest {

    private Twitter twitter = null;
    private int modeTweetLonger = 0;
    private String tweet_text = "";
    private long tweet_id = 0;
    private boolean use_geolocation = false;

    public UploadStatusRequest(Twitter twitter, int modeTweetLonger) {
        this.twitter = twitter;
        this.modeTweetLonger = modeTweetLonger;
    }

    public Twitter getTwitter() {
        return twitter;
    }
    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public int getModeTweetLonger() {
        return modeTweetLonger;
    }
    public void setModeTweetLonger(int modeTweetLonger) {
        this.modeTweetLonger = modeTweetLonger;
    }

    public String getTweetText() {
        return tweet_text;
    }
    public void setTweetText(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public long getTweetId() {
        return tweet_id;
    }
    public void setTweetId(long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public boolean getUseGeolocation() {
        return use_geolocation;
    }
    public void setUseGeolocation(boolean use_geolocation) {
        this.use_geolocation = use_geolocation;
    }
}
