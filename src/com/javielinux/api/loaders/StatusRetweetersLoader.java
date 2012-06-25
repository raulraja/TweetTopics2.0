package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.StatusRetweetersRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.StatusRetweetersResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class StatusRetweetersLoader extends AsynchronousLoader<BaseResponse> {

    private StatusRetweetersRequest request;

    public StatusRetweetersLoader(Context context, StatusRetweetersRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
			ConnectionManager.getInstance().open(getContext());
            ResponseList<User> retweeters_list = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweetedBy(request.getId(), new Paging(1, 100));

            StatusRetweetersResponse response = new StatusRetweetersResponse();
            response.setUserList(retweeters_list);
            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
