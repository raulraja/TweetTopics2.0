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
import com.javielinux.api.request.SearchRequest;
import com.javielinux.api.request.TwitterUserDBRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SearchResponse;
import com.javielinux.api.response.TwitterUserDBResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.ListFragmentListener;
import com.javielinux.utils.Utils;
import com.javielinux.widget.WidgetCounters2x1;
import com.javielinux.widget.WidgetCounters4x1;

import java.util.ArrayList;

public class SearchFragment extends BaseListFragment implements APIDelegate<BaseResponse>, ListFragmentListener {

    private static String KEY_SAVE_STATE_COLUMN_ID = "KEY_SAVE_STATE_COLUMN_ID";

    private TweetsAdapter tweetsAdapter;
    private ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();
    private Entity column_entity;
    private EntitySearch search_entity;
    private View view;
    private PullToRefreshListView listView;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    private int positionLastRead = 0;

    public SearchFragment() {
        super();
    }

    public SearchFragment(long columnId) {
        init(columnId);
    }

    public void init(long columnId) {
        column_entity = new Entity("columns", columnId);
        search_entity = new EntitySearch(Long.parseLong(column_entity.getValue("search_id").toString()));
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

                listView.getRefreshableView().setSelection(result.getPosition() + 1);

                positionLastRead = result.getPosition();
                tweetsAdapter.notifyDataSetChanged();

                boolean getTweetsFromInternet = false;

                if (infoTweets.size() <= 0) {
                    getTweetsFromInternet = true;
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
        }, new TwitterUserDBRequest(column_entity.getInt("type_id"), -1, search_entity, -1));
    }

    @Override
    public void setFlinging(boolean flinging) {
        this.flinging = flinging;
        tweetsAdapter.setFlinging(flinging);
        if (!flinging) {
            onMarkPositionLastReadAsLastReadId(false);
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
        Log.d(Utils.TAG, "reloadSearch : " + search_entity.getId());

        SearchRequest searchRequest = new SearchRequest(search_entity);

        if (infoTweets.size() > 0)
            searchRequest.setSinceId(infoTweets.get(0).getId());

        APITweetTopics.execute(getActivity(), getLoaderManager(), this, searchRequest);
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
        tweetsAdapter = new TweetsAdapter(getActivity(), getLoaderManager(), infoTweets, "", (int) column_entity.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description"));

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (positionLastRead > firstVisibleItem) {
                    positionLastRead = firstVisibleItem;
                    if (firstVisibleItem == 0) onMarkPositionLastReadAsLastReadId(false);
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

        listView.getRefreshableView().setSelection(tweetsAdapter.getLastReadPosition() + 1);

        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        if (infoTweets.size() <= 0) {
            if (search_entity.getInt("notifications") == 1)
                preLoadInfoTweetFromDB();
            else
                reload();
        } else {
            showUpdating();
        }

        return view;
    }

    @Override
    public void onResults(BaseResponse response) {
        Log.d(Utils.TAG, "SearchFragment:onResults:" + search_entity.getId());
        SearchResponse result = (SearchResponse) response;

        hideUpdating();
        listView.onRefreshComplete();

        showTweetsList();

        if (search_entity.getInt("notifications") == 0) {
            ArrayList<InfoTweet> infoTweetList = result.getInfoTweets();
            int count = 0;
            int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();

            for (int i = infoTweetList.size() - 1; i >= 0; i--) {
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
            listView.getRefreshableView().setSelection(firstVisible + count);
        } else {
            InfoSaveTweets infoSaveTweets = result.getInfoSaveTweets();

            int count = 0;
            int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();

            if (infoSaveTweets.getNewMessages() > 0) {

                ArrayList<Entity> tweets;

                try {
                    tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc");
                } catch (Exception exception) {
                    tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + search_entity.getId(), "date desc", "0," + Utils.MAX_ROW_BYSEARCH);
                }

                boolean found = false;

                for (int i = tweets.size() - 1; i >= 0; i--) {
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
            }

            try {
                infoTweets.get(tweetsAdapter.getLastReadPosition() + 1).setLastRead(false);
                tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);
                positionLastRead = tweetsAdapter.getLastReadPosition() + count;
            } catch (IndexOutOfBoundsException e) {
            }

            tweetsAdapter.notifyDataSetChanged();
            tweetsAdapter.launchVisibleTask();
            listView.getRefreshableView().setSelection(firstVisible + count);
            ((TweetTopicsActivity) getActivity()).reloadBarAvatar();
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

    @Override
    public void onMarkPositionLastReadAsLastReadId(boolean force) {
        if ( (tweetsAdapter != null && force) || (tweetsAdapter != null && tweetsAdapter.getLastReadPosition() > positionLastRead)) {
            if (force) {
                positionLastRead = listView.getRefreshableView().getFirstVisiblePosition()+1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        long id = tweetsAdapter.getItem(positionLastRead).getId();
                        if (search_entity != null) {
                            search_entity.setValue("last_tweet_id", id + "");
                            search_entity.save();
                        }
                        sendBroadcastUpdateTweets();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TweetTopicsActivity) getActivity()).reloadBarAvatar();
                            }
                        });
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }
}
