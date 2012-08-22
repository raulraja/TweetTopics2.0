package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.database.EntitySearch;
import com.javielinux.fragments.*;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class SearchFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_GENERAL = 0;
    public static int TAB_ADVANCED = 1;
    public static int TAB_GEOLOCATION = 2;

    private EntitySearch search_entity;

    private ArrayList<Integer> tweet_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> tweet_fragment_titles = new ArrayList<String>();

    private Context context;

    public SearchFragmentAdapter(Context context, FragmentManager fragmentManager, EntitySearch search_entity) {
        super(fragmentManager);
        this.search_entity = search_entity;

        this.context = context;

        loadColumns();
    }

    void loadColumns() {
        tweet_fragment_code.add(TAB_GENERAL);
        tweet_fragment_titles.add(context.getString(R.string.tab_general));

        tweet_fragment_code.add(TAB_ADVANCED);
        tweet_fragment_titles.add(context.getString(R.string.tab_avanced));

        tweet_fragment_code.add(TAB_GEOLOCATION);
        tweet_fragment_titles.add(context.getString(R.string.tab_geo));
    }

    @Override
    public Fragment getItem(int index) {

        if (tweet_fragment_code.get(index) == TAB_GENERAL) {
            return new SearchGeneralFragment(search_entity);
        } else if (tweet_fragment_code.get(index) == TAB_ADVANCED) {
            return new SearchAdvancedFragment(search_entity);
        } else if (tweet_fragment_code.get(index) == TAB_GEOLOCATION) {
            return new SearchGeoFragment(search_entity);
        }
        return new Fragment();
    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return tweet_fragment_code.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_titles.get(position % tweet_fragment_code.size()).toUpperCase();
    }
}