package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.ConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ConversationResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class ConversationLoader extends AsynchronousLoader<BaseResponse> {

    private ConversationRequest request;

    public ConversationLoader(Context context, ConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            ConversationResponse response = new ConversationResponse();

            ConnectionManager.getInstance().open(getContext());
            ArrayList<Status> tweets = new ArrayList<twitter4j.Status>();
            twitter4j.Status st = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getId());

            tweets.add(st);
            while (st.getInReplyToStatusId()>0) {
                st = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(st.getInReplyToStatusId());
                tweets.add(st);
            }
            response.setTweets(tweets);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
