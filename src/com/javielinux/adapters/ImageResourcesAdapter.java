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


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageResourcesAdapter extends ArrayAdapter<Integer> {

	public ImageResourcesAdapter(Context cnt, ArrayList<Integer> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView v = null;
		
		if (null == convertView) {
			v = new ImageView(getContext());
		} else {
			v = (ImageView) convertView;
		}
		
		v.setImageResource(getItem(position));

		return v;
	}
	

}