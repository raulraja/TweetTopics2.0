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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.components.DraggableHorizontalView;
import com.javielinux.components.OnRearrangeListener;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.dialogs.HashTagDialogFragment;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.javielinux.infos.InfoTweet;
import com.javielinux.notifications.OnAlarmReceiver;
import com.javielinux.utils.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.viewpagerindicator.TitlePageIndicator;
import preferences.Preferences;

import java.util.ArrayList;
import java.util.Random;

public class TweetTopicsActivity extends BaseActivity {

    public static final String KEY_EXTRAS_GOTO_COLUMN_USER = "KEY_EXTRAS_GOTO_COLUMN_USER";
    public static final String KEY_EXTRAS_GOTO_COLUMN_TYPE = "KEY_EXTRAS_GOTO_COLUMN_TYPE";

    protected static final int PREFERENCES_ID = Menu.FIRST;
    protected static final int EXIT_ID = Menu.FIRST + 1;
    protected static final int SIZE_TEXT_ID = Menu.FIRST + 2;

    public static final int ACTIVITY_NEWEDITSEARCH = 0;
    public static final int ACTIVITY_PREFERENCES = 1;
    public static final int ACTIVITY_NEWSTATUS = 2;
    public static final int ACTIVITY_TRENDS_LOCATION = 3;

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
    private ImageView imgNewStatus;

    private LinearLayout layoutOptionsColumns;
    private LinearLayout layoutMainOptionsColumns;
    private Button btnOptionsColumnsMain;
    private Button btnOptionsColumnsDelete;

    private LinearLayout layoutLinks;
    private LinearLayout layoutMainLinks;
    private GridView gvLinks;
    private LinksAdapter linksAdapter;
    private ArrayList<String> links = new ArrayList<String>();

    private int statusBarHeight;
    private int widthScreen;
    private int heightScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnAlarmReceiver.callAlarm(this);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PreferenceUtils.getFinishForceClose(this)){
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

        int goToColumnType = -1;
        long goToColumnUser = -1;

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey(KEY_EXTRAS_GOTO_COLUMN_TYPE)) {
                goToColumnType = extras.getInt(KEY_EXTRAS_GOTO_COLUMN_TYPE);
            }
            if (extras.containsKey(KEY_EXTRAS_GOTO_COLUMN_USER)) {
                goToColumnUser = extras.getLong(KEY_EXTRAS_GOTO_COLUMN_USER);
            }
        }

        Utils.setActivity(this);

        Display display = getWindowManager().getDefaultDisplay();
        widthScreen = display.getWidth();
        heightScreen = display.getHeight();

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.tweettopics_activity);

        fragmentAdapter = new TweetTopicsFragmentAdapter(this, getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TitlePageIndicator)findViewById(R.id.tweettopics_bar_indicator);
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
                if (i==0) {
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
                Toast.makeText(TweetTopicsActivity.this,getString(R.string.column_main_message, fragmentAdapter.setColumnActive(pos)),Toast.LENGTH_LONG).show();
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

        layoutMainLinks = (LinearLayout) findViewById(R.id.tweettopics_ll_main_links);
        layoutMainLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLinks();
            }
        });

        layoutLinks = (LinearLayout) findViewById(R.id.tweettopics_ll_links);

        linksAdapter = new LinksAdapter(this, getSupportLoaderManager(), links);
        gvLinks = (GridView) findViewById(R.id.tweettopics_gv_links);
        gvLinks.setAdapter(linksAdapter);
        gvLinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToLink(links.get(i));
            }
        });

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
                showOptionsColumns(x, index);
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
        imgNewStatus = (ImageView) findViewById(R.id.tweettopics_bar_new_status);
        imgNewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStatus();
            }
        });

        refreshTheme();

        reloadBarAvatar();

        refreshActionBarColumns();

        if (goToColumnType>=0 && goToColumnUser>=0) {
            if (goToColumnType==TweetTopicsUtils.COLUMN_TIMELINE
                    || goToColumnType==TweetTopicsUtils.COLUMN_MENTIONS
                    || goToColumnType==TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
                createUserColumn(goToColumnUser, goToColumnType);
            }
        } else {
            int col = fragmentAdapter.getPositionColumnActive();
            if (col>0) goToColumn(col, false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_NEWEDITSEARCH:

                if (data != null && data.getExtras()!=null && data.getExtras().containsKey("view")) {
                    boolean create_column = data.getExtras().getBoolean("view", false);

                    if (create_column) {
                        final int count = DataFramework.getInstance().getEntityListCount("columns", "") + 1;

                        Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_SEARCH);
                        Entity search = new Entity("columns");
                        search.setValue("description", type.getString("description"));
                        search.setValue("type_id", type);
                        search.setValue("position", count);
                        search.setValue("search_id", data.getLongExtra(DataFramework.KEY_ID, -1));
                        search.save();

                        goToColumn(count, true);

                    }
                }

                break;
            case ACTIVITY_TRENDS_LOCATION:
                if (resultCode == Activity.RESULT_OK) {
                    goToColumn(data.getIntExtra("position", 0), true);
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SIZE_TEXT_ID, 0, R.string.size)
                .setIcon(R.drawable.ic_menu_font_size);
        menu.add(0, PREFERENCES_ID, 0, R.string.preferences)
                .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, EXIT_ID, 0, R.string.exit)
                .setIcon(android.R.drawable.ic_menu_revert);
        return true;
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
            if (isShowLinks()) {
                hideLinks();
                return false;
            }
            if (isShowColumnsItems) {
                showActionBarIndicatorAndMovePager(-1);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case SIZE_TEXT_ID:
                showSizeText();
                return true;
            case PREFERENCES_ID:
                Intent i = new Intent(this, Preferences.class);
                startActivityForResult(i, ACTIVITY_PREFERENCES);
                return true;
            case EXIT_ID:
                showDialogExit();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void goToColumn(final int position, final boolean refreshBarColumn) {
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshBarColumn) {
                    fragmentAdapter.refreshColumnList();
                    refreshActionBarColumns();
                }
                pager.setCurrentItem(position, false);
            }
        }, 100);
    }

    public void createUserColumn(long userId, int typeId) {

        ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "user_id=" + userId + " AND type_id = " + typeId);

        int position = 0;

        if (created_column_list.size() == 0) {
            position = DataFramework.getInstance().getEntityListCount("columns", "") + 1;

            Entity user_list = new Entity("columns");
            user_list.setValue("type_id", typeId);
            user_list.setValue("position", position);
            user_list.setValue("user_id", userId);
            user_list.save();

            goToColumn(position, true);

        } else {
            position = created_column_list.get(0).getInt("position");
            goToColumn(position, false);
        }

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


    public void newSearch() {
        Intent newsearch = new Intent(this, SearchActivity.class);
        startActivity(newsearch);
        //Intent newsearch = new Intent(this, TabNewEditSearch.class);
        //startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void newTrending() {
        Intent trendslocation_intent = new Intent(this, TrendsLocationActivity.class);
        startActivityForResult(trendslocation_intent, TweetTopicsActivity.ACTIVITY_TRENDS_LOCATION);
    }

    public void newStatus() {
        Intent newstatus = new Intent(this, NewStatusActivity.class);
        startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
    }

    private Bitmap getThumb(String s)
    {
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

        for (int i=0; i<fragmentAdapter.getFragmentList().size(); i++) {
            Button button = new Button(this);
            button.setTextSize(11);
            button.setCompoundDrawablePadding(2);
            button.setTextColor(themeManager.getColor("color_button_bar_title_column"));
            Bitmap bmp = fragmentAdapter.getIconItem(i);
            if (bmp==null) {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            }
            bmp = Utils.getBitmap(bmp, Utils.dip2px(this, 52));
            button.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(bmp), null, null);
            button.setText(fragmentAdapter.getPageTitle(i));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setClickable(false);

            layoutBackgroundColumnsBar.addView(button);
            /*
            ImageView separator = new ImageView(this);
            separator.setBackgroundColor(Color.parseColor("#44000000"));
            layoutBackgroundColumnsItems.addView(separator, new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.FILL_PARENT));
            */
        }
    }

    public void refreshMyActivity() {
        try {
            fragmentAdapter.getMyActivityFragment().fillData();
        } catch (NullPointerException e) {}
    }

    public void refreshTheme() {

        layoutBackgroundApp.setBackgroundColor(Color.parseColor("#"+themeManager.getStringColor("color_background_new_status")));

        themeManager.setColors();

        layoutBackgroundBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        layoutBackgroundColumnsBarContainer.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        StateListDrawable statesButton = new StateListDrawable();
        statesButton.addState(new int[] {android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_default"), false, 0));
        statesButton.addState(new int[] {-android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        imgBarAvatar.setBackgroundDrawable(statesButton);
        imgNewStatus.setBackgroundDrawable(statesButton);


        //(findViewById(R.id.tweettopics_bar_divider1)).setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        //(findViewById(R.id.tweettopics_bar_divider2)).setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        Drawable d = new ColorDrawable(android.R.color.transparent);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed}, d);
        states.addState(new int[] {android.R.attr.state_window_focused}, d);
        states.addState(new int[] {android.R.attr.state_pressed}, d);
        states.addState(new int[] {android.R.attr.state_selected}, d);
        states.addState(new int[] {android.R.attr.color}, new ColorDrawable(themeManager.getColor("color_indicator_text")));
        indicator.setBackgroundDrawable(states);
        indicator.setTextSize(26);
    }

    private void reloadBarAvatar() {
        imgBarAvatar.setImageBitmap(fragmentAdapter.getIconItem(pager.getCurrentItem()));
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
                        Log.d("TweetTopics2.0","Eliminando las columnas");
                        fragmentAdapter.refreshColumnList();
                    }
                }, 100);
            }
        }
    }

    public void showActionBarColumns() {
        isShowColumnsItems = true;

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

        int distance = (int)getResources().getDimension(R.dimen.actionbar_columns_height) - (int)getResources().getDimension(R.dimen.actionbar_height);

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

        int distance = (int)getResources().getDimension(R.dimen.actionbar_columns_height) - (int)getResources().getDimension(R.dimen.actionbar_height);

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
                if (pos>=0) {
                    goToColumn(pos, false);
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

    public void showOptionsColumns(int positionX, int index) {


        int x = positionX - (layoutOptionsColumns.getWidth()/2);
        if (x<0) x = 0;
        if (x>widthScreen-layoutOptionsColumns.getWidth()) x = widthScreen-layoutOptionsColumns.getWidth();
        int y = (int)getResources().getDimension(R.dimen.actionbar_columns_height) - Utils.dip2px(this, 20);

        int xCenterView = x;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(x, y, 0, 0);
        layoutOptionsColumns.setLayoutParams(params);

        layoutMainOptionsColumns.setVisibility(View.VISIBLE);

        btnOptionsColumnsMain.setTag(index);
        btnOptionsColumnsDelete.setTag(index);

        ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutOptionsColumns, "translationX", xCenterView-x, 0f);
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
        return layoutMainOptionsColumns.getVisibility()==View.VISIBLE;
    }

    /*
        SHOW LINKS
     */

    public void showLinks(View view, InfoTweet infoTweet) {

        ArrayList<String> linksInText = LinksUtils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs());

        if (linksInText.size()==1) {
            goToLink(linksInText.get(0));
        } else {

            int widthContainer = widthScreen;
            int heightContainer = heightScreen;

            links.clear();
            links.addAll(linksInText);

            int rows = 0;

            if (links.size()>4) {
                rows = links.size()/3;
                if (links.size()%3>0) rows++;
                gvLinks.setNumColumns(3);
                widthContainer = (widthScreen/4)*3 + Utils.dip2px(this,40);
            } else {
                rows = links.size()/2;
                if (links.size()%2>0) rows++;
                gvLinks.setNumColumns(2);
                widthContainer = (widthScreen/4)*2 + Utils.dip2px(this,30);
            }
            if (rows==1) {
                heightContainer = Utils.dip2px(this,110);
            } else {
                heightContainer = Utils.dip2px(this,100) * rows;
            }

            linksAdapter.notifyDataSetChanged();

            if (statusBarHeight<=0) {
                Rect rect= new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                statusBarHeight= rect.top;
            }
            int[] loc = new int[2];
            view.getLocationOnScreen(loc);

            int widthView = view.getMeasuredWidth();
            int heightView = view.getMeasuredHeight();

            int x = loc[0] + (widthView/2) - (widthContainer/2);
            int y = loc[1] - statusBarHeight + (heightView/2) - (heightContainer/2);

            int xCenterView = loc[0] + (widthView/2);
            int yCenterView = loc[1] - statusBarHeight + (heightView/2);

            int top = (int)getResources().getDimension(R.dimen.actionbar_height);
            int bottom = heightScreen-statusBarHeight;

            if (x<0) x = 0;
            if (y<top) y = top;
            if (x>widthScreen-widthContainer) x = widthScreen-widthContainer;
            if (y>bottom-heightContainer) y = bottom-heightContainer;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(x, y, 0, 0);
            layoutLinks.setLayoutParams(params);

            layoutMainLinks.setVisibility(View.VISIBLE);

            ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutLinks, "translationX", xCenterView-x, 0f);
            translationX.setDuration(150);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutLinks, "scaleX", 0f, 1f);
            scaleX.setDuration(150);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutLinks, "scaleY", 0f, 1f);
            scaleY.setDuration(150);
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutLinks, "alpha", 0f, 1f);
            fadeAnim.setDuration(150);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translationX, scaleX, scaleY, fadeAnim);
            animatorSet.start();
        }
    }

    public void goToLink(String link) {
//        if (CacheData.getCacheImages().containsKey(link)) {
//            CacheData.getCacheImages().get(link);
//        } else {
//
//        }
        if (isShowLinks()) hideLinks();
        if (link.startsWith("@")) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserActivity.KEY_EXTRAS_USER, link);
            startActivity(intent);
        } else if (link.startsWith("#")) {
            HashTagDialogFragment frag = new HashTagDialogFragment();
            Bundle args = new Bundle();
            args.putString("hashtag", link);
            frag.setArguments(args);
            frag.show(getSupportFragmentManager(), "dialog");
        } else {
            if (link.startsWith("www")) {
                link = "http://"+link;
            }
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void hideLinks() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutLinks, "scaleX", 1f, 0f);
        scaleX.setDuration(150);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutLinks, "scaleY", 1f, 0f);
        scaleY.setDuration(150);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutLinks, "alpha", 1f, 0f);
        fadeAnim.setDuration(150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutMainLinks.setVisibility(View.INVISIBLE);
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

    public boolean isShowLinks() {
        return layoutMainLinks.getVisibility()==View.VISIBLE;
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
                    Toast.makeText(TweetTopicsActivity.this, R.string.column_delete_ok,Toast.LENGTH_LONG).show();
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

    private void showDialogExit() {

        int minutes = Integer.parseInt(Utils.getPreference(this).getString("prf_time_notifications", "15"));

        if (minutes > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_exit);
            builder.setMessage(R.string.dialog_exit_msg);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    PreferenceUtils.saveNotificationsApp(TweetTopicsActivity.this, false);
                    TweetTopicsActivity.this.finish();
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        } else {
            TweetTopicsActivity.this.finish();
        }
    }

    public void showSizeText() {

        final int minValue = 6;

        LayoutInflater factory = LayoutInflater.from(this);
        final View sizesFontView = factory.inflate(R.layout.alert_dialog_sizes_font, null);

        ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(this) + ")");
        ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(this) + ")");

        SeekBar sbSizeTitles = (SeekBar) sizesFontView.findViewById(R.id.sb_size_titles);

        sbSizeTitles.setMax(18);
        sbSizeTitles.setProgress(PreferenceUtils.getSizeTitles(this) - minValue);

        sbSizeTitles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeTitles(TweetTopicsActivity.this, progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(TweetTopicsActivity.this.getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(TweetTopicsActivity.this) + ")");
                // TODO notificar al adapter el cambio de texto
                //mAdapterResponseList.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        SeekBar sbSizeText = (SeekBar) sizesFontView.findViewById(R.id.sb_size_text);
        sbSizeText.setMax(18);
        sbSizeText.setProgress(PreferenceUtils.getSizeText(this) - minValue);

        sbSizeText.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeText(TweetTopicsActivity.this, progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(TweetTopicsActivity.this.getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(TweetTopicsActivity.this) + ")");
                // TODO notificar al adapter el cambio de texto
                //mAdapterResponseList.notifyDataSetChanged();
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
}
