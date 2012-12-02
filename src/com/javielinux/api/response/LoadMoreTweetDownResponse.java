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
