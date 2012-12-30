/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
