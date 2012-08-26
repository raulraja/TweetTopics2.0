package com.javielinux.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.TweetsConversationAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.GetConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetConversationResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TweetConversationFragment extends Fragment implements APIDelegate<BaseResponse> {

    private InfoTweet infoTweet;
    private ArrayList<InfoTweet> tweet_list;
    private View footer_view;
    private ListView list;

    private TweetsConversationAdapter adapter;

    public TweetConversationFragment(InfoTweet infoTweet) {
        this.infoTweet = infoTweet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet_list = new ArrayList<InfoTweet>();
        adapter = new TweetsConversationAdapter(getActivity(), getLoaderManager(), tweet_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.tweet_conversation_fragment, null);
        footer_view = View.inflate(getActivity(), R.layout.tweet_conversation_foot_fragment, null);
        footer_view.findViewById(R.id.tweet_conversation_loading).setVisibility(View.GONE);

        list = ((ListView)view.findViewById(R.id.tweet_conversation_list));
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
        list.addFooterView(footer_view);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
        });

        getConversationTweets();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("TweetTopics2.0", "TweetConversationFragment: onStart");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.d("TweetTopics2.0", "TweetConversationFragment: onHiddenChanged");
    }

    private void getConversationTweets() {
        long inReplyToId = infoTweet.getToReplyId();
        boolean getTweetsFromDB = true;

        footer_view.findViewById(R.id.tweet_conversation_loading).setVisibility(View.VISIBLE);

        while (getTweetsFromDB) {

            ArrayList<Entity> conversation_tweet_list = DataFramework.getInstance().getEntityList("tweets_user", "tweet_id='" + Utils.fillZeros(String.valueOf(inReplyToId)) + "'");

            if (conversation_tweet_list.size() == 0) {
                getTweetsFromDB = false;
                getConversationTweetsFromInternet(inReplyToId);
            } else {
                inReplyToId = Long.parseLong(conversation_tweet_list.get(0).getString("reply_tweet_id"));
                tweet_list.add(new InfoTweet(conversation_tweet_list.get(0)));
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void getConversationTweetsFromInternet(long tweet_id) {
        if (tweet_id > 0)
            APITweetTopics.execute(getActivity(), getLoaderManager(), this, new GetConversationRequest(-1, tweet_id));
        else
            footer_view.findViewById(R.id.tweet_conversation_loading).setVisibility(View.GONE);
    }

    @Override
    public void onResults(BaseResponse response) {
        GetConversationResponse result = (GetConversationResponse) response;

        long inReplyToId = result.getConversationTweet().getToReplyId();
        tweet_list.add(result.getConversationTweet());
        adapter.notifyDataSetChanged();

        if (inReplyToId > 0)
            getConversationTweetsFromInternet(inReplyToId);
        else
            footer_view.findViewById(R.id.tweet_conversation_loading).setVisibility(View.GONE);
    }

    @Override
    public void onError(ErrorResponse error) {
        footer_view.findViewById(R.id.tweet_conversation_loading).setVisibility(View.GONE);
        error.getError().printStackTrace();
    }
}