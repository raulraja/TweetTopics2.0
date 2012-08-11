package com.javielinux.adapters;


import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserProfileAdapter extends BaseAdapter {

    public static class ViewHolder {

        public RelativeLayout containerText;
        public TextView txtText;

        public RelativeLayout containerFriendly;
        public TextView txtUser1;
        public TextView txtUser2;
        public ImageView imgConnectUser;

    }

    public static ViewHolder generateViewHolder(View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.containerText = (RelativeLayout) v.findViewById(R.id.user_profile_row_container_text);
        viewHolder.txtText = (TextView) v.findViewById(R.id.user_profile_row_text);

        viewHolder.containerFriendly = (RelativeLayout) v.findViewById(R.id.user_profile_row_container_friendly);
        viewHolder.imgConnectUser = (ImageView) v.findViewById(R.id.user_profile_row_friendly_connect);
        viewHolder.txtUser1 = (TextView) v.findViewById(R.id.user_profile_row_friendly_user1);
        viewHolder.txtUser2 = (TextView) v.findViewById(R.id.user_profile_row_friendly_user2);

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

        reload();

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

            viewHolder.txtText.setText(item.text);
        }

        if (item.type==KEY_INFO_FRIENDLY) {
            viewHolder.containerFriendly.setVisibility(View.VISIBLE);
            viewHolder.containerText.setVisibility(View.GONE);

            viewHolder.txtUser1.setText(infoUser.getName());
            viewHolder.txtUser2.setText(item.friend.user);

        }
		
		return v;
	}
	

}