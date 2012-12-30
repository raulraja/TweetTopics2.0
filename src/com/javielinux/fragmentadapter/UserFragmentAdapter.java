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
import com.javielinux.fragments.UserFriendshipFragment;
import com.javielinux.fragments.UserMentionsFragment;
import com.javielinux.fragments.UserProfileFragment;
import com.javielinux.fragments.UserTimelineFragment;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.TweetTopicsUtils;

import java.util.ArrayList;

public class UserFragmentAdapter extends FragmentPagerAdapter {

    public static int TAB_INFO = 0;
    public static int TAB_TIMELINE = 1;
    public static int TAB_MENTIONS = 2;
    public static int TAB_FOLLOWERS = 3;
    public static int TAB_FRIENDS = 4;

    private InfoUsers infoUsers;

    private ArrayList<Integer> user_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> user_fragment_titles = new ArrayList<String>();

    private Context context;

    public UserFragmentAdapter(Context context, FragmentManager fragmentManager, InfoUsers infoUsers) {
        super(fragmentManager);
        this.infoUsers = infoUsers;

        this.context = context;

        loadColumns();
    }

    void loadColumns() {

        user_fragment_code.add(TAB_INFO);
        user_fragment_titles.add(context.getString(R.string.user));

        user_fragment_code.add(TAB_TIMELINE);
        user_fragment_titles.add(context.getString(R.string.timeline));

        user_fragment_code.add(TAB_MENTIONS);
        user_fragment_titles.add(context.getString(R.string.mentions));

        user_fragment_code.add(TAB_FOLLOWERS);
        user_fragment_titles.add(context.getString(R.string.followers));

        user_fragment_code.add(TAB_FRIENDS);
        user_fragment_titles.add(context.getString(R.string.friends));
    }


    @Override
    public Fragment getItem(int index) {
        if (user_fragment_code.get(index) == TAB_INFO) {
            return new UserProfileFragment(infoUsers);
        } else if (user_fragment_code.get(index) == TAB_TIMELINE) {
            return new UserTimelineFragment(infoUsers);
        } else if (user_fragment_code.get(index) == TAB_MENTIONS) {
            return new UserMentionsFragment(infoUsers);
        } else if (user_fragment_code.get(index) == TAB_FOLLOWERS) {
            return new UserFriendshipFragment(infoUsers, TweetTopicsUtils.COLUMN_FOLLOWERS);
        } else if (user_fragment_code.get(index) == TAB_FRIENDS) {
            return new UserFriendshipFragment(infoUsers, TweetTopicsUtils.COLUMN_FOLLOWINGS);
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return user_fragment_code.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return user_fragment_titles.get(position % user_fragment_code.size()).toUpperCase();
    }
}
