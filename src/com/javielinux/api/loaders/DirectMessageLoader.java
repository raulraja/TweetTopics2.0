package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.DirectMessageRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.DirectMessageResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class DirectMessageLoader extends AsynchronousLoader<BaseResponse> {

    private DirectMessageRequest request;

    public DirectMessageLoader(Context context, DirectMessageRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            DirectMessageResponse response = new DirectMessageResponse();
            if (request.getModeTweetLonger() == NewStatusActivity.MODE_TL_NONE) {
                ConnectionManager.getInstance().getTwitter(request.getUserId()).sendDirectMessage(request.getUser(), request.getText());
            } else {
                ArrayList<String> ar = Utils.getDivide140(request.getText(), "");
                for (String t : ar) {
                    ConnectionManager.getInstance().getTwitter(request.getUserId()).sendDirectMessage(request.getUser(), t);
                }
            }
            response.setSent(true);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}