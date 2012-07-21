package com.javielinux.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.adapters.TrendingTopicsAdapter;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.request.TrendsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.api.response.TrendsResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import twitter4j.Trend;

import java.util.ArrayList;

public class TrendingTopicsFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private Entity column_entity;

    private TrendingTopicsAdapter trendingTopicsAdapter;
    private ArrayList<Trend> trends_location_list = new ArrayList<Trend>();

    private TweetsAdapter trendsAdapter;
    private ArrayList<InfoTweet> trends_list = new ArrayList<InfoTweet>();

    private View view;
    private PullToRefreshListView trendsLocationlistView;
    private PullToRefreshListView trendslistView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewTweetList;

    private Trend selected_trend_location;

    public TrendingTopicsFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
    }

    public void loadTrendsLocation() {
        Log.d(Utils.TAG, "reloadColumnUser : " + column_entity.getInt("type_id"));

        TrendsRequest trendsRequest = new TrendsRequest(column_entity.getInt("location_id"));
        APITweetTopics.execute(getActivity(), getLoaderManager(), this, trendsRequest);
    }

    public void loadTrends() {
        Log.d(Utils.TAG, "reloadColumnUser : " + column_entity.getInt("type_id"));

        EntitySearch entitySearch = new EntitySearch();
        entitySearch.setValue("date_create", Utils.now());
        entitySearch.setValue("last_modified", Utils.now());
        entitySearch.setValue("use_count", 0);
        entitySearch.setValue("is_temp", 1);
        entitySearch.setValue("icon_id", 1);
        entitySearch.setValue("icon_big", "drawable/letter_hash");
        entitySearch.setValue("icon_small", "drawable/letter_hash_small");
        entitySearch.setValue("name", selected_trend_location.getName());
        entitySearch.setValue("words_and", selected_trend_location.getName());

        SearchRequest searchRequest = new SearchRequest(entitySearch);
        APITweetTopics.execute(getActivity(), getLoaderManager(), this, searchRequest);
    }

    @Override
    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        trendsAdapter.setFlinging(flinging);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trendingTopicsAdapter = new TrendingTopicsAdapter(getActivity(), trends_location_list);
        trendsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), trends_list, "", (int)column_entity.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + trends_location_list.size());

        view = View.inflate(getActivity(), R.layout.trendingtopics_fragment, null);

        trendsLocationlistView = (PullToRefreshListView) view.findViewById(R.id.trends_location_listview);
        trendsLocationlistView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        trendsLocationlistView.getRefreshableView().setAdapter(trendingTopicsAdapter);
        trendsLocationlistView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTrendsLocation();
            }
        });
        trendsLocationlistView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });

        trendslistView = (PullToRefreshListView) view.findViewById(R.id.trends_listview);
        trendsAdapter.setParentListView(trendslistView);
        trendslistView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        trendslistView.getRefreshableView().setAdapter(trendsAdapter);
        trendslistView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTrends();
            }
        });
        trendslistView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onTweetListItemClick(view, position, id);
            }
        });

        viewLoading = (LinearLayout) view.findViewById(R.id.trend_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.trend_view_no_internet);
        viewTweetList = (LinearLayout) view.findViewById(R.id.trend_view_tweet_list);

        Button btTrendsLocationBack = (Button)view.findViewById(R.id.bt_trendslocationback);
        btTrendsLocationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator translationTrends = ObjectAnimator.ofFloat(viewTweetList, "translationY", 0f, viewTweetList.getHeight());
                translationTrends.setDuration(250);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translationTrends);

                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        viewTweetList.setVisibility(View.GONE);
                        trendsLocationlistView.setVisibility(View.VISIBLE);

                        ObjectAnimator translationTrendsLocation = ObjectAnimator.ofFloat(trendsLocationlistView, "translationY", -trendsLocationlistView.getHeight(), 0f);
                        translationTrendsLocation.setDuration(250);

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(translationTrendsLocation);
                        animatorSet.start();
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
        });

        ObjectAnimator translationTrends = ObjectAnimator.ofFloat(viewTweetList, "translationY", 0f, viewTweetList.getHeight());
        translationTrends.setDuration(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationTrends);

        animatorSet.start();

        trendsLocationlistView.setVisibility(View.GONE);
        viewTweetList.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);

        loadTrendsLocation();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onListItemClick(View v, final int position, long id) {

        ObjectAnimator translationTrendsLocation = ObjectAnimator.ofFloat(trendsLocationlistView, "translationY", 0f, -trendsLocationlistView.getHeight());
        translationTrendsLocation.setDuration(250);

        ObjectAnimator translationTrends = ObjectAnimator.ofFloat(viewTweetList, "translationY", viewTweetList.getHeight(), 0f);
        translationTrends.setDuration(250);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationTrendsLocation, translationTrends);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                selected_trend_location = trends_location_list.get(position - 1);

                trendsLocationlistView.setVisibility(View.GONE);
                viewLoading.setVisibility(View.VISIBLE);
                loadTrends();
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

    private void onTweetListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        intent.putExtra(TweetActivity.KEY_EXTRAS_TWEET, trendsAdapter.getItem(position - 1));
        startActivity(intent);

    }

    @Override
    public void onResults(BaseResponse result) {

        if (result.getClass().equals(TrendsResponse.class)){

            TrendsResponse trendsResponse = (TrendsResponse)result;

            trendsLocationlistView.setVisibility(View.VISIBLE);
            viewLoading.setVisibility(View.GONE);

            trendsLocationlistView.onRefreshComplete();

            ArrayList<Trend> trends_data = trendsResponse.getTrends();
            trends_location_list.clear();

            for (Trend trend : trends_data) {
                trends_location_list.add(trend);
            }

            trendingTopicsAdapter.notifyDataSetChanged();
        } else {

            SearchResponse searchResponse = (SearchResponse)result;

            viewTweetList.setVisibility(View.VISIBLE);
            viewLoading.setVisibility(View.GONE);

            trendslistView.onRefreshComplete();

            ArrayList<InfoTweet> tweets_data = searchResponse.getInfoTweets();
            trends_list.clear();

            for (InfoTweet infoTweet : tweets_data) {
                trends_list.add(infoTweet);
            }

            trendsAdapter.notifyDataSetChanged();
            trendsAdapter.launchVisibleTask();
        }
    }

    @Override
    public void onError(ErrorResponse error) {

        error.getError().printStackTrace();
        trendsLocationlistView.onRefreshComplete();
        trendslistView.onRefreshComplete();

        trendsLocationlistView.setVisibility(View.GONE);
        viewTweetList.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
    }
}
