package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetUserListRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserListResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.UserList;

public class GetUserListLoader extends AsynchronousLoader<BaseResponse> {

    public final static int OWN_LISTS = 0;
    public final static int MEMBERSHIP_LIST = 1;

    private GetUserListRequest request;

    public GetUserListLoader(Context context, GetUserListRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            GetUserListResponse response = new GetUserListResponse();
            ConnectionManager.getInstance().open(getContext());

            switch (request.getUserListType()) {
                case GetUserListLoader.OWN_LISTS:
                    ResponseList<UserList> userLists;

                    if (request.getUserId() < 0) {
                        userLists = ConnectionManager.getInstance().getUserForSearchesTwitter().getUserLists(request.getScreenName());
                    } else {
                        userLists = ConnectionManager.getInstance().getTwitter(request.getUserId()).getUserLists(request.getScreenName());
                    }

                    for (UserList userList : userLists) {
                        response.getUserListArrayList().add(userList);
                    }
                    break;
                case GetUserListLoader.MEMBERSHIP_LIST:
                    PagableResponseList<UserList> userListMemberships;

                    if (request.getUserId() < 0) {
                        userListMemberships = ConnectionManager.getInstance().getUserForSearchesTwitter().getUserListMemberships(request.getScreenName(), request.getCursor());
                    } else {
                        userListMemberships = ConnectionManager.getInstance().getTwitter(request.getUserId()).getUserListMemberships(request.getScreenName(), request.getCursor());
                    }

                    response.setNextCursor(userListMemberships.getNextCursor());
                    for (UserList userList : userListMemberships) {
                        response.getUserListArrayList().add(userList);
                    }
                    break;
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }

    }

}
