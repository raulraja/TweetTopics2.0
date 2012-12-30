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
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;

import java.util.ArrayList;

public class ColorsAdapter extends ArrayAdapter<Entity> {	
	
	private Colors mColor;
	ArrayList<String> mColors = new ArrayList<String>();
	
	public ColorsAdapter(Colors cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mColor = cnt;
		ThemeManager t = new ThemeManager(cnt);
		mColors = t.getColors();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mColor, R.layout.row_color, null);
		} else {
			v = convertView;
		}
		
		TextView word = (TextView)v.findViewById(R.id.color_name);
		word.setText(item.getString("name"));
		
		Bitmap bmp =  Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
		
		if (item.getInt("pos")<0) {
			item.setValue("pos", 0);
			item.save();
		}
		
		Canvas cv = new Canvas(bmp);
		cv.drawColor(Color.parseColor( mColors.get(item.getInt("pos") ) ));
		
		ImageView img = (ImageView)v.findViewById(R.id.color_img);
		img.setImageBitmap(bmp);
		
		return v;
	}
	

}