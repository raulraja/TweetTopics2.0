package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.androidquery.AQuery;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.List;

public class RowColumnWidgetAdapter extends BaseAdapter {

	private Context context;
    private List<Entity> elements;
    private AQuery listAQuery;

    public RowColumnWidgetAdapter(Context context, List<Entity> elements) {
        this.context = context;
        this.elements = elements;

        listAQuery = new AQuery(context);
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

        Entity item = elements.get(index);

        View view_item = null;

        if (null == view) {
            view_item = View.inflate(context, R.layout.row_column_widget, null);
        } else {
            view_item = view;
        }

        AQuery aQuery = listAQuery.recycle(view_item);

        Bitmap avatar = getIconItem(item);

        if (avatar != null) {
            aQuery.id(R.id.column_image).image(avatar);
        } else {
            aQuery.id(R.id.column_image).image(R.drawable.avatar);
        }

        TextView textView = (TextView)view_item.findViewById(R.id.column_name);
        textView.setText(getTitleItem(item));

        return view_item;
    }

    private Bitmap getIconItem(Entity column) {

        int column_type = column.getInt("type_id");
        Bitmap bitmap = null;
        switch (column_type) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
            case TweetTopicsUtils.COLUMN_MENTIONS:
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
            case TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES:
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
            case TweetTopicsUtils.COLUMN_FOLLOWERS:
            case TweetTopicsUtils.COLUMN_FOLLOWINGS:
            case TweetTopicsUtils.COLUMN_FAVORITES:
                bitmap = ImageUtils.getBitmapAvatar(column.getEntity("user_id").getId(), Utils.AVATAR_LARGE);
                break;
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity search_entity = new Entity("search", column.getLong("search_id"));
                Drawable drawable = Utils.getDrawable(context, search_entity.getString("icon_big"));
                if (drawable == null) drawable = context.getResources().getDrawable(R.drawable.letter_az);
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, Utils.AVATAR_LARGE, Utils.AVATAR_LARGE, true);
                break;
            case TweetTopicsUtils.COLUMN_LIST_USER:
                bitmap = ImageUtils.getBitmapAvatar(column.getEntity("userlist_id").getEntity("user_id").getId(), Utils.AVATAR_LARGE);
                break;
        }

        return bitmap;
    }

    private String getTitleItem(Entity column) {
        int type_column = column.getInt("type_id");
        switch (type_column) {
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity ent = new Entity("search", column.getLong("search_id"));
                return ent.getString("name");
            case TweetTopicsUtils.COLUMN_LIST_USER:
                Entity list_user_entity = new Entity("user_lists", column.getLong("userlist_id"));
                return list_user_entity.getString("name");
            case TweetTopicsUtils.COLUMN_TRENDING_TOPIC:
                return column.getEntity("type_id").getString("title") + " " + column.getString("description");
            default:
                return column.getEntity("type_id").getString("title");
        }

    }
}
