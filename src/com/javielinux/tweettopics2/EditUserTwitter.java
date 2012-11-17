package com.javielinux.tweettopics2;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.javielinux.preferences.Preferences;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;

public class EditUserTwitter extends BaseActivity implements Runnable {
    private long mCurrentId = -1;

    private TextView mUserName;
    private EditText mName;
    private EditText mLocation;
    private EditText mWeb;
    private EditText mBiography;

    private Button mBtSave;
    private Button mBtCancel;

    private ImageButton mAvatar;

    public static Twitter twitter;

    private ProgressDialog progressDialog;

    private LinearLayout mLayoutBackgroundApp;

    private ThemeManager themeManager;

    private RelativeLayout layoutActionBar;

    private TextView titlePage;

    private User user;

    public void refreshTheme() {
        boolean hasWallpaper = false;
        File f = new File(Preferences.IMAGE_WALLPAPER);
        if (f.exists()) {
            try {
                BitmapDrawable bmp = (BitmapDrawable) BitmapDrawable.createFromPath(Preferences.IMAGE_WALLPAPER);
                bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                mLayoutBackgroundApp.setBackgroundDrawable(bmp);
                hasWallpaper = true;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        if (!hasWallpaper) {
            mLayoutBackgroundApp.setBackgroundColor(Color.parseColor("#" + themeManager.getStringColor("color_background_new_status")));
        }

        themeManager.setColors();
        layoutActionBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        titlePage.setTextColor(themeManager.getColor("color_indicator_text"));
        titlePage.setTextSize(getResources().getDimension(R.dimen.text_size_title_page));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DataFramework.KEY_ID))
                mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
            }
        }

        if (mCurrentId < 0) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        themeManager = new ThemeManager(this);
        themeManager.setTheme();
        themeManager.setColors();

        setContentView(R.layout.edit_user_twitter);

        findViewById(R.id.buttons_foot).setBackgroundColor(themeManager.getColor("color_bottom_bar"));

        mLayoutBackgroundApp = (LinearLayout) findViewById(R.id.layout_background_app);
        layoutActionBar = (RelativeLayout) findViewById(R.id.edit_user_bar_action);
        titlePage = (TextView) this.findViewById(R.id.edit_user_bar_title);

        refreshTheme();

        ConnectionManager.getInstance().open(this);

        twitter = ConnectionManager.getInstance().getTwitter(mCurrentId);

        progressDialog = ProgressDialog.show(
                this,
                this.getResources().getString(R.string.loading),
                this.getResources().getString(R.string.loading)
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                user = null;
                try {
                    user = twitter.showUser(twitter.getId());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        loadUser();
                    }
                });
            }
        }).start();

    }

    private void loadUser() {

        if (user == null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        } else {

            mBtSave = (Button) findViewById(R.id.bt_save);

            mBtSave.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    save();
                }

            });

            mBtCancel = (Button) findViewById(R.id.bt_cancel);

            mBtCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }

            });

            mAvatar = (ImageButton) findViewById(R.id.bt_avatar);
            mAvatar.setImageBitmap(ImageUtils.getBitmapAvatar(mCurrentId, Utils.AVATAR_LARGE));

            mUserName = (TextView) findViewById(R.id.et_username);
            mUserName.setText(user.getScreenName());

            mName = (EditText) findViewById(R.id.et_name);
            mName.setText(user.getName());

            mLocation = (EditText) findViewById(R.id.et_location);
            mLocation.setText(user.getLocation());

            mWeb = (EditText) findViewById(R.id.et_web);
            if (user.getURL() != null) mWeb.setText(user.getURL().toString());

            mBiography = (EditText) findViewById(R.id.et_biography);
            mBiography.setText(user.getDescription());

        }

    }

    private void save() {
        progressDialog = ProgressDialog.show(
                this,
                this.getResources().getString(R.string.editing_title),
                this.getResources().getString(R.string.editing_description)
        );

        Thread thread = new Thread(this);
        thread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (msg.what == 0) {
                Utils.showMessage(EditUserTwitter.this, R.string.info_save);
                setResult(RESULT_OK);
                finish();
            } else {
                Utils.showMessage(EditUserTwitter.this, R.string.error_general);
            }
        }
    };

    public void run() {
        try {
            twitter.updateProfile(mName.getText().toString(), mWeb.getText().toString(),
                    mLocation.getText().toString(), mBiography.getText().toString());

            handler.sendEmptyMessage(0);
        } catch (TwitterException e) {
            handler.sendEmptyMessage(1);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }


}
