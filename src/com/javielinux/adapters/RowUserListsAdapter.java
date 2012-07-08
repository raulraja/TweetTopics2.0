package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import infos.InfoTweet;
import layouts.AlphaTextView;
import layouts.TweetListViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RowUserListsAdapter extends ArrayAdapter<Entity> {

	private Context context;
    private ArrayList<Entity> elements;

    public static class ViewHolder {
        public ImageView avatarView;
        public AlphaTextView title;
    }

    public RowUserListsAdapter(Context context, ArrayList<Entity> elements)
    {
        super(context, R.layout.row_userlist, elements);

        this.context = context;
        this.elements = elements;
    }

    public static ViewHolder generateViewHolder(View view) {

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.avatarView = (ImageView)view.findViewById(R.id.img_userlist);
        viewHolder.title = (AlphaTextView)view.findViewById(R.id.title);

        return viewHolder;
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Entity getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Entity item = getItem(position) ;


        View view;

        if (convertView == null) {
            view = View.inflate(getContext(), R.layout.row_userlist, null);
        } else {
            view = convertView;
        }

        ViewHolder viewHolder = RowUserListsAdapter.generateViewHolder(view);

        try {
            Bitmap bmp = null;
            String urlAvatar = item.getString("url_avatar");

            File file = Utils.getFileForSaveURL(context, urlAvatar);

            if (!file.exists()) {
                bmp = Utils.saveAvatar(urlAvatar, file);
    		} else {
	    		bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
		    }

            viewHolder.avatarView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 64, 64, true));
        } catch (Exception e) {
        	e.printStackTrace();
            viewHolder.avatarView.setImageResource(R.drawable.avatar);
        }

        viewHolder.title.setText(item.getString("name"));

        viewHolder.avatarView.setAlpha(255);
        viewHolder.title.onSetAlpha(255);

		return view;
    }
}
