package api.request;

import com.javielinux.tweettopics2.TweetTopicsCore;
import database.EntitySearch;

public class SearchRequest implements BaseRequest {

    private TweetTopicsCore tweetTopicsCore;
    private EntitySearch entitySearch = null;

    public SearchRequest(TweetTopicsCore tweetTopicsCore, EntitySearch entitySearch) {
        this.tweetTopicsCore = tweetTopicsCore;
        this.entitySearch = entitySearch;
    }

    public TweetTopicsCore getTweetTopicsCore() {
        return tweetTopicsCore;
    }
    public void setTweetTopicsCore(TweetTopicsCore tweetTopicsCore) {
        this.tweetTopicsCore = tweetTopicsCore;
    }

    public EntitySearch getEntitySearch() {
        return entitySearch;
    }
    public void setEntitySearch(EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }
}
