package api.response;


import adapters.RowResponseList;

import java.util.ArrayList;

public class LoadMoreTweetDownResponse implements BaseResponse {
    private ArrayList<RowResponseList> tweets;
    private boolean hasMoreTweets = false;
    private int pos;

    public ArrayList<RowResponseList> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<RowResponseList> tweets) {
        this.tweets = tweets;
    }

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
}
