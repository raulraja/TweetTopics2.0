package com.javielinux.utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.javielinux.fragments.BaseListFragment;
import com.javielinux.infos.InfoSubMenuTweet;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

public class SplitActionBarMenu {

    public interface SplitActionBarMenuListener {
        void onShowSplitActionBarMenu(BaseListFragment fromFragment, InfoTweet infoTweet);
    }

    private FragmentActivity activity;
    private int screenHeight;
    private float splitActionBarMenuHeight;

    private LinearLayout root_layout;
    private HorizontalScrollView scroll_view_layout;
    private LinearLayout main_layout;

    /**
     * Fragment desde el que se llama al SplitActionBar
     */
    private BaseListFragment fromFragment;

    public SplitActionBarMenu(FragmentActivity activity) {
        init(activity);
    }

    public BaseListFragment getFromFragment() {
        return fromFragment;
    }

    public void setFromFragment(BaseListFragment fromFragment) {
        this.fromFragment = fromFragment;
    }

    private void init(FragmentActivity activity) {
        this.activity = activity;
        splitActionBarMenuHeight = activity.getResources().getDimension(R.dimen.footer_buttons_height);

        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        try {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            screenHeight = size.y;
        } catch (NoSuchMethodError e) {
            screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        }
    }

    public void loadSplitActionBarMenu(ViewGroup root) {
        View splitActionBarMenu = activity.getLayoutInflater().inflate(R.layout.split_action_bar_menu, null);

        root_layout = (LinearLayout) splitActionBarMenu.findViewById(R.id.split_action_bar_menu_root);
        scroll_view_layout = (HorizontalScrollView) splitActionBarMenu.findViewById(R.id.split_action_bar_menu_scroll_view);
        main_layout = (LinearLayout) splitActionBarMenu.findViewById(R.id.split_action_bar_menu_container);
        root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSplitActionBarMenu();
            }
        });

        root.addView(splitActionBarMenu);
    }

    public void hideSplitActionBarMenu() {

        ObjectAnimator translationY = ObjectAnimator.ofFloat(scroll_view_layout, "translationY", screenHeight - Utils.dip2px(activity, splitActionBarMenuHeight), screenHeight);
        translationY.setDuration(250);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                root_layout.setVisibility(View.INVISIBLE);
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

    public boolean isShowing() {
        return root_layout.getVisibility() == View.VISIBLE;
    }

    private void loadActionButtons(ArrayList<String> codes, final InfoTweet infoTweet) {

        main_layout.removeAllViews();

        boolean is_first = true;

        for (final String code : codes) {

            if (is_first) {
                is_first = false;
            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;

                ImageView imageView = new ImageView(activity);
                imageView.setBackgroundResource(R.drawable.action_bar_divider);
                imageView.setLayoutParams(layoutParams);

                main_layout.addView(imageView);
            }

            InfoSubMenuTweet infoSubMenuTweet = new InfoSubMenuTweet(activity, code);
            ImageButton imageButton = new ImageButton(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.weight = 1;
            layoutParams.setMargins(20, 0, 20, 0);
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;

            imageButton.setLayoutParams(layoutParams);
            imageButton.setBackgroundResource(infoSubMenuTweet.getResDrawable());
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSplitActionBarMenu();
                    TweetActions.execByCode(code, activity, infoTweet.getUserId(), infoTweet, getFromFragment());
                }
            });

            main_layout.addView(imageButton);
        }
    }

    public void showSplitActionBarMenu(BaseListFragment fragment, InfoTweet infoTweet) {

        setFromFragment(fragment);

        ArrayList<String> codes = PreferenceUtils.getArraySubMenuTweet(activity);

        if (codes.size() == 1) {
            TweetActions.execByCode(codes.get(0), activity, infoTweet.getUserId(), infoTweet);
        } else {
            loadActionButtons(codes, infoTweet);

            ObjectAnimator translationY = ObjectAnimator.ofFloat(scroll_view_layout, "translationY", screenHeight, screenHeight - Utils.dip2px(activity, splitActionBarMenuHeight));
            translationY.setDuration(250);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translationY);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    root_layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
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
    }
}