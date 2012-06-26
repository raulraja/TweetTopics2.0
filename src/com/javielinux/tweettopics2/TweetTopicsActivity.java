package com.javielinux.tweettopics2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
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
    private LinearLayout layoutBackgroundApp;

    private RelativeLayout layoutBackgroundBar;

    private ImageView imgBarAvatar;
    private ImageView imgNewStatus;

    private LinearLayout layoutLinks;
    private LinearLayout layoutMainLinks;
    private GridView gvLinks;
    private LinksAdapter linksAdapter;
    private ArrayList<String> links = new ArrayList<String>();

    private int statusBarHeight;

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

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.tweettopics_activity);

        fragmentAdapter = new TweetTopicsFragmentAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TitlePageIndicator)findViewById(R.id.tweettopics_bar_indicator);
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
        indicator.setFooterLineHeight(0);
        indicator.setFooterColor(Color.WHITE);
        indicator.setClipPadding(-50);
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

        layoutMainLinks = (LinearLayout) findViewById(R.id.tweettopics_ll_main_links);
        layoutMainLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLinks();
            }
        });

        layoutLinks = (LinearLayout) findViewById(R.id.tweettopics_ll_links);

        linksAdapter = new LinksAdapter(this, links);
        gvLinks = (GridView) findViewById(R.id.tweettopics_gv_links);
        gvLinks.setAdapter(linksAdapter);

        layoutBackgroundApp = (LinearLayout) findViewById(R.id.tweettopics_layout_background_app);

        layoutBackgroundBar = (RelativeLayout) findViewById(R.id.tweettopics_bar_background);

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
        Toast.makeText(this,link,Toast.LENGTH_LONG).show();
    }

    public void hideLinks() {
        layoutMainLinks.setVisibility(View.INVISIBLE);
    }

    public void showLinks(View view, InfoTweet infoTweet) {

        ArrayList<String> linksInText = Utils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs());

        if (linksInText.size()==1) {
            goToLink(linksInText.get(0));
        } else {

            links.clear();
            links.addAll(linksInText);

            if (links.size()>4) {
                gvLinks.setNumColumns(3);
            } else {
                gvLinks.setNumColumns(2);
            }

            linksAdapter.notifyDataSetChanged();

            Display display = getWindowManager().getDefaultDisplay();
            int widthScreen = display.getWidth();
            int heightScreen = display.getHeight();

            if (statusBarHeight<=0) {
                Rect rect= new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                statusBarHeight= rect.top;
            }
            int[] loc = new int[2];
            view.getLocationOnScreen(loc);

            int widthView = view.getMeasuredWidth();
            int heightView = view.getMeasuredHeight();

            int widthContainer = layoutLinks.getMeasuredWidth();
            int heightContainer = layoutLinks.getMeasuredHeight()-statusBarHeight;

            int x = loc[0] + (widthView/2) - (widthContainer/2);
            int y = loc[1] - statusBarHeight + (heightView/2) - (heightContainer/2);

            if (x<0) x = 0;
            if (y<0) y = 0;
            if (x>widthScreen-widthContainer) x = widthScreen-widthContainer;
            if (y>heightScreen-heightContainer) y = heightScreen-heightContainer;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(x, y, 0, 0);
            layoutLinks.setLayoutParams(params);
            layoutMainLinks.setVisibility(View.VISIBLE);

        }
    }

    private void reloadBarAvatar() {
        imgBarAvatar.setImageBitmap(fragmentAdapter.getIconItem(pager.getCurrentItem()));
    }

    public void refreshTheme() {

        layoutBackgroundApp.setBackgroundColor(Color.parseColor("#"+themeManager.getStringColor("color_background_new_status")));

        themeManager.setColors();

        layoutBackgroundBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

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
