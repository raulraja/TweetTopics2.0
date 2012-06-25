package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.RetweetStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.RetweetStatusResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class RetweetStatusLoader extends AsynchronousLoader<BaseResponse> {

    private RetweetStatusRequest request;

    public RetweetStatusLoader(Context context, RetweetStatusRequest request) {

        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        //TODO: Comprobar el valor devuelto con el valor esperado (error - ready)

		try {
            RetweetStatusResponse response = new RetweetStatusResponse();

			ConnectionManager.getInstance().open(getContext());
			ConnectionManager.getInstance().getTwitter(request.getUserId()).retweetStatus(request.getId());

            response.setReady(true);
            return response;
		} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
