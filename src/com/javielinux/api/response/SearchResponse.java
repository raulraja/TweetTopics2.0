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
