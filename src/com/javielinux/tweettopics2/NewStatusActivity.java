package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.TweetListDraftAdapter;
import com.javielinux.adapters.TweetLongerAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.CheckFriendlyUserRequest;
import com.javielinux.api.request.SearchContentInDBRequest;
import com.javielinux.api.response.CheckFriendlyUserResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchContentInDBResponse;
import com.javielinux.components.AutoCompleteHashTagListItem;
import com.javielinux.components.AutoCompleteListItem;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.dialogs.SelectImageDialogFragment;
import com.javielinux.infos.InfoUsers;
import com.javielinux.preferences.NewEditTweetProgrammed;
import com.javielinux.preferences.Preferences;
import com.javielinux.preferences.TweetDraft;
import com.javielinux.tweetprogrammed.OnAlarmReceiverTweetProgrammed;
import com.javielinux.updatestatus.ServiceUpdateStatus;
import com.javielinux.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class NewStatusActivity extends BaseActivity {

    private static final int MAX_RESULTS = 5;

    private RelativeLayout layoutActionBar;
    private LinearLayout mButtonsFoot;
    private LinearLayout mAutoCompleteDataFoot;
    private HorizontalScrollView mAutoCompleteFoot;

    private long mIdDeleteDraft = 0;

    private List<InfoUsers> mResultInfoUsers = new ArrayList<InfoUsers>();
    private List<String> mResultInfoHashTags = new ArrayList<String>();

    public static int CHARS_YFROG = 8;
    public static int CHARS_TWITPIC = 6;
    public static int CHARS_LOCKERZ = 9;

    public static int MODE_TL_NONE = -1;
    public static int MODE_TL_TWITLONGER = 0;
    public static int MODE_TL_N_TWEETS = 1;

    public static String URL_BASE_YFROG = "http://yfrog.com/";
    public static String URL_BASE_TWITPIC = "http://twitpic.com/";
    public static String URL_BASE_LOCKERZ = "http://lockerz.com/p/";


    public static final int ACTIVITY_USER = 0;

    private int mModeTweetLonger = MODE_TL_NONE;

    private static final int TAKEPHOTO_ID = Menu.FIRST;
    private static final int DEFAULTTEXT_ID = Menu.FIRST + 1;
    private static final int NEW_DRAFT_ID = Menu.FIRST + 2;
    private static final int VIEW_DRAFT_ID = Menu.FIRST + 3;
    protected static final int SIZE_TEXT_ID = Menu.FIRST + 4;

    protected ProgressDialog progressDialog;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_REPLY = 1;
    public static int TYPE_RETWEET = 2;
    public static int TYPE_DIRECT_MESSAGE = 3;
    public static int TYPE_REPLY_ON_COPY = 4;

    private String mTextStatus = "";
    private EditText mText;


    private TextView mTxtType;

    private LinearLayout mDataUsers;

    private TextView mCounter;

    private TextView mRefUserName;
    private TextView mRefText;
    private ImageView mRefAvatar;
    private ImageView homeIcon;
    private TextView titlePage;

    private LinearLayout mReftweetLayout;
    private LinearLayout mLayoutImages;


    private Button mSend;
    private ImageButton mGeo;
    private ImageButton mTimer;
    private ImageButton mShorter;

    private String mDMUsername;

    private ArrayList<UserStatus> mUsers = new ArrayList<UserStatus>();

    private long mReplyTweetId;
    private String mReplyScreenName;
    private String mReplyText;
    private String mReplyURLAvatar;
    private int mType = 0; // 0 normal - 1 - Reply - 2 - Retweet
    private String retweetPrev = "";

    private int mStartAutoComplete = -1;
    private int mEndAutoComplete = -1;
    private String mAuxText = "";

    private ThemeManager themeManager;

    private ArrayList<String> mImages = new ArrayList<String>();

    private RelativeLayout mLayoutBackgroundApp;

    private static NewStatusActivity thisInstance;

    private int mShortURLLength = 19;
    private int mShortURLLengthHttps = 20;

    private ImageView ivMoreOptions;


    public void refreshTheme() {
        boolean hasWallpaper = false;
        File f = new File(Preferences.IMAGE_WALLPAPER);
        if (f.exists()) {
            try {
                BitmapDrawable bmp = (BitmapDrawable) BitmapDrawable.createFromPath(Preferences.IMAGE_WALLPAPER);
                bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
                mLayoutBackgroundApp.setBackgroundDrawable(bmp);
                hasWallpaper = true;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        if (!hasWallpaper) {
            mLayoutBackgroundApp.setBackgroundColor(Color.parseColor("#" + themeManager.getStringColor("color_background_new_status")));
        }

        StateListDrawable statesButtonMoreOptions = new StateListDrawable();
        statesButtonMoreOptions.addState(new int[]{android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_default"), false, 0));
        statesButtonMoreOptions.addState(new int[]{-android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        ivMoreOptions.setBackgroundDrawable(statesButtonMoreOptions);

        StateListDrawable statesButton = new StateListDrawable();
        statesButton.addState(new int[]{android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_default"), false, 0));
        statesButton.addState(new int[]{-android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        homeIcon.setBackgroundDrawable(statesButton);
        titlePage.setTextColor(themeManager.getColor("color_indicator_text"));
        titlePage.setTextSize(getResources().getDimension(R.dimen.text_size_title_page));

        themeManager.setColors();
        layoutActionBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        //mButtonsFoot.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        //mTxtUsername.setBackgroundColor(Color.parseColor("#99"+(themeManager.getTheme()==1?"FFFFFF":"000000")));
        mTxtType.setBackgroundColor(Color.parseColor("#99" + (themeManager.getTheme() == 1 ? "FFFFFF" : "000000")));

        if (PreferenceUtils.getGeo(this))
            mGeo.setBackgroundColor(themeManager.getColor("color_button_press_default"));
        else
            mGeo.setBackgroundColor(Color.TRANSPARENT);

        //mShorter.setImageDrawable(themeManager.getDrawableMainButton(R.drawable.gd_action_bar_shorter, ThemeManager.TYPE_NORMAL));
    }

    private void deleteImages() {
        for (int i = 0; i < mImages.size(); i++) {
            String image = mImages.get(i);
            File file = new File(Utils.appUploadImageDirectory + image);
            if (file.exists()) file.delete();
        }
    }

    private void setModeTweetLonger(int mode) {
        mModeTweetLonger = mode;
    }

    private void createThumbs() {
        mLayoutImages.removeAllViews();
        for (int i = 0; i < mImages.size(); i++) {
            try {
                String image = mImages.get(i);
                /*
                    Matrix matrix = null;

                    try {
                        ExifInterface exif = new ExifInterface(Utils.appUploadImageDirectory+image);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        matrix = new Matrix();
                        if (orientation==3) {
                            matrix.postRotate(180);
                        } else if (orientation==6) {
                            matrix.postRotate(90);
                        } else if (orientation==8) {
                            matrix.postRotate(270);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */

                Bitmap bmp = ImageUtils.getBitmapFromFile(Utils.appUploadImageDirectory + image, Utils.HEIGHT_THUMB_NEWSTATUS, true);
                //if (matrix!=null) bmp = Bitmap.createBitmap(bmp, 0, 0, Utils.HEIGHT_THUMB_NEWSTATUS, Utils.HEIGHT_THUMB_NEWSTATUS, matrix, true);

                ImageView aux = new ImageView(this);
                aux.setImageBitmap(bmp);

                aux.setPadding(3, 0, 3, 3);
                aux.setTag(i);
                aux.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //int i = Integer.parseInt(v.getTag().toString());
                        //if (i<mURLImages.size()) addURLImageInEditText( mURLImages.get(i) );
                    }

                });

                mLayoutImages.addView(aux);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setDialogTheme();

        thisInstance = this;

        long userStart = -1;

        mImages = new ArrayList<String>();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("start_user_id"))
                userStart = savedInstanceState.getLong("start_user_id");
            if (savedInstanceState.containsKey("text")) mTextStatus = savedInstanceState.getString("text");
            if (savedInstanceState.containsKey("type")) mType = savedInstanceState.getInt("type");
            if (savedInstanceState.containsKey("reply_tweetid"))
                mReplyTweetId = savedInstanceState.getLong("reply_tweetid");
            if (savedInstanceState.containsKey("reply_avatar"))
                mReplyURLAvatar = savedInstanceState.getString("reply_avatar");
            if (savedInstanceState.containsKey("reply_screenname"))
                mReplyScreenName = savedInstanceState.getString("reply_screenname");
            if (savedInstanceState.containsKey("reply_text")) mReplyText = savedInstanceState.getString("reply_text");
            if (savedInstanceState.containsKey("username_direct_message"))
                mDMUsername = savedInstanceState.getString("username_direct_message");
            if (savedInstanceState.containsKey("retweet_prev")) {
                if (savedInstanceState.getString("retweet_prev").length() > 0)
                    retweetPrev = savedInstanceState.getString("retweet_prev") + " ";
            }

            if (savedInstanceState.containsKey("ar_images"))
                mImages = savedInstanceState.getStringArrayList("ar_images");

        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey("start_user_id")) userStart = extras.getLong("start_user_id");
                if (extras.containsKey("text")) mTextStatus = extras.getString("text");
                if (extras.containsKey("type")) mType = extras.getInt("type");
                if (extras.containsKey("reply_tweetid")) mReplyTweetId = extras.getLong("reply_tweetid");
                if (extras.containsKey("reply_avatar")) mReplyURLAvatar = extras.getString("reply_avatar");
                if (extras.containsKey("reply_screenname")) mReplyScreenName = extras.getString("reply_screenname");
                if (extras.containsKey("reply_text")) mReplyText = extras.getString("reply_text");
                if (extras.containsKey("username_direct_message"))
                    mDMUsername = extras.getString("username_direct_message");
                if (extras.containsKey("retweet_prev")) {
                    if (extras.getString("retweet_prev").length() > 0)
                        retweetPrev = extras.getString("retweet_prev") + " ";
                }
                if (extras.containsKey("ar_images")) mImages = extras.getStringArrayList("ar_images");

            }
        }


        overridePendingTransition(R.anim.pull_in_to_up, R.anim.hold);

        Utils.setActivity(this);

        Utils.saveApiConfiguration(this);

        mShortURLLength = PreferenceUtils.getShortURLLength(this);
        mShortURLLengthHttps = PreferenceUtils.getShortURLLengthHttps(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        String fileFromOtherApp = "";

        if (mImages.size() <= 0) {

            if (Intent.ACTION_SEND.equals(action)) {
                if (extras.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        Cursor c = managedQuery(uri, null, "", null, null);
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                            fileFromOtherApp = c.getString(dataIndex);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if ("text/plain".equals(intent.getType())) {
                    mTextStatus = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            }

        }


        setContentView(R.layout.new_status);


        mLayoutBackgroundApp = (RelativeLayout) findViewById(R.id.layout_background_app);


        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mUsers.size() == 0) {
            loadUsers(userStart);
        }

        mButtonsFoot = (LinearLayout) findViewById(R.id.buttons_foot);
        layoutActionBar = (RelativeLayout) findViewById(R.id.new_status_bar_action);
        mAutoCompleteDataFoot = (LinearLayout) findViewById(R.id.autocomplete_data_foot);
        mAutoCompleteFoot = (HorizontalScrollView) findViewById(R.id.autocomplete_foot);

        mDataUsers = (LinearLayout) this.findViewById(R.id.users_data);

        mRefUserName = (TextView) this.findViewById(R.id.tweet_user_name_text);
        mRefText = (TextView) this.findViewById(R.id.tweet_text);
        mRefAvatar = (ImageView) this.findViewById(R.id.user_avatar);

        homeIcon = (ImageView) this.findViewById(R.id.new_status_bar_icon);
        homeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyTextAndQuit();
            }
        });

        titlePage = (TextView) this.findViewById(R.id.new_status_bar_title);

        mLayoutImages = (LinearLayout) this.findViewById(R.id.images);

        refreshUsers();

        mReftweetLayout = (LinearLayout) this.findViewById(R.id.reftweet_layout);

        mText = (EditText) findViewById(R.id.text);

        mText.setTextSize(PreferenceUtils.getSizeTextNewStatus(this));

        mText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                countChars();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(Utils.TAG, "beforeTextChanged: " + start + " -- after: " + after+ " -- count: " + count+ " -- s: " + s.toString());
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(Utils.TAG, "onTextChanged: " + start + " -- before: " + before+ " -- count: " + count+ " -- s: " + s.toString());

                mAuxText = thisInstance.mText.getText().toString();
                boolean isUser = false;
                boolean isHashTag = false;
                boolean isDM = false;
                mStartAutoComplete = 0;
                mEndAutoComplete = thisInstance.mText.getSelectionStart();
                if (mAuxText.length() > 0) {
                    int pos = mEndAutoComplete;
                    while (pos > 0 && !mAuxText.substring(pos - 1, pos).equals(" ")) {
                        if (mAuxText.substring(pos - 1, pos).equals("@")) {
                            if (mAuxText.length() >= 3 && mAuxText.substring(0, 3).toLowerCase().equals("d @")) {
                                isDM = true;
                            }
                            isUser = true;
                            mStartAutoComplete = pos;
                            pos = 0;
                        } else if (mAuxText.substring(pos - 1, pos).equals("#")) {
                            isHashTag = true;
                            mStartAutoComplete = pos;
                            pos = 0;
                        } else {
                            pos--;
                        }
                    }
                    if (pos > 0 && mAuxText.substring(pos - 1, pos).equals(" ") && mAuxText.length() > 3 && mAuxText.substring(0, 3).toLowerCase().equals("d @")) {
                        onItemClickDMComplete(mAuxText.substring(3), true);
                    }
                }

                if (isUser && mAuxText.substring(mStartAutoComplete, mEndAutoComplete).length() > 0) {
                    showUsers(mAuxText.substring(mStartAutoComplete, mEndAutoComplete), isDM);
                } else if (isHashTag && mAuxText.substring(mStartAutoComplete, mEndAutoComplete).length() > 0) {
                    showHashTags(mAuxText.substring(mStartAutoComplete, mEndAutoComplete));
                } else {
                    showFootButtons();
                }

                /*
                        if (mStartAutoComplete>=0 && start>mStartAutoComplete) {
                            mAuxText = mText.getText().toString();
                            mEndAutoComplete = start+count+1;
                            if (mAuxText.length()<mEndAutoComplete) mEndAutoComplete--;
                            String text = mAuxText.substring(mStartAutoComplete+1, mEndAutoComplete);
                            showUsers(text);
                        }
                        if (s.toString().substring(start, start+count).equals("@")) {
                            mStartAutoComplete = start;
                        }
                        if (s.toString().substring(start, start+count).equals(" ")) {
                            mStartAutoComplete = -1;
                            showFootButtons();
                        }
                        */
            }
        }
        );

        mTxtType = (TextView) findViewById(R.id.txt_type);

        mSend = (Button) findViewById(R.id.bt_send);
        mSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // comprobar si tenemos geoposicion

                if (PreferenceUtils.getGeo(NewStatusActivity.this)) {
                    Location loc = LocationUtils.getLastLocation(NewStatusActivity.this);
                    if (loc == null) {
                        showNoFoundGeoDialog();
                    } else {
                        send();
                    }
                } else {
                    send();
                }

            }

        });

        mShorter = (ImageButton) findViewById(R.id.bt_shorter);
        mShorter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String text = mText.getText().toString();
                int count = LinksUtils.pullLinksHTTP(text).size() - mImages.size();
                if (count > 0) {
                    mText.setText(LinksUtils.shortLinks(text, mImages));
                    Utils.showShortMessage(NewStatusActivity.this, count + " " + NewStatusActivity.this.getString(R.string.txt_shorter_n));
                } else {
                    Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_shorter_0));
                }

            }

        });

        mGeo = (ImageButton) findViewById(R.id.bt_geo);
        mGeo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (PreferenceUtils.getGeo(NewStatusActivity.this)) {
                    //mGeo.setImageDrawable(themeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_NORMAL));
                    mGeo.setBackgroundColor(Color.TRANSPARENT);
                    Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_geoloc_off));
                    PreferenceUtils.setGeo(NewStatusActivity.this, false);
                } else {
                    mGeo.setBackgroundColor(themeManager.getColor("color_button_press_default"));
                    //mGeo.setImageDrawable(themeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_SELECTED));
                    PreferenceUtils.setGeo(NewStatusActivity.this, true);
                    Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_geoloc_on));
                }
            }

        });

        mTimer = (ImageButton) findViewById(R.id.bt_timer);
        mTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogProgrammedTweet();
            }

        });

        mCounter = (TextView) findViewById(R.id.bt_counter);

        ivMoreOptions = (ImageView) findViewById(R.id.new_status_more_options);

        ivMoreOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuOptions(view);
            }
        });

        refreshTheme();

        createThumbs();

        populateFields();

        if (!fileFromOtherApp.equals("")) {
            copyImage(fileFromOtherApp);
        }

        if (mType == TYPE_DIRECT_MESSAGE) onItemClickDMComplete(mDMUsername, false);

    }

    private void showMenuOptions(View view) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.new_status_more_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popupmenu_status_more_options_photo) {
                        showSelectImageDialog();
                    } else if (item.getItemId() == R.id.popupmenu_status_more_options_new_draft) {
                        saveDrafts();
                    } else if (item.getItemId() == R.id.popupmenu_status_more_options_view_draft) {
                        showDialogDrafts();
                    } else if (item.getItemId() == R.id.popupmenu_status_more_options_default_text) {
                        showDialogDefaultText();
                    } else if (item.getItemId() == R.id.popupmenu_status_more_options_size) {
                        showSizeText();
                    }
                    return true;
                }
            });
            popupMenu.show();
        } else {
            AlertDialogFragment frag = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.actions);
            args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_POSITIVE_BUTTON, false);
            args.putBoolean(AlertDialogFragment.KEY_ALERT_CANCELABLE, false);
            args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.popupmenu_my_activity_more_options);
            frag.setArguments(args);
            frag.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
                @Override
                public void OnAlertButtonOk() {
                }

                @Override
                public void OnAlertButtonCancel() {
                }

                @Override
                public void OnAlertButtonNeutral() {
                }

                @Override
                public void OnAlertItems(int which) {
                    if (which == 0) {
                        showSelectImageDialog();
                    } else if (which == 1) {
                        saveDrafts();
                    } else if (which == 2) {
                        showDialogDrafts();
                    } else if (which == 3) {
                        showDialogDefaultText();
                    } else if (which == 4) {
                        showSizeText();
                    }
                }
            });
            frag.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void showNoFoundGeoDialog() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.title_no_geo);
        args.putInt(AlertDialogFragment.KEY_ALERT_MESSAGE, R.string.text_no_geo);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_NEGATIVE_BUTTON, true);
        args.putInt(AlertDialogFragment.KEY_ALERT_POSITIVE_LABEL, R.string.yes);
        args.putInt(AlertDialogFragment.KEY_ALERT_NEGATIVE_LABEL, R.string.alert_dialog_cancel);
        alertDialogFragment.setArguments(args);
        alertDialogFragment.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
            @Override
            public void OnAlertButtonOk() {
                send();
            }

            @Override
            public void OnAlertButtonCancel() {
            }

            @Override
            public void OnAlertButtonNeutral() {
            }

            @Override
            public void OnAlertItems(int which) {
            }
        });
        alertDialogFragment.show(getSupportFragmentManager(), "dialog");

    }


    private void addUserInLayout(UserStatus user) {

        View v = View.inflate(this, R.layout.users_item_new_status, null);

        StateListDrawable statesButton = new StateListDrawable();
        statesButton.addState(new int[]{android.R.attr.state_selected}, new BitmapDrawable(getResources(), user.avatarON));
        statesButton.addState(new int[]{-android.R.attr.state_selected}, new BitmapDrawable(getResources(), user.avatarOFF));

        ImageView avatar = (ImageView)v.findViewById(R.id.user_item_avatar);
        avatar.setImageDrawable(statesButton);
        avatar.setSelected(user.checked);
        avatar.setTag(user.id);
        avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(Long.parseLong(v.getTag().toString()));
            }
        });

        ImageView tag_network = (ImageView) v.findViewById(R.id.user_item_tag_network);

        if (user.service.equals("facebook")) {
            tag_network.setImageResource(R.drawable.icon_facebook);
        } else {
            tag_network.setImageResource(R.drawable.icon_twitter);
        }

        ((ImageView) v.findViewById(R.id.user_item_selector)).setVisibility(user.checked?View.VISIBLE:View.GONE);

        mDataUsers.addView(v);
    }

    private void addInviteFacebookInLayout() {
        View v = View.inflate(this, R.layout.users_item_new_status, null);

        ImageView img = (ImageView) v.findViewById(R.id.user_item_avatar);
        try {
            img.setImageResource(R.drawable.icon_facebook_large);
        } catch (Exception e) {
            e.printStackTrace();
            img.setImageResource(R.drawable.avatar);
        }

        ImageView tag_network = (ImageView) v.findViewById(R.id.user_item_tag_network);

        tag_network.setImageBitmap(null);

        /*
        v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newuser = new Intent(NewStatusActivity.this, Users.class);
				NewStatusActivity.this.startActivityForResult(newuser, ACTIVITY_USER);
			}
        });
        */
        mDataUsers.addView(v);
    }

    private void loadUsers(long userStart) {
        mUsers.clear();
        List<Entity> ents = DataFramework.getInstance().getEntityList("users");

        for (Entity ent : ents) {
            UserStatus user = new UserStatus();
            user.id = ent.getId();
            user.username = ent.getString("name");
            user.service = ent.getString("service");
            if (userStart > 0) {
                user.checked = (ent.getId() == userStart);
            }

            user.avatarON = ImageUtils.createBitmapSelectedAvatar(ent.getId(), Utils.dip2px(this, Utils.AVATAR_XLARGE));
            user.avatarOFF = ImageUtils.createBitmapUnselectedAvatar(ent.getId(), Utils.dip2px(this, Utils.AVATAR_XLARGE));

            mUsers.add(user);
        }
    }

    private void refreshUsers() {

        mDataUsers.removeAllViews();

        if (mType == TYPE_DIRECT_MESSAGE) {
            for (UserStatus user : mUsers) {
                if (user.checked) {
                    addUserInLayout(user);
                }
            }
        } else {
            if (Utils.isLite(this)) {
                addUserInLayout(mUsers.get(0));
                if (mUsers.size() > 1) {
                    addUserInLayout(mUsers.get(1));
                } else {
                    addInviteFacebookInLayout();
                }
            } else {
                for (UserStatus user : mUsers) {
                    addUserInLayout(user);
                }
            }

        }

    }

    private void setChecked(long id) {
        boolean todo = true;
        int countSelected = 0;
        for (UserStatus user : mUsers) {
            if (user.checked) {
                countSelected++;
            }
        }
        if (countSelected == 1) {
            for (UserStatus user : mUsers) {
                if (user.checked && user.id == id) {
                    todo = false;
                }
            }
        }
        if (todo) {
            for (UserStatus user : mUsers) {
                if (user.id == id) user.checked = !user.checked;
            }
            refreshUsers();
        } else {
            Utils.showMessage(this, R.string.one_user_selected);
        }
    }

    public boolean copyImage(String image) {
        int type = Integer.parseInt(Utils.getPreference(this).getString("prf_service_image", "1"));
        int chars = 0;
        if (type == 1) {
            chars = CHARS_YFROG;
        } else if (type == 2) {
            chars = CHARS_TWITPIC;
        } else if (type == 3) {
            chars = CHARS_LOCKERZ;
        }

        String ext = "";

        StringTokenizer tokens = new StringTokenizer(image, ".");

        while (tokens.hasMoreTokens()) {
            ext = tokens.nextToken();
        }

        String name = System.currentTimeMillis() + "";

        if (name.length() > chars) {
            name = name.substring(name.length() - chars, name.length());
        } else if (name.length() < chars) {
            String fill = "";
            for (int i = 0; i < chars - name.length(); i++) {
                fill += "0";
            }
            name = fill + name;
        }

        String file = name + "." + ext;

        try {
            Log.d(Utils.TAG, "Copiar " + image + " a " + Utils.appUploadImageDirectory + file);
            FileUtils.copy(image, Utils.appUploadImageDirectory + file);
            ImageUtils.savePhotoInScale(this, Utils.appUploadImageDirectory + file);
            mImages.add(file);
            createThumbs();
            addTextInEditText(getURLBase() + name);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getURLCurrentImage() {
        return Utils.appDirectory + "aux_upload_" + mImages.size() + ".jpg";
    }

    private void send() {

        boolean userChecked = false;
        for (UserStatus user : mUsers) {
            if (user.checked) {
                userChecked = true;
                break;
            }
        }

        if (!userChecked) {
            Utils.showMessage(this, R.string.one_user_selected);
            return;
        }


        if (Utils.getLenghtTweet(mText.getText().toString(), mShortURLLength, mShortURLLengthHttps) > 140
                && mModeTweetLonger == MODE_TL_NONE) {
            showDialogTweetLonger();
            return;
        }

        String photos = "";
        for (String p : mImages) {
            photos += p + "--";
        }

        DataFramework.getInstance().emptyTable("send_tweets");

        boolean onlyFacebook = true;

        String users = "";
        for (UserStatus user : mUsers) {
            if (user.checked) {
                users += user.id + ",";
                if (!user.service.equals("facebook")) onlyFacebook = false;
            }
        }

        if (onlyFacebook && mImages.size() > 0) {
            Utils.showMessage(this, R.string.no_facebook_images);
        } else {

            Entity ent = new Entity("send_tweets");
            ent.setValue("users", users);
            ent.setValue("text", mText.getText());
            ent.setValue("is_sent", 0);
            ent.setValue("type_id", (mType == TYPE_DIRECT_MESSAGE) ? 2 : 1);
            ent.setValue("username_direct", mDMUsername);
            ent.setValue("photos", photos);
            ent.setValue("mode_tweetlonger", mModeTweetLonger);
            if (mIdDeleteDraft > 0) ent.setValue("tweet_draft_id", mIdDeleteDraft);
            if (mType == NewStatusActivity.TYPE_REPLY || mType == NewStatusActivity.TYPE_REPLY_ON_COPY) {
                ent.setValue("reply_tweet_id", Utils.fillZeros("" + mReplyTweetId));
            } else {
                ent.setValue("reply_tweet_id", "-1");
            }

            ent.setValue("use_geo", PreferenceUtils.getGeo(this) ? "1" : "0");
            ent.save();

            startService(new Intent(this, ServiceUpdateStatus.class));

            finish();

        }

    }

    private void showUsers(final String user, final boolean isDM) {

        try {
            List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "username like '" + user + "%'", "username asc");

            Log.d(Utils.TAG, "Searching by " + user + " con " + ents.size() + " resultados");

            mAutoCompleteDataFoot.removeAllViews();
            mResultInfoUsers.clear();

            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<SearchContentInDBResponse>() {
                @Override
                public void onResults(SearchContentInDBResponse result) {
                    if (result.getObjectList().size() > 0) {
                        loadUserInAutoComplete(result.getObjectList(), user, isDM);
                        showFootAutoComplete();
                    } else {
                        showFootButtons();
                    }
                }

                @Override
                public void onError(ErrorResponse error) {

                }
            }, new SearchContentInDBRequest(user, SearchContentInDBRequest.TypeContent.USERS));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserInAutoComplete(List<Object> users, String user, boolean isDM) {
        int count = 0;
        for (Object objUser : users) {
            InfoUsers infoUser = (InfoUsers) objUser;
            mResultInfoUsers.add(infoUser);
            AutoCompleteListItem v = (AutoCompleteListItem) View.inflate(this, R.layout.row_autocomplete_user, null);
            v.setRow(infoUser, user);
            v.setTag(count);
            if (isDM) {
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickDMComplete(Integer.parseInt(v.getTag().toString()));
                    }
                });
            } else {
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickAutoComplete(Integer.parseInt(v.getTag().toString()));
                    }
                });
            }
            mAutoCompleteDataFoot.addView(v);
            count++;
        }
    }

    private void showHashTags(final String search) {
        List<String> hashtags = new ArrayList<String>();

        try {
            List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "text like '%#%'", "");

            Log.d(Utils.TAG, "Searching hashtag by " + search + " en " + ents.size() + " resultados");

            mAutoCompleteDataFoot.removeAllViews();
            mResultInfoHashTags.clear();

            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<SearchContentInDBResponse>() {
                @Override
                public void onResults(SearchContentInDBResponse result) {
                    if (result.getObjectList().size() > 0) {
                        loadHashTagInAutoComplete(result.getObjectList(), search);
                        showFootAutoComplete();
                    } else {
                        showFootButtons();
                    }
                }

                @Override
                public void onError(ErrorResponse error) {

                }
            }, new SearchContentInDBRequest(search, SearchContentInDBRequest.TypeContent.HASHTAGS));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHashTagInAutoComplete(List<Object> hashTags, String search) {
        int count = 0;
        for (Object objHashTag : hashTags) {
            String hashtag = objHashTag.toString();
            mResultInfoHashTags.add(hashtag);
            AutoCompleteHashTagListItem v = (AutoCompleteHashTagListItem) View.inflate(this, R.layout.row_autocomplete_hashtag, null);
            v.setRow(hashtag, search);
            v.setTag(count);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickHashTagAutoComplete(Integer.parseInt(v.getTag().toString()));
                }
            });
            mAutoCompleteDataFoot.addView(v);
        }
    }

    private void onItemClickDMComplete(int position) {
        onItemClickDMComplete(mResultInfoUsers.get(position).getName(), true);
    }

    private void onItemClickDMComplete(String user, final boolean fromAutocomplete) {

        mText.setText("");
        mStartAutoComplete = -1;
        showFootButtons();

        String userDM = "";
        int countUsers = 0;

        for (UserStatus userStatus : mUsers) {
             if (userStatus.checked && userStatus.service.equals(ConstantUtils.NETWORK_TWITTER_NAME)) {
                 userDM = userStatus.username;
                 countUsers++;
             }
        }

        if (countUsers!=1) {
            Utils.showMessage(this, R.string.shouldSelectOneUser);
            return;
        }

        InfoUsers infoUsers = CacheData.getCacheUser(user);

        if (infoUsers==null || !infoUsers.hasFriendly(userDM)) {
            final String userDMFinal = userDM;

            progressDialog = ProgressDialog.show(
                    this,
                    getResources().getString(R.string.verify_dm_title),
                    getResources().getString(R.string.verify_dm_msg)
            );

            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<CheckFriendlyUserResponse>() {
                @Override
                public void onResults(CheckFriendlyUserResponse result) {
                    progressDialog.dismiss();
                    checkIfIsPossibleUserDM(result.getInfoUsers(), userDMFinal, fromAutocomplete);
                }

                @Override
                public void onError(ErrorResponse error) {
                    progressDialog.dismiss();
                }
            }, new CheckFriendlyUserRequest(infoUsers, user, userDM));
        } else {
            checkIfIsPossibleUserDM(infoUsers, userDM, fromAutocomplete);
        }
    }

    public void checkIfIsPossibleUserDM(InfoUsers infoUsers, String user, boolean fromAutocomplete) {
        if (infoUsers != null) {
            if (infoUsers.isFriend(user)) {
                Utils.showMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.verify_dm_yes, infoUsers.getName()));
                if (fromAutocomplete) {
                    mDMUsername = infoUsers.getName();
                    mType = TYPE_DIRECT_MESSAGE;
                    populateFields();
                }
            } else {
                Utils.showMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.no_is_follower));
                if (!fromAutocomplete) {
                    finish();
                }
            }
        }
    }

    private void onItemClickAutoComplete(int position) {
        //Log.d(Utils.TAG, "Texto: " + mAuxText + " " + mStartAutoComplete + " - " + mEndAutoComplete + " tam: " + mAuxText.length());
        String out = mAuxText.substring(0, mStartAutoComplete);
        out += mResultInfoUsers.get(position).getName();
        int pos = out.length();
        if (mEndAutoComplete < mAuxText.length()) out += mAuxText.substring(mEndAutoComplete, mAuxText.length());
        mText.setText(out);
        mText.setSelection(pos);
        mStartAutoComplete = -1;
        showFootButtons();
    }

    private void onItemClickHashTagAutoComplete(int position) {
        //Log.d(Utils.TAG, "Texto: " + mAuxText + " " + mStartAutoComplete + " - " + mEndAutoComplete + " tam: " + mAuxText.length());
        String out = mAuxText.substring(0, mStartAutoComplete);
        out += mResultInfoHashTags.get(position);
        int pos = out.length();
        if (mEndAutoComplete < mAuxText.length()) out += mAuxText.substring(mEndAutoComplete, mAuxText.length());
        mText.setText(out);
        mText.setSelection(pos);
        mStartAutoComplete = -1;
        showFootButtons();
    }

    private void showFootButtons() {
        mButtonsFoot.setVisibility(View.VISIBLE);
        mAutoCompleteFoot.setVisibility(View.GONE);
    }

    private void showFootAutoComplete() {
        if (thisInstance.mText.getText().toString().endsWith(" ")) {
            showFootButtons();
        } else {
            mButtonsFoot.setVisibility(View.GONE);
            mAutoCompleteFoot.setVisibility(View.VISIBLE);
        }
    }

    private void populateFields() {
        if (mType == TYPE_NORMAL) {
            String def = PreferenceUtils.getDefaultTextInTweet(this);
            if (def.length() > 0) {
                mText.setText(def + " " + mTextStatus);
            } else {
                mText.setText(mTextStatus);
            }
            mTxtType.setVisibility(View.GONE);
        } else if (mType == TYPE_REPLY) {
            mTxtType.setText(getString(R.string.txt_type_reply));
            mText.setText("@" + mReplyScreenName + " " + mTextStatus);
        } else if (mType == TYPE_REPLY_ON_COPY) {
            mTxtType.setText(getString(R.string.txt_type_reply));
            mText.setText("@" + mReplyScreenName + " " + mTextStatus);
            mText.setSelection(mReplyScreenName.length() + 2);
        } else if (mType == TYPE_RETWEET) {
            mTxtType.setText(getString(R.string.txt_type_retweet));
            mText.setText(retweetPrev + "RT: @" + mReplyScreenName + ": " + mTextStatus);
        } else if (mType == TYPE_DIRECT_MESSAGE) {
            mTxtType.setVisibility(View.VISIBLE);
            mTxtType.setText(getString(R.string.txt_type_dm) + " " + mDMUsername);
        }

        refreshUsers();

        if ((mType == TYPE_REPLY) || (mType == TYPE_RETWEET) || (mType == TYPE_REPLY_ON_COPY)) {
            mReftweetLayout.setVisibility(View.VISIBLE);
            //Log.d(Utils.TAG, "dentro: " + mReplyScreenName);
            mRefUserName.setText(mReplyScreenName);
            mRefText.setText(Html.fromHtml(Utils.toHTML(this, mReplyText)));
            try {
                File file = Utils.getFileForSaveURL(this, mReplyURLAvatar);
                Bitmap bmp = null;
                if (!file.exists()) {
                    bmp = ImageUtils.saveAvatar(mReplyURLAvatar, file);
                    /*URL url = new URL(mReplyURLAvatar);
                         bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));
                         FileOutputStream out = new FileOutputStream(file);
                         bmp.compress(Bitmap.CompressFormat.PNG, 90, out);      */
                } else {
                    bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    /*if (bmp==null) { // lo intentamos de nuevo
                             file.delete();
                             URL url = new URL(mReplyURLAvatar);
                             bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));
                             FileOutputStream out = new FileOutputStream(file);
                             bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                         }*/
                }
                mRefAvatar.setImageBitmap(bmp);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                mRefAvatar.setImageResource(R.drawable.avatar);
                Log.d(Utils.TAG, "Could not load image.", e);
            } catch (Exception e) {
                e.printStackTrace();
                mRefAvatar.setImageResource(R.drawable.avatar);
                Log.d(Utils.TAG, "Could not load image.", e);
            }
        } else {
            mReftweetLayout.setVisibility(View.GONE);
        }

        mText.setSelection(mText.getText().toString().length());
        countChars();
    }

    private void countChars() {
        int length = Utils.getLenghtTweet(mText.getText().toString(), mShortURLLength, mShortURLLengthHttps);

        int number = 140 - length;
        //mCounter.setImageBitmap(Utils.getBitmapNumber(this, number, (number<0)?Color.RED:Color.GREEN, Utils.TYPE_BUBBLE, 15));
        mCounter.setText(number + "");

        if (number < 0) {
            mCounter.setTextColor(Color.RED);
        } else {
            mCounter.setTextColor(Color.WHITE);
        }
    }

    public void showSizeText() {

        final int minValue = 10;

        LayoutInflater factory = LayoutInflater.from(this);
        final View sizesFontView = factory.inflate(R.layout.alert_dialog_sizes_newstatus, null);

        ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeTextNewStatus(this) + ")");

        SeekBar sbSizeText = (SeekBar) sizesFontView.findViewById(R.id.sb_size_text);
        sbSizeText.setMax(18);
        sbSizeText.setProgress(PreferenceUtils.getSizeTextNewStatus(this) - minValue);

        sbSizeText.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeTextNewStatus(NewStatusActivity.this, progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeTextNewStatus(NewStatusActivity.this) + ")");
                mText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.font_size);
        builder.setView(sizesFontView);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();

    }

    public void showDialogTweetLonger() {

        Dialog dialog = new Dialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.actions);
        builder.setMessage(R.string.is_twitlonger_msg);

        ArrayList<TweetLongerAdapter.TypeTweetLonger> items = new ArrayList<TweetLongerAdapter.TypeTweetLonger>();

        TweetLongerAdapter.TypeTweetLonger type1 = new TweetLongerAdapter.TypeTweetLonger();

        if (mType != TYPE_DIRECT_MESSAGE) {
            type1.mode = MODE_TL_TWITLONGER;
            type1.title = getString(R.string.twitlonger);
            type1.description = getString(R.string.twitlonger_msg);
            items.add(type1);
        }

        String replyuser = "";
        if (mType == NewStatusActivity.TYPE_REPLY || mType == NewStatusActivity.TYPE_REPLY_ON_COPY) {
            replyuser = "@" + mReplyScreenName;
        }


        TweetLongerAdapter.TypeTweetLonger type2 = new TweetLongerAdapter.TypeTweetLonger();
        type2.mode = MODE_TL_N_TWEETS;
        type2.title = getString(R.string.n_tweets);
        type2.description = getString(R.string.n_tweets_msg, Utils.getDivide140(mText.getText().toString(), replyuser).size());
        items.add(type2);

        ListView modeList = new ListView(this);
        modeList.setBackgroundColor(Color.WHITE);
        modeList.setCacheColorHint(Color.WHITE);
        final TweetLongerAdapter adapterTweetLonger = new TweetLongerAdapter(this, items);
        modeList.setAdapter(adapterTweetLonger);

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setModeTweetLonger(((TweetLongerAdapter.TypeTweetLonger) adapterTweetLonger.getItem(i)).mode);
                send();
            }
        });

        builder.setView(modeList);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        dialog = builder.create();

        dialog.show();

    }

    int whichProgrammedTweet;

    public void showDialogProgrammedTweet() {
        whichProgrammedTweet = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.options);
        //builder.setMessage(mTweetTopics.getString(R.string.follow_tweettopics_msg));
        builder.setSingleChoiceItems(R.array.values_tweetprogrammed, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                whichProgrammedTweet = whichButton;
            }
        });
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Calendar calendar = Calendar.getInstance();


                if (whichProgrammedTweet == 0) {
                    calendar.add(Calendar.MINUTE, 5);
                } else if (whichProgrammedTweet == 1) {
                    calendar.add(Calendar.MINUTE, 15);
                } else if (whichProgrammedTweet == 2) {
                    calendar.add(Calendar.MINUTE, 30);
                } else if (whichProgrammedTweet == 3) {
                    calendar.add(Calendar.HOUR, 1);
                } else if (whichProgrammedTweet == 4) {
                    calendar.add(Calendar.HOUR, 2);
                } else if (whichProgrammedTweet == 5) {
                    calendar.add(Calendar.HOUR, 6);
                } else if (whichProgrammedTweet == 6) {
                    calendar.add(Calendar.HOUR, 12);
                } else if (whichProgrammedTweet == 7) {
                    calendar.add(Calendar.HOUR, 24);
                } else if (whichProgrammedTweet == 8) {
                    calendar.add(Calendar.HOUR, 24 * 7);
                }


                long date = calendar.getTimeInMillis();

                String users = "";
                for (UserStatus user : mUsers) {
                    if (user.checked) users += user.id + ",";
                }

                Entity ent = new Entity("tweets_programmed");
                ent.setValue("users", users);
                ent.setValue("text", mText.getText().toString());
                ent.setValue("date", date);
                ent.setValue("type_id", (mType == TYPE_DIRECT_MESSAGE) ? 2 : 1);
                ent.setValue("username_direct", mDMUsername);
                ent.setValue("is_sent", 0);
                ent.save();
                OnAlarmReceiverTweetProgrammed.callNextAlarm(NewStatusActivity.this);
                Utils.showMessage(NewStatusActivity.this, R.string.programmed_save);
                finish();

            }
        });
        builder.setNeutralButton(R.string.personalize, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sendProgrammedTweet();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendProgrammedTweet() {
        Intent send = new Intent(this, NewEditTweetProgrammed.class);
        send.putExtra("text", mText.getText().toString());
        startActivity(send);
    }

    private void saveDrafts() {
        Entity ent = new Entity("tweets_draft");
        ent.setValue("text", mText.getText().toString());
        ent.save();
        Utils.showMessage(this, this.getString(R.string.draft_save));
    }

    private void showDialogDrafts() {
        final CheckBox cb = new CheckBox(this);
        cb.setChecked(false);
        cb.setText(R.string.delete_draft_sent);
        cb.setTextColor(Color.GRAY);
        final List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_draft");
        TweetListDraftAdapter drafts = new TweetListDraftAdapter(this, ents);
        AlertDialog builder = new AlertDialog.Builder(this)
                .setView(cb)
                .setTitle(R.string.view_draft)
                .setAdapter(drafts, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mText.append(ents.get(which).getString("text"));
                        if (cb.isChecked()) {
                            mIdDeleteDraft = ents.get(which).getId();
                        }
                    }

                })
                .setPositiveButton(R.string.view_draft, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent send = new Intent(NewStatusActivity.this, TweetDraft.class);
                        startActivity(send);
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        builder.show();
    }

    public void showDialogDefaultText() {
        final EditText et = new EditText(this);
        et.setText(PreferenceUtils.getDefaultTextInTweet(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.dialog_default_text));
        builder.setMessage(this.getString(R.string.dialog_default_text_msg));
        builder.setView(et);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.setDefaultTextInTweet(NewStatusActivity.this, et.getText().toString());
            }

        });
        builder.setNeutralButton(R.string.clean, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.setDefaultTextInTweet(NewStatusActivity.this, "");
            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSelectImageDialog() {

        SelectImageDialogFragment frag = new SelectImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.actions);
        args.putString("file", getURLCurrentImage());
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "dialog");

    }

    /*
    public void loadUser(long id) {
        List<Entity> ents = DataFramework.getInstance().getEntityList("users", DataFramework.KEY_ID + " = " + id);
        if (ents.size()==1) {
            mUserOut = ents.get(0).getId();
            app.loadUser(id, false);
            twitter = app.getTwitter();
            try {
                mBtAvatar.setImageBitmap(Utils.getBitmapAvatar(ents.get(0).getId(), Utils.AVATAR_LARGE));
                mTxtUsername.setText(ents.get(0).getString("name"));
            } catch (Exception ex) {
                ex.printStackTrace();
                mBtAvatar.setImageResource(R.drawable.avatar);
            }
        }
    }
    */
    private void verifyTextAndQuit() {
        if (mText.getText().toString().equals("")) {
            quit();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.actions);
            builder.setMessage(R.string.quit_newstatus);
            builder.setPositiveButton(R.string.save_draft, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    saveDrafts();
                    quit();
                }
            });
            builder.setNeutralButton(R.string.discard, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteImages();
                    quit();
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }
    }

    private void quit() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            verifyTextAndQuit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        switch (requestCode) {
            case SelectImageDialogFragment.ACTIVITY_CAMERA:
                if (resultCode != 0) {
                    copyImage(getURLCurrentImage());
                }
                break;
            case SelectImageDialogFragment.ACTIVITY_SELECTIMAGE:
                if (resultCode != 0) {
                    Cursor c = getContentResolver().query(intent.getData(), null, null, null, null);
                    if (c != null) {
                        if (c.moveToFirst()) {
                            String media_path = c.getString(1);
                            copyImage(media_path);
                        }
                        c.close();
                    } else {
                        Utils.showMessage(this, R.string.other_gallery);
                    }
                }
                break;
            case ACTIVITY_USER:
                loadUsers(-1);
                refreshUsers();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    private void addTextInEditText(String text) {
        if (mText.getText().toString().equals("")) {
            mText.append(text + " ");
        } else {
            mText.append(" " + text);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.saveStatusWorkApp(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.push_out_from_up);
        PreferenceUtils.saveStatusWorkApp(this, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("text", mTextStatus);
        outState.putInt("type", mType);
        outState.putLong("reply_tweetid", mReplyTweetId);
        outState.putString("reply_avatar", mReplyURLAvatar);
        outState.putString("reply_screenname", mReplyScreenName);
        outState.putString("reply_text", mReplyText);
        outState.putString("username_direct_message", mDMUsername);
        outState.putString("retweet_prev", retweetPrev);
        outState.putStringArrayList("ar_images", mImages);

        super.onSaveInstanceState(outState);
    }


    private String getURLBase() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

        int type = Integer.parseInt(preference.getString("prf_service_image", "1"));

        if (type == 1) {
            return URL_BASE_YFROG;
        } else if (type == 2) {
            return URL_BASE_TWITPIC;
        } else if (type == 3) {
            return URL_BASE_LOCKERZ;
        }

        return "http://service.com/";
    }

    class UserStatus {
        public Bitmap avatarON = null;
        public Bitmap avatarOFF = null;
        public String username = "";
        public boolean checked = false;
        public String service = "";
        public long id = 0;
    }


}
