package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.TweetTopicsFragment;
import com.javielinux.tweettopics2.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter  {

    private Context context;
    private LoaderManager loaderManager;
    private ArrayList<Entity> tweet_fragment_list;

    public TweetTopicsFragmentAdapter(Context context, LoaderManager loaderManager, FragmentManager fragmentManager) {
        super(fragmentManager);

        this.context = context;
        this.loaderManager = loaderManager;

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
        try
        {
            Log.d(Utils.TAG, "Cargando columna "+index+" : " +tweet_fragment_list.get(index).getString("description").toUpperCase());
            return new TweetTopicsFragment(context, loaderManager, tweet_fragment_list.get(index).getId());
        }
        catch(Exception exception)
        {
            return null;
        }
    }

    @Override
    public int getCount() {
        return tweet_fragment_list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_list.get(position).getString("description").toUpperCase();
    }
}
