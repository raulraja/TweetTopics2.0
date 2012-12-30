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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.javielinux.infos.InfoSubMenuTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.PreferenceUtils;

import java.util.ArrayList;

public class SubMenuTweetsAdapter extends ArrayAdapter<InfoSubMenuTweet> {
	
	private Context context;
	
	public SubMenuTweetsAdapter(Context cnt, ArrayList<InfoSubMenuTweet> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		context = cnt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoSubMenuTweet item = getItem(position);
		View v = null;
		v = View.inflate(context, R.layout.row_submenu_tweet, null);
				
		ImageView im = (ImageView)v.findViewById(R.id.image_submenu);
		im.setImageResource(item.getResDrawable());
		
		TextView name = (TextView)v.findViewById(R.id.name_submenu);
		name.setText(item.getResName());
		
		CheckBox cb = (CheckBox)v.findViewById(R.id.cb_submenu);
		cb.setChecked(item.isValue());
		cb.setTag(item.getCode());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                PreferenceUtils.setSubMenuTweet(buttonView.getContext(), buttonView.getTag().toString(), isChecked);
			}
			
		});
		
		return v;
	}
	

}
