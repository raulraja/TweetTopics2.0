package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.database.EntitySearch;
import com.javielinux.fragments.SearchAdvancedFragment;
import com.javielinux.fragments.SearchGeneralFragment;
import com.javielinux.fragments.SearchGeoFragment;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class SearchFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_GENERAL = 0;
    public static int TAB_ADVANCED = 1;
    public static int TAB_GEOLOCATION = 2;

    private EntitySearch search_entity;

    private ArrayList<Integer> tweet_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> tweet_fragment_titles = new ArrayList<String>();

    private SearchGeneralFragment search_general_fragment;
    private SearchAdvancedFragment search_advanced_fragment;
    private SearchGeoFragment search_geo_fragment;

    private Context context;
    private String defaultSearch = "";

    public SearchFragmentAdapter(Context context, FragmentManager fragmentManager, EntitySearch search_entity, String defaultSearch) {
        super(fragmentManager);
        this.search_entity = search_entity;

        this.context = context;
        this.defaultSearch = defaultSearch;

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
            search_general_fragment = new SearchGeneralFragment(search_entity, defaultSearch);
            return search_general_fragment;
        } else if (tweet_fragment_code.get(index) == TAB_ADVANCED) {
            search_advanced_fragment = new SearchAdvancedFragment(search_entity);
            return search_advanced_fragment;
        } else if (tweet_fragment_code.get(index) == TAB_GEOLOCATION) {
            search_geo_fragment = new SearchGeoFragment(search_entity);
            return search_geo_fragment;
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

    public SearchGeneralFragment getSearchGeneralFragment() {
        return search_general_fragment;
    }

    public SearchAdvancedFragment getSearchAdvancedFragment() {
        return search_advanced_fragment;
    }

    public SearchGeoFragment getSearchGeoFragment() {
        return search_geo_fragment;
    }
}