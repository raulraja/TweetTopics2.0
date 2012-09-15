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