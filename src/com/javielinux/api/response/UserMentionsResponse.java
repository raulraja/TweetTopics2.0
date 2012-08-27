package com.javielinux.api.response;

import com.javielinux.infos.InfoTweet;

import java.util.ArrayList;

public class UserMentionsResponse implements BaseResponse {

    private ArrayList<InfoTweet> infoTweetArrayList;

    public ArrayList<InfoTweet> getInfoTweets() {
        return infoTweetArrayList;
    }
    public void setInfoTweets(ArrayList<InfoTweet> list) {
        this.infoTweetArrayList = list;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
