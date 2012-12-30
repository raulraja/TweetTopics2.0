/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.androidquery.AQuery;
import com.google.android.maps.MapView;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadImageWidgetRequest;
import com.javielinux.api.request.LoadTranslateTweetRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadImageWidgetResponse;
import com.javielinux.api.response.LoadTranslateTweetResponse;
import com.javielinux.components.ImageViewZoomTouch;
import com.javielinux.dialogs.HashTagDialogFragment;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.javielinux.fragments.BaseListFragment;
import com.javielinux.infos.InfoLink;
import com.javielinux.infos.InfoTweet;
import com.javielinux.utils.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.viewpagerindicator.TabPageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class TweetActivity extends BaseLayersActivity implements APIDelegate<BaseResponse>, PopupLinks.PopupLinksListener, SplitActionBarMenu.SplitActionBarMenuListener {

    public static final String KEY_EXTRAS_TWEET = "tweet";
    public static final String KEY_EXTRAS_LINK = "link";

    private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;
    private InfoTweet infoTweet;

    private ImageView imgAvatar;
    private TextView txtUsername;
    private TextView txtDate;
    private TextView txtText;

    private FrameLayout llRoot;
    private LinearLayout tweetInfoLayout;
    private ImageViewZoomTouch ivImageLarger;
    private RelativeLayout tweetContent;
    private LinearLayout viewLoading;
    private LinearLayout tweetActionsContainer;
    private PopupLinks popupLinks;
    private SplitActionBarMenu splitActionBarMenu;
    private boolean isTranslating;
    private boolean imageLargerDisplayed;

    // map view
    private MapView mapView;

    @Override
    public void onBackPressed() {
        if (imageLargerDisplayed)
            hideImage();
        else
            super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        InfoLink infoLink = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Utils.KEY_EXTRAS_INFO)) {
                infoTweet = (InfoTweet) extras.getBundle(Utils.KEY_EXTRAS_INFO).getParcelable(KEY_EXTRAS_TWEET);
            }
            if (extras.containsKey(Utils.KEY_EXTRAS_INFO) && extras.getBundle(Utils.KEY_EXTRAS_INFO).containsKey(KEY_EXTRAS_LINK)) {
                infoLink = CacheData.getInstance().getCacheInfoLink(extras.getBundle(Utils.KEY_EXTRAS_INFO).getString(KEY_EXTRAS_LINK));
            }
        }

        if (infoTweet == null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        } else {

            setContentView(R.layout.tweet_activity);

            imgAvatar = ((ImageView) findViewById(R.id.tweet_avatar));
            imgAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    goToLink("@" + (infoTweet.isRetweet() ? infoTweet.getUsernameRetweet() : infoTweet.getUsername()));
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

            txtUsername = ((TextView) findViewById(R.id.tweet_username));
            txtUsername.setText(name + ((fullname.equals("")) ? "" : " (" + fullname + ")"));

            txtDate = ((TextView) findViewById(R.id.tweet_date));
            txtDate.setText(Utils.timeFromTweetExtended(this, infoTweet.getDate()));

            txtText = ((TextView) findViewById(R.id.tweet_text));
            String html = infoTweet.getTextHTMLFinal();
            if (html.equals("")) html = Utils.toHTML(this, infoTweet.getText());
            txtText.setText(Html.fromHtml(html));

            fragmentAdapter = new TweetFragmentAdapter(this, getSupportFragmentManager(), infoTweet);

            pager = (ViewPager) findViewById(R.id.tweet_pager);
            pager.setAdapter(fragmentAdapter);

            indicator = (TabPageIndicator) findViewById(R.id.tweet_indicator);
            indicator.setViewPager(pager);

            (findViewById(R.id.tweet_btn_favorite)).setOnClickListener(clickFavorite);
            (findViewById(R.id.tweet_btn_reply)).setOnClickListener(clickReply);
            (findViewById(R.id.tweet_btn_retweet)).setOnClickListener(clickRetweet);
            (findViewById(R.id.tweet_btn_translate)).setOnClickListener(clickTranslate);
            (findViewById(R.id.tweet_btn_translate)).setOnLongClickListener(longClickTranslate);
            (findViewById(R.id.tweet_btn_original_tweet)).setOnClickListener(clickOriginalTweet);
            (findViewById(R.id.tweet_btn_more)).setOnClickListener(clickMore);

            llRoot = (FrameLayout) findViewById(R.id.tweet_ll);
            tweetInfoLayout = (LinearLayout) findViewById(R.id.tweet_info_ll);
            ivImageLarger = (ImageViewZoomTouch) findViewById(R.id.zoom_image);
            tweetContent = (RelativeLayout) findViewById(R.id.tweet_content);
            tweetActionsContainer = (LinearLayout) findViewById(R.id.tweet_actions_container);
            viewLoading = (LinearLayout) findViewById(R.id.tweet_text_loading);

            tweetContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imageLargerDisplayed)
                        hideImage();
                }
            });
            popupLinks = new PopupLinks(this);
            popupLinks.loadPopup(llRoot);

            splitActionBarMenu = new SplitActionBarMenu(this);
            splitActionBarMenu.loadSplitActionBarMenu(llRoot);

            refreshTheme();

            // muestra la imagen si está en  versiones anteriores a HONEYCOMB
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && infoLink != null && infoLink.isExtensiveInfo() && infoLink.getType() == InfoLink.IMAGE) {
                showImage(infoLink);
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // muestra la imagen si está en  versiones superiores a HONEYCOMB
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (hasFocus) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    if (extras.containsKey(Utils.KEY_EXTRAS_INFO) && extras.getBundle(Utils.KEY_EXTRAS_INFO).containsKey(KEY_EXTRAS_LINK)) {
                        InfoLink infoLink = CacheData.getInstance().getCacheInfoLink(extras.getBundle(Utils.KEY_EXTRAS_INFO).getString(KEY_EXTRAS_LINK));

                        if (infoLink != null && infoLink.isExtensiveInfo() && infoLink.getType() == InfoLink.IMAGE) {
                            showImage(infoLink);
                        }
                    }
                }
            }
        }
    }

    public void showImage(InfoLink infoLink) {

        int screenHeight = 0;
        try {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            screenHeight = size.y;
        } catch (NoSuchMethodError e) {
            screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        }

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        final float translationOffset = (float) screenHeight - statusBarHeight - tweetActionsContainer.getTop();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Intent showImage = new Intent(this, ShowImageActivity.class);
            showImage.putExtra(ShowImageActivity.KEY_EXTRA_URL_IMAGE, infoLink.getLinkImageLarge());
            startActivity(showImage);
        } else {

            AQuery aQuery = new AQuery(this).recycle(ivImageLarger);
            aQuery.id(ivImageLarger).image(infoLink.getLinkImageLarge(), true, true, 0, R.drawable.icon_tweet_image_large, aQuery.getCachedImage(R.drawable.icon_tweet_image_large), 0);

            ObjectAnimator tweetInfoLayoutAnimator = ObjectAnimator.ofFloat(tweetInfoLayout, "translationY", 0.0f, translationOffset);
            tweetInfoLayoutAnimator.setDuration(250);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivImageLarger.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, tweetActionsContainer.getTop());
                    ivImageLarger.setLayoutParams(layoutParams);
                    ivImageLarger.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    imageLargerDisplayed = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            animatorSet.playTogether(tweetInfoLayoutAnimator);
            animatorSet.start();
        }
    }

    public void hideImage() {

        int screenHeight = 0;
        try {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            screenHeight = size.y;
        } catch (NoSuchMethodError e) {
            screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        }

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        float translationOffset = (float) screenHeight - statusBarHeight - tweetActionsContainer.getTop();

        ObjectAnimator tweetInfoLayoutAnimator = ObjectAnimator.ofFloat(tweetInfoLayout, "translationY", translationOffset, 0.0f);
        tweetInfoLayoutAnimator.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                imageLargerDisplayed = false;
                ivImageLarger.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.playTogether(tweetInfoLayoutAnimator);
        animatorSet.start();
    }

    public void goToLink(String link) {
        if (link.startsWith("@")) {
            Bundle bundle = new Bundle();
            bundle.putString(UserActivity.KEY_EXTRAS_USER, link.replace("@", ""));
            startAnimationActivity(UserActivity.class, bundle);
        } else if (link.startsWith("#")) {
            HashTagDialogFragment frag = new HashTagDialogFragment();
            Bundle args = new Bundle();
            args.putString("hashtag", link);
            frag.setArguments(args);
            frag.show(getSupportFragmentManager(), "dialog");
        } else {
            InfoLink infoLink = CacheData.getInstance().getCacheInfoLink(link);

            if (infoLink != null && infoLink.isExtensiveInfo() && infoLink.getType() == InfoLink.IMAGE) {
                showImage(infoLink);
            } else {
                if (link.startsWith("www")) {
                    link = "http://" + link;
                }
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }

    private void refreshTheme() {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            llRoot.setPadding(29, 0, 0, 0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar : R.drawable.bg_sidebar_dark);
            tweetInfoLayout.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar_no_border : R.drawable.bg_sidebar_no_border_dark);
        } else {
            llRoot.setPadding(0, 29, 0, 0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar_left : R.drawable.bg_sidebar_left_dark);
            tweetInfoLayout.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar_no_border : R.drawable.bg_sidebar_no_border_dark);
        }
    }

    public void translateTweet(final String language) {

        Button btn_translate = (Button) findViewById(R.id.tweet_btn_translate);
        Button btn_original_tweet = (Button) findViewById(R.id.tweet_btn_original_tweet);

        ObjectAnimator hideTweetText = ObjectAnimator.ofFloat(txtText, "alpha", 1f, 0f);
        hideTweetText.setDuration(250);

        viewLoading.setVisibility(View.VISIBLE);
        ObjectAnimator showViewLoading = ObjectAnimator.ofFloat(viewLoading, "alpha", 0f, 1f);
        showViewLoading.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(hideTweetText, showViewLoading);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isTranslating = true;
                APITweetTopics.execute(TweetActivity.this, getSupportLoaderManager(), TweetActivity.this, new LoadTranslateTweetRequest(infoTweet.getText(), language));
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

    View.OnClickListener clickFavorite = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.goToFavorite(TweetActivity.this, infoTweet);
        }
    };

    View.OnClickListener clickReply = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.goToReply(TweetActivity.this, userActive, infoTweet);
        }
    };

    View.OnClickListener clickRetweet = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.showDialogRetweet(TweetActivity.this, userActive, infoTweet, new Callable() {
                @Override
                public Object call() throws Exception {
                    finish();
                    return null;
                }
            });
        }
    };

    View.OnLongClickListener longClickTranslate = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            PreferenceUtils.saveTraslationDefaultLanguage(TweetActivity.this, "");
            TweetActions.showDialogTranslation(TweetActivity.this);

            return true;
        }
    };

    View.OnClickListener clickTranslate = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (!isTranslating) {
                if (PreferenceUtils.getTraslationDefaultLanguage(TweetActivity.this) == "")
                    TweetActions.showDialogTranslation(TweetActivity.this);
                else
                    translateTweet(PreferenceUtils.getTraslationDefaultLanguage(TweetActivity.this));
            }
        }
    };

    View.OnClickListener clickOriginalTweet = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Button btn_translate = (Button) findViewById(R.id.tweet_btn_translate);
            final Button btn_original_tweet = (Button) findViewById(R.id.tweet_btn_original_tweet);

            ObjectAnimator showTranslateButton = ObjectAnimator.ofFloat(btn_translate, "alpha", 0f, 1f);
            showTranslateButton.setDuration(250);

            ObjectAnimator hideOriginalTweetButton = ObjectAnimator.ofFloat(btn_original_tweet, "alpha", 1f, 0f);
            hideOriginalTweetButton.setDuration(250);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(showTranslateButton, hideOriginalTweetButton);

            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    btn_original_tweet.setVisibility(View.GONE);
                    String html = infoTweet.getTextHTMLFinal();
                    if (html.equals("")) html = Utils.toHTML(TweetActivity.this, infoTweet.getText());
                    txtText.setText(Html.fromHtml(html));
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
    };

    View.OnClickListener clickMore = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            final ArrayList<String> arCode = new ArrayList<String>();
            ArrayList<String> ar = new ArrayList<String>();

            if (!infoTweet.isDm()) {
                if (infoTweet.isSavedTweet())
                    ar.add(getString(R.string.delete_read_after));
                else
                    ar.add(getString(R.string.create_read_after));
                arCode.add(TweetActions.TWEET_ACTION_READ_AFTER);

                ar.add(getString(R.string.send_direct_message));
                arCode.add(TweetActions.TWEET_ACTION_SEND_DM);
            }

            // TODO Borrar tweet de un usuario
            /*
       if (infoTweet.isTimeline()) {
           if (infoTweet.getUsername().equals(mTweetTopicsCore.getTweetTopics().getActiveUser().getString("name"))) {
               ar.add(getString(R.string.delete_tweet));
               arCode.add("delete_tweet");
           }
       }
            */
            ar.add(getString(R.string.copy_to_clipboard));
            arCode.add(TweetActions.TWEET_ACTION_CLIPBOARD);

            ar.add(getString(R.string.share));
            arCode.add(TweetActions.TWEET_ACTION_SHARE);

            if (infoTweet.hasLocation()) {
                ar.add(getString(R.string.view_map));
                arCode.add(TweetActions.TWEET_ACTION_MAP);
            }

            CharSequence[] c = new CharSequence[ar.size()];
            for (int i = 0; i < ar.size(); i++) {
                c[i] = ar.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(TweetActivity.this);
            builder.setTitle(R.string.actions);
            builder.setItems(c, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TweetActions.execByCode(arCode.get(which), TweetActivity.this, userActive, infoTweet);
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

    private void showOriginalTweetText() {
        Button btn_translate = (Button) findViewById(R.id.tweet_btn_translate);
        Button btn_original_tweet = (Button) findViewById(R.id.tweet_btn_original_tweet);

        txtText.setVisibility(View.VISIBLE);
        ObjectAnimator showTweetText = ObjectAnimator.ofFloat(txtText, "alpha", 0f, 1f);
        showTweetText.setDuration(250);

        ObjectAnimator hideViewLoading = ObjectAnimator.ofFloat(viewLoading, "alpha", 1f, 0f);
        hideViewLoading.setDuration(250);

        ObjectAnimator hideTranslateButton = ObjectAnimator.ofFloat(btn_translate, "alpha", 1f, 0f);
        hideTranslateButton.setDuration(250);

        btn_original_tweet.setVisibility(View.VISIBLE);
        ObjectAnimator showOriginalTweetButton = ObjectAnimator.ofFloat(btn_original_tweet, "alpha", 0f, 1f);
        showOriginalTweetButton.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(showTweetText, hideViewLoading, hideTranslateButton, showOriginalTweetButton);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                viewLoading.setVisibility(View.GONE);
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

    @Override
    public void onResults(BaseResponse response) {
        isTranslating = false;

        LoadTranslateTweetResponse result = (LoadTranslateTweetResponse) response;

        String html = Utils.toHTML(TweetActivity.this, result.getText());
        txtText.setText(Html.fromHtml(html));

        showOriginalTweetText();
    }

    @Override
    public void onError(ErrorResponse error) {
        isTranslating = false;

        error.getError().printStackTrace();

        String html = infoTweet.getTextHTMLFinal();
        if (html.equals("")) html = Utils.toHTML(TweetActivity.this, infoTweet.getText());
        txtText.setText(Html.fromHtml(html));

        showOriginalTweetText();
    }

    @Override
    public void onShowLinks(View view, InfoTweet infoTweet) {
        popupLinks.showLinks(view, infoTweet);
    }

    @Override
    public void onShowSplitActionBarMenu(BaseListFragment baseListFragment, InfoTweet infoTweet) {
        splitActionBarMenu.showSplitActionBarMenu(baseListFragment, infoTweet, userActive);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupLinks.isShowLinks()) {
                popupLinks.hideLinks();
                return false;
            }
            if (splitActionBarMenu.isShowing()) {
                splitActionBarMenu.hideSplitActionBarMenu();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public MapView getMapView() {
        if (mapView == null) {
            mapView = new MapView(this, getString(R.string.google_maps_api));
            mapView.setClickable(true);
            mapView.setBuiltInZoomControls(true);
        }
        return mapView;
    }

}
