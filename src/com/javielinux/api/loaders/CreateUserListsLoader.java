package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.CreateUserListsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.CreateUserListsResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class CreateUserListsLoader extends AsynchronousLoader<BaseResponse> {

    private CreateUserListsRequest request;

    public CreateUserListsLoader(Context context, CreateUserListsRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            ConnectionManager.getInstance().open(getContext());
            ConnectionManager.getInstance().getTwitter(request.getUserId()).createUserList(request.getTitle(), request.isPublic(), request.getDescription());
            return new CreateUserListsResponse();
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
