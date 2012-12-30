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
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.android.dataframework.Entity;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.GetUserFriendshipMembersLoader;
import com.javielinux.api.loaders.LoadTypeStatusLoader;
import com.javielinux.api.request.GetUserFriendshipMembersRequest;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserFriendshipMembersResponse;
import com.javielinux.api.response.LoadTypeStatusResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class UserFriendshipFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private static String KEY_SAVE_STATE_USER = "KEY_SAVE_STATE_USER";
    private static String KEY_SAVE_STATE_COLUMN_TYPE = "KEY_SAVE_STATE_COLUMN_TYPE";

    private ArrayList<InfoTweet> tweet_list;
    private LinearLayout viewLoading;
    private ListView list;

    private TweetsAdapter adapter;
    private InfoUsers infoUsers;
    private long[] userIdList;

    private int column_type;
    private int getUserFriendshipMembersTypeUserColumn = 0;
    private int loadTypeStatusTypeUserColumn = 0;

    public UserFriendshipFragment() {
        super();
    }

    public UserFriendshipFragment(InfoUsers infoUsers, int column_type) {
        init(infoUsers,column_type);
    }

    public void init(InfoUsers infoUsers,int column_type) {
        this.infoUsers = infoUsers;

        if (column_type == TweetTopicsUtils.COLUMN_FOLLOWERS) {
            getUserFriendshipMembersTypeUserColumn = GetUserFriendshipMembersLoader.FOLLOWERS;
            loadTypeStatusTypeUserColumn = LoadTypeStatusLoader.FOLLOWERS;
        } else {
            getUserFriendshipMembersTypeUserColumn = GetUserFriendshipMembersLoader.FRIENDS;
            loadTypeStatusTypeUserColumn = LoadTypeStatusLoader.FRIENDS;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SAVE_STATE_USER, infoUsers.getName());
        outState.putInt(KEY_SAVE_STATE_COLUMN_TYPE, column_type);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_STATE_USER)) {
            init(CacheData.getInstance().getCacheUser(savedInstanceState.getString(KEY_SAVE_STATE_USER)),savedInstanceState.getInt(KEY_SAVE_STATE_COLUMN_TYPE));
        }
        tweet_list = new ArrayList<InfoTweet>();
        adapter = new TweetsAdapter(getActivity(), getLoaderManager(), tweet_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.user_mentions_fragment, null);

        list = ((ListView)view.findViewById(R.id.user_mentions_list));
        // poner estilo de la listas de las preferencias del usuario
        ThemeManager themeManager = new ThemeManager(getActivity());
        list.setDivider(ImageUtils.createDividerDrawable(getActivity(), themeManager.getColor("color_divider_tweet")));
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("prf_use_divider_tweet", true)) {
            list.setDividerHeight(2);
        } else {
            list.setDividerHeight(0);
        }
        list.setFadingEdgeLength(6);
        list.setCacheColorHint(themeManager.getColor("color_shadow_listview"));
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onClickItemList(adapter.getItem(position));
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return onLongClickItemList(adapter.getItem(position));
            }
        });

        viewLoading = (LinearLayout) view.findViewById(R.id.user_mentions_loading);

        getUserFriendshipMembers();

        return view;
    }

    private void getUserFriendshipMembers() {
        if (infoUsers != null) {
            showLoading();

            APITweetTopics.execute(getActivity(), getLoaderManager(), new APIDelegate() {
                @Override
                public void onResults(BaseResponse response) {
                    GetUserFriendshipMembersResponse result = (GetUserFriendshipMembersResponse) response;
                    userIdList = result.getFriendshipMembersIds();
                    getUserFriendshipTweets();
                }

                @Override
                public void onError(ErrorResponse error) {
                    error.getError().printStackTrace();
                    list.setVisibility(View.VISIBLE);
                }
            }, new GetUserFriendshipMembersRequest(getUserFriendshipMembersTypeUserColumn,infoUsers.getName()));
        } else {
            showTweetsList();
        }
    }

    public void getUserFriendshipTweets() {
        long[] userIds;

        if (userIdList.length < 100)
            userIds = userIdList;
        else
            userIds = Arrays.copyOfRange(userIdList, 0, 99);

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new LoadTypeStatusRequest(-1, loadTypeStatusTypeUserColumn, infoUsers.getName(), "", -1, userIds));
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    public void showTweetsList() {
        viewLoading.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResults(BaseResponse response) {

        LoadTypeStatusResponse result = (LoadTypeStatusResponse) response;

        for (InfoTweet infoTweet: result.getInfoTweets())
            tweet_list.add(infoTweet);

        adapter.notifyDataSetChanged();

        showTweetsList();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        showTweetsList();
    }

    @Override
    void setFlinging(boolean flinging) {
        this.flinging = flinging;
    }

    @Override
    public Entity getColumnEntity() {
        return null;
    }

    @Override
    public void goToTop() {}
}
