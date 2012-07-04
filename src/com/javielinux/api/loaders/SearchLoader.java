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
import infos.InfoTweet;
import twitter4j.*;

import java.util.ArrayList;

public class SearchLoader extends AsynchronousLoader<BaseResponse> {

    private EntitySearch entitySearch;
    private long since_id;

    public SearchLoader(Context context, SearchRequest request) {
        super(context);

        this.entitySearch = request.getEntitySearch();
        this.since_id = request.getSinceId();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SearchResponse response = new SearchResponse();

            ConnectionManager.getInstance().open(getContext());

            if (entitySearch.getInt("notifications")==1) {
                response.setInfoSaveTweets(entitySearch.saveTweets(getContext(), ConnectionManager.getInstance().getAnonymousTwitter(), false, since_id));
            } else {
                response.setInfoSaveTweets(null);

                ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

                if (entitySearch.isUser()) {
                    // La búsqueda es de un usuario, así que buscamos en twitter directamente
                    ResponseList<Status> statuses = ConnectionManager.getInstance().getAnonymousTwitter().getUserTimeline(entitySearch.getString("from_user"));
                    for (twitter4j.Status status : statuses) {
                        infoTweets.add(new InfoTweet(status));
                    }
                } else {
                    Query query = entitySearch.getQuery(getContext());
                    if (since_id != -1)
                        query.setSinceId(since_id);
                    QueryResult result = ConnectionManager.getInstance().getAnonymousTwitter().search(query);
                    ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
                    for (Tweet tweet : tweets) {
                        infoTweets.add(new InfoTweet(tweet));
                    }
                }

                response.setInfoTweets(infoTweets);
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
