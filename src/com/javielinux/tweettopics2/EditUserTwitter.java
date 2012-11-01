package com.javielinux.tweettopics2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

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

        //Entity ent = new Entity("users", mCurrentId);

        ConnectionManager.getInstance().open(this);

        twitter = ConnectionManager.getInstance().getTwitter(mCurrentId);

        User user = null;
        try {
            user = twitter.showUser(twitter.getId());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        if (user == null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        } else {

            ThemeManager mThemeManager = new ThemeManager(this);
            mThemeManager.setTheme();

            setContentView(R.layout.edit_user_twitter);

            setTitle(R.string.edit_user);

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
