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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.Utils;


public class InfoColorsApp {
		
	public static int TYPE_SECTION = 0;
	public static int TYPE_COLOR = 1;
	
	private String color = "";
	private String pref = "";
	private String title = "";
	private String description = "";
	private int type = TYPE_COLOR;
	
	public InfoColorsApp(String title) {
		this.title = title;
		type = TYPE_SECTION;
	}
	
	public InfoColorsApp(Context cnt, String pref) {
		this.pref = pref;
		this.color = getColor(cnt);
		if (pref.startsWith("color_") && pref.length()==7) {
			String n = pref.replace("color_", "");
			title = cnt.getString(cnt.getResources().getIdentifier(Utils.packageName + ":string/title_color_x", null, null), n);
			description = cnt.getString(cnt.getResources().getIdentifier(Utils.packageName + ":string/desc_color_x", null, null), n);
		} else {
			title = cnt.getString(cnt.getResources().getIdentifier(Utils.packageName + ":string/title_"+pref, null, null));
			description = cnt.getString(cnt.getResources().getIdentifier(Utils.packageName + ":string/desc_"+pref, null, null));
		}
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public String getRGB() {
		return color;
	}

	public String getColor() {
		return "#"+color;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public String getPref() {
		return pref;
	}
	
	public void restartColor(Context cnt) {
		ThemeManager theme = new ThemeManager(cnt);
    	SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putString(pref, theme.getStringColorOriginal(pref));
    	editor.commit();
    }
	
    public void setColor(Context cnt, String color) {
    	SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putString(pref, color);
    	editor.commit();
    }
    
    public String getColor(Context cnt) {
    	ThemeManager theme = new ThemeManager(cnt);
    	SharedPreferences prefs = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(pref)) {
        	return prefs.getString(pref, theme.getStringColor(pref));
        } else {
            Editor editor = prefs.edit();
           	editor.putString(pref, theme.getStringColor(pref));
            editor.commit();
        	return theme.getStringColor(pref);
        }
    }

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
}
