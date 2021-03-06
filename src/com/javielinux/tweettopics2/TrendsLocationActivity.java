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
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RowTrendsLocationAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.TrendsLocationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsLocationResponse;
import com.javielinux.utils.ColumnsUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import twitter4j.Location;
import twitter4j.ResponseList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TrendsLocationActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    private LinearLayout viewTrendsParent;
    private LinearLayout viewNoTrendsLocation;
    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private ListView listView;

    private ThemeManager themeManager;
    
    private ArrayList<Location> trendslocation_list;
    private RowTrendsLocationAdapter trendslocation_adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.trendslocation_activity);

        viewTrendsParent = (LinearLayout) this.findViewById(R.id.trends_location_parent);
        viewNoTrendsLocation = (LinearLayout) this.findViewById(R.id.trends_location_view_no_lists);
        viewLoading = (LinearLayout) this.findViewById(R.id.trends_location_view_loading);
        viewNoInternet = (LinearLayout) this.findViewById(R.id.trends_location_view_no_internet);
        listView = (ListView) this.findViewById(R.id.trends_location_list);

        BitmapDrawable bmp = (BitmapDrawable) this.getResources().getDrawable(themeManager.getResource("search_tile"));
        if (bmp != null) {
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            viewTrendsParent.setBackgroundDrawable(bmp);
        }

        trendslocation_list = new ArrayList<Location>();
        trendslocation_adapter = new RowTrendsLocationAdapter(this, trendslocation_list);
        listView.setAdapter(trendslocation_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Location trendslocation = trendslocation_adapter.getItem(pos);

                createTrendsLocationColumn(trendslocation);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                return true;
            }
        });

        showLoading();
        loadTrendsLocation();
    }

    private void createTrendsLocationColumn(Location trendslocation) {

        ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "location_id=" + trendslocation.getWoeid());

        int position = 0;

        if (created_column_list.size() == 0) {
            position = ColumnsUtils.nextPositionColumn();

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_TRENDING_TOPIC);
            Entity user_list = new Entity("columns");
            user_list.setValue("description", trendslocation.getName());
            user_list.setValue("type_id", type);
            user_list.setValue("position", position);
            user_list.setValue("location_id", trendslocation.getWoeid());
            user_list.save();
            Toast.makeText(this, getString(R.string.column_created, trendslocation.getName()), Toast.LENGTH_LONG).show();
        } else {
            position = created_column_list.get(0).getInt("position");
        }

        Intent intent = getIntent();
        intent.putExtra("position", position);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void showNoTrendsLocation() {
        viewNoTrendsLocation.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public void showLoading() {
        viewNoTrendsLocation.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewNoTrendsLocation.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    public void showTrendsLocationLists() {
        viewNoTrendsLocation.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void loadTrendsLocation() {

        TrendsLocationRequest trendsLocationRequest = new TrendsLocationRequest();
        APITweetTopics.execute(this, getSupportLoaderManager(), this, trendsLocationRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    @Override
    public void onResults(BaseResponse result) {

        TrendsLocationResponse trendsLocationResponse = (TrendsLocationResponse)result;
        ResponseList<Location> trendslocation_data = trendsLocationResponse.getLocationList();

        try {
            Collections.sort(trendslocation_data, new Comparator<Location>() {

                public int compare(Location location1, Location location2) {
                    return location1.getName().compareToIgnoreCase(location2.getName());
                }

            });

            for (int i = 0; i < trendslocation_data.size(); i++)
                trendslocation_list.add(trendslocation_data.get(i));

        } catch (Exception e) {
            e.printStackTrace();
        }

        trendslocation_adapter.notifyDataSetChanged();

        if (trendslocation_adapter.getCount() == 0) {
            showNoTrendsLocation();
        } else {
            showTrendsLocationLists();
        }
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
    }

}
