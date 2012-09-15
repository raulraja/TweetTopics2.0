package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.android.dataframework.Entity;
import com.javielinux.components.AlphaTextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.List;

public class RowSearchAdapter extends BaseAdapter {
	
	private Context mContext;
    private List<Entity> elements;
	 
    public RowSearchAdapter(Context mContext, List<Entity> elements)
    {
        this.mContext = mContext;
        this.elements = elements;
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
			v = View.inflate(mContext, R.layout.row_search, null);
		} else {
			v = convertView;
		}

		ImageView img = (ImageView)v.findViewById(R.id.img_search);
		
		try {
			Drawable d = Utils.getDrawable(v.getContext(), item.getString("icon_big"));
			if (d==null) {
				img.setImageResource(R.drawable.letter_az);	
			} else {
				img.setImageDrawable( d );
			}
		} catch (Exception e) {
			img.setImageResource(R.drawable.letter_az);
			e.printStackTrace();
		}
					
		ImageView tagNew = (ImageView)v.findViewById(R.id.tag_new);
		ImageView tagLang = (ImageView)v.findViewById(R.id.tag_lang);
		
		String name = item.getString("name");
				
		if (item.getString("lang").equals("")) {
			tagLang.setVisibility(View.GONE);
		} else {
			tagLang.setVisibility(View.VISIBLE);
			int i = v.getResources().getIdentifier(Utils.packageName+":drawable/tag_flag_"+item.getString("lang"), null, null);
			tagLang.setImageResource(i);
		}
		
		
		if (item.getInt("notifications")==1) {
						
			tagNew.setVisibility(View.VISIBLE);
			
			try {
				if (item.getLong("last_tweet_id")<item.getLong("last_tweet_id_notifications")) {							
					tagNew.setImageBitmap(ImageUtils.getBitmapNumber(mContext, item.getInt("new_tweets_count"), Color.GREEN, Utils.TYPE_CIRCLE));
				} else {
					tagNew.setImageResource(R.drawable.tag_notification);
				}
			} catch (Exception e) {
				tagNew.setImageResource(R.drawable.tag_notification);
			}
			
		} else {				
			tagNew.setVisibility(View.GONE);
		}
		
	
		AlphaTextView lTitle = (AlphaTextView)v.findViewById(R.id.title);
		lTitle.setText(name);
		
		if (item.getInt("is_temp")==1) {
			img.setAlpha(80);
			lTitle.onSetAlpha(80);
		} else {				
			img.setAlpha(255);
			lTitle.onSetAlpha(255);
		}
			
		return v;
	}

}
