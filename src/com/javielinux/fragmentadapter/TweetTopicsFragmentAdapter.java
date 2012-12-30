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

package com.javielinux.fragmentadapter;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.*;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ColumnsUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private MyActivityFragment myActivityFragment;
    private ArrayList<Entity> fragmentList = new ArrayList<Entity>();

    private int nColumns = 1;

    public TweetTopicsFragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        fillColumnList();
        nColumns = context.getResources().getInteger(R.integer.columns_tweets);
    }

    public void fillColumnList() {

        try {

            fragmentList.clear();
            //  creo MyActivity y la añado primero a la lista

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            // incluyo las columnas de la base de datos, sólo si son válidas
            for (Entity entity : DataFramework.getInstance().getEntityList("columns", "", "position asc")) {
                boolean addColumn = true;
                if (entity.getInt("type_id") == TweetTopicsUtils.COLUMN_SEARCH) {
                    try {
                        Entity ent = new Entity("search", entity.getLong("search_id"));
                    } catch (CursorIndexOutOfBoundsException e) {
                        addColumn = false;
                    }
                }
                if (addColumn) {
                    fragmentList.add(entity);
                } else {
                    entity.delete();
                }
            }

            notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public ArrayList<Entity> getFragmentList() {
        return fragmentList;
    }

    public int getFragmentColumnType(int position) {
        return fragmentList.get(position).getInt("type_id");
    }

    public Entity getFragmentColumnSearch(int position) {
        return fragmentList.get(position).getEntity("search_id");
    }

    public Entity getFragmentColumnUser(int position) {
        return fragmentList.get(position).getEntity("user_id");
    }

    public Bitmap getButtonBigActionBar(int position) {
        return ColumnsUtils.getButtonBigActionBar(context, fragmentList.get(position), true);
    }


    public Bitmap getIconItem(int position) {
        return ColumnsUtils.getIconItem(context, fragmentList.get(position));
    }

    public int getPositionColumnActive() {
        int count = 0;
        for (Entity column : fragmentList) {
            if (column.getInt("active") == 1) {
                return count;
            }
            count++;
        }
        return 0;
    }

    public String setColumnActive(int position) {
        String text = "";
        int count = 0;
        for (Entity column : fragmentList) {
            if (position == count) {
                column.setValue("active", 1);
                text = (String) getPageTitle(count);
            } else {
                column.setValue("active", 0);
            }
            if (column.getInt("type_id") != TweetTopicsUtils.COLUMN_MY_ACTIVITY) column.save();
            count++;
        }
        return text;
    }

    @Override
    public Fragment getItem(int index) {
        Log.d(Utils.TAG, "Creando columna " + index + " : " + fragmentList.get(index).getString("description").toUpperCase());

        int type_column = fragmentList.get(index).getInt("type_id");
        switch (type_column) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
            case TweetTopicsUtils.COLUMN_MENTIONS:
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                return new TweetTopicsFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_MY_ACTIVITY:
                myActivityFragment = new MyActivityFragment();
                return myActivityFragment;
            case TweetTopicsUtils.COLUMN_SEARCH:
                return new SearchFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_LIST_USER:
                return new ListUserFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_TRENDING_TOPIC:
                return new TrendingTopicsFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_FAVORITES:
                return new FavoritesFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
                return new RetweetFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_FOLLOWERS:
            case TweetTopicsUtils.COLUMN_FOLLOWINGS:
                return new UsersFragment(fragmentList.get(index).getId());
            case TweetTopicsUtils.COLUMN_SAVED_TWEETS:
                return new SavedTweetFragment(fragmentList.get(index).getId());
            default:
                return new NoFoundFragment(fragmentList.get(index).getString("description"));
        }
    }

    public long getUserOwnerColumn(int position) {
        return fragmentList.get(position).getLong("user_id");
    }

    @Override
    public int getItemPosition(Object item) {
        if (item instanceof BaseListFragment) {
            BaseListFragment fragment = (BaseListFragment) item;
            Entity column_entity = fragment.getColumnEntity();

            int position = fragmentList.indexOf(column_entity);
            if (position < 0) {
                Log.d("TweetTopics2.0", "Fragment doesn't exist");
                return POSITION_NONE;
            } else {
                Log.d("TweetTopics2.0", "Fragment exists: " + column_entity.getString("title") + " - " + position);
                return position;
            }
        } else {
            return 0;
        }
    }

    public MyActivityFragment getMyActivityFragment() {
        return myActivityFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ColumnsUtils.getTitleColumn(context, fragmentList.get(position));
    }

    public void clearColumnList() {

        try {
            fragmentList.clear();

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void refreshColumnList() {

        try {
            fragmentList.clear();

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            fragmentList.addAll(DataFramework.getInstance().getEntityList("columns", "", "position asc"));

            notifyDataSetChanged();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
