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

package com.javielinux.tweettopics2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.components.DraggableGridView;
import com.javielinux.components.OnRearrangeListener;
import com.javielinux.utils.ColumnsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class SortColumnsActivity extends BaseActivity {

    private DraggableGridView draggableGridView;
    private ArrayList<Entity> listColumns;
    private ThemeManager themeManager;
    private boolean changedColumns = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        themeManager = new ThemeManager(this);
        themeManager.setDialogTheme();
        themeManager.setColors();

        overridePendingTransition(R.anim.pull_in_to_up, R.anim.hold);

        setContentView(R.layout.sort_columns_activity);

        draggableGridView = (DraggableGridView) findViewById(R.id.sort_columns_dgv_grid);
        draggableGridView.setOnRearrangeListener(new OnRearrangeListener() {
            public void onRearrange(int oldIndex, int newIndex) {
                reorganizeColumns(oldIndex, newIndex);
            }

            @Override
            public void onStartDrag(int x, int index) {

            }

            @Override
            public void onMoveDragged(int index) {

            }
        });

        listColumns = DataFramework.getInstance().getEntityList("columns", "", "position asc");

        refreshColumns();

    }

    public void refreshColumns() {
        int padding = (int)getResources().getDimension(R.dimen.default_padding);
        for (int i = 0; i < listColumns.size(); i++) {
            ImageView view = new ImageView(this);
            view.setPadding(padding, padding, padding, padding);
            view.setImageBitmap(ColumnsUtils.getButtonWithTitle(this, listColumns.get(i), false));
            draggableGridView.addView(view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    private void reorganizeColumns(final int startPosition, final int endPosition) {
        listColumns.add(endPosition, listColumns.remove(startPosition));
        int count = 1;
        for (Entity entity : listColumns) {
            entity.setValue("position", count);
            entity.save();
            count++;
        }
        changedColumns = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (changedColumns) {
            Intent i = new Intent(this, TweetTopicsActivity.class);
            i.setAction(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.push_out_from_up);
    }
}