package com.javielinux.fragmentadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.TweetConversationFragment;
import com.javielinux.fragments.TweetLinksFragment;
import com.javielinux.fragments.TweetMapFragment;
import com.javielinux.tweettopics2.Utils;
import infos.InfoTweet;

import java.util.ArrayList;

public class TweetFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_LINKS = 0;
    public static int TAB_CONVERSATION = 1;
    public static int TAB_MAP = 2;

    private InfoTweet infoTweet;

    private ArrayList<Integer> tweet_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> tweet_fragment_titles = new ArrayList<String>();

    public TweetFragmentAdapter(FragmentManager fragmentManager, InfoTweet infoTweet) {
        super(fragmentManager);
        this.infoTweet = infoTweet;
        loadColumns();
    }

    void loadColumns() {
        if (Utils.pullLinks(infoTweet.getText()).size()>0) {
            tweet_fragment_code.add(TAB_LINKS);
            tweet_fragment_titles.add("Links");
        }
        if (infoTweet.hasConversation()) {
            tweet_fragment_code.add(TAB_CONVERSATION);
            tweet_fragment_titles.add("Conversation");
        }
        if (infoTweet.getLatitude()!=0 && infoTweet.getLongitude()!=0) {
            tweet_fragment_code.add(TAB_MAP);
            tweet_fragment_titles.add("Map");
        }
    }


    @Override
    public Fragment getItem(int index) {
       if (tweet_fragment_code.get(index)==TAB_LINKS) {
            return new TweetLinksFragment();
       } else if (tweet_fragment_code.get(index)==TAB_CONVERSATION) {
           return new TweetConversationFragment();
       } else if (tweet_fragment_code.get(index)==TAB_MAP) {
           return new TweetMapFragment();
       }
        return new Fragment();
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
