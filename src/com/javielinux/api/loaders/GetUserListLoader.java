package com.javielinux.api.loaders;


import android.content.Context;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetUserListRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserListResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.ResponseList;
import twitter4j.UserList;

public class GetUserListLoader extends AsynchronousLoader<BaseResponse> {

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

            // Delete user list from com.javielinux.database for updating the info
            String sql_delete_user_list = "DELETE FROM user_lists WHERE user_id="+ request.getEntity().getInt("user_id") + " AND type_id=1";
            DataFramework.getInstance().getDB().execSQL(sql_delete_user_list);

            ResponseList<UserList> user_list = ConnectionManager.getInstance().getTwitter(request.getEntity().getInt("user_id")).getAllUserLists(request.getEntity().getString("name"));

            for (int i = 0; i < user_list.size(); i++) {
                Entity user_list_entity = new Entity("user_lists");

                user_list_entity.setValue("user_id", request.getEntity().getInt("user_id"));
                user_list_entity.setValue("user_screenname", user_list.get(i).getUser().getScreenName());
                user_list_entity.setValue("url_avatar", user_list.get(i).getUser().getProfileImageURL());
                user_list_entity.setValue("userlist_id", user_list.get(i).getId());
                user_list_entity.setValue("type_id", 1);
                user_list_entity.setValue("name", user_list.get(i).getName());
                user_list_entity.setValue("full_name", user_list.get(i).getFullName());

                if (!user_list_entity.save())
                    throw new Exception();
            }

            // Delete user list from com.javielinux.database for updating the info
            String sql_delete_user_following_list = "DELETE FROM user_lists WHERE user_id="+ request.getEntity().getInt("user_id") + " AND type_id=2";
            DataFramework.getInstance().getDB().execSQL(sql_delete_user_following_list);

            ResponseList<UserList> user_following_list = ConnectionManager.getInstance().getTwitter(request.getEntity().getInt("user_id")).getUserListMemberships(request.getEntity().getString("name"), -1);

            for (int i = 0; i < user_following_list.size(); i++) {
                Entity user_list_entity = new Entity("user_lists");
                user_list_entity.setValue("user_id", request.getEntity().getInt("user_id"));
                user_list_entity.setValue("user_screenname", user_following_list.get(i).getUser().getScreenName());
                user_list_entity.setValue("url_avatar", user_following_list.get(i).getUser().getProfileImageURL());
                user_list_entity.setValue("userlist_id", user_following_list.get(i).getId());
                user_list_entity.setValue("type_id", 2);
                user_list_entity.setValue("name", user_following_list.get(i).getName());
                user_list_entity.setValue("full_name", user_following_list.get(i).getFullName());

                if (!user_list_entity.save())
                    throw new Exception();
            }

            response.setReady(true);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }

    }

}
