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
