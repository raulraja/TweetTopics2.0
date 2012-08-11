package com.javielinux.tweettopics2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import com.javielinux.fragmentadapter.UserFragmentAdapter;
import com.javielinux.infos.InfoUsers;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.TweetActions;
import com.javielinux.utils.UserActions;
import com.javielinux.utils.Utils;
import com.viewpagerindicator.TabPageIndicator;

import java.io.File;
import java.util.ArrayList;

public class UserActivity extends BaseLayersActivity {

    public static final String KEY_EXTRAS_USER = "user";

    private ViewPager pager;
    private UserFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;

    private String username = null;
    private InfoUsers infoUser;

    private ImageView imgAvatar;
    private TextView txtUsername;
    private TextView txtFullName;
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
        if (extras != null) {
            if (extras.containsKey(KEY_EXTRAS_USER)) {
                username = extras.getString(KEY_EXTRAS_USER).replace("@", "");
            }
        }

        if (username == null) {
            Utils.showMessage(UserActivity.this, UserActivity.this.getString(R.string.no_server));
            finish();
        }

        infoUser = CacheData.getCacheUser(username);

        setContentView(R.layout.user_activity);

        viewLoading = (LinearLayout) findViewById(R.id.user_view_loading);
        viewInfo = (RelativeLayout) findViewById(R.id.user_view_info);

        imgAvatar = ((ImageView) findViewById(R.id.user_avatar));
        imgAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO abrir imagen en grande
            }

        });

        llRoot = (RelativeLayout) findViewById(R.id.user_ll);
        txtFullName = ((TextView) findViewById(R.id.user_fullname));
        txtUsername = ((TextView) findViewById(R.id.user_username));
        txtText = ((TextView) findViewById(R.id.user_text));

        (findViewById(R.id.user_btn_reply)).setOnClickListener(clickMention);
        (findViewById(R.id.user_btn_web)).setOnClickListener(clickWeb);
        (findViewById(R.id.user_btn_highlight)).setOnClickListener(clickHighlight);
        (findViewById(R.id.user_btn_more)).setOnClickListener(clickMore);

        pager = (ViewPager) findViewById(R.id.user_pager);

        indicator = (TabPageIndicator) findViewById(R.id.user_indicator);

        if (infoUser != null) {
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

        fragmentAdapter = new UserFragmentAdapter(this, getSupportFragmentManager(), infoUser);
        pager.setAdapter(fragmentAdapter);
        indicator.setViewPager(pager);

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

        txtUsername.setText("@" + name);
        txtFullName.setText(((fullname.equals("")) ? name : fullname));
        txtText.setText(infoUser.getBio());

    }

    View.OnClickListener clickMention = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.goToMention(UserActivity.this, infoUser.getName());
        }
    };

    View.OnClickListener clickWeb = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (infoUser.getUrl()!=null && !infoUser.getUrl().equals("")) {
                goToLink(infoUser.getUrl());
            } else {
                Utils.showMessage(UserActivity.this, getString(R.string.user_no_web));
            }
        }
    };

    View.OnClickListener clickHighlight = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            UserActions.goToColoringTweets(UserActivity.this, infoUser);
        }
    };

    View.OnClickListener clickMore = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            final ArrayList<String> arCode = new ArrayList<String>();
            ArrayList<String> ar = new ArrayList<String>();

            ar.add(getString(R.string.view_photo_profile));
            arCode.add("view_photo_profile");

            ar.add(getString(R.string.create_block));
            arCode.add("create_block");

            ar.add(getString(R.string.report_spam));
            arCode.add("report_spam");

            ar.add(getString(R.string.included_list));
            arCode.add("included_list");

            ar.add(getString(R.string.hide));
            arCode.add("hide");

            ar.add(getString(R.string.create_topic));
            arCode.add("create_topic");

            ar.add(getString(R.string.send_direct_message));
            arCode.add("send_direct");

            CharSequence[] c = new CharSequence[ar.size()];
            for (int i=0; i<ar.size(); i++) {
                c[i] = ar.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
            builder.setTitle(R.string.actions);
            builder.setItems(c, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserActions.execByCode(arCode.get(which), UserActivity.this, infoUser);
                }


            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }
    };

    private void refreshTheme() {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            llRoot.setPadding(29, 0, 0, 0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar : R.drawable.bg_sidebar_dark);
        } else {
            llRoot.setPadding(0, 29, 0, 0);
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

    public void goToLink(String url) {
        try {
            if (url.startsWith("www")) {
                url = "http://"+url;
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Utils.showMessage(this, getString(R.string.error_view_url) + " " + url);
        }
    }

}
