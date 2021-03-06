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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class ColorsAppAdapter extends ArrayAdapter<InfoColorsApp> {
	
	private ColorsApp mColorsApp;
	
	public ColorsAppAdapter(ColorsApp cnt, ArrayList<InfoColorsApp> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mColorsApp = cnt;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoColorsApp item = getItem(position);
		
		if (item.getType()==InfoColorsApp.TYPE_SECTION) {
			View v = View.inflate(mColorsApp, R.layout.row_colors_app_section, null);
			
			TextView title = (TextView)v.findViewById(R.id.name);
			title.setText(item.getTitle());
			return v;
		} else {
		
			View v = View.inflate(mColorsApp, R.layout.row_colors_app, null);
			
			Bitmap bmp = Bitmap.createBitmap(50, 50, Config.RGB_565);
			Canvas c = new Canvas(bmp);
			c.drawColor(Color.parseColor(item.getColor()));
			
			ImageView imgColor = (ImageView)v.findViewById(R.id.image_color);	
			imgColor.setImageBitmap(bmp);
			
	
			TextView title = (TextView)v.findViewById(R.id.color_app_title);
			title.setText(item.getTitle());
			
			TextView desc = (TextView)v.findViewById(R.id.color_app_desc);
			desc.setText(item.getDescription());
			return v;
		}

		
	}
	

}