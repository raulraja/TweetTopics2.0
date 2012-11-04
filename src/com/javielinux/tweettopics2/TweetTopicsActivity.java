package com.javielinux.tweettopics2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.components.DraggableHorizontalView;
import com.javielinux.components.OnRearrangeListener;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.javielinux.fragments.BaseListFragment;
import com.javielinux.infos.InfoTweet;
import com.javielinux.notifications.OnAlarmReceiver;
import com.javielinux.utils.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.Random;

public class TweetTopicsActivity extends BaseLayersActivity implements PopupLinks.PopupLinksListener, SplitActionBarMenu.SplitActionBarMenuListener {

    private static String KEY_SAVE_STATE_COLUMN_POS = "KEY_SAVE_STATE_COLUMN_POS";

    public static final String KEY_EXTRAS_GOTO_COLUMN_USER = "KEY_EXTRAS_GOTO_COLUMN_USER";
    public static final String KEY_EXTRAS_GOTO_COLUMN_TYPE = "KEY_EXTRAS_GOTO_COLUMN_TYPE";
    public static final String KEY_EXTRAS_GOTO_COLUMN_POSITION = "KEY_EXTRAS_GOTO_COLUMN_POSITION";
    public static final String KEY_EXTRAS_GOTO_TWEET_ID = "KEY_EXTRAS_GOTO_TWEET_ID";

    public static final int ACTIVITY_NEWEDITSEARCH = 0;
    public static final int ACTIVITY_NEWSTATUS = 1;
    public static final int ACTIVITY_TRENDS_LOCATION = 2;
    public static final int ACTIVITY_EDIT_SEARCH = 3;

    private ViewPager pager;
    private TweetTopicsFragmentAdapter fragmentAdapter;
    private TitlePageIndicator indicator;
    private ThemeManager themeManager;
    private RelativeLayout layoutBackgroundApp;

    private RelativeLayout layoutBackgroundBar;

    private LinearLayout layoutBackgroundColumnsBarContainer;
    private DraggableHorizontalView layoutBackgroundColumnsBar;
    private boolean isShowColumnsItems = false;

    private ImageView imgBarAvatar;
    private ImageView imgBarAvatarBg;
    private TextView imgBarCounter;
    private ImageView imgNewStatus;

    private LinearLayout layoutOptionsColumns;
    private LinearLayout layoutMainOptionsColumns;
    private Button btnOptionsColumnsMain;
    private Button btnOptionsColumnsDelete;

    private int widthScreen;
    private int heightScreen;

    private PopupLinks popupLinks;
    private SplitActionBarMenu splitActionBarMenu;

    private GestureDetector imgBarAvatarGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        OnAlarmReceiver.callAlarm(this);

        if (PreferenceUtils.getFinishForceClose(this)) {
            PreferenceUtils.setFinishForceClose(this, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_crash);
            builder.setMessage(R.string.msg_crash);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Utils.sendLastCrash(TweetTopicsActivity.this);
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }

        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter(currentHandler, getApplication()));
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("prf_orientation", "1").equals("2")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        // borrar notificaciones
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prf_notif_delete_notifications_inside", true)) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        }

        long goToColumnPosition = -1;
        int goToColumnType = -1;
        long goToColumnUser = -1;
        long selectedTweetId = -1;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_EXTRAS_GOTO_COLUMN_POSITION)) {
                goToColumnPosition = extras.getLong(KEY_EXTRAS_GOTO_COLUMN_POSITION);
            }
            if (extras.containsKey(KEY_EXTRAS_GOTO_COLUMN_TYPE)) {
                goToColumnType = extras.getInt(KEY_EXTRAS_GOTO_COLUMN_TYPE);
            }
            if (extras.containsKey(KEY_EXTRAS_GOTO_COLUMN_USER)) {
                goToColumnUser = extras.getLong(KEY_EXTRAS_GOTO_COLUMN_USER);
            }
            if (extras.containsKey(KEY_EXTRAS_GOTO_TWEET_ID)) {
                selectedTweetId = extras.getLong(KEY_EXTRAS_GOTO_TWEET_ID);
            }
        }

        int positionFromSensor = -1;
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_STATE_COLUMN_POS)) {
            positionFromSensor = savedInstanceState.getInt(KEY_SAVE_STATE_COLUMN_POS);
        }

        Utils.createDirectoriesIfIsNecessary();

        Display display = getWindowManager().getDefaultDisplay();
        widthScreen = display.getWidth();
        heightScreen = display.getHeight();

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.tweettopics_activity);

        fragmentAdapter = new TweetTopicsFragmentAdapter(this, getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TitlePageIndicator) findViewById(R.id.tweettopics_bar_indicator);
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
        indicator.setFooterLineHeight(0);
        indicator.setFooterColor(Color.WHITE);
        indicator.setClipPadding(-getWindowManager().getDefaultDisplay().getWidth());
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                reloadBarAvatar();
                if (i == 0) {
                    refreshMyActivity();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionBarColumns();
            }
        });

        layoutOptionsColumns = (LinearLayout) findViewById(R.id.tweettopics_ll_options_columns);
        layoutMainOptionsColumns = (LinearLayout) findViewById(R.id.tweettopics_ll_main_options_columns);
        layoutMainOptionsColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideOptionsColumns();
            }
        });
        btnOptionsColumnsMain = (Button) findViewById(R.id.tweettopics_ll_options_columns_btn_main);
        btnOptionsColumnsMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.valueOf(view.getTag().toString());
                Toast.makeText(TweetTopicsActivity.this, getString(R.string.column_main_message, fragmentAdapter.setColumnActive(pos)), Toast.LENGTH_LONG).show();
                hideOptionsColumns();
            }
        });
        btnOptionsColumnsDelete = (Button) findViewById(R.id.tweettopics_ll_options_columns_btn_delete);
        btnOptionsColumnsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.valueOf(view.getTag().toString());
                showDialogDeleteColumn(pos);
                hideOptionsColumns();
            }
        });

        // cargar el popup de enlaces

        FrameLayout root = ((FrameLayout) findViewById(R.id.tweettopics_root));
        popupLinks = new PopupLinks(this);
        popupLinks.loadPopup(root);

        splitActionBarMenu = new SplitActionBarMenu(this);
        splitActionBarMenu.loadSplitActionBarMenu(root);

        layoutBackgroundApp = (RelativeLayout) findViewById(R.id.tweettopics_layout_background_app);

        layoutBackgroundBar = (RelativeLayout) findViewById(R.id.tweettopics_bar_background);

        layoutBackgroundColumnsBarContainer = (LinearLayout) findViewById(R.id.tweettopics_bar_columns_container);

        layoutBackgroundColumnsBar = (DraggableHorizontalView) findViewById(R.id.tweettopics_bar_columns);
        layoutBackgroundColumnsBar.setCols(4);

        layoutBackgroundColumnsBar.setOnRearrangeListener(new OnRearrangeListener() {
            public void onRearrange(int oldIndex, int newIndex) {
                reorganizeColumns(oldIndex, newIndex);
            }

            @Override
            public void onStartDrag(int x, int index) {
                showOptionsColumns(x, index, true);
            }

            @Override
            public void onMoveDragged(int index) {
                hideOptionsColumns();
            }

        });
        layoutBackgroundColumnsBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                showActionBarIndicatorAndMovePager(position);
            }
        });

        imgBarAvatar = (ImageView) findViewById(R.id.tweettopics_bar_avatar);
        imgBarAvatarBg = (ImageView) findViewById(R.id.tweettopics_bar_avatar_bg);
        imgBarCounter = (TextView) findViewById(R.id.tweettopics_bar_counter);
        imgNewStatus = (ImageView) findViewById(R.id.tweettopics_bar_new_status);
        imgNewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStatus();
            }
        });

        imgBarAvatarGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                ((BaseListFragment)fragmentAdapter.instantiateItem(pager,pager.getCurrentItem())).goToTop();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                ((BaseListFragment)fragmentAdapter.instantiateItem(pager,pager.getCurrentItem())).goToTop();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed (MotionEvent e) {
                if (pager.getCurrentItem() > 0) {
                    animateDragged();
                }

                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        imgBarAvatarBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return imgBarAvatarGestureDetector.onTouchEvent(motionEvent);
            }
        });

        refreshTheme();

        reloadBarAvatar();

        refreshActionBarColumns();

        if (goToColumnType >= 0 && goToColumnUser >= 0) {
            if (goToColumnType == TweetTopicsUtils.COLUMN_TIMELINE
                    || goToColumnType == TweetTopicsUtils.COLUMN_MENTIONS
                    || goToColumnType == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
                createUserColumn(goToColumnUser, goToColumnType);
            }
        } else if (goToColumnType == TweetTopicsUtils.COLUMN_MY_ACTIVITY) {
        } else if (goToColumnPosition > 0) {
            goToColumn((int)goToColumnPosition, false, selectedTweetId);
        } else if (positionFromSensor>=0) {
            goToColumn(positionFromSensor, false, selectedTweetId);
        } else {
            int col = fragmentAdapter.getPositionColumnActive();
            if (col > 0) goToColumn(col, false, selectedTweetId);
        }

        // comprobar si hay que proponer ir al market

        int access_count = PreferenceUtils.getApplicationAccessCount(this);

        if (access_count <= 20) {
            if (access_count == 20) {
                try {
                    // TODO Cambiar este diálogo y ponerlo bien
                    AlertDialog dialog = DialogUtils.RateAppDialogBuilder.create(this);
                    dialog.show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            PreferenceUtils.setApplicationAccessCount(this, access_count + 1);
        }
    }

    protected void animateDragged() {
        View view = imgBarAvatar;
        float x = 0;
        try {
            imgBarAvatar.getX();
        } catch (NoSuchMethodError e) {}
        view.layout(imgBarAvatar.getLeft(), imgBarAvatar.getTop(), imgBarAvatar.getRight(), imgBarAvatar.getBottom());
        AnimationSet animSet = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, imgBarAvatar.getHeight() * 3 / 4, imgBarAvatar.getWidth() * 3 / 4);
        scale.setDuration(150);

        animSet.addAnimation(scale);
        animSet.setFillEnabled(true);
        animSet.setFillAfter(true);

        view.clearAnimation();
        view.startAnimation(animSet);

        showOptionsColumns((int) x, pager.getCurrentItem(), false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_NEWEDITSEARCH:

                if (data != null && data.getExtras() != null && data.getExtras().containsKey("view")) {
                    boolean create_column = data.getExtras().getBoolean("view", false);

                    if (create_column) {
                        final int count = DBUtils.nextPositionColumn();

                        Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_SEARCH);
                        Entity search = new Entity("columns");
                        search.setValue("description", type.getString("description"));
                        search.setValue("type_id", type);
                        search.setValue("position", count);
                        search.setValue("search_id", data.getLongExtra(DataFramework.KEY_ID, -1));
                        search.save();

                        goToColumn(count, true, -1);

                    } else {
                        getFragmentPagerAdapter().getMyActivityFragment().fillData();
                    }
                }

                break;
            case ACTIVITY_TRENDS_LOCATION:
                if (resultCode == Activity.RESULT_OK) {
                    goToColumn(data.getIntExtra("position", 0), true, -1);
                }
                break;
            case ACTIVITY_EDIT_SEARCH:

                if (data != null && data.getExtras() != null && data.getExtras().containsKey("view")) {
                    boolean view_column = data.getExtras().getBoolean("view", false);

                    if (view_column) {
                        Entity search_entity = new Entity("search", data.getLongExtra(DataFramework.KEY_ID, -1));
                        clickSearch(search_entity);
                    }
                }

                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowOptionsColumns()) {
                hideOptionsColumns();
                return false;
            }
            if (popupLinks.isShowLinks()) {
                popupLinks.hideLinks();
                return false;
            }
            if (splitActionBarMenu.isShowing()) {
                splitActionBarMenu.hideSplitActionBarMenu();
                return false;
            }
            if (isShowColumnsItems) {
                showActionBarIndicatorAndMovePager(-1);
                return false;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            goToColumn(0, false, -1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onShowLinks(View view, InfoTweet infoTweet) {
        popupLinks.showLinks(view, infoTweet);
    }

    @Override
    public void onShowSplitActionBarMenu(BaseListFragment fragment, InfoTweet infoTweet) {
        splitActionBarMenu.showSplitActionBarMenu(fragment, infoTweet, getUserOwnerCurrentColumn());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SAVE_STATE_COLUMN_POS, pager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    public ViewPager getViewPager() {
        return pager;
    }

    public TweetTopicsFragmentAdapter getFragmentPagerAdapter() {
        return fragmentAdapter;
    }

    /*
        ACTIONS
     */

    public void goToColumn(final int position, final boolean refreshBarColumn, final long selectedTweetId) {
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshBarColumn) {
                    refreshColumns();
                }
                pager.setCurrentItem(position, false);
                if (fragmentAdapter.instantiateItem(pager,pager.getCurrentItem()) instanceof BaseListFragment) {
                    ((BaseListFragment)fragmentAdapter.instantiateItem(pager,pager.getCurrentItem())).selected_tweet_id = selectedTweetId;
                }
            }
        }, 100);
    }

    public void refreshColumns() {
        fragmentAdapter.refreshColumnList();
        refreshActionBarColumns();
    }


    public void clickSearch(Entity clickSearch) {
        final ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "search_id=" + clickSearch.getId());

        if (created_column_list.size() == 0) {
            final int position = DBUtils.nextPositionColumn();

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_SEARCH);
            Entity search = new Entity("columns");
            search.setValue("description", type.getString("description"));
            search.setValue("type_id", type);
            search.setValue("position", position);
            search.setValue("search_id", clickSearch.getId());
            search.save();
            Toast.makeText(this, getString(R.string.column_created, clickSearch.getString("name")), Toast.LENGTH_LONG).show();

            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFragmentPagerAdapter().refreshColumnList();
                    getViewPager().setCurrentItem(position, false);
                }
            }, 100);
        } else {
            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getViewPager().setCurrentItem(created_column_list.get(0).getInt("position"), false);
                }
            }, 100);
        }
    }

    public void createUserColumn(long userId, int typeId) {

        ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "user_id=" + userId + " AND type_id = " + typeId);

        int position = 0;

        if (created_column_list.size() == 0) {
            position = DBUtils.nextPositionColumn();

            Entity user_list = new Entity("columns");
            user_list.setValue("type_id", typeId);
            user_list.setValue("position", position);
            user_list.setValue("user_id", userId);
            user_list.save();

            goToColumn(position, true, -1);

        } else {
            position = created_column_list.get(0).getInt("position");
            goToColumn(position, false, -1);
        }

    }

    public void deleteSearchInColumn(long id) {
        for (Entity entity : fragmentAdapter.getFragmentList()) {
            if (entity.getInt("search_id") == id) {
                entity.delete();
            }
        }
        fragmentAdapter.fillColumnList();
    }

    private boolean deleteColumn(final int position) {

        ArrayList<Entity> deleted_column = DataFramework.getInstance().getEntityList("columns", "position=" + position);

        boolean result = false;

        if (deleted_column.size() > 0) {

            result = deleted_column.get(0).delete();

            if (result) {
                DataFramework.getInstance().getDB().execSQL("UPDATE columns SET position=position-1 WHERE position>" + position);

                Handler myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentAdapter.refreshColumnList();
                        showActionBarIndicatorAndMovePager(pager.getCurrentItem());
                        refreshActionBarColumns();

                        /*if (pager.getCurrentItem() == position) {
                            if (position == fragmentAdapter.getCount())
                                pager.setCurrentItem(position - 1, false);
                            else
                                pager.setCurrentItem(position, false);
                        } */
                    }
                }, 100);
            }
        }

        return result;
    }

    public void editSearch(Entity search) {
        Intent edit_search = new Intent(this, SearchActivity.class);
        edit_search.putExtra(DataFramework.KEY_ID, search.getId());
        startActivityForResult(edit_search, ACTIVITY_EDIT_SEARCH);
    }

    public void newSearch() {
        Intent newsearch = new Intent(this, SearchActivity.class);
        startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void newTrending() {
        Intent trendslocation_intent = new Intent(this, TrendsLocationActivity.class);
        startActivityForResult(trendslocation_intent, TweetTopicsActivity.ACTIVITY_TRENDS_LOCATION);
    }

    public void newStatus() {
        Intent newstatus = new Intent(this, NewStatusActivity.class);
        newstatus.putExtra("start_user_id", getUserOwnerCurrentColumn());
        startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
    }

    public long getUserOwnerCurrentColumn() {
        return fragmentAdapter.getUserOwnerColumn(pager.getCurrentItem());
    }

    private Bitmap getThumb(String s) {
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Random random = new Random();
        paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
        paint.setTextSize(24);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawRect(new Rect(0, 0, 150, 150), paint);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(s, 75, 75, paint);

        return bmp;
    }


    public void refreshActionBarColumns() {

        layoutBackgroundColumnsBar.removeAllViews();

        for (int i = 0; i < fragmentAdapter.getFragmentList().size(); i++) {

            Button button = new Button(this);
            button.setPadding(0, (int)getResources().getDimension(R.dimen.default_padding), 0, 0);
            button.setTextSize(getResources().getDimension(R.dimen.text_size_horizontal_buttons));
            button.setCompoundDrawablePadding(2);
            button.setTextColor(themeManager.getColor("color_button_bar_title_column"));

            Bitmap bmp = fragmentAdapter.getButtonBigActionBar(i);

            if (bmp == null) {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            }

            bmp = ImageUtils.getBitmap(bmp, (int) getResources().getDimension(R.dimen.icon_columns_height));
            button.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(), bmp), null, null);
            button.setText(fragmentAdapter.getPageTitle(i));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setClickable(false);

            layoutBackgroundColumnsBar.addView(button);

        }
    }

    public void refreshMyActivity() {
        try {
            fragmentAdapter.getMyActivityFragment().fillData();
        } catch (NullPointerException e) {
        }
    }

    public void refreshTheme() {

        layoutBackgroundApp.setBackgroundColor(Color.parseColor("#" + themeManager.getStringColor("color_background_new_status")));

        themeManager.setColors();

        layoutBackgroundBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        layoutBackgroundColumnsBarContainer.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        StateListDrawable statesButtonBg = new StateListDrawable();
        statesButtonBg.addState(new int[]{android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_default"), false, 0));
        statesButtonBg.addState(new int[]{-android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        imgBarAvatarBg.setBackgroundDrawable(statesButtonBg);

        StateListDrawable statesButton = new StateListDrawable();
        statesButton.addState(new int[]{android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_default"), false, 0));
        statesButton.addState(new int[]{-android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        imgNewStatus.setBackgroundDrawable(statesButton);

        float size = getResources().getDimension(R.dimen.actionbar_height) - Utils.dip2px(this, 12);
        imgBarCounter.setBackgroundDrawable(new BitmapDrawable(getResources(), ImageUtils.getBackgroundBitmapInBubble(this, Color.RED, Utils.TYPE_RECTANGLE, size, size)));

        //(findViewById(R.id.tweettopics_bar_divider1)).setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        //(findViewById(R.id.tweettopics_bar_divider2)).setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        Drawable d = new ColorDrawable(android.R.color.transparent);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, d);
        states.addState(new int[]{android.R.attr.state_window_focused}, d);
        states.addState(new int[]{android.R.attr.state_pressed}, d);
        states.addState(new int[]{android.R.attr.state_selected}, d);
        states.addState(new int[]{android.R.attr.color}, new ColorDrawable(themeManager.getColor("color_indicator_text")));
        indicator.setBackgroundDrawable(states);
        indicator.setTextSize(getResources().getDimension(R.dimen.text_size_title_page_indicator));
    }

    public void reloadBarAvatar() {
        int position = pager.getCurrentItem();
        int column_type = fragmentAdapter.getFragmentList().get(position).getInt("type_id");
        int tweets_count = 0;
        if (column_type == TweetTopicsUtils.COLUMN_TIMELINE ||
                column_type == TweetTopicsUtils.COLUMN_MENTIONS ||
                column_type == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
            tweets_count = DBUtils.getUnreadTweetsUser(column_type,
                    fragmentAdapter.getFragmentList().get(position).getEntity("user_id").getId());
        }
        if (column_type == TweetTopicsUtils.COLUMN_SEARCH) {
            tweets_count = DBUtils.getUnreadTweetsSearch(
                    fragmentAdapter.getFragmentList().get(position).getEntity("search_id").getId());
        }
        if (tweets_count > 0) {
            imgBarAvatar.setVisibility(View.GONE);
            imgBarCounter.setVisibility(View.VISIBLE);
            imgBarCounter.setText(tweets_count + "");
        } else {
            imgBarAvatar.setVisibility(View.VISIBLE);
            imgBarCounter.setVisibility(View.GONE);
            imgBarAvatar.setImageBitmap(fragmentAdapter.getIconItem(pager.getCurrentItem()));
        }
    }

    private void reorganizeColumns(final int starting_position, final int ending_position) {

        ArrayList<Entity> moved_column = DataFramework.getInstance().getEntityList("columns", "position=" + starting_position);

        if (moved_column.size() > 0) {
            if (starting_position > ending_position) {
                DataFramework.getInstance().getDB().execSQL("UPDATE columns SET position=position+1 WHERE position BETWEEN " + ending_position + " AND " + (starting_position - 1));
            } else if (starting_position < ending_position) {
                DataFramework.getInstance().getDB().execSQL("UPDATE columns SET position=position-1 WHERE position BETWEEN " + (starting_position + 1) + " AND " + ending_position);
            }

            boolean result = false;

            moved_column.get(0).setValue("position", ending_position);
            result = moved_column.get(0).save();

            if (result) {
                Handler myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TweetTopics2.0", "Eliminando las columnas");
                        fragmentAdapter.refreshColumnList();
                    }
                }, 100);
            }
        }
    }

    public void showActionBarColumns() {
        refreshActionBarColumns();
        isShowColumnsItems = true;

//        int left = layoutBackgroundColumnsBar.getChildAt(pager.getCurrentItem()).getLeft();
//        int top = layoutBackgroundColumnsBar.getChildAt(pager.getCurrentItem()).getTop();

        layoutBackgroundColumnsBar.scrollToView(pager.getCurrentItem());

        layoutBackgroundColumnsBarContainer.setVisibility(View.VISIBLE);

        /*
        ValueAnimator moveMargins = ValueAnimator.ofFloat(getResources().getDimension(R.dimen.actionbar_height), getResources().getDimension(R.dimen.actionbar_columns_height));
        moveMargins.setDuration(250);
        moveMargins.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                float value = (Float)valueAnimator.getAnimatedValue();
                params.setMargins(0,(int)value, 0,0);
                pager.setLayoutParams(params);
            }
        });
        */

        int distance = (int) getResources().getDimension(R.dimen.actionbar_columns_height) - (int) getResources().getDimension(R.dimen.actionbar_height);

        ObjectAnimator translationPager = ObjectAnimator.ofFloat(pager, "translationY", 0f, distance);
        translationPager.setDuration(250);

        ObjectAnimator translationOut = ObjectAnimator.ofFloat(layoutBackgroundBar, "translationY", 0f, -getResources().getDimension(R.dimen.actionbar_height));
        translationOut.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationOut, translationPager);
        animatorSet.start();
    }

    public void showActionBarIndicatorAndMovePager(final int pos) {
        isShowColumnsItems = false;

        ObjectAnimator translationIn = ObjectAnimator.ofFloat(layoutBackgroundBar, "translationY", -getResources().getDimension(R.dimen.actionbar_height), 0f);
        translationIn.setDuration(250);
        /*
        ValueAnimator moveMargins = ValueAnimator.ofFloat(getResources().getDimension(R.dimen.actionbar_columns_height), (int)getResources().getDimension(R.dimen.actionbar_height));
        moveMargins.setDuration(250);
        moveMargins.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                float value = (Float)valueAnimator.getAnimatedValue();
                params.setMargins(0,(int)value,0,0);
                pager.setLayoutParams(params);
            }
        });
         */

        int distance = (int) getResources().getDimension(R.dimen.actionbar_columns_height) - (int) getResources().getDimension(R.dimen.actionbar_height);

        ObjectAnimator translationPager = ObjectAnimator.ofFloat(pager, "translationY", distance, 0f);
        translationPager.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationPager, translationIn);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutBackgroundColumnsBarContainer.setVisibility(View.GONE);
                if (pos >= 0) {
                    goToColumn(pos, false, -1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animatorSet.start();

    }

    /*
       SHOW OPTIONS COMLUMNS
    */

    public void showOptionsColumns(int positionX, int index, boolean action_bar_opened) {


        int x = positionX - (layoutOptionsColumns.getWidth() / 2);
        if (x < 0) x = 0;
        if (x > widthScreen - layoutOptionsColumns.getWidth()) x = widthScreen - layoutOptionsColumns.getWidth();
        int y = -1;

        if (action_bar_opened)
            y = (int) getResources().getDimension(R.dimen.actionbar_columns_height) - Utils.dip2px(this, 20);
        else
            y = (int) getResources().getDimension(R.dimen.actionbar_height) - Utils.dip2px(this, 20);

        int xCenterView = x;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(x, y, 0, 0);
        layoutOptionsColumns.setLayoutParams(params);

        layoutMainOptionsColumns.setVisibility(View.VISIBLE);

        btnOptionsColumnsMain.setTag(index);
        btnOptionsColumnsDelete.setTag(index);

        ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutOptionsColumns, "translationX", xCenterView - x, 0f);
        translationX.setDuration(150);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleX", 0f, 1f);
        scaleX.setDuration(150);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleY", 0f, 1f);
        scaleY.setDuration(150);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutOptionsColumns, "alpha", 0f, 1f);
        fadeAnim.setDuration(150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, scaleX, scaleY, fadeAnim);
        animatorSet.start();

    }

    public void hideOptionsColumns() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleX", 1f, 0f);
        scaleX.setDuration(150);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleY", 1f, 0f);
        scaleY.setDuration(150);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutOptionsColumns, "alpha", 1f, 0f);
        fadeAnim.setDuration(150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutMainOptionsColumns.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();

    }

    public boolean isShowOptionsColumns() {
        return layoutMainOptionsColumns.getVisibility() == View.VISIBLE;
    }

    /*
        DIALOGS
     */


    private void showDialogDeleteColumn(final int position) {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.delete);
        args.putInt(AlertDialogFragment.KEY_ALERT_MESSAGE, R.string.column_delete);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_NEGATIVE_BUTTON, true);
        frag.setArguments(args);
        frag.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
            @Override
            public void OnAlertButtonOk() {
                if (deleteColumn(position))
                    Toast.makeText(TweetTopicsActivity.this, R.string.column_delete_ok, Toast.LENGTH_LONG).show();
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
        frag.show(getSupportFragmentManager(), "dialog");
    }
}
