package com.javielinux.fragmentadapter;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.MyActivityFragment;
import com.javielinux.fragments.NoFoundFragment;
import com.javielinux.fragments.SearchFragment;
import com.javielinux.fragments.TweetTopicsFragment;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter  {

    private ArrayList<Entity> fragmentList = new ArrayList<Entity>();

    public TweetTopicsFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        fillColumnList();
    }

    private void fillColumnList() {

        try {

            //  creo MyActivity y la añado primero a la lista

            Entity myActivity = new Entity("columns");
            myActivity.setValue("type_id", TweetTopicsUtils.COLUMN_MY_ACTIVITY);
            fragmentList.add(myActivity);

            // incluyo las columnas de la base de datos

            fragmentList.addAll(DataFramework.getInstance().getEntityList("columns"));

            notifyDataSetChanged();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Entity getEntityItem(int position) {
        return fragmentList.get(position);
    }

    public Bitmap getIconItem(int position) {
        if (fragmentList.size() > 0) {
            int typeColumn = fragmentList.get(position).getInt("type_id");
            if (typeColumn == TweetTopicsUtils.COLUMN_TIMELINE || typeColumn == TweetTopicsUtils.COLUMN_MENTIONS
                    || typeColumn == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES || typeColumn == TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES) {
                return Utils.getBitmapAvatar(fragmentList.get(position).getEntity("user_id").getId(), Utils.AVATAR_MEDIUM);
            }
        }
        return null;
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
                return new MyActivityFragment();
            case TweetTopicsUtils.COLUMN_SEARCH:
                return new SearchFragment(fragmentList.get(index).getId());
            default:
                return new NoFoundFragment(fragmentList.get(index).getString("description"));
        }
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int type_column = fragmentList.get(position).getInt("type_id");
        switch (type_column) {
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity ent = new Entity("search", fragmentList.get(position).getLong("search_id"));
                return ent.getString("name");
            default:
                return fragmentList.get(position).getEntity("type_id").getString("title");
        }

    }
}
