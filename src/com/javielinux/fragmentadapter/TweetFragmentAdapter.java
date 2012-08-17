package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.TweetConversationFragment;
import com.javielinux.fragments.TweetLinksFragment;
import com.javielinux.fragments.TweetMapFragment;
import com.javielinux.fragments.TweetTranslateFragment;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.LinksUtils;

import java.util.ArrayList;

public class TweetFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_LINKS = 0;
    public static int TAB_CONVERSATION = 1;
    public static int TAB_MAP = 2;
    public static int TAB_TRANSLATE = 3;

    private InfoTweet infoTweet;

    private ArrayList<Integer> tweet_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> tweet_fragment_titles = new ArrayList<String>();
    private ArrayList<String> tweet_fragment_language = new ArrayList<String>();

    private Context context;

    public TweetFragmentAdapter(Context context, FragmentManager fragmentManager, InfoTweet infoTweet) {
        super(fragmentManager);
        this.infoTweet = infoTweet;

        this.context = context;

        loadColumns();
    }

    void loadColumns() {
        if (LinksUtils.pullLinks(infoTweet.getText()).size()>0) {
            tweet_fragment_code.add(TAB_LINKS);
            tweet_fragment_titles.add(context.getString(R.string.links));
            tweet_fragment_language.add("");
        }
        if (infoTweet.hasConversation()) {
            tweet_fragment_code.add(TAB_CONVERSATION);
            tweet_fragment_titles.add(context.getString(R.string.conversation));
            tweet_fragment_language.add("");
        }
        if (infoTweet.getLatitude()!=0 && infoTweet.getLongitude()!=0) {
            tweet_fragment_code.add(TAB_MAP);
            tweet_fragment_titles.add(context.getString(R.string.map));
            tweet_fragment_language.add("");
        }
    }

    public void addTranslateColumn(String language) {

        if (!tweet_fragment_language.contains(language)) {
            tweet_fragment_code.add(TAB_TRANSLATE);
            tweet_fragment_titles.add(context.getString(R.string.translate) + " " + language.toUpperCase());
            tweet_fragment_language.add(language);

            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int index) {
       if (tweet_fragment_code.get(index) == TAB_LINKS) {
            return new TweetLinksFragment(infoTweet);
       } else if (tweet_fragment_code.get(index) == TAB_CONVERSATION) {
           return new TweetConversationFragment(infoTweet);
       } else if (tweet_fragment_code.get(index) == TAB_MAP) {
           return new TweetMapFragment();
       } else if (tweet_fragment_code.get(index) == TAB_TRANSLATE) {
           return new TweetTranslateFragment(infoTweet, tweet_fragment_language.get(index));
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