package com.javielinux.adapters;


import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.CheckFriendlyUserRequest;
import com.javielinux.api.request.ExecuteActionUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.DBUtils;
import com.javielinux.utils.UserActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserProfileAdapter extends BaseAdapter {

    public static class ViewHolder {

        public LinearLayout containerLoading;

        public RelativeLayout containerText;
        public TextView txtText;

        public RelativeLayout containerFriendly;
        public TextView txtUser1;
        public TextView txtUser2;
        public ImageView imgConnectUser;
        public Button btnFollow;

    }

    public static ViewHolder generateViewHolder(final View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.containerLoading = (LinearLayout) v.findViewById(R.id.user_profile_container_loading);

        viewHolder.containerText = (RelativeLayout) v.findViewById(R.id.user_profile_row_container_text);
        viewHolder.txtText = (TextView) v.findViewById(R.id.user_profile_row_text);

        viewHolder.containerFriendly = (RelativeLayout) v.findViewById(R.id.user_profile_row_container_friendly);
        viewHolder.imgConnectUser = (ImageView) v.findViewById(R.id.user_profile_row_friendly_connect);
        viewHolder.txtUser1 = (TextView) v.findViewById(R.id.user_profile_row_friendly_user1);
        viewHolder.txtUser2 = (TextView) v.findViewById(R.id.user_profile_row_friendly_user2);

        viewHolder.btnFollow = (Button) v.findViewById(R.id.user_profile_row_follow);

        return viewHolder;
    }

    public static int KEY_INFO_TEXT = 0;
    public static int KEY_INFO_FRIENDLY = 1;
    public static int KEY_INFO_ADD_FRIEND = 2;
    private FragmentActivity activity;
    private ArrayList<UserProfileItemAdapter> userProfileItemAdapters = new ArrayList<UserProfileItemAdapter>();
    private InfoUsers infoUser;

    public static class UserProfileItemAdapter {
        public int type;
        public InfoUsers.Friend friend;
        public String text;
    }

	public UserProfileAdapter(FragmentActivity activity, InfoUsers infoUser) {
        this.activity = activity;
        this.infoUser = infoUser;

        if (activity != null && infoUser != null) {
            reload();
        }

	}

    public void changeRelationShip(InfoUsers.Friend friend) {
        APITweetTopics.execute(activity, activity.getSupportLoaderManager(), new APIDelegate() {
            @Override
            public void onResults(BaseResponse result) {
                notifyDataSetChanged();
            }

            @Override
            public void onError(ErrorResponse error) {

            }
        }, new ExecuteActionUserRequest(UserActions.USER_ACTION_CHANGE_RELATIONSHIP, friend, infoUser));
    }

    public void reload() {
        UserProfileItemAdapter item1 = new UserProfileItemAdapter();
        item1.type = KEY_INFO_TEXT;
        item1.text = String.format("%d %s", infoUser.getFollowing(), activity.getString(R.string.following));
        userProfileItemAdapters.add(item1);

        UserProfileItemAdapter item2 = new UserProfileItemAdapter();
        item2.type = KEY_INFO_TEXT;
        item2.text = String.format("%d %s", infoUser.getFollowers(), activity.getString(R.string.followers));
        userProfileItemAdapters.add(item2);

        UserProfileItemAdapter item3 = new UserProfileItemAdapter();
        item3.type = KEY_INFO_TEXT;
        item3.text = String.format("%d %s", infoUser.getTweets(), activity.getString(R.string.tweets));
        userProfileItemAdapters.add(item3);

        HashMap<String, InfoUsers.Friend> friendly = infoUser.getFriendly();

        Iterator it = friendly.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            UserProfileItemAdapter item = new UserProfileItemAdapter();
            item.type = KEY_INFO_FRIENDLY;
            item.friend = (InfoUsers.Friend) e.getValue();
            userProfileItemAdapters.add(item);
        }
    }

    @Override
    public int getCount() {
        return userProfileItemAdapters.size();
    }

    @Override
    public UserProfileItemAdapter getItem(int position) {
        return userProfileItemAdapters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        UserProfileItemAdapter item = getItem(position);
        View v = null;

        ViewHolder viewHolder;

        if (null == convertView) {
            v = View.inflate(activity, R.layout.user_profile_row, null);
            viewHolder = generateViewHolder(v);
            v.setTag(generateViewHolder(v));
        } else {
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }

        if (item.type==KEY_INFO_TEXT) {
            viewHolder.containerFriendly.setVisibility(View.GONE);
            viewHolder.containerText.setVisibility(View.VISIBLE);
            viewHolder.containerLoading.setVisibility(View.GONE);

            viewHolder.txtText.setText(item.text);
        }

        if (item.type==KEY_INFO_FRIENDLY) {

            if (item.friend.checked) {
                viewHolder.containerFriendly.setVisibility(View.VISIBLE);
                viewHolder.containerText.setVisibility(View.GONE);
                viewHolder.containerLoading.setVisibility(View.GONE);

                viewHolder.txtUser1.setText(infoUser.getName());
                viewHolder.txtUser2.setText(item.friend.user);

                if (item.friend.friend && item.friend.follower) {
                    viewHolder.imgConnectUser.setImageResource(R.drawable.connects_on_on);
                } else if (item.friend.friend && !item.friend.follower) {
                    viewHolder.imgConnectUser.setImageResource(R.drawable.connects_off_on);
                } else if (!item.friend.friend && item.friend.follower) {
                    viewHolder.imgConnectUser.setImageResource(R.drawable.connects_on_off);
                } else {
                    viewHolder.imgConnectUser.setImageResource(R.drawable.connects_off_off);
                }

                if (DBUtils.getIdFromUserName(item.friend.user)>0) {
                    viewHolder.btnFollow.setVisibility(View.VISIBLE);
                    viewHolder.btnFollow.setTag(item.friend);
                    viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changeRelationShip((InfoUsers.Friend) view.getTag());
                        }
                    });
                    if (item.friend.follower) {
                        viewHolder.btnFollow.setText(R.string.unfollow);
                    } else {
                        viewHolder.btnFollow.setText(R.string.follow);
                    }
                } else {
                    viewHolder.btnFollow.setVisibility(View.GONE);
                }

            } else {
                viewHolder.containerFriendly.setVisibility(View.GONE);
                viewHolder.containerText.setVisibility(View.GONE);
                viewHolder.containerLoading.setVisibility(View.VISIBLE);

                APITweetTopics.execute(activity, activity.getSupportLoaderManager(), new APIDelegate() {
                    @Override
                    public void onResults(BaseResponse result) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorResponse error) {

                    }
                }, new CheckFriendlyUserRequest(infoUser, infoUser.getName(), item.friend.user));

            }

        }
		
		return v;
	}
	

}