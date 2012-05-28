package com.javielinux.tweettopics2;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.APIDelegate;
import api.APITweetTopics;
import api.request.LoadImageWidgetRequest;
import api.response.ErrorResponse;
import api.response.LoadImageWidgetResponse;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;
import infos.InfoTweet;

import java.io.File;

public class TweetActivity extends BaseActivity {

    public static final String KEY_EXTRAS_TWEET = "tweet";

    private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;
    private InfoTweet infoTweet;

    private ImageView imgAvatar;
    private TextView txtUsername;
    private TextView txtDate;
    private TextView txtText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        overridePendingTransition(R.anim.pull_in_to_right, R.anim.hold);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_EXTRAS_TWEET)) {
            infoTweet = (InfoTweet) extras.getParcelable(KEY_EXTRAS_TWEET);
        }

        if (infoTweet==null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        setContentView(R.layout.tweet_activity);

        imgAvatar = ((ImageView)findViewById(R.id.tweet_avatar));
        imgAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO open user activity
            }

        });

        String urlAvatar = infoTweet.getUrlAvatar();
        String name = infoTweet.getUsername();
        String fullname = infoTweet.getFullname();
        if (infoTweet.isRetweet()) {
            name = infoTweet.getUsernameRetweet();
            urlAvatar = infoTweet.getUrlAvatarRetweet();
            fullname = infoTweet.getFullnameRetweet();
        }

        File file = Utils.getFileForSaveURL(this, urlAvatar);
        if (file.exists()) {
            imgAvatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            APITweetTopics.execute(this, getLoaderManager(), new APIDelegate<LoadImageWidgetResponse>() {
                @Override
                public void onResults(LoadImageWidgetResponse result) {
                    try {
                        imgAvatar.setImageBitmap(result.getBitmap());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ErrorResponse error) {
                }
            }, new LoadImageWidgetRequest(urlAvatar));

        }

        txtUsername = ((TextView)findViewById(R.id.tweet_username));
        txtUsername.setText( name + ((fullname.equals(""))?"":" (" + fullname + ")") );

        txtDate = ((TextView)findViewById(R.id.tweet_date));
        txtDate.setText(Utils.timeFromTweetExtended(this, infoTweet.getDate()));

        txtText = ((TextView)findViewById(R.id.tweet_text));
        String html = infoTweet.getTextHTMLFinal();
        if (html.equals("")) html = Utils.toHTML(this, infoTweet.getText());
        txtText.setText(Html.fromHtml(html));

        fragmentAdapter = new TweetFragmentAdapter(getSupportFragmentManager(), infoTweet);

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
