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
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import twitter4j.Trend;

import java.util.ArrayList;

public class TrendingTopicsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Trend> elements;

    public TrendingTopicsAdapter(Context mContext, ArrayList<Trend> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
    }
    
	@Override
	public int getCount() {
		return elements.size();
	}
	

	@Override
	public Trend getItem(int position) {
        return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Trend item = elements.get(position);

		View view = null;
		
		if (null == convertView) {
            view = View.inflate(mContext, R.layout.row_trending_topics, null);
		} else {
            view = convertView;
		}

        view.setBackgroundDrawable(ImageUtils.createStateListDrawable(mContext, new ThemeManager(mContext).getColor("list_background_row_color")));

        TextView location_name = (TextView)view.findViewById(R.id.trend_name);
        location_name.setText(item.getName());

        return view;
	}

}
