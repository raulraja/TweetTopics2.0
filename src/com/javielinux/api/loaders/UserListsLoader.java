package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.UserListsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.UserListsResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.UserList;

import java.util.ArrayList;

public class UserListsLoader extends AsynchronousLoader<BaseResponse> {

    public static int ADD_USER = 0;
    public static int SHOW_TWEETS = 1;
    public static int SHOW_TWEETS_FOLLOWINGLIST = 2;

    private UserListsRequest request;

    public UserListsLoader(Context context, UserListsRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            UserListsResponse response = new UserListsResponse();

            ConnectionManager.getInstance().open(getContext());

            response.action = request.getAction();
            response.addUser = request.getAddUser();

            if (request.getAction() == SHOW_TWEETS) {
                    response.setUserList(ConnectionManager.getInstance().getAnonymousTwitter().getAllUserLists(request.getUser()));
            } else if (request.getAction() == SHOW_TWEETS_FOLLOWINGLIST) {
                    response.setUserList(ConnectionManager.getInstance().getAnonymousTwitter().getUserListMemberships(request.getUser(), -1));
            } else {
                ResponseList<UserList> responseList = ConnectionManager.getInstance().getAnonymousTwitter().getAllUserLists(request.getUser());
                ArrayList<UserList> deleteList = new ArrayList<UserList>();

                for (UserList ul : responseList) {
                    if (ul.isFollowing()) deleteList.add(ul);
                }

                responseList.removeAll(deleteList);
                response.setUserList(responseList);
            }

            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
