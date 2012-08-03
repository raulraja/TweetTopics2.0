/**
 * 
 */
package com.javielinux.adapters;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import twitter4j.RateLimitStatus;

import java.util.ArrayList;

public class UserTwitterListAdapter extends ArrayAdapter<InfoUsers> {

	private FragmentActivity activity;
	private int error;
	private RateLimitStatus rate;
    private AQuery listAQuery;

	public UserTwitterListAdapter(FragmentActivity activity, ArrayList<InfoUsers> statii) {
		super(activity, android.R.layout.simple_list_item_1, statii);
		this.activity = activity;
        listAQuery = new AQuery(activity);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoUsers user = getItem(position);
		View v = null;
		if (null == convertView) {
			v = View.inflate(getContext(), R.layout.row_users_twitter, null);
		} else {
			v = convertView;
		}

        AQuery aQuery = listAQuery.recycle(convertView);

        aQuery.id(R.id.icon).image(user.getUrlAvatar(), true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);
		
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