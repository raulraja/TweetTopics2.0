package com.javielinux.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class TweetQuickAlertAdapter extends ArrayAdapter<Entity> {
	
	private Context context;
	
	public TweetQuickAlertAdapter(Context cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		context = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(context, R.layout.row_tweetquick_alert, null);
		} else {
			v = convertView;
		}
		
		TextView name = (TextView)v.findViewById(R.id.quick_name);
		name.setText(item.getString("name"));
				
		TextView counter = (TextView)v.findViewById(R.id.quick_counter);
		counter.setText(context.getText(R.string.counter) + ": "+item.getString("count"));
		
		
		return v;
	}
	

}