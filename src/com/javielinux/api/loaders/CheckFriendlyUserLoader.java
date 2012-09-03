package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.CheckFriendlyUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.CheckFriendlyUserResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.CacheData;
import twitter4j.User;

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
            if (infoUsers==null) {
                User user_data = ConnectionManager.getInstance().getAnonymousTwitter().showUser(request.getUser());
                infoUsers = new InfoUsers(user_data);
                CacheData.addCacheUsers(infoUsers);
            }
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
