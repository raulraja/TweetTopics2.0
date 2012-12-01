package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.StatusRetweetersRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.StatusRetweetersResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

public class StatusRetweetersLoader extends AsynchronousLoader<BaseResponse> {

    private StatusRetweetersRequest request;

    public StatusRetweetersLoader(Context context, StatusRetweetersRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            // TODO retweet
			ConnectionManager.getInstance().open(getContext());
            ResponseList<Status> retweeters_list = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweets(request.getId());

            StatusRetweetersResponse response = new StatusRetweetersResponse();
            //response.setUserList(retweeters_list);
            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
