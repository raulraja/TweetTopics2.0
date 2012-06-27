package com.javielinux.adapters;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import layouts.LinkRowViewItem;
import layouts.TweetListViewItem;

import java.util.List;

public class LinksAdapter extends BaseAdapter {

    private Context context;
    private List<String> links;

    private LoaderManager loaderManager;

    public static class ViewHolder {
        public ImageView image;
        public TextView title;
    }

    public LinksAdapter(Context context, LoaderManager loaderManager, List<String> links)
    {
        this.context = context;
        this.loaderManager = loaderManager;
        this.links = links;
    }

    public static ViewHolder generateViewHolder(View v) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.image = (ImageView)v.findViewById(R.id.row_links_icon);
        viewHolder.title = (TextView)v.findViewById(R.id.row_links_title);

        return viewHolder;
    }
    
	@Override
	public int getCount() {
		return links.size();
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

        LinkRowViewItem view;

        if (null == convertView) {
            view = (LinkRowViewItem) View.inflate(context, R.layout.row_links, null);
            view.setTag(generateViewHolder(view));
        } else {
            if(convertView instanceof TweetListViewItem) {
                view = (LinkRowViewItem) convertView;
            } else {
                view = (LinkRowViewItem) View.inflate(context, R.layout.row_links, null);
                view.setTag(generateViewHolder(view));
            }
        }

        view.setRow(link, context, loaderManager);


        return view;
	}

}
