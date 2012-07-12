package com.javielinux.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.Location;
import twitter4j.ResponseList;

import java.util.ArrayList;
import java.util.List;

public class RowTrendsLocationAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Location> elements;

    public RowTrendsLocationAdapter(Context mContext, ArrayList<Location> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
    }
    
	@Override
	public int getCount() {
		return elements.size();
	}
	

	@Override
	public Location getItem(int position) {
        return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Location item = elements.get(position);

		View view = null;
		
		if (null == convertView) {
            view = View.inflate(mContext, R.layout.row_trends_location, null);
		} else {
            view = convertView;
		}

        TextView location_name = (TextView)view.findViewById(R.id.location_name);
        location_name.setText(item.getName());

        return view;
	}

}
