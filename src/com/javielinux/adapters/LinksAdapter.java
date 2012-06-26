package com.javielinux.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import infos.CacheData;
import infos.InfoLink;

import java.util.List;

public class LinksAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> links;

    public LinksAdapter(Context mContext, List<String> links)
    {
        this.mContext = mContext;
        this.links = links;
    }
    
	@Override
	public int getCount() {
		return links.size();
	}
	
	public int getPositionById(long id) {
        for (int i=0; i<getCount(); i++) {
        	if ( ((Entity)getItem(i)).getId() == id ) {
        		return i;
        	}
        }
        return -1;
	}
	

	@Override
	public Object getItem(int position) {
        return links.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        String link = links.get(position);
		
		View v = null;
		
		if (null == convertView) {
			v = View.inflate(mContext, R.layout.row_links, null);
		} else {
			v = convertView;
		}

        ImageView img = (ImageView)v.findViewById(R.id.row_links_icon);
        TextView title = (TextView)v.findViewById(R.id.row_links_title);

        if (CacheData.getCacheImages().containsKey(link)) {
            InfoLink item = CacheData.getCacheImages().get(link);

            try {
                img.setImageBitmap(item.getBitmapThumb());
            } catch (Exception e) {
                e.printStackTrace();
                img.setImageResource(R.drawable.avatar);
            }

            title.setText(item.getTitle());
        } else {
            img.setImageResource(R.drawable.avatar);
            title.setText(link);
        }

        return v;
	}

}
