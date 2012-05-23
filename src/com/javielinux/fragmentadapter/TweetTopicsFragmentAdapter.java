package com.javielinux.fragmentadapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragments.TweetTopicsFragment;
import com.javielinux.tweettopics2.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter  {

    public static final int MY_ACTIVITY = 0;
    public static final int TIMELINE = 1;
    public static final int MENTIONS = 2;
    public static final int DIRECT_MESSAGES = 3;
    public static final int FAVORITES = 4;
    public static final int LIST_USER = 5;
    public static final int YOUR_TWEETS_RETWEETED = 6;
    public static final int RETWEETS_BY_YOU = 7;
    public static final int RETWEETS_BY_OTHERS = 8;
    public static final int TRENDING_TOPIC = 9;
    public static final int SEARCH = 10;
    public static final int SAVED_TWEETS = 11;
    public static final int FOLLOWERS = 12;
    public static final int FOLLOWINGS = 13;
    public static final int TIMELINE_OF_USER = 14;

    private static final String[] tweet_fragment_titles = new String[] { "My activity", "Timeline", "Mentions", "Direct messages", "Favorites", "User lists", "Your tweets retweeted", "Retweets by you", "Retweets by others", "Trending Topics", "Search", "Saved tweets", "Followers", "Followings", "Timeline of user" };

    private Context context;
    private ArrayList<Entity> tweet_fragment_list;
    private ArrayList<Fragment> fragment_list;

    public TweetTopicsFragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        this.context = context;

        fragment_list = new ArrayList<Fragment>();
        FillColumnList();
    }

    private void FillColumnList() {

        try {
            DataFramework.getInstance().open(context, Utils.packageName);
            tweet_fragment_list = DataFramework.getInstance().getEntityList("columns");
            DataFramework.getInstance().close();

            for(int i=0; i < tweet_fragment_list.size(); i++) {
                fragment_list.add(new TweetTopicsFragment(tweet_fragment_list.get(i).getId()));
            }

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
            Log.d("TweetTopics 2.0", "Cargando column");
            return fragment_list.get(index);
        }
        catch(Exception exception)
        {
            Log.d("TweetTopics 2.0", exception.getMessage());
            return null;
        }
    }

    @Override
    public int getCount() {
        return fragment_list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_titles[tweet_fragment_list.get(position).getInt("type_id")].toUpperCase();
    }
}
