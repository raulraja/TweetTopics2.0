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
