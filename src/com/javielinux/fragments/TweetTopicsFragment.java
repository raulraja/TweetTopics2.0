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
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.TwitterUserDBRequest;
import com.javielinux.api.request.TwitterUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.TwitterUserDBResponse;
import com.javielinux.api.response.TwitterUserResponse;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import widget.WidgetCounters2x1;
import widget.WidgetCounters4x1;

import java.util.ArrayList;
import java.util.Date;

public class TweetTopicsFragment extends BaseListFragment implements APIDelegate<BaseResponse> {

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

    public Entity getColumnEntity() {
        return column_entity;
    }

    private void preLoadInfoTweetFromDB() {

        showLoading();

        APITweetTopics.execute(getActivity(), getLoaderManager(), new APIDelegate() {
            @Override
            public void onResults(BaseResponse r) {
                TwitterUserDBResponse result = (TwitterUserDBResponse) r;
                infoTweets.clear();
                infoTweets.addAll(result.getInfoTweets());
                tweetsAdapter.setHideMessages(result.getCountHide());
                tweetsAdapter.setLastReadPosition(result.getPosition());

                listView.getRefreshableView().setSelection(result.getPosition()+1);

                positionLastRead = result.getPosition();
                tweetsAdapter.notifyDataSetChanged();

                boolean getTweetsFromInternet = false;

                if (infoTweets.size()<=0) {
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

                if (getTweetsFromInternet) {
                    reload();
                } else {
                    showTweetsList();
                }

            }

            @Override
            public void onError(ErrorResponse error) {
                reload();
            }
        }, new TwitterUserDBRequest(column_entity.getInt("type_id"), user_entity.getId(), null, typeUserColumn));

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

    private void markPositionLastReadAsLastReadId() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (tweetsAdapter != null) {

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
                                sendBroadcastUpdateTweets();
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

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

        tweetsAdapter.setParentListView(listView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (positionLastRead > firstVisibleItem) {
                    positionLastRead = firstVisibleItem;
                    if (firstVisibleItem == 0) markPositionLastReadAsLastReadId();
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


        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        if (infoTweets.size()<=0) {
            preLoadInfoTweetFromDB();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResults(BaseResponse r) {
        Log.d(Utils.TAG, "TweetTopicsFragment:onResults:" + column_entity.getInt("type_id"));
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
        WidgetCounters4x1.updateAll(getActivity());
        WidgetCounters2x1.updateAll(getActivity());
    }
}