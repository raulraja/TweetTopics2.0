package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.TrendsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Trend;
import twitter4j.TwitterException;

import java.util.ArrayList;

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
            Trend[] trends_list = ConnectionManager.getInstance().getAnonymousTwitter().getLocationTrends(location_id).getTrends();
            ArrayList<Trend> trends_arraylist = new ArrayList<Trend>();

            for (int i = 0; i < trends_list.length; i++)
                trends_arraylist.add(trends_list[i]);

            response.setTrends(new ArrayList<Trend>(trends_arraylist));
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
