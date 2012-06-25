/**
 * 
 */
package com.javielinux.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import infos.InfoUsers;
import twitter4j.RateLimitStatus;

import java.util.ArrayList;

public class UserTwitterListAdapter extends ArrayAdapter<InfoUsers> {

	private Context context;
	private int error;
	private RateLimitStatus rate;

	public UserTwitterListAdapter(Context context, ArrayList<InfoUsers> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoUsers user = getItem(position);
		View v = null;
		if (null == convertView) {
			v = View.inflate(context, R.layout.row_users_twitter, null);
		} else {
			v = convertView;
		}
		
		ImageView icon = (ImageView)v.findViewById(R.id.icon);
		
		if (user.getAvatar()!=null) {
			icon.setImageBitmap(user.getAvatar());
		} else {
			icon.setImageResource(R.drawable.avatar);
		}
		
		TextView name = (TextView)v.findViewById(R.id.name);
		
		name.setText(user.getName());
		
		return v;
	}


	public void setError(int error) {
		this.error = error;
	}


	public int getError() {
		return error;
	}


	public void setRate(RateLimitStatus rate) {
		this.rate = rate;
	}


	public RateLimitStatus getRate() {
		return rate;
	}

}