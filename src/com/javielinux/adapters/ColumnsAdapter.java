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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ColumnsUtils;

import java.util.ArrayList;

public class ColumnsAdapter extends BaseAdapter {

    public static class CheckedColumn {
        public Entity entity;
        public boolean checked = false;
    }

    public interface CheckedColumnListener {
        void onChecked();
    }

    private CheckedColumnListener checkedColumnListener;
    private Context context;
    private ArrayList<CheckedColumn> columns;

    public ColumnsAdapter(Context context, ArrayList<CheckedColumn> columns, CheckedColumnListener checkedColumnListener) {
        this.columns = columns;
        this.context = context;
        this.checkedColumnListener = checkedColumnListener;
    }

    @Override
    public int getCount() {
        return columns.size();
    }

    @Override
    public CheckedColumn getItem(int position) {
        return columns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CheckedColumn item = getItem(position);
        View v = null;
        if (null == convertView) {
            v = View.inflate(context, R.layout.columns_row, null);
        } else {
            v = convertView;
        }

        StateListDrawable statesButtonBg = new StateListDrawable();
        statesButtonBg.addState(new int[]{android.R.attr.state_checked}, new BitmapDrawable(context.getResources(), ColumnsUtils.getButtonWithTitle(context, item.entity, false, Color.RED)));
        statesButtonBg.addState(new int[]{-android.R.attr.state_checked},  new BitmapDrawable(context.getResources(), ColumnsUtils.getButtonWithTitle(context, item.entity, false, Color.BLACK)));

        CheckBox checkBox = ((CheckBox)v.findViewById(R.id.column_row_check));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.checked = b;
                checkedColumnListener.onChecked();
            }
        });

        checkBox.setButtonDrawable(statesButtonBg);

        checkBox.setChecked(item.checked);

        return v;
    }

}