package com.javielinux.preferences;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class TweetDraftAdapter extends ArrayAdapter<Entity> {
	
	private TweetDraft mTweetDraft;
	
	public TweetDraftAdapter(TweetDraft cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mTweetDraft = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mTweetDraft, R.layout.row_tweetdraft, null);
		} else {
			v = convertView;
		}
				
		TextView text = (TextView)v.findViewById(R.id.draft_text);
		text.setText(item.getString("text"));
		
		
		return v;
	}
	

}