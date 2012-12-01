package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetUserFriendshipMembersRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserFriendshipMembersResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.IDs;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class GetUserFriendshipMembersLoader extends AsynchronousLoader<BaseResponse> {

	public static int FOLLOWERS = 1;
	public static int FRIENDS = 2;

    private GetUserFriendshipMembersRequest request;

    public GetUserFriendshipMembersLoader(Context context, GetUserFriendshipMembersRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            GetUserFriendshipMembersResponse response = new GetUserFriendshipMembersResponse();
            ArrayList<InfoTweet> result = new ArrayList<InfoTweet>();

            ConnectionManager.getInstance().open(getContext());

             if (request.getType()==FOLLOWERS) {
                long cursor = -1;
                IDs followers_ids_response = ConnectionManager.getInstance().getUserForSearchesTwitter().getFollowersIDs(request.getUser(), cursor);

                response.setFriendshipMembersIds(followers_ids_response.getIDs());
			} else if (request.getType()==FRIENDS) {
                long cursor = -1;
                IDs friends_ids_response = ConnectionManager.getInstance().getUserForSearchesTwitter().getFriendsIDs(request.getUser(), cursor);

                response.setFriendshipMembersIds(friends_ids_response.getIDs());
            }

            return response;

		} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
		} catch (OutOfMemoryError e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
        } catch (Exception e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
		}
    }
}
