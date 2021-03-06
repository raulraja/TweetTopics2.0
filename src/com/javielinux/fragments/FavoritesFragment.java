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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.LoadTypeStatusLoader;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTypeStatusResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class FavoritesFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private static String KEY_SAVE_STATE_COLUMN_ID = "KEY_SAVE_STATE_COLUMN_ID";

    private TweetsAdapter tweetsAdapter;
    private ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();
    private Entity column_entity;
    private Entity user_entity;
    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    private int positionLastRead = 0;

    private int typeUserColumn = 0;

    public FavoritesFragment() {
        super();
    }

    public FavoritesFragment(long columnId) {
        super();
        init(columnId);
    }

    public void init(long columnId) {
        column_entity = new Entity("columns", columnId);
        typeUserColumn = TweetTopicsUtils.COLUMN_FAVORITES;
        user_entity = new Entity("users", column_entity.getLong("user_id"));
    }

    public Entity getColumnEntity() {
        return column_entity;
    }

    @Override
    public void goToTop() {
        listView.getRefreshableView().setSelection(0);
    }

    @Override
    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        tweetsAdapter.setFlinging(flinging);
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    public void showTweetsList() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void reload() {
        Log.d(Utils.TAG, "reloadColumnUser : " + column_entity.getInt("type_id"));

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new LoadTypeStatusRequest(user_entity.getId(), LoadTypeStatusLoader.FAVORITES, user_entity.getString("name"),"",-1,null));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_SAVE_STATE_COLUMN_ID, column_entity.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_STATE_COLUMN_ID)) {
            init(savedInstanceState.getLong(KEY_SAVE_STATE_COLUMN_ID));
        }
        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, user_entity.getId(), user_entity.getString("name"), (int)column_entity.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + infoTweets.size());

        //markPositionLastReadAsLastReadId();

        view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);

        // poner estilo de la listas de las preferencias del usuario
        ThemeManager themeManager = new ThemeManager(getActivity());
        listView.getRefreshableView().setDivider(ImageUtils.createDividerDrawable(getActivity(), themeManager.getColor("color_divider_tweet")));
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("prf_use_divider_tweet", true)) {
            listView.getRefreshableView().setDividerHeight(2);
        } else {
            listView.getRefreshableView().setDividerHeight(0);
        }
        listView.getRefreshableView().setFadingEdgeLength(6);
        listView.getRefreshableView().setCacheColorHint(themeManager.getColor("color_shadow_listview"));


        listView.getRefreshableView().setAdapter(tweetsAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                reload();
            }
        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onClickItemList(tweetsAdapter.getItem(position - 1));
            }
        });
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return onLongClickItemList(tweetsAdapter.getItem(position - 1));
            }
        });

        tweetsAdapter.setParentListView(listView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    setFlinging(true);
                }

                if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && scrollState != SCROLL_STATE_TOUCH_SCROLL) {
                    setFlinging(false);
                }
            }
        });


        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        if (infoTweets.size()<=0) {
            showLoading();
            reload();
        }

        return view;
    }

    @Override
    public void onResults(BaseResponse r) {

        LoadTypeStatusResponse result = (LoadTypeStatusResponse) r;

        listView.onRefreshComplete();
        showTweetsList();

        ArrayList<InfoTweet> infoTweetList = result.getInfoTweets();

        int count = 0;
        int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();

        for (int i = infoTweetList.size()-1; i >=0; i--) {
            try {
                if (!infoTweets.contains(infoTweetList.get(i))) {
                    infoTweets.add(0, infoTweetList.get(i));
                    count++;
                }
            } catch (OutOfMemoryError er) {
                i = infoTweetList.size();
            }
        }

        //tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
        tweetsAdapter.notifyDataSetChanged();
        tweetsAdapter.launchVisibleTask();
        listView.getRefreshableView().setSelection(firstVisible + count);

        if(selected_tweet_id > 0) {
            int i = 0;
            boolean found = false;

            while (i < tweetsAdapter.getCount() && !found) {
                if (tweetsAdapter.getItem(i).getId() == selected_tweet_id) {
                    onClickItemList(tweetsAdapter.getItem(i));
                    selected_tweet_id = -1;
                    found = true;
                }
                i++;
            }
        }
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        listView.onRefreshComplete();
        showNoInternet();
    }
}
