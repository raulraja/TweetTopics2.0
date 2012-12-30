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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class IntegrationADWAdapter extends ArrayAdapter<InfoADWIntegration> {

	public static String PREFERENCES_SEARCH = "prefs_search";
	public static String PREFERENCES_TIMELINE = "prefs_timeline";
	public static String PREFERENCES_MENTIONS = "prefs_mentions";
	public static String PREFERENCES_DIRECTS = "prefs_directs";
	
	private IntegrationADW mIntegracionADW;
	
	public IntegrationADWAdapter(IntegrationADW cnt, ArrayList<InfoADWIntegration> statii) {
		super(cnt, android.R.layout.simple_list_item_1, statii);
		mIntegracionADW = cnt;
	}
	
	public static String getPreferenceColor(String pref) {
		return pref + "_color";
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoADWIntegration item = getItem(position);
		View v = null;
		
		final int pos = position+1;
		
		if (null == convertView) {
			v = View.inflate(mIntegracionADW, R.layout.row_adw, null);
		} else {
			v = convertView;
		}
		
		Bitmap bmp = Bitmap.createBitmap(50, 30, Config.RGB_565);
		Canvas c = new Canvas(bmp);
		c.drawColor(Color.parseColor(item.getColor()));
		
		ImageView imgColor = (ImageView)v.findViewById(R.id.image_color);	
		imgColor.setImageBitmap(bmp);
		imgColor.setTag(position+1);
		
		imgColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
        		AlertDialog.Builder builder = new AlertDialog.Builder(mIntegracionADW);
        		builder.setTitle(R.string.alarm_longtime_title);
        		builder.setItems(R.array.led_colors, new DialogInterface.OnClickListener() {
        			@Override
					public void onClick(DialogInterface dialog, int which) {
        				String[] res = mIntegracionADW.getResources().getStringArray(R.array.led_colors_values);
        				mIntegracionADW.color(pos, res[which]);
					}
        			
        		});
                builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {         	
                    }
                });
                builder.create();
                builder.show();	 
			}
			
		});
		
		ImageView btUp = (ImageView)v.findViewById(R.id.bt_up);	
		btUp.setTag(position+1);
		btUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIntegracionADW.up(Integer.parseInt(v.getTag().toString()));
			}
			
		});
		
		ImageView btDown = (ImageView)v.findViewById(R.id.bt_down);
		btDown.setTag(position+1);
		btDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIntegracionADW.down(Integer.parseInt(v.getTag().toString()));
			}
			
		});		
		
		if (position==0) {
			btUp.setVisibility(View.INVISIBLE);
		} else {
			btUp.setVisibility(View.VISIBLE);
		}
		
		if (position==3) {
			btDown.setVisibility(View.INVISIBLE);
		} else {
			btDown.setVisibility(View.VISIBLE);
		}
		
		TextView title = (TextView)v.findViewById(R.id.adw_title);
		title.setText(pos + "- " + mIntegracionADW.getString(item.getResTitle()));
		
		TextView desc = (TextView)v.findViewById(R.id.adw_description);
		desc.setText(item.getResDescription());
		
		return v;
	}
	

}