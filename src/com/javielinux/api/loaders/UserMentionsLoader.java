package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.request.UserMentionsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.api.response.UserMentionsResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.*;

import java.util.ArrayList;

public class UserMentionsLoader extends AsynchronousLoader<BaseResponse> {

    private UserMentionsRequest request;
    private long since_id;

    public UserMentionsLoader(Context context, UserMentionsRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            UserMentionsResponse response = new UserMentionsResponse();

            ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

            String query_text = " to:" + this.request.getInfoUsers().getName();
            Query query = new Query(query_text);
            QueryResult result = ConnectionManager.getInstance().getAnonymousTwitter().search(query);

            ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
            for (Tweet tweet : tweets) {
                infoTweets.add(new InfoTweet(tweet));
            }

            response.setInfoTweets(infoTweets);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
