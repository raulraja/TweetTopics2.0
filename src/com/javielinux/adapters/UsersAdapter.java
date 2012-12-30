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
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<Entity> {

	private Context context;

	public UsersAdapter(Context context, ArrayList<Entity> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity user = getItem(position);
		View v = null;
		if (null == convertView) {
			v = View.inflate(context, R.layout.row_users_twitter, null);
		} else {
			v = convertView;
		}
		
		ImageView icon = (ImageView)v.findViewById(R.id.icon);
		icon.setImageBitmap(ImageUtils.getBitmapAvatar(user.getId(), Utils.AVATAR_LARGE));
		
		TextView name = (TextView)v.findViewById(R.id.name);
		
		name.setText(user.getString("name"));
		
		return v;
	}


}