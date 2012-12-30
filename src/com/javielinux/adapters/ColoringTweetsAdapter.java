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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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

public class ColoringTweetsAdapter extends ArrayAdapter<Entity> {
	
	private Context context;
	ArrayList<String> mColors = new ArrayList<String>();
	
	public ColoringTweetsAdapter(Context cnt, ArrayList<Entity> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		context = cnt;
		ThemeManager t = new ThemeManager(cnt);
		mColors = t.getColors();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = getItem(position);
		View v = null;

		if (null == convertView) {
			v = View.inflate(context, R.layout.row_coloringtweets, null);
		} else {
			v = convertView;
		}
		
        try {
            Bitmap bmp = Bitmap.createBitmap(30, 30, Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.parseColor( mColors.get(item.getInt("pos")) ));
            
            ImageView im = (ImageView)v.findViewById(R.id.color_image);
            
            im.setImageBitmap(bmp);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
            
		TextView name = (TextView)v.findViewById(R.id.color_name);
		name.setText(item.getString("name"));		
		
		return v;
	}
	

}