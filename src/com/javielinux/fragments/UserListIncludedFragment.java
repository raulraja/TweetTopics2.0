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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.javielinux.adapters.UserListsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.UserListsLoader;
import com.javielinux.api.request.UserListsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.UserListsResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.ImageUtils;
import twitter4j.UserList;

import java.util.ArrayList;

public class UserListIncludedFragment extends Fragment implements APIDelegate<BaseResponse> {

    private static String KEY_SAVE_STATE_USER = "KEY_SAVE_STATE_USER";

    private ArrayList<UserList> user_list;
    private LinearLayout viewLoading;
    private ListView list;

    private UserListsAdapter adapter;
    private InfoUsers infoUsers;

    public UserListIncludedFragment() {
        super();
    }

    public UserListIncludedFragment(InfoUsers infoUsers) {
        init(infoUsers);
    }

    public void init(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SAVE_STATE_USER, infoUsers.getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_STATE_USER)) {
            init(CacheData.getInstance().getCacheUser(savedInstanceState.getString(KEY_SAVE_STATE_USER)));
        }
        user_list = new ArrayList<UserList>();
        adapter = new UserListsAdapter(getActivity(), user_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.user_list_included_fragment, null);

        list = ((ListView)view.findViewById(R.id.user_lists_list));
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
        });

        viewLoading = (LinearLayout) view.findViewById(R.id.user_lists_loading);

        getUserLists();

        return view;
    }

    private void getUserLists() {
        if (infoUsers != null) {
            showLoading();
            APITweetTopics.execute(getActivity(), getLoaderManager(), this, new UserListsRequest(UserListsLoader.SHOW_TWEETS_FOLLOWINGLIST, "", infoUsers.getName()));
        }
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    public void showUserLists() {
        viewLoading.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResults(BaseResponse response) {

        showUserLists();
        user_list.clear();

        UserListsResponse result = (UserListsResponse) response;

        for (UserList userList: result.getUserList())
            user_list.add(userList);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        showUserLists();
    }

}
