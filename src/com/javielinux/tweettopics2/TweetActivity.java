package com.javielinux.tweettopics2;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class TweetActivity extends BaseActivity {

    private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        overridePendingTransition(R.anim.pull_in_to_right, R.anim.hold);

        setContentView(R.layout.tweet_activity);

        fragmentAdapter = new TweetFragmentAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TabPageIndicator)findViewById(R.id.tweet_indicator);
        indicator.setViewPager(pager);

        ((LinearLayout)findViewById(R.id.tweet_ll)).setBackgroundResource((themeManager.getTheme()==1)?R.drawable.bg_sidebar:R.drawable.bg_sidebar_dark);

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.hold, R.anim.push_out_from_right);
        super.onPause();
    }

}
