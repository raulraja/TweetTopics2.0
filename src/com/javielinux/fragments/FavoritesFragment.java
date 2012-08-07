package com.javielinux.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.LoadTypeStatusLoader;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.request.TwitterUserDBRequest;
import com.javielinux.api.request.TwitterUserRequest;
import com.javielinux.api.response.*;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class FavoritesFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

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

    public FavoritesFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
        typeUserColumn = TweetTopicsUtils.COLUMN_FAVORITES;
        user_entity = new Entity("users", column_entity.getLong("user_id"));
    }

    public Entity getColumnEntity() {
        return column_entity;
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

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new LoadTypeStatusRequest(user_entity.getId(), LoadTypeStatusLoader.FAVORITES, user_entity.getString("name"),"",-1));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, user_entity.getString("name"), (int)column_entity.getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + infoTweets.size());

        //markPositionLastReadAsLastReadId();

        view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);
        listView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        listView.getRefreshableView().setAdapter(tweetsAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onListItemClick(view, position, id);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void onListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        intent.putExtra(TweetActivity.KEY_EXTRAS_TWEET, tweetsAdapter.getItem(position - 1));
        startActivity(intent);

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

        tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
        tweetsAdapter.notifyDataSetChanged();
        tweetsAdapter.launchVisibleTask();
        listView.getRefreshableView().setSelection(firstVisible + count);
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        listView.onRefreshComplete();
        showNoInternet();
    }
}