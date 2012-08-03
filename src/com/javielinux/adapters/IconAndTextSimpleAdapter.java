/**
 * 
 */
package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class IconAndTextSimpleAdapter extends ArrayAdapter<IconAndTextSimpleAdapter.IconAndText> {

    public static class IconAndText {
        public int resource = 0;
        public Bitmap bitmap;
        public String text;
        public Object extra;
    }

	private Context context;

	public IconAndTextSimpleAdapter(Context context, ArrayList<IconAndText> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        IconAndText item = getItem(position);
		View v = null;
		if (null == convertView) {
			v = View.inflate(context, R.layout.row_users_twitter, null);
		} else {
			v = convertView;
		}

        ImageView icon = (ImageView)v.findViewById(R.id.icon);
        if (item.bitmap!=null) {
            icon.setImageBitmap(item.bitmap);
        } else {
            icon.setImageResource(item.resource);
        }
		
		TextView name = (TextView)v.findViewById(R.id.name);
		
		name.setText(item.text);
		
		return v;
	}


}