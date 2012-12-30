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

package com.javielinux.adapters;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.preferences.QuietWords;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class QuietWordsAdapter extends ArrayAdapter<Entity> {
	
	private QuietWords mTweetQuick;
	
	public QuietWordsAdapter(QuietWords cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mTweetQuick = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mTweetQuick, R.layout.row_quiet, null);
		} else {
			v = convertView;
		}
		
		TextView word = (TextView)v.findViewById(R.id.quick_name);
		word.setText(item.getString("word"));
		
		TextView type = (TextView)v.findViewById(R.id.quick_type);
		if (item.getInt("type_id")==1) {
			type.setText("("+mTweetQuick.getString(R.string.word)+")");
		} 
		if (item.getInt("type_id")==2) {
			type.setText("("+mTweetQuick.getString(R.string.user)+")");
		}
		if (item.getInt("type_id")==3) {
			type.setText("("+mTweetQuick.getString(R.string.quit_source)+")");
		}
		
		
		return v;
	}
	

}