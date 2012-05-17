package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.UserListsRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.UserListsResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.UserList;

import java.util.ArrayList;

public class UserListsLoader extends AsynchronousLoader<BaseResponse> {

    public static int ADD_USER = 0;
    public static int SHOW_TWEETS = 1;
    public static int SHOW_TWEETS_FOLLOWINGLIST = 2;

    private int action;
    private String addUser;
    private String user;

    public UserListsLoader(Context context, UserListsRequest request) {
        super(context);

        this.action = request.getAction();
        this.addUser = request.getAddUser();
        this.user = request.getUser();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            UserListsResponse response = new UserListsResponse();

            ConnectionManager.getInstance().open(getContext());

            response.action = action;
            response.addUser = addUser;

            if (action == SHOW_TWEETS) {
                    response.setUserList(ConnectionManager.getInstance().getTwitter().getAllUserLists(user));
            } else if (action == SHOW_TWEETS_FOLLOWINGLIST) {
                    response.setUserList(ConnectionManager.getInstance().getTwitter().getUserListMemberships(user, -1));
            } else {
                ResponseList<UserList> responseList = ConnectionManager.getInstance().getTwitter().getAllUserLists(user);
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
