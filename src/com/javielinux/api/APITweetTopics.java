package com.javielinux.api;


import android.content.Context;
import android.support.v4.app.LoaderManager;
import com.javielinux.api.request.BaseRequest;

public class APITweetTopics {

    public static void execute(Context context, LoaderManager loaderManager, APIDelegate delegate, BaseRequest request) {
        APILoader api = new APILoader(context, loaderManager, delegate);
        api.execute(request);
    }

}
