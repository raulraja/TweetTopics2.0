package com.javielinux.api.response;

import com.javielinux.infos.InfoSaveTweets;

public class SaveFirstTweetsResponse implements BaseResponse {

    private InfoSaveTweets infoSaveTweets;

    public InfoSaveTweets getInfoSaveTweets() {
        return infoSaveTweets;
    }
    public void setInfoSaveTweets(InfoSaveTweets infoSaveTweets) {
        this.infoSaveTweets = infoSaveTweets;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
