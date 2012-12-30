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

import java.util.List;

public class LoadMoreTweetDownResponse implements BaseResponse {
    private boolean hasMoreTweets = false;
    private List<InfoTweet> tweets;

    public boolean isHasMoreTweets() {
        return hasMoreTweets;
    }

    public void setHasMoreTweets(boolean hasMoreTweets) {
        this.hasMoreTweets = hasMoreTweets;
    }

    @Override
    public boolean isError() {
        return false;
    }

    public List<InfoTweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<InfoTweet> tweets) {
        this.tweets = tweets;
    }
}
