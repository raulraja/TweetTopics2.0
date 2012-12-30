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

package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.TweetConversationFragment;
import com.javielinux.fragments.TweetLinksFragment;
import com.javielinux.fragments.TweetMapFragment;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.LinksUtils;

import java.util.ArrayList;

public class TweetFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_LINKS = 0;
    public static int TAB_CONVERSATION = 1;
    public static int TAB_MAP = 2;

    private InfoTweet infoTweet;

    private ArrayList<Integer> tweet_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> tweet_fragment_titles = new ArrayList<String>();

    private Context context;

    public TweetFragmentAdapter(Context context, FragmentManager fragmentManager, InfoTweet infoTweet) {
        super(fragmentManager);
        this.infoTweet = infoTweet;

        this.context = context;

        loadColumns();
    }

    void loadColumns() {
        if (LinksUtils.pullLinks(infoTweet.getText()).size()>0) {
            tweet_fragment_code.add(TAB_LINKS);
            tweet_fragment_titles.add(context.getString(R.string.links));
        }
        if (infoTweet.hasConversation()) {
            tweet_fragment_code.add(TAB_CONVERSATION);
            tweet_fragment_titles.add(context.getString(R.string.conversation));
        }
//        if (infoTweet.hasLocation()) {
//            tweet_fragment_code.add(TAB_MAP);
//            tweet_fragment_titles.add(context.getString(R.string.map));
//        }
    }

    @Override
    public Fragment getItem(int index) {
        if (tweet_fragment_code.get(index) == TAB_LINKS) {
            return new TweetLinksFragment(infoTweet);
        } else if (tweet_fragment_code.get(index) == TAB_CONVERSATION) {
            return new TweetConversationFragment(infoTweet);
        } else if (tweet_fragment_code.get(index) == TAB_MAP) {
            return new TweetMapFragment(infoTweet.getLatitude(),infoTweet.getLongitude());
        }

        return new Fragment();
    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return tweet_fragment_code.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tweet_fragment_titles.get(position % tweet_fragment_code.size()).toUpperCase();
    }
}