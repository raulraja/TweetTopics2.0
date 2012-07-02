package com.javielinux.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import infos.InfoSaveTweets;
import infos.InfoTweet;

import java.util.ArrayList;
import java.util.Date;

public class SearchFragment extends Fragment implements APIDelegate<BaseResponse> {

    private TweetsAdapter tweetsAdapter;
    private ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();;
    private Entity column_entity;
    private EntitySearch search_entity;
    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    private int positionLastRead = 0;
    private boolean flinging = false;

    public SearchFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
        search_entity = new EntitySearch(Long.parseLong(column_entity.getValue("search_id").toString()));
    }

    private void preLoadInfoTweetIfIsNecessary() {

        ArrayList<Entity> tweets;

        try {
            tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc");
        } catch (Exception exception) {
            tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc", "0," + Utils.MAX_ROW_BYSEARCH);
        }

        int pos = 0;
        int count = 0;
        boolean found = false;
        int countHide = 0;

        for (int i = 0; i < tweets.size(); i++) {

            boolean delete_tweet = false;

            if (i > 0) {
                if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                    delete_tweet = true;
                }
            }
            if (delete_tweet) {
                try {
                    tweets.get(i).delete();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                InfoTweet infoTweet = new InfoTweet(tweets.get(i));
                if (!found && search_entity.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                    infoTweet.setLastRead(true);
                    pos = count;
                    found = true;
                }


                if (i >= tweets.size() - 1 && !found) {
                    infoTweet.setLastRead(true);
                    pos = count;
                    found = true;
                }

                infoTweet.setRead(found);

                try {
                    infoTweets.add(infoTweet);
                    count++;
                } catch (OutOfMemoryError er) {
                    i = tweets.size();
                }
            }

        }

        tweetsAdapter.setLastReadPosition(pos);
        positionLastRead = pos;

    }

    public boolean isFlinging() {
        return flinging;
    }

    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        tweetsAdapter.setFlinging(flinging);
        if (!flinging) {
            markPositionLastReadAsLastReadId();
        }
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

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new SearchRequest(search_entity));
    }

    private boolean markPositionLastReadAsLastReadId() {

        //TODO:sendBroadcastWidgets
        //sendBroadcastWidgets();
        if (tweetsAdapter != null) {
            if (!tweetsAdapter.isUserLastItemLastRead()) {
                if (tweetsAdapter.getCount() > positionLastRead) {
                    try {
                        long id = tweetsAdapter.getItem(positionLastRead).getId();
                        if (search_entity != null) {
                            search_entity.setValue("last_tweet_id", id + "");
                            search_entity.save();

                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, "", (int)column_entity.getId());
        if (search_entity.getInt("notifications") == 1)
            preLoadInfoTweetIfIsNecessary();

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (positionLastRead > firstVisibleItem) {
                    positionLastRead = firstVisibleItem;
                    if (firstVisibleItem==0) markPositionLastReadAsLastReadId();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //if (scrollState == SCROLL_STATE_TOUCH_SCROLL) closeSidebar();

                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    setFlinging(true);
                }

                if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && scrollState != SCROLL_STATE_TOUCH_SCROLL) {
                    setFlinging(false);
                }
            }

        });

        listView.getRefreshableView().setSelection(tweetsAdapter.getLastReadPosition()+1);

        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        boolean getTweetsFromInternet = false;

        if (infoTweets.size()<=0) {
            showLoading();
            getTweetsFromInternet = true;
        } else {
            int minutes = Integer.parseInt(Utils.getPreference(getActivity()).getString("prf_time_refresh", "10"));

            if (minutes > 0) {
                int miliseconds = minutes * 60 * 1000;
                Date date = new Date(tweetsAdapter.getItem(0).getDate().getTime() + miliseconds);

                if (new Date().after(date)) {
                    showUpdating();
                    getTweetsFromInternet = true;
                }
            }
        }

        if (getTweetsFromInternet) {
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
    public void onResults(BaseResponse response) {

        SearchResponse result = (SearchResponse) response;

        hideUpdating();
        listView.onRefreshComplete();

        showTweetsList();

        if (search_entity.getInt("notifications") == 0) {
            ArrayList<InfoTweet> infoTweetList = result.getInfoTweets();
            int count = 0;
            int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();

            for (int i = infoTweetList.size()-1; i >=0; i--) {
                try {
                    infoTweets.add(0, infoTweetList.get(i));
                    count++;
                } catch (OutOfMemoryError er) {
                    i = infoTweetList.size();
                }
            }

            tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
            tweetsAdapter.notifyDataSetChanged();
            listView.getRefreshableView().setSelection(firstVisible + count);
        } else {
            InfoSaveTweets infoSaveTweets = result.getInfoSaveTweets();

            if (infoSaveTweets.getNewMessages() > 0) {

                ArrayList<Entity> tweets;

                try {
                    tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc");
                } catch (Exception exception) {
                    tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc", "0," + Utils.MAX_ROW_BYSEARCH);
                }

                int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();
                int count = 0;
                boolean found = false;

                for (int i = tweets.size()-1; i >=0; i--) {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));

                    if (!found && search_entity.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                        infoTweet.setLastRead(true);
                        found = true;
                    }

                    if (i >= tweets.size() - 1 && !found) {
                        infoTweet.setLastRead(true);
                        found = true;
                    }

                    infoTweet.setRead(found);

                    try {
                        infoTweets.add(0, infoTweet);
                        count++;
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }

                tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
                tweetsAdapter.notifyDataSetChanged();
                listView.getRefreshableView().setSelection(firstVisible + count);
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
