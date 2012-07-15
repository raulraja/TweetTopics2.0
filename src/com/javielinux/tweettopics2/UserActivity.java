package com.javielinux.tweettopics2;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadImageWidgetRequest;
import com.javielinux.api.request.LoadUserRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadImageWidgetResponse;
import com.javielinux.api.response.LoadUserResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.Utils;

import java.io.File;

public class UserActivity extends BaseLayersActivity {

    public static final String KEY_EXTRAS_USER = "user";

    /*private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;    */
    private ThemeManager themeManager;

    private String username = null;
    private InfoUsers infoUser;

    private ImageView imgAvatar;
    private TextView txtUsername;
    private TextView txtText;

    private RelativeLayout llRoot;

    private LinearLayout viewLoading;
    private RelativeLayout viewInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey(KEY_EXTRAS_USER)) {
                username = extras.getString(KEY_EXTRAS_USER);
            }
        }

        if (username==null) {
            Utils.showMessage(UserActivity.this, UserActivity.this.getString(R.string.no_server));
            finish();
        }

        infoUser = CacheData.getCacheUser(username);

        setContentView(R.layout.user_activity);

        viewLoading = (LinearLayout) findViewById(R.id.user_view_loading);
        viewInfo = (RelativeLayout) findViewById(R.id.user_view_info);

        imgAvatar = ((ImageView)findViewById(R.id.user_avatar));
        imgAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO abrir imagen en grande
            }

        });

        llRoot = (RelativeLayout)findViewById(R.id.user_ll);
        txtUsername = ((TextView)findViewById(R.id.user_username));
        txtText = ((TextView)findViewById(R.id.user_text));

        /*
       fragmentAdapter = new TweetFragmentAdapter(this, getSupportFragmentManager(), infoTweet);

       pager = (ViewPager)findViewById(R.id.tweet_pager);
       pager.setAdapter(fragmentAdapter);

       indicator = (TabPageIndicator)findViewById(R.id.tweet_indicator);
       indicator.setViewPager(pager); */

        if (infoUser!=null) {
            populateFields();
        } else {
            showLoading();

            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<LoadUserResponse>() {
                @Override
                public void onResults(LoadUserResponse result) {
                    infoUser = result.getInfoUsers();
                    hideLoading();
                    populateFields();
                }

                @Override
                public void onError(ErrorResponse error) {
                    Utils.showMessage(UserActivity.this, UserActivity.this.getString(R.string.no_server));
                    finish();
                }
            }, new LoadUserRequest(username));
        }


        refreshTheme();

    }

    private void populateFields() {
        String urlAvatar = infoUser.getUrlAvatar();
        String name = infoUser.getName();
        String fullname = infoUser.getFullname();

        File file = Utils.getFileForSaveURL(this, urlAvatar);
        if (file.exists()) {
            imgAvatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<LoadImageWidgetResponse>() {
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

        txtUsername.setText( name + ((fullname.equals(""))?"":" (" + fullname + ")") );

        txtText.setText(infoUser.getBio());
    }

    private void refreshTheme() {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            llRoot.setPadding(29,0,0,0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar : R.drawable.bg_sidebar_dark);
        } else {
            llRoot.setPadding(0,29,0,0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar_left : R.drawable.bg_sidebar_left_dark);
        }
    }

    private void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        viewInfo.setVisibility(View.GONE);
    }

    private void hideLoading() {
        viewLoading.setVisibility(View.GONE);
        viewInfo.setVisibility(View.VISIBLE);
    }

}
