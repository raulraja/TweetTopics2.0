package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.androidquery.AQuery;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.infos.InfoLink;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.*;

import java.util.List;

public class RowLinkWidgetAdapter extends BaseAdapter {

	private Context context;
    private List<String> elements;
    private AQuery listAQuery;

    public static class ViewHolder {
        public ImageView image;
        public TextView title;
    }

    public RowLinkWidgetAdapter(Context context, List<String> elements) {
        this.context = context;
        this.elements = elements;

        listAQuery = new AQuery(context);
    }

    public static ViewHolder generateViewHolder(View view) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.image = (ImageView)view.findViewById(R.id.link_image);
        viewHolder.title = (TextView)view.findViewById(R.id.link_title);

        return viewHolder;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int index) {
        return elements.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {

        String item = elements.get(index);

        if (null == view) {
            view = View.inflate(context, R.layout.row_link_widget, null);
            view.setTag(generateViewHolder(view));
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        AQuery aQuery = listAQuery.recycle(view);
        int typeResource = getTypeResource(item);
        boolean hasImage = false;

        if (!item.startsWith("#") && !item.startsWith("@")) {

            if (CacheData.existCacheInfoLink(item)) {
                InfoLink infoLink = CacheData.getCacheInfoLink(item);
                String thumb = infoLink.getLinkImageThumb();

                if (thumb.equals("")) {
                    aQuery.id(viewHolder.image).image(typeResource);
                } else {
                    Bitmap image = aQuery.getCachedImage(thumb);

                    if (image!=null) {
                        aQuery.id(viewHolder.image).image(image);
                    } else {
                        aQuery.id(viewHolder.image).image(thumb, true, true, 0, typeResource, aQuery.getCachedImage(typeResource), 0);
                    }
                }
                hasImage = true;
                viewHolder.title.setText(writeTitle(infoLink.getLink()));
            }

            if (!hasImage) {
                viewHolder.title.setText(writeTitle(item));
                if (LinksUtils.isLinkImage(item)) {
                    if (LinksUtils.isLinkVideo(item)) {
                        viewHolder.image.setImageResource(R.drawable.icon_tweet_video);
                    } else {
                        viewHolder.image.setImageResource(R.drawable.icon_tweet_image);
                    }
                } else {
                    viewHolder.image.setImageResource(R.drawable.icon_tweet_link);
                }
            }
        }

        return view;
    }

    private int getTypeResource(String link) {
        int res = R.drawable.icon_tweet_link;
        if (LinksUtils.isLinkImage(link)) {
            if (LinksUtils.isLinkVideo(link)) {
                res = R.drawable.icon_tweet_video;
            } else {
                res = R.drawable.icon_tweet_image;
            }
        }
        return res;
    }

    private String writeTitle(String title) {
        if (title.startsWith("http://")) {
            title = title.substring(7);
        }
        if (title.startsWith("www.")) {
            title = title.substring(4);
        }
        if (title.length()>14) {
            title = title.substring(0,12)+"...";
        }
        return title;
    }
}
