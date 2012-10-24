package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.BaseLayersActivity;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.SplitActionBarMenu;

abstract public class BaseListFragment extends Fragment {

    protected boolean flinging = false;
    public long selected_tweet_id = -1;

    public BaseListFragment() {
        super();
    }

    public boolean isFlinging() {
        return flinging;
    }

    abstract void setFlinging(boolean flinging);

    abstract public Entity getColumnEntity();

    abstract public void goToTop();

    protected void onClickItemList(InfoTweet infoTweet) {
        if (getActivity() instanceof BaseLayersActivity) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TweetActivity.KEY_EXTRAS_TWEET, infoTweet);
            ((BaseLayersActivity) getActivity()).startAnimationActivity(TweetActivity.class, bundle);
        }
    }

    protected boolean onLongClickItemList(InfoTweet infoTweet) {
        if (getActivity() instanceof SplitActionBarMenu.SplitActionBarMenuListener) {
            ((SplitActionBarMenu.SplitActionBarMenuListener) getActivity()).onShowSplitActionBarMenu(this, infoTweet);
        }
        return true;
    }

}
