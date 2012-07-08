package com.javielinux.tweettopics2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.dialogs.HashTagDialogFragment;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.viewpagerindicator.TitlePageIndicator;
import infos.InfoTweet;
import preferences.Preferences;

import java.util.ArrayList;

public class TweetTopicsActivity extends BaseActivity {

    protected static final int NEW_ID = Menu.FIRST;
    protected static final int PREFERENCES_ID = Menu.FIRST + 1;
    protected static final int EXIT_ID = Menu.FIRST + 2;
    protected static final int MANAGER_USER_ID = Menu.FIRST + 3;
    protected static final int SIZE_TEXT_ID = Menu.FIRST + 4;

    public static final int ACTIVITY_NEWEDITSEARCH = 0;
    public static final int ACTIVITY_PREFERENCES = 1;
    public static final int ACTIVITY_NEWSTATUS = 2;
    public static final int ACTIVITY_USER = 3;
    public static final int ACTIVITY_WALLPAPER = 4;
    public static final int ACTIVITY_COLORS_APP = 5;

    private ViewPager pager;
    private TweetTopicsFragmentAdapter fragmentAdapter;
    private TitlePageIndicator indicator;
    private ThemeManager themeManager;
    private RelativeLayout layoutBackgroundApp;

    private RelativeLayout layoutBackgroundBar;
    private HorizontalScrollView layoutBackgroundColumnsBar;
    private LinearLayout layoutBackgroundColumnsItems;
    private boolean isShowColumnsItems = false;

    private ImageView imgBarAvatar;
    private ImageView imgNewStatus;

    private LinearLayout layoutLinks;
    private LinearLayout layoutMainLinks;
    private GridView gvLinks;
    private LinksAdapter linksAdapter;
    private ArrayList<String> links = new ArrayList<String>();

    private int statusBarHeight;
    private int widthScreen;
    private int heightScreen;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_NEWEDITSEARCH:
                boolean create_column = data.getBooleanExtra("view", false);

                if (create_column) {
                    int count = DataFramework.getInstance().getEntityListCount("columns", "") + 1;

                    Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_SEARCH);
                    Entity search = new Entity("columns");
                    search.setValue("description", type.getString("description"));
                    search.setValue("type_id", type);
                    search.setValue("position", count);
                    search.setValue("search_id", data.getLongExtra(DataFramework.KEY_ID, -1));
                    search.save();
                }

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, NEW_ID, 0, R.string.new_search)
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, SIZE_TEXT_ID, 0, R.string.size)
                .setIcon(R.drawable.ic_menu_font_size);
        menu.add(0, PREFERENCES_ID, 0, R.string.preferences)
                .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, EXIT_ID, 0, R.string.exit)
                .setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, MANAGER_USER_ID, 0, R.string.manager_user);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case NEW_ID:
                newSearch();
                return true;
            case SIZE_TEXT_ID:
                showSizeText();
                return true;
            case MANAGER_USER_ID:
                newUser();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
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

        layoutBackgroundColumnsBar = (HorizontalScrollView) findViewById(R.id.tweettopics_bar_columns);
        layoutBackgroundColumnsItems = (LinearLayout) findViewById(R.id.tweettopics_bar_columns_items);

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

    }

    public void refreshActionBarColumns() {
        layoutBackgroundColumnsItems.removeAllViews();
        for (int i=0; i<fragmentAdapter.getFragmentList().size(); i++) {
            View view = View.inflate(this, R.layout.row_actionbar_column, null);
            Bitmap bmp = fragmentAdapter.getIconItem(i);
            if (bmp!=null) {
                ((ImageView)view.findViewById(R.id.row_actionbar_column_img)).setImageBitmap(bmp);
            } else {
                ((ImageView)view.findViewById(R.id.row_actionbar_column_img)).setImageResource(R.drawable.icon);
            }
            ((TextView)view.findViewById(R.id.row_actionbar_column_title)).setText(fragmentAdapter.getPageTitle(i));
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showActionBarIndicatorAndMovePager((Integer) view.getTag());
                }
            });
            layoutBackgroundColumnsItems.addView(view);

            ImageView separator = new ImageView(this);
            separator.setBackgroundColor(Color.parseColor("#44000000"));
            layoutBackgroundColumnsItems.addView(separator, new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.FILL_PARENT));

        }
    }

    public void showActionBarColumns() {
        isShowColumnsItems = true;

        layoutBackgroundColumnsBar.setVisibility(View.VISIBLE);
        layoutBackgroundColumnsItems.setVisibility(View.VISIBLE);

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

        ObjectAnimator translationOut = ObjectAnimator.ofFloat(layoutBackgroundBar, "translationY", 0f, -getResources().getDimension(R.dimen.actionbar_height));
        translationOut.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationOut, moveMargins);
        animatorSet.start();


    }

    public void showActionBarIndicatorAndMovePager(final int pos) {
        isShowColumnsItems = false;

        ObjectAnimator translationIn = ObjectAnimator.ofFloat(layoutBackgroundBar, "translationY", -getResources().getDimension(R.dimen.actionbar_height), 0f);
        translationIn.setDuration(250);

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

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveMargins, translationIn);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutBackgroundColumnsBar.setVisibility(View.GONE);
                layoutBackgroundColumnsItems.setVisibility(View.GONE);
                if (pos>=0) pager.setCurrentItem(pos, true);
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

    public void refreshMyActivity() {
        fragmentAdapter.getMyActivityFragment().fillData();
    }

    public boolean isShowLinks() {
        return layoutMainLinks.getVisibility()==View.VISIBLE;
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
            args.putInt("title", R.string.actions);
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

    public void showLinks(View view, InfoTweet infoTweet) {

        ArrayList<String> linksInText = Utils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs());

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

    private void reloadBarAvatar() {
        imgBarAvatar.setImageBitmap(fragmentAdapter.getIconItem(pager.getCurrentItem()));
    }

    public void refreshTheme() {

        layoutBackgroundApp.setBackgroundColor(Color.parseColor("#"+themeManager.getStringColor("color_background_new_status")));

        themeManager.setColors();

        layoutBackgroundBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));
        layoutBackgroundColumnsBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        StateListDrawable statesButton = new StateListDrawable();
        statesButton.addState(new int[] {android.R.attr.state_pressed}, ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_button_press_bar"), false, 0));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Entity user = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (user == null) {
            newUser();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
             if (isShowColumnsItems) {
                 showActionBarIndicatorAndMovePager(-1);
                 return false;
             }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    public void newSearch() {
        Intent newsearch = new Intent(this, TabNewEditSearch.class);
        startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void newUser() {
        Intent newuser = new Intent(this, Users.class);
        startActivityForResult(newuser, ACTIVITY_USER);
    }

    public void newStatus() {
        Intent newstatus = new Intent(this, NewStatusActivity.class);
        startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
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
