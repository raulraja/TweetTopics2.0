package com.javielinux.fragmentadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.TweetTopicsFragment;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter  {

    private ArrayList<Entity> tweet_fragment_list;

    public TweetTopicsFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        FillColumnList();
    }

    private void FillColumnList() {

        try {
            tweet_fragment_list = DataFramework.getInstance().getEntityList("columns");

            notifyDataSetChanged();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Fragment getItem(int index) {
        Log.d(Utils.TAG, "Creando columna "+index+" : " +tweet_fragment_list.get(index).getString("description").toUpperCase());
        return new TweetTopicsFragment(tweet_fragment_list.get(index).getId());
    }

    @Override
    public int getCount() {
        return tweet_fragment_list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_list.get(position).getEntity("type_id").getString("title").toUpperCase();
    }
}
