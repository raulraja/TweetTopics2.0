/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.androidquery.AQuery;
import com.javielinux.components.AlphaTextView;
import com.javielinux.tweettopics2.R;
import twitter4j.UserList;

import java.util.ArrayList;

public class RowUserListsAdapter extends ArrayAdapter<UserList> {

	private Activity activity;
    private ArrayList<UserList> elements;
    private boolean existsMoreElements;
    private AQuery listAQuery;

    public static class ViewHolder {
        public ImageView avatarView;
        public AlphaTextView title;
    }

    public RowUserListsAdapter(Activity activity, ArrayList<UserList> elements)
    {
        super(activity, R.layout.row_userlist, elements);

        this.activity = activity;
        this.elements = elements;
        this.existsMoreElements = false;

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

    public boolean getExistsMoreElements() {
        return this.existsMoreElements;
    }

    public void setExistsMoreElements(boolean existsMoreElements) {
        this.existsMoreElements = existsMoreElements;
    }

    @Override
    public int getCount() {
        int count = elements.size();

        if (existsMoreElements) {
            while (count > 3 && count % 3 != 0) {
                count--;
            }
        }

        return count;
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

        UserList item = getItem(position);


        View view;

        if (convertView == null) {
            view = View.inflate(getContext(), R.layout.row_userlist, null);
        } else {
            view = convertView;
        }

        ViewHolder viewHolder = RowUserListsAdapter.generateViewHolder(view);

        AQuery aQuery = listAQuery.recycle(convertView);
        aQuery.id(viewHolder.avatarView).image(item.getUser().getProfileImageURL().toString(), true, true, 0, R.drawable.avatar, aQuery.getCachedImage(R.drawable.avatar), 0);


        viewHolder.title.setText(item.getName());

		return view;
    }
}
