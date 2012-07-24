package com.javielinux.api.response;

import com.javielinux.infos.InfoTweet;

import java.util.ArrayList;

public class TwitterUserDBResponse implements BaseResponse {

    private ArrayList<InfoTweet> infoTweets;
    private int countHide;
    private int position;


    @Override
    public boolean isError() {
        return false;
    }

    public ArrayList<InfoTweet> getInfoTweets() {
        return infoTweets;
    }

    public void setInfoTweets(ArrayList<InfoTweet> infoTweets) {
        this.infoTweets = infoTweets;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCountHide() {
        return countHide;
    }

    public void setCountHide(int countHide) {
        this.countHide = countHide;
    }
}
