package com.javielinux.fragmentadapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.android.dataframework.DataFramework;
import com.javielinux.fragments.TweetTopicsFragment;
import com.javielinux.tweettopics2.Utils;

import java.util.ArrayList;

public class TweetTopicsFragmentAdapter extends FragmentPagerAdapter  {

    public static final int MY_ACTIVITY = 1;
    public static final int TIMELINE = 2;
    public static final int MENTIONS = 3;
    public static final int DIRECT_MESSAGES = 4;
    public static final int FAVORITES = 5;
    public static final int LIST_USER = 6;
    public static final int YOUR_TWEETS_RETWEETED = 7;
    public static final int RETWEETS_BY_YOU = 8;
    public static final int RETWEETS_BY_OTHERS = 9;
    public static final int TRENDING_TOPIC = 10;
    public static final int SEARCH = 11;
    public static final int SAVED_TWEETS = 12;
    public static final int FOLLOWERS = 13;
    public static final int FOLLOWINGS = 14;
    public static final int TIMELINE_OF_USER = 15;

    private static final String[] tweet_fragment_titles = new String[] { "My activity", "Timeline", "Mentions", "Direct messages", "Favorites", "User lists", "Your tweets retweeted", "Retweets by you", "Retweets by others", "Trending Topics", "Search", "Saved tweets", "Followers", "Followings", "Timeline of user" };

    private Context context;
    private ArrayList<Long> tweet_fragment_list;

    public TweetTopicsFragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        this.context = context;

        FillColumnList();
    }

    private void FillColumnList() {
        tweet_fragment_list = new ArrayList<Long>();

        try {
            DataFramework.getInstance().open(context, Utils.packageName);
            Cursor columnsCursor = DataFramework.getInstance().getCursor("columns");

            if (columnsCursor.moveToFirst()) {
                do {
                    tweet_fragment_list.add(columnsCursor.getLong(columnsCursor.getColumnIndex("_id")));
                } while (columnsCursor.moveToNext());
            }

            columnsCursor.close();
            DataFramework.getInstance().close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Fragment getItem(int index) {
        try
        {
            return new TweetTopicsFragment(tweet_fragment_list.get(index));
            //return new TweetTopicsFragment();
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public int getCount() {
        return tweet_fragment_list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_titles[position % tweet_fragment_list.size()].toUpperCase();
    }
}
