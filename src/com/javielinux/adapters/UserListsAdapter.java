package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.android.dataframework.Entity;
import com.javielinux.components.AlphaTextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;
import twitter4j.UserList;

import java.io.File;
import java.util.ArrayList;

public class UserListsAdapter extends ArrayAdapter<UserList> {

    private Context context;
    private ArrayList<UserList> elements;

    public static class ViewHolder {
        public ImageView avatarView;
        public AlphaTextView title;
    }

    public UserListsAdapter(Context context, ArrayList<UserList> elements)
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
    public UserList getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserList item = getItem(position) ;


        View view;

        if (convertView == null) {
            view = View.inflate(getContext(), R.layout.row_user_list_fragment, null);
        } else {
            view = convertView;
        }

        ViewHolder viewHolder = UserListsAdapter.generateViewHolder(view);

        try {
            Bitmap bmp = null;
            String urlAvatar = item.getUser().getProfileImageURL().toString();

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

        viewHolder.title.setText(item.getName());

        viewHolder.avatarView.setAlpha(255);
        viewHolder.title.onSetAlpha(255);

        return view;
    }
}