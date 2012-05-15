package api.response;


import twitter4j.Status;

import java.util.ArrayList;

public class ConversationResponse implements BaseResponse {
    private ArrayList<Status> tweets;

    public ArrayList<Status> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Status> tweets) {
        this.tweets = tweets;
    }
}