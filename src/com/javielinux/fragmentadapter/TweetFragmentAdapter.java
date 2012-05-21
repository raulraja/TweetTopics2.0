package com.javielinux.fragmentadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.TweetConversationFragment;
import com.javielinux.fragments.TweetLinksFragment;
import com.javielinux.fragments.TweetMapFragment;

public class TweetFragmentAdapter extends FragmentPagerAdapter {

    public static int TAB_LINKS = 0;
    public static int TAB_CONVERSATION = 1;
    public static int TAB_MAP = 2;

    private static final String[] texts = new String[] { "Links", "Conversation", "Map" };

    protected static final Integer[] CONTENT = new Integer[] { TAB_LINKS, TAB_CONVERSATION, TAB_MAP };

    public TweetFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        if (CONTENT[position]==TAB_LINKS) {
            fragment = new TweetLinksFragment();
        } else if (CONTENT[position]==TAB_CONVERSATION) {
            fragment = new TweetConversationFragment();
        } else if (CONTENT[position]==TAB_MAP) {
            fragment = new TweetMapFragment();
        }
        return fragment;
    }


    @Override
    public int getCount() {
        return CONTENT.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return texts[position % CONTENT.length].toUpperCase();
    }

}
