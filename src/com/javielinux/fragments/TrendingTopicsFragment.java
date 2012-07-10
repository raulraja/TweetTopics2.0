package com.javielinux.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.adapters.TrendingTopicsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.TrendsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TrendsResponse;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;
import twitter4j.Trend;

import java.util.ArrayList;

public class TrendingTopicsFragment extends Fragment implements APIDelegate<BaseResponse> {

    private Entity column_entity;

    private TrendingTopicsAdapter trendingTopicsAdapter;
    private ArrayList<Trend> trends_list = new ArrayList<Trend>();

    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    public TrendingTopicsFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
    }

    public void showUpdating() {
        viewUpdate.setVisibility(View.VISIBLE);
    }

    public void hideUpdating() {
        viewUpdate.setVisibility(View.GONE);
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

    public void showTrendsList() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void loadTrends() {
        Log.d(Utils.TAG, "reloadColumnUser : " + column_entity.getInt("type_id"));

        TrendsRequest trendsRequest = new TrendsRequest(column_entity.getInt("location_id"));
        APITweetTopics.execute(getActivity(), getLoaderManager(), this, trendsRequest);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingTopicsAdapter = new TrendingTopicsAdapter(getActivity(), trends_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + trends_list.size());

        view = View.inflate(getActivity(), R.layout.trendingtopics_fragment, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.trend_listview);
        listView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        listView.getRefreshableView().setAdapter(trendingTopicsAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTrends();
            }
        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });

        viewLoading = (LinearLayout) view.findViewById(R.id.trend_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.trend_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.trend_view_update);

        showLoading();
        loadTrends();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onListItemClick(View v, int position, long id) {

        //Intent intent = new Intent(getActivity(), TweetActivity.class);
        //intent.putExtra(TweetActivity.KEY_EXTRAS_TWEET, trendingTopicsAdapter.getItem(position - 1));
        //startActivity(intent);

    }

    @Override
    public void onResults(BaseResponse result) {

        TrendsResponse trendsResponse = (TrendsResponse)result;

        hideUpdating();
        showTrendsList();
        listView.onRefreshComplete();

        ArrayList<Trend> trends_data = trendsResponse.getTrends();
        trends_list.clear();

        for (Trend trend : trends_data) {
            trends_list.add(trend);
        }

        trendingTopicsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(ErrorResponse error) {

        error.getError().printStackTrace();
        listView.onRefreshComplete();
        showNoInternet();
    }
}
