package com.javielinux.fragmentadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.TweetConversationFragment;
import com.javielinux.fragments.TweetLinksFragment;
import com.javielinux.fragments.TweetMapFragment;

public class TweetFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_LINKS = 0;
    public static int TAB_CONVERSATION = 1;
    public static int TAB_MAP = 2;

    private static final int[] tweet_fragment_code = new int[] { TAB_LINKS, TAB_CONVERSATION, TAB_MAP };

    private static final String[] tweet_fragment_titles = new String[] { "Links", "Conversation", "Map" };

    public TweetFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int index) {
       if (tweet_fragment_code[index]==TAB_LINKS) {
            return new TweetLinksFragment();
       } else if (tweet_fragment_code[index]==TAB_CONVERSATION) {
           return new TweetConversationFragment();
       } else if (tweet_fragment_code[index]==TAB_MAP) {
           return new TweetMapFragment();
       }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return tweet_fragment_code.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_titles[position % tweet_fragment_code.length].toUpperCase();
    }
}
