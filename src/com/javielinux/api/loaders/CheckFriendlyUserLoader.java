package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.CheckFriendlyUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.CheckFriendlyUserResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoUsers;

public class CheckFriendlyUserLoader extends AsynchronousLoader<BaseResponse> {

    private CheckFriendlyUserRequest request;

    public CheckFriendlyUserLoader(Context context, CheckFriendlyUserRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            CheckFriendlyUserResponse response = new CheckFriendlyUserResponse();
            InfoUsers infoUsers = request.getInfoUsers();
            infoUsers.checkFriend(getContext(), request.getUser());
            response.setInfoUsers(infoUsers);
            return response;
		} catch (NullPointerException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		} catch (Exception e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		}
    }
}
