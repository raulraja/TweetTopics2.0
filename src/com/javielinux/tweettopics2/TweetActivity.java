package com.javielinux.tweettopics2;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class TweetActivity extends BaseActivity {

    private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tweet_activity);

        fragmentAdapter = new TweetFragmentAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TabPageIndicator)findViewById(R.id.tweet_indicator);
        indicator.setViewPager(pager);

    }

}
