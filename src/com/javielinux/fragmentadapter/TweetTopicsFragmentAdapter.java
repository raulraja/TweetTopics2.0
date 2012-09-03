package com.javielinux.fragmentadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.*;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private MyActivityFragment myActivityFragment;
    private ArrayList<Entity> fragmentList = new ArrayList<Entity>();

    public TweetTopicsFragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        fillColumnList();
    }

    private void fillColumnList() {

        try {

            //  creo MyActivity y la añado primero a la lista

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            // incluyo las columnas de la base de datos

            fragmentList.addAll(DataFramework.getInstance().getEntityList("columns", "", "position asc"));

            notifyDataSetChanged();
        }
        catch (Exception exception) {
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

    public Bitmap getIconItem(int position) {
        if (fragmentList.size() > 0) {
            int typeColumn = fragmentList.get(position).getInt("type_id");
            if (typeColumn == TweetTopicsUtils.COLUMN_TIMELINE || typeColumn == TweetTopicsUtils.COLUMN_MENTIONS
                    || typeColumn == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES || typeColumn == TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES
                    || typeColumn == TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS || typeColumn == TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU
                    || typeColumn == TweetTopicsUtils.COLUMN_FOLLOWERS || typeColumn == TweetTopicsUtils.COLUMN_FOLLOWINGS
                    || typeColumn == TweetTopicsUtils.COLUMN_FAVORITES) {
                return ImageUtils.getBitmapAvatar(fragmentList.get(position).getEntity("user_id").getId(), Utils.AVATAR_LARGE);
            }
            if (typeColumn == TweetTopicsUtils.COLUMN_SEARCH) {
                Entity ent = new Entity("search", fragmentList.get(position).getLong("search_id"));
                Drawable d = Utils.getDrawable(context, ent.getString("icon_big"));
                if (d==null) {
                    d = context.getResources().getDrawable(R.drawable.letter_az);
                }
                return ((BitmapDrawable)d).getBitmap();
            }

        }
        return null;
    }

    public int getPositionColumnActive() {
        int count = 0;
        for (Entity column : fragmentList) {
            if (column.getInt("active")==1) {
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
            default:
                return new NoFoundFragment(fragmentList.get(index).getString("description"));
        }
    }

    public long getUserOwnerColumn(int position) {
        return fragmentList.get(position).getLong("user_id");
    }

    @Override
    public int getItemPosition(Object item) {
        BaseListFragment fragment = (BaseListFragment)item;
        Entity column_entity = fragment.getColumnEntity();

        int position = fragmentList.indexOf(column_entity);
        if (position < 0) {
            Log.d("TweetTopics2.0","Fragment doesn't exist");
            return POSITION_NONE;
        } else {
            Log.d("TweetTopics2.0","Fragment exists: " + column_entity.getString("title") + " - " + position);
            return position;
        }
    }

    public MyActivityFragment getMyActivityFragment() {
        return myActivityFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int type_column = fragmentList.get(position).getInt("type_id");
        switch (type_column) {
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity ent = new Entity("search", fragmentList.get(position).getLong("search_id"));
                return ent.getString("name");
            case TweetTopicsUtils.COLUMN_LIST_USER:
                Entity list_user_entity = new Entity("user_lists", fragmentList.get(position).getLong("userlist_id"));
                return list_user_entity.getString("name");
            case TweetTopicsUtils.COLUMN_TRENDING_TOPIC:
                return fragmentList.get(position).getEntity("type_id").getString("title") + " " + fragmentList.get(position).getString("description");
            default:
                return fragmentList.get(position).getEntity("type_id").getString("title");
        }

    }

    public void clearColumnList() {

        try {
            fragmentList.clear();

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            notifyDataSetChanged();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void refreshColumnList() {

        try {
            fragmentList.clear();

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            fragmentList.addAll(DataFramework.getInstance().getEntityList("columns","","position asc"));

            notifyDataSetChanged();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
