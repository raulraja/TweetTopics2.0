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

package com.javielinux.tweettopics2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class ThemeManager {
	
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_SELECTED = 2;
	public static final int TYPE_PRESS = 3;
	public static final int TYPE_OFF = 4;
	
	public static final int THEME_DEFAULT = 1;
	public static final int THEME_DARK = 2;
	
	private int mTheme = THEME_DEFAULT;
	private Context mContext;
	private Resources mResources;
	
	ArrayList<String> mColors = new ArrayList<String>();

	public ThemeManager(Context context) {
		mContext = context;
		mResources = context.getResources();
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		mTheme = (Integer.parseInt(preference.getString("prf_theme", "2")));
		setColors();
	}
	
	public int getTheme() {
		return mTheme;	
	}
	
	public void setTheme() {
		if ( mTheme == 1 ) {
			mContext.setTheme(R.style.Theme_Twitter);
    	} else {
    		mContext.setTheme(R.style.Theme_TwitterBlack);
    	}
	}

    public void setTranslucentTheme() {
        if ( mTheme == 1 ) {
            mContext.setTheme(R.style.Theme_Twitter_Translucent);
        } else {
            mContext.setTheme(R.style.Theme_TwitterBlack_Translucent);
        }
    }

    public void setDialogTheme() {
        if ( mTheme == 1 ) {
            mContext.setTheme(R.style.Theme_Twitter_Dialog);
        } else {
            mContext.setTheme(R.style.Theme_TwitterBlack_Dialog);
        }
    }
		
	public int getResource(String resource){
		int rsc = -1;
		
		if ( mTheme == 1 ) {
			int resource_id = mResources.getIdentifier(resource, "drawable", Utils.packageName);
			if (resource_id!=0) return resource_id;
    	} else {
    		int resource_id = mResources.getIdentifier(resource+"_dark", "drawable", Utils.packageName);
			if (resource_id!=0) return resource_id;	
    	}
		
		// sino el tema por defecto
		if (rsc==-1) {
			int resource_id = mResources.getIdentifier(resource, "drawable", Utils.packageName);
			if (resource_id!=0) return resource_id;
		}
		
		return rsc;
	}
	
	public Drawable getDrawable(String resource){
		return mResources.getDrawable(getResource(resource));
	}
	
	public Drawable getDrawableMainButton(int resource, int type){
		Drawable d = mResources.getDrawable(resource);
		if (type==TYPE_NORMAL) {
			d = ImageUtils.colorDrawable(d, getColor("color_main_button_normal"));
		}
		if (type==TYPE_SELECTED) {
			d = ImageUtils.colorDrawable(d, getColor("color_main_button_selected"));
		}
		return d;
	}
	
	public Drawable getDrawableTweetButton(int resource, int type){
		Drawable d = mResources.getDrawable(resource);
		if (type==TYPE_NORMAL) {
			d = ImageUtils.colorDrawable(d, getColor("color_tweet_buttons_normal"));
			d.setAlpha(255);
		}
		if (type==TYPE_PRESS) {
			d = ImageUtils.colorDrawable(d, getColor("color_tweet_buttons_press"));
			d.setAlpha(255);
		}
		if (type==TYPE_OFF) {
			d = ImageUtils.colorDrawable(d, getColor("color_tweet_buttons_normal"));
			d.setAlpha(80);
		}
		return d;
	}
	
	public String getStringColor(String resource) {
		return getStringColor(resource, true);
	}
	
	public String getStringColorOriginal(String resource) {
		return getStringColor(resource, false);
	}
	
	public String getStringColor(String resource, boolean searchPrefBefore) {
		
		if (searchPrefBefore) {
			SharedPreferences prefs = mContext.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
	        if (prefs.contains(resource)) {
	        	return prefs.getString(resource, "000000");
	        }
		}
		
		int rsc = -1;
		
		if ( mTheme == 1 ) {
			rsc = mResources.getIdentifier(resource, "color", Utils.packageName);
    	} else {
    		rsc = mResources.getIdentifier(resource+"_dark", "color", Utils.packageName);
    	}
		
		// sino el tema por defecto
		if (rsc==-1) {
			rsc = mResources.getIdentifier(resource, "color", Utils.packageName);
		}
		
		try {
			String color = Integer.toHexString(mResources.getColor(rsc));
			if (color.startsWith("ff") && color.length()>=8) {
				color = color.substring(2);
			}
			return color;  
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
				
		return "000000";
	}
	
	public int getColor(String resource) {
		return Color.parseColor("#"+getStringColor(resource));
	}
	
	public ArrayList<String> getColors() {
		return mColors;
	}
	
	
	public void setColors() {
		
		mColors.clear();
		
		mColors.add("#"+getStringColor("color_1"));
		mColors.add("#"+getStringColor("color_2"));
		mColors.add("#"+getStringColor("color_3"));
		mColors.add("#"+getStringColor("color_4"));
		mColors.add("#"+getStringColor("color_5"));
		mColors.add("#"+getStringColor("color_6"));
		mColors.add("#"+getStringColor("color_7"));
		mColors.add("#"+getStringColor("color_8"));
		
		/*
    	if ( mTheme == 1 ) {
    		mColors.add("#e0c8ce"); // rojo
    		mColors.add("#9be4e5"); // azul
    		mColors.add("#cdf3be"); // verde
    		mColors.add("#e1b8e3"); // violeta
    		mColors.add("#f8da88"); // naranja
    		mColors.add("#e3c2a7"); // marrón
    		mColors.add("#e4e58f"); // amarillo
    		mColors.add("#b8c4e3"); // morao
    	} else {
    		mColors.add("#9a5f6e"); // rojo
    		mColors.add("#4f999a"); // azul
    		mColors.add("#608a4f"); // verde
    		mColors.add("#a466a6"); // violeta
    		mColors.add("#c69308"); // naranja
    		mColors.add("#ac8769"); // marrón
    		mColors.add("#737406"); // amarillo
    		mColors.add("#7080a9"); // morao
    	}*/
	}
	
}
