package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.ExecuteActionUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.ExecuteActionUserResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.utils.UserActions;

public class ExecuteActionUserLoader extends AsynchronousLoader<BaseResponse> {

    private ExecuteActionUserRequest request;
    private Context context;

    public ExecuteActionUserLoader(Context context, ExecuteActionUserRequest request) {
        super(context);
        this.context = context;
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            ExecuteActionUserResponse response = new ExecuteActionUserResponse();

            if (request.getAction().equals(UserActions.USER_ACTION_CREATE_BLOCK)) {
                UserActions.goToCreateBlock(context, request.getInfoUsers());
                response.setInfoUsers(request.getInfoUsers());
                return response;
            } else if (request.getAction().equals(UserActions.USER_ACTION_REPORT_SPAM)) {
                UserActions.goToReportSpam(context, request.getInfoUsers());
                response.setInfoUsers(request.getInfoUsers());
                return response;
            } else if (request.getAction().equals(UserActions.USER_ACTION_INCLUDED_LIST)) {
                UserActions.goToIncludeList(context, request.getUserActiveId(), request.getInfoUsers(), request.getUserListId());
                response.setInfoUsers(request.getInfoUsers());
                return response;
            } else if (request.getAction().equals(UserActions.USER_ACTION_CHANGE_RELATIONSHIP)) {
                InfoUsers infoUsers = UserActions.goToChangeRelationship(context, request.getInfoUsers(), request.getFriend());
                if (infoUsers!=null) {
                    response.setInfoUsers(infoUsers);
                    return response;
                } else {
                    ErrorResponse error = new ErrorResponse();
                    error.setError("InfoUsers equal null");
                    return error;
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setError("Action not exist");
                return error;
            }

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
