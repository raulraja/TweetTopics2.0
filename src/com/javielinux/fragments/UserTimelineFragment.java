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
import com.javielinux.api.loaders.LoadTypeStatusLoader;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTypeStatusResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;

import java.util.ArrayList;

public class UserTimelineFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private ArrayList<InfoTweet> tweet_list;
    private LinearLayout viewLoading;
    private ListView list;

    private TweetsAdapter adapter;
    private InfoUsers infoUsers;

    public UserTimelineFragment(InfoUsers infoUsers) {

        this.infoUsers = infoUsers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet_list = new ArrayList<InfoTweet>();
        adapter = new TweetsAdapter(getActivity(), getLoaderManager(), tweet_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.user_timeline_fragment, null);

        list = ((ListView)view.findViewById(R.id.user_timeline_list));
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

        viewLoading = (LinearLayout) view.findViewById(R.id.user_timeline_loading);

        getUserTimelineTweets();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void getUserTimelineTweets() {
        if (infoUsers != null) {
            showLoading();
            APITweetTopics.execute(getActivity(), getLoaderManager(), this, new LoadTypeStatusRequest(-1, LoadTypeStatusLoader.USER_TIMELINE, infoUsers.getName(), "", -1));
        }
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
}
