package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.TrendsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class TrendsLoader extends AsynchronousLoader<BaseResponse> {

    private int location_id;

    public TrendsLoader(Context context, TrendsRequest request) {
        super(context);

        this.location_id = request.getLocationId();
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            ConnectionManager.getInstance().open(getContext());

            TrendsResponse response = new TrendsResponse();
            response.setTrends(ConnectionManager.getInstance().getAnonymousTwitter().getLocationTrends(location_id).getTrends());
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
