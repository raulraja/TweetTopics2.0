package com.javielinux.api.response;


public class LoadMoreTweetDownResponse implements BaseResponse {
    private boolean hasMoreTweets = false;
    private int pos;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

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
}
