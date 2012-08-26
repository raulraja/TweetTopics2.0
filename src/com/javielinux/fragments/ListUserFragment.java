package com.javielinux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class ListUserFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private Entity column_entity;
    private Entity list_user_entity;

    private TweetsAdapter tweetsAdapter;
    private ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    public ListUserFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
        list_user_entity = new Entity("user_lists", Long.parseLong(column_entity.getValue("userlist_id").toString()));
    }

    public Entity getColumnEntity() {
        return column_entity;
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

    public void showTweetsList() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void reload() {
        Log.d(Utils.TAG, "reloadColumnUser : " + column_entity.getInt("type_id"));

        LoadTypeStatusRequest loadTypeStatusRequest = new LoadTypeStatusRequest(list_user_entity.getLong("user_id"), LoadTypeStatusLoader.LIST, "", "", list_user_entity.getInt("userlist_id"));

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, loadTypeStatusRequest);
    }

    @Override
    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        tweetsAdapter.setFlinging(flinging);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, "", (int)column_entity.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + infoTweets.size());

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

        tweetsAdapter.setParentListView(listView);
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
                onListItemClick(view, position, id);
            }
        });

        listView.getRefreshableView().setSelection(tweetsAdapter.getLastReadPosition()+1);

        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        if (infoTweets.size()<=0)
            showLoading();
        else
            showUpdating();

        reload();

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
    public void onResults(BaseResponse response) {

        LoadTypeStatusResponse result = (LoadTypeStatusResponse) response;

        hideUpdating();
        listView.onRefreshComplete();

        showTweetsList();

        ArrayList<InfoTweet> infoTweetList = result.getInfoTweets();
        int count = 0;
        int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();

        for (int i = infoTweetList.size()-1; i >=0; i--) {
            try {
                if (infoTweets.size() == 0 || infoTweetList.get(i).getId() > infoTweets.get(0).getId()) {
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
