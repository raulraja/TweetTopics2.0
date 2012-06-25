package com.javielinux.api.request;

import infos.InfoTweet;

import java.util.ArrayList;

public class Export2HTMLRequest implements BaseRequest {

    private ArrayList<InfoTweet> tweets;

    public Export2HTMLRequest(ArrayList<InfoTweet> tweets) {
        this.tweets = tweets;
    }


    public ArrayList<InfoTweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<InfoTweet> tweets) {
        this.tweets = tweets;
    }
}
