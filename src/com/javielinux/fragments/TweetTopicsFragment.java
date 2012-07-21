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
import com.javielinux.api.request.TwitterUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TwitterUserResponse;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import widget.WidgetCounters2x1;
import widget.WidgetCounters4x1;

import java.util.ArrayList;
import java.util.Date;

public class TweetTopicsFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

    private TweetsAdapter tweetsAdapter;
    private ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();;
    private Entity column_entity;
    private Entity user_entity;
    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    private int positionLastRead = 0;

    private int typeUserColumn = 0;

    public TweetTopicsFragment(long column_id) {

        column_entity = new Entity("columns", column_id);
        if (column_entity.getInt("type_id")== TweetTopicsUtils.COLUMN_TIMELINE) {
            typeUserColumn = TweetTopicsUtils.TWEET_TYPE_TIMELINE;
        } else if (column_entity.getInt("type_id")== TweetTopicsUtils.COLUMN_MENTIONS) {
            typeUserColumn = TweetTopicsUtils.TWEET_TYPE_MENTIONS;
        } else if (column_entity.getInt("type_id")== TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
            typeUserColumn = TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES;
        }
        user_entity = new Entity("users", column_entity.getLong("user_id"));

    }

    private void preLoadInfoTweetIfIsNecessary() {

        EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), typeUserColumn);

        String whereType = "";

        switch (column_entity.getInt("type_id")) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
                Utils.fillHide();
                whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE;
                break;
            case TweetTopicsUtils.COLUMN_MENTIONS:
                whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS;
                break;
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                whereType = " AND (type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " OR type_id = " + TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES + ")";
                break;
        }

        ArrayList<Entity> tweets;

        try {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");
        } catch (Exception exception) {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
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
                boolean is_timeline = column_entity.getInt("type_id") ==  TweetTopicsUtils.COLUMN_TIMELINE;

                if (is_timeline && Utils.hideUser.contains(tweets.get(i).getString("username").toLowerCase())) { // usuario
                    countHide++;
                } else if (is_timeline && Utils.isHideWordInText(tweets.get(i).getString("text").toLowerCase())) { // palabra
                    countHide++;
                } else if (is_timeline && Utils.isHideSourceInText(tweets.get(i).getString("source").toLowerCase())) { // fuente
                    countHide++;
                } else {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));
                    if (!found && entityTweetUser.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
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
                        /*if (r.hasMoreTweetDown()) {
                            response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                        }*/
                        count++;
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }
            }

        }

        tweetsAdapter.setHideMessages(countHide);
        tweetsAdapter.setLastReadPosition(pos);
        positionLastRead = pos;

    }

    @Override
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

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new TwitterUserRequest(column_entity.getInt("type_id"), user_entity.getId()));
    }
    /*
    public void showHideMessage() {
        boolean show = Utils.getPreference(getActivity()).getBoolean("prf_quiet_show_msg", true);

        if (show && tweetsAdapter.getHideMessages() > 0) {
            Utils.showMessage(getActivity(), tweetsAdapter.getHideMessages() + " " + getActivity().getString(R.string.tweets_hidden));
        }
    }
    */
    private boolean markPositionLastReadAsLastReadId() {

        sendBroadcastUpdateTweets();

        if (tweetsAdapter != null) {
            if (!tweetsAdapter.isUserLastItemLastRead()) {
                if (tweetsAdapter.getCount() > positionLastRead) {
                    try {
                        long id = tweetsAdapter.getItem(positionLastRead).getId();
                        if (user_entity != null) {
                            switch (column_entity.getInt("type_id")) {
                                case TweetTopicsUtils.COLUMN_TIMELINE:
                                    user_entity.setValue("last_timeline_id", id + "");
                                    user_entity.save();
                                    break;
                                case TweetTopicsUtils.COLUMN_MENTIONS:
                                    user_entity.setValue("last_mention_id", id + "");
                                    user_entity.save();
                                    break;
                                case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                                    user_entity.setValue("last_direct_id", id + "");
                                    user_entity.save();
                                    break;
                            }

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

        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, user_entity.getString("name"), (int)column_entity.getId());
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

        tweetsAdapter.setParentListView(listView);

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
            if (column_entity.getInt("type_id") == TweetTopicsUtils.COLUMN_TIMELINE) {

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
        }

        tweetsAdapter.launchVisibleTask();

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
    public void onResults(BaseResponse r) {

        TwitterUserResponse result = (TwitterUserResponse) r;

        hideUpdating();
        listView.onRefreshComplete();

        showTweetsList();

        InfoSaveTweets infoSaveTweets = result.getInfo();

        if (infoSaveTweets.getNewMessages() > 0) {
            String whereType = "";

            switch (column_entity.getInt("type_id")) {
                case TweetTopicsUtils.COLUMN_TIMELINE:
                    Utils.fillHide();
                    whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE;
                    break;
                case TweetTopicsUtils.COLUMN_MENTIONS:
                    whereType = " AND type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS;
                    break;
                case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                    whereType = " AND (type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " OR type_id = " + TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES + ")";
                    break;
            }

            whereType += " AND tweet_id >= '" + Utils.fillZeros("" + infoSaveTweets.getOlderId()) + "'";

            ArrayList<Entity> tweets;

            try {
                tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");
            } catch (Exception exception) {
                tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
            }

            int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();
            int count = 0;
            boolean found = false;
            int countHide = 0;
            boolean is_timeline = column_entity.getInt("type_id") ==  TweetTopicsUtils.COLUMN_TIMELINE;
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), column_entity.getInt("type_id"));

            for (int i = tweets.size()-1; i >=0; i--) {
                if (is_timeline && Utils.hideUser.contains(tweets.get(i).getString("username").toLowerCase())) { // usuario
                    countHide++;
                } else if (is_timeline && Utils.isHideWordInText(tweets.get(i).getString("text").toLowerCase())) { // palabra
                    countHide++;
                } else if (is_timeline && Utils.isHideSourceInText(tweets.get(i).getString("source").toLowerCase())) { // fuente
                    countHide++;
                } else {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));

                    if (!found && entityTweetUser.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
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
                        /*if (r.hasMoreTweetDown()) {
                            response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                        }*/
                        count++;
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }
            }

            tweetsAdapter.addHideMessages(countHide);
            infoTweets.get(tweetsAdapter.getLastReadPosition()+1).setLastRead(false);
            tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
            positionLastRead = tweetsAdapter.getLastReadPosition() + count;

            tweetsAdapter.notifyDataSetChanged();
            tweetsAdapter.launchVisibleTask();

            listView.getRefreshableView().setSelection(firstVisible + count + 1);

            sendBroadcastUpdateTweets();

        }
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        listView.onRefreshComplete();
        showNoInternet();
    }

    private void sendBroadcastUpdateTweets() {
        ((TweetTopicsActivity)getActivity()).refreshMyActivity();
        WidgetCounters4x1.updateAll(getActivity());
        WidgetCounters2x1.updateAll(getActivity());
    }

}
