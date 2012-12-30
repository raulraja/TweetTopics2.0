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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.List;

public class RowUsersNewStatusAdapter extends BaseAdapter {
	
    private List<Entity> elements;
    private Context mContext = null;
	 
    public RowUsersNewStatusAdapter(Context cnt, List<Entity> elements)
    {
        this.elements = elements;
        mContext = cnt;
    }
	
	public int getCount() {
		return elements.size();
	}

	public Entity getItem(int position) {
		return (Entity)elements.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Entity item = elements.get(position);		
		
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_icon, null);
		} else {
			v = convertView;
		}
		
		ImageView img = (ImageView)v.findViewById(R.id.img_icon);
		img.setImageBitmap(ImageUtils.getBitmapAvatar(item.getId(), Utils.AVATAR_LARGE));

		return v;
		
	}

}
