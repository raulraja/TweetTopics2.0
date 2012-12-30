/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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