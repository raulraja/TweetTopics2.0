package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.twitter.ConnectionManager;
import infos.InfoSaveTweets;

public class SearchLoader extends AsynchronousLoader<BaseResponse> {

    private EntitySearch entitySearch;

    public SearchLoader(Context context, SearchRequest request) {
        super(context);

        this.entitySearch = request.getEntitySearch();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SearchResponse response = new SearchResponse();

            ConnectionManager.getInstance().open(getContext());

            if (entitySearch.getInt("notifications")==1) {
                response.setInfoSaveTweets(entitySearch.saveTweets(getContext(), ConnectionManager.getInstance().getAnonymousTwitter(), false));
            } else {
                response.setInfoSaveTweets(new InfoSaveTweets());

                // TODO Cargas el infoTweet con todos los tweets

                /*
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

                */
            }

            return response;
        /*} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());

            RateLimitStatus rate = e.getRateLimitStatus();

            if (rate!=null) {
                response.setRateError(rate);
            }

            return response;  */
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}