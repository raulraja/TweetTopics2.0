package com.javielinux.utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class SplitActionBarMenu {

    public interface SplitActionBarMenuListener {
        void onShowSplitActionBarMenu(View view, InfoTweet infoTweet);
    }

    private FragmentActivity activity;
    private int screenHeight;
    private int screenWidth;
    private int statusBarHeight;
    private float splitActionBarMenuHeight;
    private float actionbar_columns_height;

    private LinearLayout root_layout;
    private LinearLayout main_layout;

    public SplitActionBarMenu(FragmentActivity activity) {
        init(activity);
    }

    private void init(FragmentActivity activity) {
        this.activity = activity;
        splitActionBarMenuHeight = activity.getResources().getDimension(R.dimen.footer_buttons_height);
        actionbar_columns_height = activity.getResources().getDimension(R.dimen.actionbar_columns_height);

        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        statusBarHeight = rect.bottom;

        try {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            screenHeight = size.y;
            screenWidth = size.x;
        } catch (NoSuchMethodError e) {
            screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
            screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        }
    }

    public void loadSplitActionBarMenu(ViewGroup root) {
        View splitActionBarMenu = activity.getLayoutInflater().inflate(R.layout.split_action_bar_menu, null);

        root_layout = (LinearLayout) splitActionBarMenu.findViewById(R.id.split_action_bar_menu_root);
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

        ObjectAnimator translationY = ObjectAnimator.ofFloat(main_layout, "translationY", screenHeight - Utils.dip2px(activity,splitActionBarMenuHeight), screenHeight);
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

    public void showSplitActionBarMenu(View view, InfoTweet infoTweet) {

        ObjectAnimator translationY = ObjectAnimator.ofFloat(main_layout, "translationY", screenHeight, screenHeight - Utils.dip2px(activity,splitActionBarMenuHeight));
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
