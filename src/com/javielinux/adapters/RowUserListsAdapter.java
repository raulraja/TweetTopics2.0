package com.javielinux.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.android.dataframework.Entity;
import com.androidquery.AQuery;
import com.javielinux.components.AlphaTextView;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class RowUserListsAdapter extends ArrayAdapter<Entity> {

	private Activity activity;
    private ArrayList<Entity> elements;
    private AQuery listAQuery;

    public static class ViewHolder {
        public ImageView avatarView;
        public AlphaTextView title;
    }

    public RowUserListsAdapter(Activity activity, ArrayList<Entity> elements)
    {
        super(activity, R.layout.row_userlist, elements);

        this.activity = activity;
        this.elements = elements;

        listAQuery = new AQuery(activity);
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

        AQuery aQuery = listAQuery.recycle(convertView);

        aQuery.id(viewHolder.avatarView).image(item.getString("url_avatar"), true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);

        viewHolder.title.setText(item.getString("name"));

		return view;
    }
}
