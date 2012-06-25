package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.TrendsLocationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsLocationResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class TrendsLocationLoader extends AsynchronousLoader<BaseResponse> {

    public TrendsLocationLoader(Context context, TrendsLocationRequest request) {
        super(context);
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            TrendsLocationResponse response = new TrendsLocationResponse();

            ConnectionManager.getInstance().open(getContext());

            response.setLocationList(ConnectionManager.getInstance().getAnonymousTwitter().getAvailableTrends());
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
