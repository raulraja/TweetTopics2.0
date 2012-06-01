package com.javielinux.tweettopics2;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import com.android.dataframework.DataFramework;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class TweetTopicsActivity extends BaseActivity {

    private ViewPager pager;
    private TweetTopicsFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;
    private LinearLayout mLayoutBackgroundApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.setActivity(this);

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.tweettopics_activity);

        fragmentAdapter = new TweetTopicsFragmentAdapter(getApplicationContext(), getSupportLoaderManager(), getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TabPageIndicator)findViewById(R.id.tweet_indicator);
        indicator.setViewPager(pager);

        mLayoutBackgroundApp = (LinearLayout) findViewById(R.id.layout_background_app);

        refreshTheme();

    }

    public void refreshTheme() {

        mLayoutBackgroundApp.setBackgroundColor(Color.parseColor("#"+themeManager.getStringColor("color_background_new_status")));

        themeManager.setColors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

}
