package api.request;

import twitter4j.Twitter;

public class UploadTwitlongerRequest implements BaseRequest {

    private Twitter twitter = null;
    private String tweet_text = "";
    private long tweet_id = 0;
    private boolean use_geolocation = false;

    public Twitter getTwitter() {
        return twitter;
    }
    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public String getTweetText() {
        return tweet_text;
    }
    public void setTweetText(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public long getTweetId() {
        return tweet_id;
    }
    public void setTweetId(long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public boolean getUseGeolocation() {
        return use_geolocation;
    }
    public void setUseGeolocation(boolean use_geolocation) {
        this.use_geolocation = use_geolocation;
    }
}
