package api.loaders;

import adapters.ResponseListAdapter;
import adapters.RowResponseList;
import android.content.Context;
import api.AsynchronousLoader;
import api.request.SearchRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.SearchResponse;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import database.EntitySearch;
import infos.InfoSaveTweets;
import twitter4j.*;

import java.util.ArrayList;

public class SearchLoader extends AsynchronousLoader<BaseResponse> {

    private TweetTopicsCore tweetTopicsCore;
    private EntitySearch entitySearch;

    public SearchLoader(Context context, SearchRequest request) {
        super(context);

        this.tweetTopicsCore = request.getTweetTopicsCore();
        this.entitySearch = request.getEntitySearch();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SearchResponse response = new SearchResponse();

            ConnectionManager.getInstance().open(getContext());

            if (entitySearch.getInt("notifications")==1) {
                tweetTopicsCore.setTypeList(TweetTopicsCore.TYPE_LIST_SEARCH_NOTIFICATIONS);
                response.setInfoSaveTweets(entitySearch.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(), false));
            } else {
                tweetTopicsCore.setTypeList(TweetTopicsCore.TYPE_LIST_SEARCH);
                response.setInfoSaveTweets(new InfoSaveTweets());

                ArrayList<RowResponseList> rowResponseLists = new ArrayList<RowResponseList>();

                if (entitySearch.isUser()) {
                    // La búsqueda es de un usuario, así que buscamos en twitter directamente
                    ResponseList<Status> statuses = ConnectionManager.getInstance().getTwitter().getUserTimeline(entitySearch.getString("from_user"));
                    for (twitter4j.Status status : statuses) {
                        RowResponseList rowResponseList = new RowResponseList(status);
                        rowResponseLists.add(rowResponseList);
                    }
                } else {
                    QueryResult result = ConnectionManager.getInstance().getTwitter().search(entitySearch.getQuery(getContext()));
                    ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
                    for (int i=0; i<tweets.size(); i++) {
                        RowResponseList rowResponseList = new RowResponseList(tweets.get(i));
                        rowResponseLists.add(rowResponseList);
                    }
                }

                response.setListAdapter(new ResponseListAdapter(getContext(), tweetTopicsCore, rowResponseLists, entitySearch.getLong("last_tweet_id")));
            }

            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());

            RateLimitStatus rate = e.getRateLimitStatus();

            if (rate!=null) {
                response.setRateError(rate);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
