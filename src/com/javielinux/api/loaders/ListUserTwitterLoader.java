package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.ListUserTwitterRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.ListUserTwitterResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;

public class ListUserTwitterLoader extends AsynchronousLoader<BaseResponse> {

    public ListUserTwitterRequest request;

    public ListUserTwitterLoader(Context context, ListUserTwitterRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            ListUserTwitterResponse response = new ListUserTwitterResponse();

            ArrayList<InfoUsers> ar = new ArrayList<InfoUsers>();
            ResponseList<User> users = ConnectionManager.getInstance().getTwitter(request.getUserId()).searchUsers(request.getUser(), 0);
            for (int i=0; i<users.size(); i++) {
                ar.add(new InfoUsers(users.get(i)));
            }
            response.setUsers(ar);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            RateLimitStatus rate = e.getRateLimitStatus();
            if (rate!=null) {
                errorResponse.setTypeError(Utils.LIMIT_ERROR);
                errorResponse.setRateError(rate);
            } else {
                errorResponse.setTypeError(Utils.UNKNOWN_ERROR);
            }
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }

    }

}
