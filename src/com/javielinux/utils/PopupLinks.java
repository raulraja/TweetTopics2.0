package com.javielinux.utils;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.javielinux.adapters.LinksAdapter;
import com.javielinux.dialogs.HashTagDialogFragment;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.BaseLayersActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.UserActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

public class PopupLinks {

    public interface PopupLinksListener {
        void onShowLinks(View view, InfoTweet infoTweet);
    }

    private LinearLayout layoutLinks;
    private LinearLayout layoutMainLinks;
    private GridView gvLinks;
    private LinksAdapter linksAdapter;
    private ArrayList<String> links = new ArrayList<String>();
    private FragmentActivity activity;
    private int widthScreen;
    private int heightScreen;
    private int statusBarHeight;

    public PopupLinks(FragmentActivity activity) {
        init(activity);
    }

    private void init(FragmentActivity activity) {
        this.activity = activity;
        Display display = activity.getWindowManager().getDefaultDisplay();

        widthScreen = display.getWidth();
        heightScreen = display.getHeight();

    }

    public void loadPopup(ViewGroup root) {
        View popupLinks = activity.getLayoutInflater().inflate(R.layout.popup_links, null);

        layoutMainLinks = (LinearLayout) popupLinks.findViewById(R.id.tweettopics_ll_main_links);
        layoutMainLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLinks();
            }
        });

        layoutLinks = (LinearLayout) popupLinks.findViewById(R.id.tweettopics_ll_links);

        linksAdapter = new LinksAdapter(activity, activity.getSupportLoaderManager(), links);
        gvLinks = (GridView) popupLinks.findViewById(R.id.tweettopics_gv_links);
        gvLinks.setAdapter(linksAdapter);
        gvLinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToLink(links.get(i));
            }
        });

        root.addView(popupLinks);
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
                widthContainer = (widthScreen/4)*3 + Utils.dip2px(activity,40);
            } else {
                rows = links.size()/2;
                if (links.size()%2>0) rows++;
                gvLinks.setNumColumns(2);
                widthContainer = (widthScreen/4)*2 + Utils.dip2px(activity,30);
            }
            if (rows==1) {
                heightContainer = Utils.dip2px(activity,110);
            } else {
                heightContainer = Utils.dip2px(activity,100) * rows;
            }

            linksAdapter.notifyDataSetChanged();

            if (statusBarHeight<=0) {
                Rect rect= new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
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

            int top = (int)activity.getResources().getDimension(R.dimen.actionbar_height);
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
            if (activity instanceof BaseLayersActivity) {
                Bundle bundle = new Bundle();
                bundle.putString(UserActivity.KEY_EXTRAS_USER, link);
                ((BaseLayersActivity)activity).startAnimationActivity(UserActivity.class, bundle);
            }
        } else if (link.startsWith("#")) {
            HashTagDialogFragment frag = new HashTagDialogFragment();
            Bundle args = new Bundle();
            args.putString("hashtag", link);
            frag.setArguments(args);
            frag.show(activity.getSupportFragmentManager(), "dialog");
        } else {
            if (link.startsWith("www")) {
                link = "http://"+link;
            }
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
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


}
