package com.javielinux.tweettopics2;

import adapters.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import animations.Rotate3dAnimation;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.twitter.TwitterApplication;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import database.EntitySearch;
import database.EntityTweetUser;
import infos.*;
import interfaces.FinishTwitterDownload;
import layouts.LoadMoreListItem;
import layouts.TweetListItem;
import notifications.OnAlarmReceiver;
import preferences.Colors;
import preferences.ColorsApp;
import preferences.Preferences;
import preferences.RetweetsTypes;
import sidebar.Sidebar;
import sidebar.SidebarMenu;
import task.*;
import task.Export2HTMLAsyncTask.Export2HTMLAsyncTaskResponder;
import task.GetConversationAsyncTask.GetConversationAsyncTaskResponder;
import task.LoadMoreAsyncTask.LoadMoreResponder;
import task.LoadMoreTweetDownAsyncTask.LoadMoreTweetDownResponder;
import task.LoadMoreTweetDownAsyncTask.LoadMoreTweetDownResult;
import task.LoadTranslateTweetAsyncTask.LoadTranslateTweetAsyncAsyncTaskResponder;
import task.LoadTypeStatusAsyncTask.LoadTypeStatusResponder;
import task.LoadUserAsyncTask.LoadUserAsyncAsyncTaskResponder;
import task.PreparingLinkForSidebarAsyncTask.PreparingLinkForSidebarAsyncTaskResponder;
import task.SearchAsyncTask.SearchAsyncTaskResponder;
import task.SearchAsyncTask.SearchResult;
import task.StatusRetweetersAsyncTask.StatusRetweetersAsyncTaskResponder;
import task.StatusRetweetersAsyncTask.StatusRetweetersResult;
import task.TrendsAsyncTask.TrendsAsyncTaskResponder;
import task.TrendsLocationAsyncTask.TrendsLocationAsyncTaskResponder;
import task.TwitterUserAsyncTask.TwitterUserResult;
import task.UserListsAsyncTask.UserListsAsyncTaskResponder;
import task.UserListsAsyncTask.UserListsResult;
import twitter4j.*;
import updatestatus.ServiceUpdateStatus;
import widget.WidgetCounters2x1;
import widget.WidgetCounters4x1;

import java.io.File;
import java.util.*;

public class TweetTopicsCore implements OnGestureListener, SearchAsyncTaskResponder,
        GetConversationAsyncTaskResponder,
        LoadMoreTweetDownResponder,
        TrendsAsyncTaskResponder,
        FinishTwitterDownload, TrendsLocationAsyncTaskResponder,
        LoadMoreResponder, LoadTypeStatusResponder,
        UserListsAsyncTaskResponder, Export2HTMLAsyncTaskResponder {

    public static final int TYPE_LIST_SEARCH = 0;
    public static final int TYPE_LIST_SEARCH_NOTIFICATIONS = 1;
    public static final int TYPE_LIST_CONVERSATION = 2;
    public static final int TYPE_LIST_COLUMNUSER = 3;
    public static final int TYPE_LIST_READAFTER = 4;
    public static final int TYPE_LIST_USERS = 5;
    public static final int TYPE_LIST_RETWEETS = 6;
    public static final int TYPE_LIST_LISTUSERS = 7;
    public static final int TYPE_LIST_RETWEETERS = 8;

    public static final int TYPE_SIDEBAR_TWEET = 0;
    public static final int TYPE_SIDEBAR_LINK = 1;
    public static final int TYPE_SIDEBAR_USER = 2;

    public static final int DIALOG_SUBMENU_SEARCH = 0;
    public static final int DIALOG_DELETE = 1;
    public static final int DIALOG_IMPORT = 2;
    public static final int DIALOG_EXPORT = 3;
    public static final int DIALOG_CURRENT_EXPORT = 4;
    public static final int DIALOG_REPLY = 5;
    public static final int DIALOG_HASHTAG = 6;
    public static final int DIALOG_USER_TWEETS = 7;
    public static final int DIALOG_USER_ACTIONS = 8;
    public static final int DIALOG_RETWEETS_ACTIONS = 9;
    public static final int DIALOG_LISTS_ACTIONS = 10;
    public static final int DIALOG_SUBMENU_SEARCH_TEMP = 11;
    public static final int DIALOG_MANAGER_THEME = 12;

    public static int TIMELINE = 0;
    public static int MENTIONS = 1;
    public static int FAVORITES = 2;
    public static int DIRECTMESSAGES = 3;
    public static int SENT_DIRECTMESSAGES = 4;

    protected static final int NEW_ID = Menu.FIRST;
    protected static final int IMPORT_ID = Menu.FIRST + 1;
    protected static final int TWEET_SAVED_ID = Menu.FIRST + 2;
    protected static final int TRENDING_ID = Menu.FIRST + 3;
    protected static final int PREFERENCES_ID = Menu.FIRST + 4;
    protected static final int EXIT_ID = Menu.FIRST + 5;
    protected static final int SAMPLES_ID = Menu.FIRST + 6;
    protected static final int EXPORT_HTML_ID = Menu.FIRST + 7;
    protected static final int MANAGER_USER_ID = Menu.FIRST + 8;
    protected static final int SIZE_TEXT_ID = Menu.FIRST + 9;
    protected static final int SEARCH_USER_ID = Menu.FIRST + 10;

    public static final int ACTIVITY_NEWEDITSEARCH = 0;
    public static final int ACTIVITY_PREFERENCES = 1;
    public static final int ACTIVITY_NEWSTATUS = 2;
    public static final int ACTIVITY_USER = 3;
    public static final int ACTIVITY_WALLPAPER = 4;
    public static final int ACTIVITY_COLORS_APP = 5;

    private GestureDetector gestureDetector;

    protected static int mTypeList = TYPE_LIST_COLUMNUSER;
    protected static int mTypeLastList = TYPE_LIST_COLUMNUSER;
    public static int mTypeLastColumn = TIMELINE;

    private int mTypeSidebar = TYPE_SIDEBAR_TWEET;

    private InfoLink mCurrentInfoLink;

    private int mColumStart = TIMELINE;

    protected TwitterApplication app;
    public Twitter twitter;
    protected ProgressDialog progressDialog;

    private boolean isLoadingData = false;
    private boolean isFinishOnCreate = false;
    private boolean isAnimationOutStarted = false;

    protected LinearLayout mLayoutBackgroundApp;
    protected LinearLayout mLayoutList;
    protected LinearLayout mLayoutBottomBar;
    protected LinearLayout mLayoutInfoBackground;
    protected TweetTopicsListView mListView;

    public static UserList mCurrentList = null;

    protected LoadMoreListItem mFooterView;
    protected boolean hasFooter = false;

    protected boolean mListViewScroll = false;
    protected int mPositionLastRead = 0;

    protected StatusListAdapter mAdapterStatusList;
    protected RowSearchAdapter mAdapterSearch;
    protected ResponseListAdapter mAdapterResponseList;

    protected ArrayList<Status> mConversationStatusList;

    protected ImageView mCountTimeline;
    protected ImageView mCountMentions;
    protected ImageView mCountDirectMessages;

    protected EntitySearch mEntitySearch = null;
    protected EntityTweetUser mEntityUser = null;


    public static boolean isGenericSearch = false;

    protected LinearLayout mSidebarTweet;

    protected LinearLayout mSidebarHead;
    protected LinearLayout mSidebarContent;
    protected LinearLayout mSidebarMenu;
    protected LinearLayout mSidebarFoot;
    protected LinearLayout mSidebarBackground;

    protected LinearLayout mLayoutBlack;

    protected static InfoLink mSelectedInfoLink = null;

    protected ThemeManager mThemeManager;

    protected boolean listIsConversation = false;

    protected LinearLayout mLayoutSamplesSearch;
    protected Button mButtonSamplesSearch;

    protected int mPositionSelectedTweet = -1;
    protected int mPositionSelectedTweetConversation = -1;

    protected InfoUsers mUserNameSelected = null;
    protected String mHashTagSelected = "";

    protected int mPositionSelectedSearch = -1;
    protected boolean isToDoSearch = false;

    protected long mCurrentId = -1;

    protected TweetTopics mTweetTopics = null;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (Intent.ACTION_VIEW.equals(action)) {
                    if (intent.getExtras() != null) {
                        if (intent.getExtras().containsKey("refresh-column")) {
                            int column = intent.getExtras().getInt("refresh-column");
                            if (mTypeLastColumn == column) {
                                reload();
                            }
                        }
                    }
                }
            }

        }
    };

    public TweetTopicsCore(TweetTopics cnt) {
        mTweetTopics = cnt;
    }

    public TweetTopics getTweetTopics() {
        return mTweetTopics;
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SUBMENU_SEARCH:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_search, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mPositionSelectedSearch >= 0) {
                                    if (which == 0) {
                                        toDoSearch(mAdapterSearch.getItem(mPositionSelectedSearch).getId());
                                    } else if (which == 1) {
                                        editSearch();
                                    } else if (which == 2) {
                                        mTweetTopics.showDialog(DIALOG_EXPORT);
                                    } else if (which == 3) {
                                        mTweetTopics.showDialog(DIALOG_DELETE);
                                    }
                                }
                            }
                        })
                        .create();
            case DIALOG_MANAGER_THEME:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.manager_theme)
                        .setItems(R.array.items_manager_theme, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent colorsApp = new Intent(mTweetTopics, ColorsApp.class);
                                    mTweetTopics.startActivityForResult(colorsApp, ACTIVITY_COLORS_APP);
                                } else if (which == 1) {
                                    dialogLoadTheme();
                                } else if (which == 2) {
                                    IntentIntegrator.initiateScan(mTweetTopics, R.string.title_download_scan, R.string.msg_download_scan, R.string.alert_dialog_ok, R.string.alert_dialog_cancel);
                                } else if (which == 3) {
                                    ColorsApp.exportTheme(mTweetTopics);
                                }
                            }
                        })
                        .create();
            case DIALOG_SUBMENU_SEARCH_TEMP:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_search_temp, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mPositionSelectedSearch >= 0) {
                                    if (which == 0) {
                                        toDoSearch(mAdapterSearch.getItem(mPositionSelectedSearch).getId());
                                    } else if (which == 1) {
                                        Entity ent = mAdapterSearch.getItem(mPositionSelectedSearch);
                                        ent.setValue("is_temp", 0);
                                        ent.save();
                                        loadGridSearch();
                                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.convert_search_correct));
                                    } else if (which == 2) {
                                        mTweetTopics.showDialog(DIALOG_DELETE);
                                    }
                                }
                            }
                        })
                        .create();
            case DIALOG_IMPORT:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_import, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    try {
                                        ClipboardManager clipboard = (ClipboardManager) mTweetTopics.getSystemService(Context.CLIPBOARD_SERVICE);
                                        importSearch(clipboard.getText().toString());
                                    } catch (NullPointerException e) {
                                        Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.error_general));
                                        e.printStackTrace();
                                    }
                                } else if (which == 1) {
                                    IntentIntegrator.initiateScan(mTweetTopics, R.string.title_download_scan, R.string.msg_download_scan, R.string.alert_dialog_ok, R.string.alert_dialog_cancel);
                                }
                            }
                        })
                        .create();
            case DIALOG_USER_TWEETS:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.items_show_tweets, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                createSearchUser(mUserNameSelected, which);
                            }
                        })
                        .create();
            case DIALOG_RETWEETS_ACTIONS:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_retweets_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    loadTypeStatus(LoadTypeStatusAsyncTask.RETWEETED_OFME);
                                } else if (which == 1) {
                                    loadTypeStatus(LoadTypeStatusAsyncTask.RETWEETED_BYME);
                                } else if (which == 2) {
                                    loadTypeStatus(LoadTypeStatusAsyncTask.RETWEETED_TOME);
                                }
                            }
                        })
                        .create();
            case DIALOG_LISTS_ACTIONS:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_list_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    showListUser(UserListsAsyncTask.SHOW_TWEETS);
                                } else if (which == 1) {
                                    showListUser(UserListsAsyncTask.SHOW_TWEETS_FOLLOWINGLIST);
                                }
                            }
                        })
                        .create();
            case DIALOG_USER_ACTIONS:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.items_user_actions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    loadTypeStatus(LoadTypeStatusAsyncTask.FRIENDS, mUserNameSelected.getName());
                                } else if (which == 1) {
                                    loadTypeStatus(LoadTypeStatusAsyncTask.FOLLOWERS, mUserNameSelected.getName());
                                } else if (which == 2) {
                                    closeSidebar();
                                    showListUser(mUserNameSelected.getName(), UserListsAsyncTask.SHOW_TWEETS);
                                } else if (which == 3) {
                                    closeSidebar();
                                    showDialogColoringTweets();
                                } else if (which == 4) {
                                    closeSidebar();
                                    showListUser(mUserNameSelected.getName(), UserListsAsyncTask.ADD_USER);
                                } else if (which == 5) {
                                    closeSidebar();
                                    updateStatus(NewStatus.TYPE_NORMAL, "@" + mUserNameSelected.getName(), null);
                                } else if (which == 6) {
                                    closeSidebar();
                                    Entity ent = new Entity("quiet");
                                    ent.setValue("word", mUserNameSelected.getName());
                                    ent.setValue("type_id", 2);
                                    ent.save();
                                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.user_hidden_correct));
                                } else if (which == 7) {
                                    closeSidebar();
                                    Intent newsearch = new Intent(mTweetTopics, TabNewEditSearch.class);
                                    newsearch.putExtra("user", mUserNameSelected.getName());
                                    mTweetTopics.startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
                                } else if (which == 8) {
                                    closeSidebar();
                                    directMessage(mUserNameSelected.getName());
                                }
                            }
                        })
                        .create();
            case DIALOG_HASHTAG:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.items_hashtag_actions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    closeSidebar();
                                    Entity ent = new Entity("search");
                                    ent.setValue("date_create", Utils.now());
                                    ent.setValue("last_modified", Utils.now());
                                    ent.setValue("use_count", 0);
                                    ent.setValue("is_temp", 1);
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_hash");
                                    ent.setValue("icon_small", "drawable/letter_hash_small");
                                    ent.setValue("name", mHashTagSelected);
                                    ent.setValue("words_and", mHashTagSelected);
                                    ent.save();
                                    deleteTempSearch();
                                    toDoSearch(ent.getId());
                                } else if (which == 1) {
                                    closeSidebar();
                                    Entity ent = new Entity("quiet");
                                    ent.setValue("word", mHashTagSelected);
                                    ent.setValue("type_id", 1);
                                    ent.save();
                                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.hashtag_hidden_correct));
                                } else if (which == 2) {
                                    closeSidebar();
                                    Intent newsearch = new Intent(mTweetTopics, TabNewEditSearch.class);
                                    newsearch.putExtra("search", mHashTagSelected);
                                    mTweetTopics.startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
                                } else if (which == 3) {
                                    closeSidebar();
                                    updateStatus(NewStatus.TYPE_NORMAL, mHashTagSelected, null);
                                } else if (which == 4) {
                                    closeSidebar();
                                    PreferenceUtils.setDefaultTextInTweet(mTweetTopics, mHashTagSelected);
                                }
                            }
                        })
                        .create();
            case DIALOG_CURRENT_EXPORT:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_export, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    long id = mCurrentId;
                                    Entity search = new Entity("search", id);
                                    String name = search.getString("name");
                                    String text = Utils.HASHTAG_SHARE + " " + Utils.exportSearch(id) + " " + name;
                                    updateStatus(NewStatus.TYPE_NORMAL, text, null);
                                } else if (which == 1) {
                                    long id = mCurrentId;
                                    Entity search = new Entity("search", id);
                                    String name = search.getString("name");
                                    Intent msg = new Intent(Intent.ACTION_SEND);
                                    msg.putExtra(Intent.EXTRA_SUBJECT, name);
                                    msg.putExtra(Intent.EXTRA_TEXT, Utils.exportSearch(id) + " " + name);
                                    msg.setType("text/plain");
                                    mTweetTopics.startActivity(msg);
                                }
                            }
                        })
                        .create();
            case DIALOG_EXPORT:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_export, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    long id = mAdapterSearch.getItem(mPositionSelectedSearch).getId();
                                    String name = mAdapterSearch.getItem(mPositionSelectedSearch).getString("name");
                                    String text = Utils.HASHTAG_SHARE + " " + Utils.exportSearch(id) + " " + name;
                                    updateStatus(NewStatus.TYPE_NORMAL, text, null);
                                } else if (which == 1) {
                                    long id = mAdapterSearch.getItem(mPositionSelectedSearch).getId();
                                    String name = mAdapterSearch.getItem(mPositionSelectedSearch).getString("name");
                                    Intent msg = new Intent(Intent.ACTION_SEND);
                                    msg.putExtra(Intent.EXTRA_SUBJECT, name);
                                    msg.putExtra(Intent.EXTRA_TEXT, Utils.exportSearch(id) + " " + name);
                                    msg.setType("text/plain");
                                    mTweetTopics.startActivity(msg);
                                }
                            }
                        })
                        .create();
            case DIALOG_REPLY:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.actions)
                        .setItems(R.array.actions_reply, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                InfoTweet it = getCurrentInfoTweet();
                                if (which == 0) {
                                    if (it != null) {
                                        ArrayList<String> users = Utils.pullLinksUsers(it.getText());
                                        String text = "";
                                        String user = "";
                                        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                                        if (e != null) {
                                            user = e.getString("name");
                                        }
                                        for (int i = 0; i < users.size(); i++) {
                                            if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                                    && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                                text += users.get(i) + " ";
                                            }
                                        }
                                        updateStatus(NewStatus.TYPE_REPLY, text, it);
                                    }
                                } else if (which == 1) {
                                    if (it != null) {
                                        ArrayList<String> users = Utils.pullLinksUsers(it.getText());
                                        String text = " //cc ";
                                        String user = "";
                                        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                                        if (e != null) {
                                            user = e.getString("name");
                                        }
                                        for (int i = 0; i < users.size(); i++) {
                                            if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                                    && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                                text += users.get(i) + " ";
                                            }
                                        }
                                        updateStatus(NewStatus.TYPE_REPLY_ON_COPY, text, it);
                                    }
                                } else if (which == 2) {
                                    if (it != null) {
                                        updateStatus(NewStatus.TYPE_REPLY, "", it);
                                    }
                                }
                            }
                        })
                        .create();
            case DIALOG_DELETE:
                return new AlertDialog.Builder(mTweetTopics)
                        .setTitle(R.string.title_question_delete)
                        .setMessage(R.string.question_delete)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteSearch();
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
        }
        return null;
    }

    protected void onCreate(Bundle savedInstanceState) {

        gestureDetector = new GestureDetector(this);

        Intent intent = new Intent();
        intent.setAction("org.adw.launcher.counter.SEND");
        intent.putExtra("PNAME", Utils.packageName);
        intent.putExtra("COUNT", 0);
        mTweetTopics.sendBroadcast(intent);

        if (mTweetTopics.getPreference().getBoolean("prf_notif_delete_notifications_inside", true)) {
            ((NotificationManager) mTweetTopics.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        }

        PreferenceUtils.saveNotificationsApp(mTweetTopics, true);

        Utils.setActivity(mTweetTopics);

        if (mTweetTopics.getPreference().getString("prf_orientation", "1").equals("2")) {
            mTweetTopics.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        OnAlarmReceiver.callAlarm(mTweetTopics);

        try {
            DataFramework.getInstance().open(mTweetTopics, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long mUserStart = -1;

        if (savedInstanceState != null) {
            mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
            if (savedInstanceState.containsKey("start_user_id"))
                mUserStart = Long.parseLong(savedInstanceState.getString("start_user_id"));
            if (savedInstanceState.containsKey("start_column"))
                mColumStart = Integer.parseInt(savedInstanceState.getString("start_column"));
        } else {
            Bundle extras = mTweetTopics.getIntent().getExtras();
            if (extras != null) {
                mCurrentId = extras.getLong(DataFramework.KEY_ID);
                if (extras.containsKey("start_user_id")) mUserStart = Long.parseLong(extras.getString("start_user_id"));
                if (extras.containsKey("start_column"))
                    mColumStart = Integer.parseInt(extras.getString("start_column"));
            }
        }

        mThemeManager = new ThemeManager(mTweetTopics);
        mThemeManager.setTheme();

        mCountTimeline = (ImageView) mTweetTopics.findViewById(R.id.count_timeline);
        mCountMentions = (ImageView) mTweetTopics.findViewById(R.id.count_mentions);
        mCountDirectMessages = (ImageView) mTweetTopics.findViewById(R.id.count_directmessages);

        mSidebarHead = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_head);
        mSidebarContent = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_content);

        mSidebarMenu = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_menu);
        mSidebarFoot = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_foot);
        mSidebarBackground = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_background);

        mLayoutBlack = (LinearLayout) mTweetTopics.findViewById(R.id.layout_black);

        mLayoutBackgroundApp = (LinearLayout) mTweetTopics.findViewById(R.id.layout_background_app);

        mLayoutList = (LinearLayout) mTweetTopics.findViewById(R.id.layout_list);

        mLayoutBottomBar = (LinearLayout) mTweetTopics.findViewById(R.id.layout_bottom_bar);

        mLayoutInfoBackground = (LinearLayout) mTweetTopics.findViewById(R.id.layout_info_background);

        //mListView = (ListView) mTweetTopics.findViewById(R.id.list);

        createListView();

        mLayoutList.addView(mListView.get());

        mFooterView = (LoadMoreListItem) mTweetTopics.getLayoutInflater().inflate(R.layout.load_more, null);
        mFooterView.setBackgroundDrawable(ImageUtils.createStateListDrawable(mTweetTopics, mThemeManager.getColor("list_background_row_color")));
        mFooterView.showFooterText();

        mSidebarTweet = (LinearLayout) mTweetTopics.findViewById(R.id.sidebar_menu_tweet);

        mSidebarTweet.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

        });


        mTweetTopics.setTitle(mTweetTopics.getText(R.string.app_name));


        refreshTheme();

        // botones para ejemplos

        mLayoutSamplesSearch = (LinearLayout) mTweetTopics.findViewById(R.id.layout_samples_search);

        mButtonSamplesSearch = (Button) mTweetTopics.findViewById(R.id.bt_samples_search);

        mButtonSamplesSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogSamples();
            }

        });

        loadGridSearch();


        app = (TwitterApplication) mTweetTopics.getApplication();

        ConnectionManager.getInstance().open(mTweetTopics);

        if (mUserStart > 0) {
            twitter = ConnectionManager.getInstance().getTwitter(mUserStart, true);
        } else {
            twitter = ConnectionManager.getInstance().getTwitter();
        }

        //OnAlarmReceiver.callAlarm(this);

        final Object data = mTweetTopics.getLastNonConfigurationInstance();
        if (data != null) {
            InfoTweetList itl = (InfoTweetList) data;
            mTypeLastColumn = itl.getColumn();
            mTypeList = itl.getTypeList();
            mTypeLastList = itl.getTypeList();
            mCurrentTypeStatus = itl.getTypeStatus();
            mCurrentTextTypeStatus = itl.getTextTypeStatus();
            mAdapterResponseList = itl.getResponseListAdapter();
            if (mTypeLastList == TYPE_LIST_SEARCH || mTypeLastList == TYPE_LIST_SEARCH_NOTIFICATIONS) {
                mCurrentId = itl.getSearch();
                mEntitySearch = new EntitySearch(mCurrentId);
            }
            mListView.setAdapter(mAdapterResponseList);
            mListView.setSelection(itl.getPosition());
            refreshTitle();
        } else {
            columnUser(mColumStart);
        }

        isFinishOnCreate = true;

    }


    public void onCreateOptionsMenu(Menu menu) {
        menu.add(0, NEW_ID, 0, R.string.new_search)
                .setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, SEARCH_USER_ID, 0, R.string.search_users)
                .setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, SIZE_TEXT_ID, 0, R.string.size)
                .setIcon(R.drawable.ic_menu_font_size);
        menu.add(0, PREFERENCES_ID, 0, R.string.preferences)
                .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, EXIT_ID, 0, R.string.exit)
                .setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, IMPORT_ID, 0, R.string.bt_import);
        menu.add(0, EXPORT_HTML_ID, 0, R.string.export_html);
        menu.add(0, TWEET_SAVED_ID, 0, R.string.tweets_saved);
        menu.add(0, TRENDING_ID, 0, R.string.view_trending_topics);
        menu.add(0, SAMPLES_ID, 0, R.string.samples_search);
        menu.add(0, MANAGER_USER_ID, 0, R.string.manager_user);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case NEW_ID:
                newSearch();
                return true;
            case SEARCH_USER_ID:
                searchUsers();
                return true;
            case SIZE_TEXT_ID:
                showSizeText();
                return true;
            case MANAGER_USER_ID:
                newUser();
                return true;
            case EXPORT_HTML_ID:
                export2HTML();
                return true;
            case IMPORT_ID:
                mTweetTopics.showDialog(DIALOG_IMPORT);
                return true;
            case TWEET_SAVED_ID:
                toDoReadAfter();
                return true;
            case TRENDING_ID:
                //showTrends(-1);
                showLocationsTrends();
                return true;
            case SAMPLES_ID:
                showDialogSamples();
                return true;
            case PREFERENCES_ID:
                Intent i = new Intent(mTweetTopics, Preferences.class);
                mTweetTopics.startActivityForResult(i, ACTIVITY_PREFERENCES);
                return true;
            case EXIT_ID:
                showDialogExit();
                return true;
        }
        return false;
    }

    protected void onResume() {

        try {
            DataFramework.getInstance().open(mTweetTopics, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Entity user = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (user == null) {
            newUser();
        }

        mTweetTopics.registerReceiver(receiver, new IntentFilter(Intent.ACTION_VIEW));

        if (app.isReloadUserTwitter()) {
            if (app.getLoad() == 2) {
                mLayoutInfoBackground.setVisibility(View.VISIBLE);
            }
        }

        PreferenceUtils.saveStatusWorkApp(mTweetTopics, true);
        app.setOnFinishTwitterDownload(this);

        if (isFinishOnCreate) {
            isFinishOnCreate = false;
            /*final Object data = mTweetTopics.getLastNonConfigurationInstance();
            if (data != null) {
            	Utils.showMessage(mTweetTopics, "datos");
            	mListView.setAdapter((ResponseListAdapter)data);
            } else {
            	columnUser(mColumStart);
            }*/
        } else {
            twitter = ConnectionManager.getInstance().getTwitterForceActiveUser();
        }

        if (PreferenceUtils.getShowHelp(mTweetTopics)) {
            showHelp();
        } else {
            PreferenceUtils.showChangeLog(mTweetTopics);
        }

    }

    protected void onPause() {
        mTweetTopics.unregisterReceiver(receiver);
        PreferenceUtils.saveStatusWorkApp(mTweetTopics, false);
        app.setOnFinishTwitterDownload(null);
    }

    private void onListItemClick(View v, int position, long id) {

        try {
            if (v.equals(mFooterView)) {

                loadOlderTweets();

            } else {

                if (listIsConversation) {

                    mPositionSelectedTweetConversation = position;
                    showSidebarTweet();

                    mAdapterStatusList.selectedRow(position);

                } else {
                    mPositionSelectedTweet = position;
                    if (mAdapterResponseList.getItem(position).getType() == RowResponseList.TYPE_MORE_TWEETS) {
                        showDialogMoreTweetDown();
                    } else {
                        showSidebarTweet();
                        mAdapterResponseList.selectedRow(position);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (result != null) {
                if (result.startsWith("tweettopics%%qr")) {
                    importSearch(result);
                }
                if (result.startsWith("tweettopics%%theme")) {
                    importTheme(result);
                }
            } else {
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.error_general));
            }
        }

        switch (requestCode) {
            case ACTIVITY_NEWEDITSEARCH:
                if (resultCode != 0) {
                    loadGridSearch();
                    Bundle extras = intent.getExtras();
                    if (extras.containsKey("view")) {
                        if (extras.getBoolean("view")) {
                            if (extras.containsKey(DataFramework.KEY_ID)) {
                                toDoSearch(extras.getLong(DataFramework.KEY_ID));
                            }
                        }
                    }
                }
                break;
            case ACTIVITY_PREFERENCES:
                refreshTheme();
                loadGridSearch();
                break;
            case ACTIVITY_WALLPAPER:
                refreshTheme();
                break;
            case ACTIVITY_COLORS_APP:
                refreshTheme();
                break;
            case ACTIVITY_NEWSTATUS:
                /*
    		if( resultCode != Activity.RESULT_CANCELED ) {
    			if (mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn == TIMELINE) {
    				reloadColumnUser(true);
    			}
    		}*/
                break;
            case ACTIVITY_USER:

                if (resultCode == Activity.RESULT_OK) { // ha cambiado de usuario
                    twitter = ConnectionManager.getInstance().getTwitterForceActiveUser();
                    if (DataFramework.getInstance().getEntityListCount("tweets_user", "user_tt_id=" + ConnectionManager.getInstance().getIdUserDB()) <= 0) {
                        columnUser(TIMELINE);
                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.load_data_first));
                    }
                    /*
    			Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
    			if (e!=null) {
    				app.loadUser(e.getId(), false);
    				twitter = app.getTwitter();
    				if (DataFramework.getInstance().getEntityListCount("tweets_user", "user_tt_id="+e.getId())<=0) {
    					columnUser(TIMELINE);
    					Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.load_data_first));
    				}
    			}*/
                }

                break;
        }

    }

    public Object onRetainNonConfigurationInstance() {
        InfoTweetList itl = new InfoTweetList();
        itl.setResponseListAdapter(mAdapterResponseList);
        int pos = mListView.getFirstVisiblePosition();
        itl.setPosition(pos);
        itl.setTypeList(mTypeList);
        if (mTypeLastList == TYPE_LIST_COLUMNUSER) {
            itl.setColumn(mTypeLastColumn);
        }
        itl.setSearch(mCurrentId);
        itl.setTypeStatus(mCurrentTypeStatus);
        itl.setTextTypeStatus(mCurrentTextTypeStatus);
        return itl;
    }

    protected void onDestroy() {
        DataFramework.getInstance().close();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (searchTask != null) {
                searchTask.cancel(true);
                searchTask = null;
                isGenericSearch = false;
                return false;
            }

            if (mSidebarTweet.getVisibility() == View.VISIBLE) {
                if (!isAnimationOutStarted) {
                    closeSidebar();
                }

                return false;
            }

            if (listIsConversation) {
                backToSearchFromConversation();
                return false;
            }

            // salvamos el ultimo id en caso de salir

            if (mTypeList == TYPE_LIST_COLUMNUSER) {
                markPositionLastReadAsLastReadId();
            }

        }
        return true;
    }

    protected void onSaveInstanceState(Bundle outState) {
//		outState.putString("file", file);

    }

    /*
     *
     * Funciones auxiliares
     *
     */

    /*
     *
     * retweet status
     *
     */

    private AsyncTask<Long, Void, String> exportHTML;

    public void export2HTML() {

        progressDialog = ProgressDialog.show(
                mTweetTopics,
                mTweetTopics.getResources().getString(R.string.export_html_title),
                mTweetTopics.getResources().getString(R.string.export_html_description)
        );

        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                if (exportHTML != null) exportHTML.cancel(true);
            }

        });

        if (listIsConversation) {
            exportHTML = new Export2HTMLAsyncTask(this, null, mAdapterStatusList).execute();
        } else {
            exportHTML = new Export2HTMLAsyncTask(this, mAdapterResponseList, null).execute();
        }

    }


    @Override
    public void export2HTMLCancelled() {
        if (exportHTML != null) exportHTML.cancel(true);
    }

    @Override
    public void export2HTMLLoaded(String file) {
        progressDialog.dismiss();
        if (file.equals("")) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
        } else {
            Intent msg = new Intent(Intent.ACTION_SEND);
            msg.setType("text/html");
            msg.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
            mTweetTopics.startActivity(Intent.createChooser(msg, mTweetTopics.getString(R.string.share)));
        }
    }

    @Override
    public void export2HTMLLoading() {

    }

    /*
	public void export2HTML2() {
		try {

			String f = Utils.appDirectory+"export_html_"+ System.currentTimeMillis() +".html";

			File file = new File(f);
    		if (file.exists()) file.delete();
    		FileOutputStream fOut = new FileOutputStream(f);
    		OutputStreamWriter osw = new OutputStreamWriter(fOut);

    		osw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n"
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<title>Listado de tweets</title>\n"
				+ "<style type=\"text/css\">\n"
				+ "body {\n"
				+ "padding: 10px;\n"
				+ "background-color: #89ace5;\n"
				+ "}\n"
				+ "div.tweet {\n"
				+ "font-family: Helvetica, Geneva, Arial, sans-serif;\n"
				+ "border: 2px solid #286cd9;\n"
				+ "background-color: #ffffff;\n"
				+ "-webkit-border-radius: 10px;\n"
				+ "moz-border-radius: 10px;\n"
				+ "border-radius: 10px;\n"
				+ "padding: 5px;\n"
				+ "margin-bottom: 10px;\n"
				+ "}\n"
				+ "div.username {\n"
				+ "font-size: 13px;\n"
				+ "font-weight: bolder;\n"
				+ "}\n"
				+ "div.date {\n"
				+ "font-size: 11px;\n"
				+ "color: #999999;\n"
				+ "}\n"
				+ ".image img {\n"
				+ "width: 50px;\n"
				+ "height: 50px;\n"
				+ "margin: 5px;\n"
				+ "float: left;\n"
				+ "}\n"
				+ "p {\n"
				+ "margin-left: 10px;\n"
				+ "font-size: 14px;\n"
				+ "}\n"
				+ "</style>\n"
				+ "</head>\n"
				+ "<body>\n");

			if (mIdConversation>0) {
				for (int i=0; i<mAdapterStatusList.getCount(); i++) {
					Status r = mAdapterStatusList.getItem(i);
					User user = r.getUser();
					osw.append("<div class=\"tweet\">\n"
        			+ "<div class=\"image\">\n"
        			+ "<img src=\""+user.getProfileImageURL().toString()+"\" />\n"
        			+ "</div>\n"
        			+ "<div class=\"username\">"+user.getScreenName()+"</div>\n"
        			+ "<p>"+r.getText()+"</p>\n"
        			+ "<div class=\"date\">"+r.getCreatedAt().toLocaleString()+"</div>\n"
        			+ "</div>\n");
        		}
        	} else {
        		for (int i=0; i<mAdapterResponseList.getCount(); i++) {
        			RowResponseList r = mAdapterResponseList.getItem(i);
        			osw.append("<div class=\"tweet\">\n"
        			+ "<div class=\"image\">\n"
        			+ "<img src=\""+r.getUrlAvatar()+"\" />\n"
        			+ "</div>\n"
        			+ "<div class=\"username\">"+r.getUsername()+"</div>\n"
        			+ "<p>"+r.getText()+"</p>\n"
        			+ "<div class=\"date\">"+r.getDate().toLocaleString()+"</div>\n"
        			+ "</div>\n");
        		}
        	}

			osw.append("</body>\n"
				+ "</html>");

            osw.flush();
            osw.close();

    		Intent msg=new Intent(Intent.ACTION_SEND);
			msg.setType("text/html");
			msg.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+f));
			mTweetTopics.startActivity(Intent.createChooser(msg, mTweetTopics.getString(R.string.share)));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
    public void createSearchUser(InfoUsers user, int which) {
        closeSidebar();
        Entity ent = new Entity("search");
        ent.setValue("date_create", Utils.now());
        ent.setValue("last_modified", Utils.now());
        ent.setValue("use_count", 0);
        if (user.getAvatar() != null) {
            ent.setValue("icon_id", 0);
            String tokenFile = Utils.createIconForSearch(user.getAvatar());
            ent.setValue("icon_token_file", tokenFile);
            ent.setValue("icon_big", "file/" + tokenFile + ".png");
            ent.setValue("icon_small", "file/" + tokenFile + "_small.png");
        } else {
            ent.setValue("icon_id", 1);
            ent.setValue("icon_big", "drawable/letter_user");
            ent.setValue("icon_small", "drawable/letter_user_small");
        }
        ent.setValue("is_temp", 1);
        ent.setValue("name", user.getName());
        if (which == 0) {
            ent.setValue("from_user", user.getName());
        } else if (which == 1) {
            ent.setValue("to_user", user.getName());
        }
        ent.save();
        deleteTempSearch();
        toDoSearch(ent.getId());
    }

    private View getLoadingView() {
        View v = mTweetTopics.getLayoutInflater().inflate(R.layout.loading, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        v.setPadding(5, 5, 5, 5);
        v.setLayoutParams(lp);
        v.setBackgroundColor(Color.parseColor("#99" + (mThemeManager.getTheme() == 1 ? "FFFFFF" : "000000")));
        return v;
    }

    private void setLayoutLoading() {
        if (!isLoadingData) {
            isLoadingData = true;
            mLayoutList.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            lp.gravity = Gravity.CENTER;

            LinearLayout ll = new LinearLayout(mTweetTopics);
            ll.addView(getLoadingView());
            ll.setLayoutParams(lp);

            mLayoutList.addView(ll, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }
    }

    private void setLayoutListView() {
        if (isLoadingData) {
            mListView.onRefreshComplete();
            isLoadingData = false;
            mLayoutList.removeAllViews();
            mLayoutList.addView(mListView.get());
        }
    }

    protected void refreshColorsBars() {
    }

    public void refreshTheme() {

        try {
            boolean hasWallpaper = false;
            File f = new File(Preferences.IMAGE_WALLPAPER);
            if (f.exists()) {
                try {
                    BitmapDrawable bmp = (BitmapDrawable) BitmapDrawable.createFromPath(Preferences.IMAGE_WALLPAPER);
                    bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
                    mLayoutBackgroundApp.setBackgroundDrawable(bmp);
                    hasWallpaper = true;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

            if (!hasWallpaper) {
                if (mThemeManager.getTheme() == ThemeManager.THEME_DEFAULT) {
                    mLayoutBackgroundApp.setBackgroundResource(R.drawable.background);
                } else {
                    mLayoutBackgroundApp.setBackgroundResource(R.drawable.background_dark);
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            mLayoutBackgroundApp.setBackgroundColor(Color.GRAY);
        } catch (NullPointerException e) {
            e.printStackTrace();
            mLayoutBackgroundApp.setBackgroundColor(Color.GRAY);
        } catch (Exception e) {
            e.printStackTrace();
            mLayoutBackgroundApp.setBackgroundColor(Color.GRAY);
        }
        mThemeManager.setColors();
        refreshColorsBars();
        refreshAdapters();
        refreshButtonsColumns();
    }

    public void refreshAdapters() {
        if (mAdapterResponseList != null) mAdapterResponseList.notifyDataSetChanged();
        if (mAdapterStatusList != null) mAdapterStatusList.notifyDataSetChanged();
    }

    private void dialogLoadTheme() {
        final ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("themes");

        if (ents.size() > 0) {
            CharSequence[] c = new CharSequence[ents.size()];
            for (int i = 0; i < ents.size(); i++) {
                c[i] = ents.get(i).getString("name");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
            builder.setTitle(R.string.load_theme);
            builder.setItems(c, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ColorsApp.loadTheme(mTweetTopics, ents.get(which).getString("theme"));
                    refreshTheme();
                }


            });
            builder.create();
            builder.show();
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_themes));
        }
    }

    private void createListView() {
        mListView = new TweetTopicsListView(mTweetTopics);
        mListView.setDivider(ImageUtils.createDividerDrawable(mTweetTopics, new ThemeManager(mTweetTopics).getColor("color_divider_tweet")));
        if (mTweetTopics.getPreference().getBoolean("prf_use_divider_tweet", true)) {
            mListView.setDividerHeight(2);
        } else {
            mListView.setDividerHeight(0);
        }
        mListView.setFadingEdgeLength(6);
        mListView.setCacheColorHint(mThemeManager.getColor("color_shadow_listview"));


        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                onListItemClick(v, position, id);
            }

        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != mFooterView) {
                    if (mAdapterResponseList != null) {
                        if (listIsConversation) {
                            mPositionSelectedTweetConversation = position;
                        } else {
                            if ((mAdapterResponseList.getItem(position).getType() != RowResponseList.TYPE_MORE_TWEETS)
                                    && (mAdapterResponseList.getItem(position).getType() != RowResponseList.TYPE_PUB)) {
                                mPositionSelectedTweet = position;
                            }
                        }


                        ArrayList<String> codes = PreferenceUtils.getArraySubMenuTweet(mTweetTopics);
                        if (codes.size() == 1) {
                            InfoTweet it = new InfoTweet(mAdapterResponseList.getItem(position));
                            if (it != null) {
                                return it.execByCode(codes.get(0), TweetTopicsCore.this, position);
                            }
                        }

                    }
                }
                return false;
            }


        });

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //mListView.setOnScroll(arg0, firstVisibleItem, visibleItemCount, totalItemCount);
                //if (mListView.isPullToRefresh() && firstVisibleItem >0) firstVisibleItem--;

                if (mTypeList == TYPE_LIST_COLUMNUSER && mListViewScroll) {
                    if (mPositionLastRead > firstVisibleItem) {
                        //Log.d(Utils.TAG, "firstVisibleItem: " + firstVisibleItem);
                        mPositionLastRead = firstVisibleItem;
                    }
                }
                /*
				if (app!=null && !app.isReloadUserTwitter()) {
					if (mListViewScroll && firstVisibleItem==0) {
						mListViewScroll = false;
						reloadNewMsgInCurrentColumns(0);
					}
				}
				*/
                if (app != null && (!app.isReloadUserTwitter() || (app.isReloadUserTwitter() && app.getLoad() == 2))) {
                    if (mListViewScroll && firstVisibleItem == 0) {
                        mListViewScroll = false;
                        reloadNewMsgInCurrentColumns(0);
                        markPositionLastReadAsLastReadId();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //mListView.setOnScrollStateChanged(view, scrollState);
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) closeSidebar();

                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //Log.d(Utils.TAG, "moviendo");
                    flinging = true;
                }

                if (scrollState != OnScrollListener.SCROLL_STATE_FLING && scrollState != SCROLL_STATE_TOUCH_SCROLL) {
                    //Log.d(Utils.TAG, "parado");
                    flinging = false;
                    //refreshAdapters();
                    TweetListItem.executeLoadTasks();
                }
            }

        });

    }

    private boolean flinging = false;

    public boolean isFlinging() {
        return flinging;
    }

    public ThemeManager getThemeManager() {
        return mThemeManager;
    }

    public boolean isSidebarOpen() {
        return mSidebarTweet.getVisibility() == View.VISIBLE;
    }


    public boolean openSidebar() {
        if (!isSidebarOpen()) {
            mSidebarTweet.setVisibility(View.VISIBLE);
            boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
            if (anim) {
                animationLayoutIn(mSidebarTweet);
            }
            return true;
        }
        return false;
    }

    public boolean closeSidebar() {
        if (isSidebarOpen()) {
            if (loadSidebarUser != null) loadSidebarUser.cancel(true);
            Sidebar.cancelLoadLinkAsyncTask();
            if (mSidebarTweet.getVisibility() == View.VISIBLE) {
                if (listIsConversation) {
                    if (mAdapterStatusList != null && mAdapterStatusList.hasSelectedRow()) {
                        mAdapterStatusList.unSelectedRow();
                    }
                } else {
                    if (mAdapterResponseList != null && mAdapterResponseList.hasSelectedRow()) {
                        mAdapterResponseList.unSelectedRow();
                    }
                }

                boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
                if (anim) {
                    animationLayoutOut(mSidebarTweet);
                } else {
                    mSidebarTweet.setVisibility(View.GONE);
                }
            }
            Log.d(Utils.TAG, "Sidebar cerrado");
            return true;
        }
        return false;
    }

    public void setFavoritedSelected(boolean b) {
        if (mAdapterResponseList != null) {
            if (mPositionSelectedTweet >= 0) {
                mAdapterResponseList.getItem(mPositionSelectedTweet).setFavorited(b);
            }
        }
    }

    public boolean isFavoritedSelected() {
        if (mAdapterResponseList != null) {
            if (mPositionSelectedTweet >= 0) {
                return mAdapterResponseList.getItem(mPositionSelectedTweet).isFavorited();
            }
        }
        return false;
    }

    public void searchUsers() {
        final EditText et = new EditText(mTweetTopics);
        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setTitle(mTweetTopics.getString(R.string.search_user));
        builder.setMessage(mTweetTopics.getString(R.string.search_user_msg));
        builder.setView(et);
        builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadTypeStatus(LoadTypeStatusAsyncTask.SEARCH_USERS, et.getText().toString());
            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogRetweetOptionsUser() {
        mTweetTopics.showDialog(DIALOG_RETWEETS_ACTIONS);
    }

    public void showListUser() {
        mTweetTopics.showDialog(DIALOG_LISTS_ACTIONS);
    }

    private void showDialogExit() {

        int minutes = Integer.parseInt(mTweetTopics.getPreference().getString("prf_time_notifications", "15"));

        if (minutes > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
            builder.setTitle(R.string.dialog_exit);
            builder.setMessage(R.string.dialog_exit_msg);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    PreferenceUtils.saveNotificationsApp(mTweetTopics, false);
                    mTweetTopics.finish();
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        } else {
            mTweetTopics.finish();
        }
    }

    private void showDialogColoringTweets() {

        final ArrayList<Entity> entsColor = DataFramework.getInstance().getEntityList("type_colors");

        ColoringTweetsAdapter ad = new ColoringTweetsAdapter(mTweetTopics, entsColor);


        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setTitle(R.string.colors);
        builder.setAdapter(ad, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Entity ent = new Entity("colors");
                ent.setValue("word", mUserNameSelected.getName());
                ent.setValue("type_id", 2);
                ent.setValue("type_color_id", entsColor.get(which).getId());
                ent.save();
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.color_add_user));
                refreshAdapters();
            }


        });
        builder.setPositiveButton(R.string.new_item, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent newuser = new Intent(mTweetTopics, Colors.class);
                mTweetTopics.startActivity(newuser);
            }
        });
        builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Entity color = DataFramework.getInstance().getTopEntity("colors", "type_id=2 and word=\"" + mUserNameSelected.getName() + "\"", "");
                if (color != null) {
                    color.delete();
                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.color_delete_user));
                    refreshAdapters();
                }
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    public void showDialogRetweet() {
        final ArrayList<String> phrases = new ArrayList<String>();
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("types_retweets");

        CharSequence[] c = new CharSequence[ents.size() + 3];
        c[0] = mTweetTopics.getString(R.string.retweet_now);
        phrases.add("_RN_");
        c[1] = mTweetTopics.getString(R.string.edit_message);
        phrases.add("_EM_");
        c[2] = mTweetTopics.getString(R.string.retweet_url);
        phrases.add("_RU_");
        for (int i = 0; i < ents.size(); i++) {
            c[i + 3] = mTweetTopics.getString(R.string.retweet) + " \"" + ents.get(i).getString("phrase") + "\" " + mTweetTopics.getString(R.string.now);
            phrases.add(ents.get(i).getString("phrase"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setTitle(R.string.retweet);
        builder.setItems(c, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phrase = phrases.get(which);
                InfoTweet it = getCurrentInfoTweet();
                if (phrase.equals("_RN_")) {
                    if (it != null) {
                        retweetStatus(it.getId());
                    }
                } else if (phrase.equals("_EM_")) {
                    if (it != null) {
                        updateStatus(NewStatus.TYPE_RETWEET, it.getText(), it);
                    }
                } else if (phrase.equals("_RU_")) {
                    if (it != null) {
                        updateStatus(NewStatus.TYPE_RETWEET, it.getUrlTweet(), it);
                    }
                } else {
                    if (it != null) {
                        String text = phrase + " RT: @" + it.getUsername() + ": " + it.getText();
                        if (text.length() > 140) {
                            updateStatus(NewStatus.TYPE_RETWEET, it.getText(), it, phrase);
                        } else {
                            updateStatus(text);
                        }
                    }
                }
                closeSidebar();
            }


        });
        builder.setNeutralButton(R.string.show_retweets, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent newstatus = new Intent(mTweetTopics, RetweetsTypes.class);
                mTweetTopics.startActivity(newstatus);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    private void deleteTempSearch() {
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("search", "is_temp=1", "date_create desc");
        if (ents.size() > 6) {
            Log.d(Utils.TAG, "Limpiando busquedas temporales");
            String date = ents.get(5).getString("date_create");
            String sqldelete = "DELETE FROM search WHERE is_temp=1 AND date_create  < '" + date + "'";
            DataFramework.getInstance().getDB().execSQL(sqldelete);
        }
    }

    public void showDialogHashTag(String hashtag) {
        mHashTagSelected = hashtag;
        mTweetTopics.showDialog(DIALOG_HASHTAG);
    }

    public void addFooter() {
        try {
            if (!hasFooter) {
                mListView.addFooterView(mFooterView);
                hasFooter = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void removeFooter() {
        try {
            if (hasFooter) {
                if (mListView != null && mFooterView != null) mListView.removeFooterView(mFooterView);
                hasFooter = false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showHelp() {
        ImageView help = (ImageView) mTweetTopics.findViewById(R.id.image_help);
        int res = R.drawable.help_en;
        if (Locale.getDefault().getLanguage().equals("es")) {
            res = R.drawable.help_es;
        }
        help.setImageResource(res);

        help.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideHelp();
            }

        });

        LinearLayout layout = (LinearLayout) mTweetTopics.findViewById(R.id.layout_image_help);

        layout.setVisibility(View.VISIBLE);

    }

    public void hideHelp() {
        ImageView help = (ImageView) mTweetTopics.findViewById(R.id.image_help);
        help.setImageBitmap(null);
        LinearLayout layout = (LinearLayout) mTweetTopics.findViewById(R.id.layout_image_help);
        layout.setVisibility(View.GONE);
    }

    public void goToDirectMessages() {
        if (mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn == DIRECTMESSAGES) {
            reloadColumnUser(true);
        } else {
            columnUser(DIRECTMESSAGES);
        }
    }

    public void goToTop() {
        mListView.setSelection(0);
    }

    public void goToBottom() {
        if (mAdapterResponseList != null) {
            mListView.setSelection(mAdapterResponseList.getCount() - 1);
        }
    }

    public void goToLastRead() {
        if (mAdapterResponseList != null) {
            mListView.setSelection(mAdapterResponseList.getLastReadPosition());
        }
    }

    public void showMap(InfoTweet it) {
        if (it.hasLocation()) {
            Intent map = new Intent(mTweetTopics, MapSearch.class);
            map.putExtra("longitude", it.getLongitude());
            map.putExtra("latitude", it.getLatitude());
            mTweetTopics.startActivity(map);
            //Utils.showMessage(this, "long: " + it.getLongitude() + " -- lat: " + it.getLatitude());
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_tweet_map));
        }
    }

    public void directMessage(String username) {
        /*boolean isFriend = false;

        Entity ent = mTweetTopics.getActiveUser();
        String screenName = "";
        if (ent!=null) {
            screenName = ent.getString("name");
        }

        try {
            isFriend = ConnectionManager.getInstance().getTwitter().existsFriendship(screenName, username);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        if (isFriend) {
	    	mPositionSelectedTweet = -1;
	    	Intent newstatus = new Intent(mTweetTopics, NewStatus.class);
	    	newstatus.putExtra("type", NewStatus.TYPE_DIRECT_MESSAGE);
	    	newstatus.putExtra("username_direct_message", username);
	    	mTweetTopics.startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
		} else {
			Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_is_follower));
		} */

        mPositionSelectedTweet = -1;
        Intent newstatus = new Intent(mTweetTopics, NewStatus.class);
        newstatus.putExtra("type", NewStatus.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", username);
        mTweetTopics.startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);

    }

    public void updateStatus(int type, String text, InfoTweet tweet) {
        updateStatus(type, text, tweet, "");
    }

    private void updateStatus(int type, String text, InfoTweet tweet, String prev) {
        mPositionSelectedTweet = -1;
        Intent newstatus = new Intent(mTweetTopics, NewStatus.class);
        newstatus.putExtra("text", text);
        newstatus.putExtra("type", type);
        newstatus.putExtra("retweet_prev", prev);
        if (tweet != null) {
            if (type == NewStatus.TYPE_REPLY || type == NewStatus.TYPE_REPLY_ON_COPY) {
                newstatus.putExtra("reply_tweetid", tweet.getId());
            }
            if (tweet.isRetweet()) {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatarRetweet());
                newstatus.putExtra("reply_screenname", tweet.getUsernameRetweet());
                newstatus.putExtra("reply_text", tweet.getTextRetweet());
            } else {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatar());
                newstatus.putExtra("reply_screenname", tweet.getUsername());
                newstatus.putExtra("reply_text", tweet.getText());
            }
        }
        mTweetTopics.startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
        closeSidebar();
    }

    private void sendBroadcastWidgets() {
        WidgetCounters4x1.updateAll(mTweetTopics);
        WidgetCounters2x1.updateAll(mTweetTopics);
    }

    protected void reloadNewMsgInSlide() {
    }

    private void reloadNewMsgInTimeline(int totalTimeline) {
        if (totalTimeline > 0) {
            mCountTimeline.setVisibility(View.VISIBLE);
            mCountTimeline.setImageBitmap(Utils.getBitmapNumber(mTweetTopics, totalTimeline, Color.RED, Utils.TYPE_RECTANGLE));
        } else {
            mCountTimeline.setVisibility(View.GONE);
        }
    }

    private void reloadNewMsgInMentions(int totalMentions) {
        if (totalMentions > 0) {
            mCountMentions.setVisibility(View.VISIBLE);
            mCountMentions.setImageBitmap(Utils.getBitmapNumber(mTweetTopics, totalMentions, Color.RED, Utils.TYPE_RECTANGLE));
        } else {
            mCountMentions.setVisibility(View.GONE);
        }
    }

    private void reloadNewMsgInDirectMessage(int totalDirectMessages) {
        if (totalDirectMessages > 0) {
            mCountDirectMessages.setVisibility(View.VISIBLE);
            mCountDirectMessages.setImageBitmap(Utils.getBitmapNumber(mTweetTopics, totalDirectMessages, Color.RED, Utils.TYPE_RECTANGLE));
        } else {
            mCountDirectMessages.setVisibility(View.GONE);
        }
    }

    private void reloadNewMsgInCurrentColumns(int total) {
        if (mTypeList == TYPE_LIST_COLUMNUSER) {
            if (mTypeLastColumn == TIMELINE) {
                reloadNewMsgInTimeline(total);
            }
            if (mTypeLastColumn == MENTIONS) {
                reloadNewMsgInMentions(total);
            }
            if (mTypeLastColumn == DIRECTMESSAGES) {
                reloadNewMsgInDirectMessage(total);
            }
        }
    }

    private void reloadNewMsgInAllColumns() {
        reloadNewMsgInAllColumns(true, true, true);
    }

    private void reloadNewMsgInAllColumns(boolean timeline, boolean mentions, boolean directs) {
        mListViewScroll = true;
        int totalTimeline = 0;
        int totalMentions = 0;
        int totalDirectMessages = 0;
        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {

            if (timeline) {
                if (e.getInt("no_save_timeline") != 1) {
                    totalTimeline = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TIMELINE
                            + " AND user_tt_id=" + e.getId() + " AND tweet_id >'" + Utils.fillZeros("" + e.getString("last_timeline_id")) + "'");
                }
            }

            if (mentions) {
                totalMentions = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + MENTIONS
                        + " AND user_tt_id=" + e.getId() + " AND tweet_id >'" + Utils.fillZeros("" + e.getString("last_mention_id")) + "'");
            }

            if (directs) {
                totalDirectMessages = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + DIRECTMESSAGES
                        + " AND user_tt_id=" + e.getId() + " AND tweet_id >'" + Utils.fillZeros("" + e.getString("last_direct_id")) + "'");
            }
        }

        if (timeline) reloadNewMsgInTimeline(totalTimeline);

        if (mentions) reloadNewMsgInMentions(totalMentions);

        if (directs) reloadNewMsgInDirectMessage(totalDirectMessages);


    }

    protected void refreshButtonsColumns() {
    }

    private boolean markPositionLastReadFirstVisible() {
        if (mTypeList == TYPE_LIST_COLUMNUSER) {
            if (mTypeLastColumn == TIMELINE || mTypeLastColumn == MENTIONS || mTypeLastColumn == DIRECTMESSAGES) {
                if (mListView != null) {
                    mPositionLastRead = mListView.getFirstVisiblePosition();
                    return markPositionLastReadAsLastReadId();
                }
            }
        }
        return false;
    }

    public boolean markLastReadId(int position, long id) {
        if (mEntityUser != null) {
            if (mTypeList == TYPE_LIST_COLUMNUSER) {
                if (mTypeLastColumn == TIMELINE || mTypeLastColumn == MENTIONS || mTypeLastColumn == DIRECTMESSAGES) {
                    mEntityUser.setValueLastId(id + "");
                    mEntityUser.save();
                    if (mAdapterResponseList != null) mAdapterResponseList.itemIsLastRead(position);
                    reloadNewMsgInAllColumns();
                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.mark_last_noread));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean markPositionLastReadAsLastReadId() {
        sendBroadcastWidgets();
        if (mTypeList == TYPE_LIST_COLUMNUSER) {
            if (mAdapterResponseList != null) {
                if (!mAdapterResponseList.isUserLastItemLastRead()) {
                    if (mAdapterResponseList.getCount() > mPositionLastRead) {
                        try {
                            long id = mAdapterResponseList.getItem(mPositionLastRead).getTweetId();
                            Entity user = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                            if (user != null) {
                                if (mTypeLastColumn == TIMELINE) {
                                    user.setValue("last_timeline_id", id + "");
                                    user.save();
                                } else if (mTypeLastColumn == MENTIONS) {
                                    user.setValue("last_mention_id", id + "");
                                    user.save();
                                } else if (mTypeLastColumn == DIRECTMESSAGES) {
                                    user.setValue("last_direct_id", id + "");
                                    user.save();
                                }
                                return true;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }
        return false;
    }


    public void importSearch(String text) {
        if (Utils.importSearch(mTweetTopics, text)) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.import_correct));
            loadGridSearch();
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.import_no_correct));
        }
    }

    public void importTheme(String text) {
        if (Utils.importTheme(mTweetTopics, text)) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.import_correct));
            refreshTheme();
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.import_no_correct));
        }
    }

    public void showSizeText() {

        final int minValue = 6;

        LayoutInflater factory = LayoutInflater.from(mTweetTopics);
        final View sizesFontView = factory.inflate(R.layout.alert_dialog_sizes_font, null);

        ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(mTweetTopics.getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(mTweetTopics) + ")");
        ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(mTweetTopics.getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(mTweetTopics) + ")");

        SeekBar sbSizeTitles = (SeekBar) sizesFontView.findViewById(R.id.sb_size_titles);

        sbSizeTitles.setMax(18);
        sbSizeTitles.setProgress(PreferenceUtils.getSizeTitles(mTweetTopics) - minValue);

        sbSizeTitles.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeTitles(mTweetTopics, progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(mTweetTopics.getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(mTweetTopics) + ")");
                mAdapterResponseList.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        SeekBar sbSizeText = (SeekBar) sizesFontView.findViewById(R.id.sb_size_text);
        sbSizeText.setMax(18);
        sbSizeText.setProgress(PreferenceUtils.getSizeText(mTweetTopics) - minValue);

        sbSizeText.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeText(mTweetTopics, progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(mTweetTopics.getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(mTweetTopics) + ")");
                mAdapterResponseList.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setTitle(R.string.font_size);
        builder.setView(sizesFontView);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();

    }

    public void showDialogSamples() {
        final boolean[] samplesChecked = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};

        final CheckBox cb = new CheckBox(mTweetTopics);
        cb.setText(R.string.samples_search_lang);
        cb.setTextColor(Color.GRAY);

        AlertDialog builder = new AlertDialog.Builder(mTweetTopics)
                .setTitle(R.string.samples_search)
                .setView(cb)
                .setMultiChoiceItems(R.array.actions_samples_search,
                        new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                                samplesChecked[whichButton] = isChecked;
                            }
                        })
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String[] names = mTweetTopics.getResources().getStringArray(R.array.actions_samples_search);
                        String lang = "";
                        if (cb.isChecked()) {
                            String[] langs = mTweetTopics.getResources().getStringArray(R.array.languages_values);
                            for (int l = 0; l < langs.length; l++) {
                                if (langs[l].equals(Locale.getDefault().getLanguage())) {
                                    lang = Locale.getDefault().getLanguage();
                                }
                            }
                        }
                        for (int i = 0; i < samplesChecked.length; i++) {
                            if (samplesChecked[i]) {
                                Entity ent = new Entity("search");
                                ent.setValue("name", names[i]);
                                ent.setValue("date_create", Utils.now());
                                ent.setValue("last_modified", Utils.now());
                                ent.setValue("use_count", 0);
                                if (i == 0) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i == 1) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("words_or", "juego juegos videojuegos videjuego game games");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i == 2) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("words_or", "app aplicacion aplication");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i == 3) {
                                    ent.setValue("words_and", "tilt shift");
                                    ent.setValue("filter", 2);
                                    ent.setValue("icon_id", 17);
                                    ent.setValue("icon_big", "drawable/icon_photo");
                                    ent.setValue("icon_small", "drawable/icon_photo_small");
                                } else if (i == 4) {
                                    ent.setValue("words_and", "hdr");
                                    ent.setValue("filter", 2);
                                    ent.setValue("icon_id", 18);
                                    ent.setValue("icon_big", "drawable/icon_photo2");
                                    ent.setValue("icon_small", "drawable/icon_photo2_small");
                                } else if (i == 5) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "true blood");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_t");
                                    ent.setValue("icon_small", "drawable/letter_t_small");
                                } else if (i == 6) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "walking dead");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_w");
                                    ent.setValue("icon_small", "drawable/letter_w_small");
                                } else if (i == 7) {
                                    ent.setValue("words_and", "4 8 15 16 23 42");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_n");
                                    ent.setValue("icon_small", "drawable/letter_n_small");
                                } else if (i == 8) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_or", "geek \"humor geek\"");
                                    ent.setValue("filter", 5);
                                    ent.setValue("icon_id", 14);
                                    ent.setValue("icon_big", "drawable/icon_news");
                                    ent.setValue("icon_small", "drawable/icon_news_small");
                                } else if (i == 9) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_or", "receta recipe");
                                    ent.setValue("filter", 5);
                                    ent.setValue("icon_id", 14);
                                    ent.setValue("icon_big", "drawable/icon_news");
                                    ent.setValue("icon_small", "drawable/icon_news_small");
                                } else if (i == 10) {
                                    ent.setValue("words_and", "slow motion");
                                    ent.setValue("filter", 3);
                                    ent.setValue("icon_id", 3);
                                    ent.setValue("icon_big", "drawable/icon_cinema");
                                    ent.setValue("icon_small", "drawable/icon_cinema_small");
                                } else if (i == 11) {
                                    ent.setValue("words_and", "stop motion");
                                    ent.setValue("filter", 3);
                                    ent.setValue("icon_id", 4);
                                    ent.setValue("icon_big", "drawable/icon_cinema2");
                                    ent.setValue("icon_small", "drawable/icon_cinema2_small");
                                }
                                ent.save();
                            }
                        }
                        loadGridSearch();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        builder.show();
    }

    public static boolean isTypeList(int t) {
        if (mTypeList == t)
            return true;
        else
            return false;
    }

    public static boolean isTypeLastColumn(int t) {
        if (mTypeLastColumn == t)
            return true;
        else
            return false;
    }

    protected void onClickIconActivity() {
        if ((mTypeList == TYPE_LIST_CONVERSATION) || (mTypeList == TYPE_LIST_READAFTER)) {

        }
        if (mTypeList == TYPE_LIST_COLUMNUSER) {

        }
        if ((mTypeList == TYPE_LIST_SEARCH_NOTIFICATIONS) || (mTypeList == TYPE_LIST_SEARCH)) {

        }
    }

    public int getPositionTweet() {
        int pos = -1;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mPositionSelectedTweetConversation >= 0) {
                if (mAdapterResponseList != null) pos = mPositionSelectedTweetConversation;
            }
        } else {
            if (mPositionSelectedTweet >= 0) {
                if (mAdapterResponseList != null) pos = mPositionSelectedTweet;
            }
        }

        return pos;
    }

    public InfoTweet getInfoTweet(int pos) {
        InfoTweet it = null;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (pos >= 0) {
                if (mAdapterResponseList != null) it = new InfoTweet(mAdapterStatusList.getItem(pos));
            }
        } else {
            if (pos >= 0) {
                if (mAdapterResponseList != null) it = new InfoTweet(mAdapterResponseList.getItem(pos));
            }
        }

        return it;
    }


    public InfoTweet getCurrentInfoTweet() {
        InfoTweet it = null;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mPositionSelectedTweetConversation >= 0) {
                if (mAdapterStatusList != null)
                    it = new InfoTweet(mAdapterStatusList.getItem(mPositionSelectedTweetConversation));
            }
        } else {
            if (mPositionSelectedTweet >= 0) {
                if (mAdapterResponseList != null)
                    it = new InfoTweet(mAdapterResponseList.getItem(mPositionSelectedTweet));
            }
        }

        return it;
    }

    public boolean isBOFTweet(int pos) {
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mAdapterStatusList != null) {
                return pos <= 0;
            }
        } else {
            if (mAdapterResponseList != null) {
                return pos <= 0;
            }
        }

        return false;
    }

    public boolean isBOFTweet() {
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mAdapterStatusList != null) {
                return mPositionSelectedTweetConversation < 0;
            }
        } else {
            if (mAdapterResponseList != null) {
                return mPositionSelectedTweet < 0;
            }
        }

        return false;
    }

    public boolean isEOFTweet(int pos) {
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mAdapterStatusList != null) {
                return pos >= mAdapterStatusList.getCount();
            }
        } else {
            if (mAdapterResponseList != null) {
                return pos >= mAdapterResponseList.getCount();
            }
        }

        return true;
    }

    public boolean isEOFTweet() {
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mAdapterStatusList != null) {
                return mPositionSelectedTweetConversation >= mAdapterStatusList.getCount();
            }
        } else {
            if (mAdapterResponseList != null) {
                return mPositionSelectedTweet >= mAdapterResponseList.getCount();
            }
        }

        return true;
    }

    public void setTypeList(int t) {
        mTypeLastList = mTypeList;
        mTypeList = t;
    }

    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) mTweetTopics.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
        Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.copied_to_clipboard));
    }

    public void newSearch() {
        Intent newsearch = new Intent(mTweetTopics, TabNewEditSearch.class);
        mTweetTopics.startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void newUser() {
        Intent newuser = new Intent(mTweetTopics, Users.class);
        mTweetTopics.startActivityForResult(newuser, ACTIVITY_USER);
    }

    /*
     *
     * Animations
     *
     */

    private void animationLayoutIn(LinearLayout l) {
        animationLayoutIn(l, Utils.TYPE_ANIM_RIGHT);
    }

    private void animationLayoutIn(LinearLayout l, int type) {
        int anim = R.anim.inlayout_right_left;
        if (type == Utils.TYPE_ANIM_TOP) {
            anim = R.anim.inlayout_top_bottom;
        } else if (type == Utils.TYPE_ANIM_BOTTOM) {
            anim = R.anim.inlayout_bottom_top;
        }
        LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mTweetTopics, anim);
        l.setLayoutAnimation(lac);
        l.setLayoutAnimationListener(null);
        l.startLayoutAnimation();
    }

    private void animationLayoutOut(final LinearLayout l) {
        animationLayoutOut(l, Utils.TYPE_ANIM_RIGHT);
    }

    private void animationLayoutOut(final LinearLayout l, int type) {
        int anim = R.anim.outlayout_left_right;
        if (type == Utils.TYPE_ANIM_TOP) {
            anim = R.anim.outlayout_bottom_top;
        } else if (type == Utils.TYPE_ANIM_BOTTOM) {
            anim = R.anim.outlayout_top_bottom;
        }

        l.setVisibility(View.GONE);
        l.setVisibility(View.VISIBLE);
        LayoutAnimationController lac = AnimationUtils.loadLayoutAnimation(mTweetTopics, anim);
        l.setLayoutAnimation(lac);
        l.setLayoutAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation lac) {
                isAnimationOutStarted = false;
                l.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationOutStarted = true;
            }

        });

        l.startLayoutAnimation();
    }

    public class AnimationRotation {

        final public static int TYPE_CONVERSATION = 0;
        final public static int TYPE_TRANSLATE = 1;
        final public static int TYPE_MENU = 2;

        private Status mStatusConversation;
        private int typeRotate = 0;
        private int type = 0;

        public AnimationRotation() {
        }

        public void goToConversation(Status st) {
            type = TYPE_CONVERSATION;
            mStatusConversation = st;

            if (mConversationStatusList == null)
                mConversationStatusList = new ArrayList<Status>();

            mConversationStatusList.clear();
            mConversationStatusList.add(st);

            boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
            if (anim) {
                applyRotation();
            } else {
                setViewConversation();
            }
        }

        public void goToMenu() {
            type = TYPE_MENU;
            boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
            if (anim) {
                applyRotation();
            } else {
                setViewMenuSidebar();
            }
        }

        public void goToTranslate() {
            type = TYPE_TRANSLATE;
            boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
            if (anim) {
                applyRotation();
            } else {
                setViewTranslate();
            }
        }

        public void goToRetweetersInfo() {
            type = TYPE_LIST_RETWEETERS;
            boolean anim = mTweetTopics.getPreference().getBoolean("prf_animations", true);
            if (anim) {
                applyRotation();
            } else {
                setViewRetweetersInfo();
            }
        }

        public void applyRotation() {
            float centerX = mSidebarMenu.getWidth() / 2.0f;
            float centerY = mSidebarMenu.getHeight() / 2.0f;
            float start = 0;
            float end = 90;
            if (typeRotate == 1) {
                start = 270;
                end = 360;
            }
            Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, typeRotate == 0 ? true : false);
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            if (typeRotate == 0) {
                rotation.setInterpolator(new AccelerateInterpolator());
                rotation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (type == TYPE_CONVERSATION) {
                            setViewConversation();
                        } else if (type == TYPE_MENU) {
                            setViewMenuSidebar();
                        } else if (type == TYPE_TRANSLATE) {
                            setViewTranslate();
                        } else if (type == TYPE_LIST_RETWEETERS) {
                            setViewRetweetersInfo();
                        }
                        typeRotate = 1;
                        applyRotation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                });
            } else {
                rotation.setInterpolator(new DecelerateInterpolator());
            }


            mSidebarMenu.startAnimation(rotation);
        }

        public void setViewConversation() {
            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.VISIBLE);
            mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarMenu.addView(Sidebar.getViewConversationSidebar(mTweetTopics, TweetTopicsCore.this, mStatusConversation), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }

        public void setViewMenuSidebar() {
            SidebarMenu menu = new SidebarMenu(TweetTopicsCore.this);
            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.VISIBLE);
            mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarMenu.addView(menu.getView(), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarContent.setVisibility(View.VISIBLE);
        }

        public void setViewTranslate() {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
            ll.gravity = Gravity.CENTER;

            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.VISIBLE);
            mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarMenu.addView(Sidebar.getLoadingView(mTweetTopics, mTweetTopics.getString(R.string.translating)), ll);
            mSidebarContent.setVisibility(View.GONE);
        }

        public void setViewRetweetersInfo() {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
            ll.gravity = Gravity.CENTER;

            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.VISIBLE);
            mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarMenu.addView(Sidebar.getLoadingView(mTweetTopics, mTweetTopics.getString(R.string.loading)), ll);
            mSidebarContent.setVisibility(View.GONE);
        }

    }

    /*
     *
     * Sidebars
     *
     */

    public void goToLink(InfoLink il) {
        goToLink(il.getOriginalLink());
    }

    public void goToLink(String url) {
        try {
            //markPositionLastReadAsLastReadId();
            markPositionLastReadFirstVisible();
            if (url.startsWith("www")) {
                url = "http://" + url;
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mTweetTopics.startActivity(intent);
        } catch (Exception e) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.error_view_url) + " " + url);
        }
    }

    public void showSidebarLink(final String link) {

        mTypeSidebar = TYPE_SIDEBAR_LINK;

        InfoLink il = CacheData.getInfoLinkCaches(link);

        if (il != null) {
            showSidebarLink(il);
            return;
        }

        mSidebarHead.removeAllViews();
        mSidebarHead.setVisibility(View.GONE);

        mSidebarMenu.removeAllViews();
        mSidebarMenu.setVisibility(View.GONE);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
        ll.gravity = Gravity.CENTER;

        mSidebarContent.setVisibility(View.VISIBLE);
        mSidebarContent.removeAllViews();
        mSidebarContent.setLayoutParams(ll);
        mSidebarContent.addView(Sidebar.getLoadingView(mTweetTopics), ll);

        mSidebarFoot.removeAllViews();
        mSidebarFoot.setVisibility(View.GONE);

        openSidebar();

        new PreparingLinkForSidebarAsyncTask(new PreparingLinkForSidebarAsyncTaskResponder() {
            @Override
            public void preparingLinkLoading() {
            }

            @Override
            public void preparingLinkCancelled() {
            }

            @Override
            public void preparingLinkLoaded(Boolean bool) {
                if (bool) {
                    showSidebarLink(CacheData.getInfoLinkCaches(link));
                }
            }
        }).execute(link);

    }

    public void showSidebarLink(InfoLink il) {
        showSidebarLink(il, -1);
    }

    public void showSidebarLink(InfoLink il, int positionTweet) {

        if (il == null) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            closeSidebar();
        } else {

            mTypeSidebar = TYPE_SIDEBAR_LINK;

            mCurrentInfoLink = il;

            if (positionTweet >= 0) {
                if (mTypeList == TYPE_LIST_CONVERSATION) {
                    mPositionSelectedTweetConversation = positionTweet;
                } else {
                    mPositionSelectedTweet = positionTweet;
                }
            }

            if (il.getType() == Utils.TYPE_LINK_TWEETOPICS_QR) {
                final String link = il.getLink();
                AlertDialog.Builder qrDialog = new AlertDialog.Builder(mTweetTopics);
                qrDialog.setTitle(R.string.title_tweettopics_qr);
                qrDialog.setMessage(R.string.msg_tweettopics_qr);
                qrDialog.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        importSearch(link);
                    }
                });
                qrDialog.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                qrDialog.show();
                return;
            }

            if (il.getType() == Utils.TYPE_LINK_TWEETOPICS_THEME) {
                final String link = il.getLink();
                AlertDialog.Builder qrDialog = new AlertDialog.Builder(mTweetTopics);
                qrDialog.setTitle(R.string.title_tweettopics_theme);
                qrDialog.setMessage(R.string.msg_tweettopics_theme);
                qrDialog.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        importTheme(link);
                    }
                });
                qrDialog.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                qrDialog.show();
                return;
            }
            /*
	    	if (il.getType()==Utils.TYPE_LINK_TWEET) {

				return;
	    	}
	    	*/
            boolean openWindows = mTweetTopics.getPreference().getBoolean("prf_show_window_in_links", true);

            if ((il.getLink().endsWith(".pdf"))) {
                openWindows = false;
            }

            if (openWindows) {

                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);

                mSidebarHead.removeAllViews();
                mSidebarHead.setVisibility(View.VISIBLE);
                mSidebarHead.addView(Sidebar.getViewLinkHeadSidebar(mTweetTopics, this, il), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                mSidebarContent.removeAllViews();
                mSidebarContent.setVisibility(View.VISIBLE);

                mSidebarContent.setLayoutParams(ll);
                mSidebarContent.addView(Sidebar.getViewLinkContentSidebar(mTweetTopics, this, il), ll);

                mSidebarMenu.removeAllViews();
                mSidebarMenu.setVisibility(View.GONE);

                mSidebarFoot.removeAllViews();
                mSidebarFoot.setVisibility(View.VISIBLE);
                mSidebarFoot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                mSidebarFoot.addView(Sidebar.getViewLinkFootSidebar(mTweetTopics, this, il), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                if (!openSidebar()) {
                    animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
                    animationLayoutIn(mSidebarContent);
                    animationLayoutIn(mSidebarFoot, Utils.TYPE_ANIM_BOTTOM);
                }

            } else {
                goToLink(il.getOriginalLink());
                /*
	    		String u = il.getOriginalLink();
	    		try {
		    		if (u.startsWith("www")) {
		    			u = "http://"+il.getOriginalLink();
		    		}
		    		Uri uri = Uri.parse(u);
		    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		    		mTweetTopics.startActivity(intent);
	    		} catch (Exception e) {
					Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.error_view_url) + " " + u);
				}*/
            }

        }

    }

    public void refreshLink(InfoLink il) {
        if (il != null) {
            if (mCurrentInfoLink == il) {
                mSidebarContent.removeAllViews();
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
                ll.gravity = Gravity.CENTER;
                mSidebarContent.addView(Sidebar.getViewLinkContentSidebar(mTweetTopics, this, il), ll);
            }
        } else {
            goToLink(mCurrentInfoLink);
        }
    }

    public void showSidebarConversation(Status mStatusConversation) {
        mSidebarContent.setVisibility(View.GONE);

        new AnimationRotation().goToConversation(mStatusConversation);

    }

    public void loadSidebarTranslate(long id) {

        new AnimationRotation().goToTranslate();

        new LoadTranslateTweetAsyncTask(mTweetTopics, new LoadTranslateTweetAsyncAsyncTaskResponder() {

            @Override
            public void translateLoading() {
            }

            @Override
            public void translateCancelled() {
            }

            @Override
            public void translateLoaded(InfoUsers iu) {
                showSidebarTranslate(iu);
            }

        }).execute(id);

    }

    public void showSidebarTranslate(InfoUsers iu) {
        if (iu == null) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            closeSidebar();
        } else {
            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.VISIBLE);
            mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarMenu.addView(Sidebar.getViewTranslateSidebar(mTweetTopics, this, iu), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarContent.setVisibility(View.GONE);
        }
    }

    public void loadSidebarRetweeters(InfoTweet infoTweet) {

        if (infoTweet == null) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            closeSidebar();
        } else {
            new AnimationRotation().goToRetweetersInfo();

            new StatusRetweetersAsyncTask(mTweetTopics, new StatusRetweetersAsyncTaskResponder() {

                @Override
                public void statusRetweetersLoading() {
                }

                @Override
                public void statusRetweetersCancelled() {
                }

                @Override
                public void statusRetweetersLoaded(StatusRetweetersResult result) {
                    showSidebarRetweetersInfo(result);
                }

            }).execute(infoTweet.getId());
        }
    }

    public void showSidebarRetweetersInfo(StatusRetweetersResult result) {
        if (result.error) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            closeSidebar();
        } else {
            if (result.retweeters_list.size() == 0) {
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_retweeters));
                this.backToMenuSidebar();
            } else {
                mSidebarMenu.removeAllViews();
                mSidebarMenu.setVisibility(View.VISIBLE);
                mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                mSidebarMenu.addView(Sidebar.getViewRetweetersSidebar(mTweetTopics, this, result.retweeters_list), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                mSidebarContent.setVisibility(View.GONE);
            }
        }
    }

    public void backToMenuSidebar() {
        new AnimationRotation().goToMenu();
    }

    public void showSidebarTweet() {
        // TODO Cambio para usar la nueva actividad de tweet
        Intent intent = new Intent(mTweetTopics, TweetActivity.class);
        mTweetTopics.startActivity(intent);
        /*
        mTypeSidebar = TYPE_SIDEBAR_TWEET;

        mSidebarHead.removeAllViews();
        mSidebarHead.setVisibility(View.VISIBLE);
        mSidebarHead.addView(Sidebar.getViewTweetHeadSidebar(mTweetTopics, this));

        SidebarGalleryLinks gallery = new SidebarGalleryLinks(this, getPositionTweet());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
        ll.gravity = Gravity.CENTER;

        mSidebarContent.removeAllViews();
        mSidebarContent.setVisibility(View.VISIBLE);
        mSidebarContent.setLayoutParams(ll);
        mSidebarContent.addView(gallery.getView(), ll);

        SidebarMenu menu = new SidebarMenu(this);

        mSidebarMenu.removeAllViews();
        mSidebarMenu.setVisibility(View.VISIBLE);
        mSidebarMenu.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        mSidebarMenu.addView(menu.getView(), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        mSidebarFoot.setVisibility(View.GONE);
        mSidebarFoot.removeAllViews();

        if (!openSidebar()) {
            animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_RIGHT);
            animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_RIGHT);
            animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_RIGHT);
        }
        */
    }

    /*
    private void setPosition(int pos) {
	    if (mTypeList == TYPE_LIST_CONVERSATION) {
			mPositionSelectedTweetConversation = pos;
		} else {
			mPositionSelectedTweet = pos;
		}
    }
    */
    private void prevSidebarLink() {

        int pos = -1;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            pos = mPositionSelectedTweetConversation;
        } else {
            pos = mPositionSelectedTweet;
        }

        InfoTweet it = getCurrentInfoTweet();
        if (it != null) {

            ArrayList<String> links = Utils.pullLinksHTTP(it.getText());
            int posLink = -1;

            try {
                if (links.contains(mCurrentInfoLink.getOriginalLink())) {
                    posLink = links.indexOf(mCurrentInfoLink.getOriginalLink());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (posLink >= 0) {

                if (posLink == 0) {

                    boolean search = true;

                    while (search) {
                        pos--;
                        //Log.d(Utils.TAG, pos+"");
                        if (pos >= 0) {
                            InfoTweet prevIt = getInfoTweet(pos);
                            if (prevIt != null) {
                                ArrayList<String> prevLinks = Utils.pullLinksHTTP(prevIt.getText());
                                if (prevLinks.size() > 0) {
                                    search = false;
                                    final int posfinal = pos;
                                    final String prevLink = prevLinks.get(prevLinks.size() - 1);
                                    InfoLink il = CacheData.getInfoLinkCaches(prevLink);
                                    if (il != null) {
                                        showSidebarLink(il, pos);
                                        animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
                                        animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_TOP);
                                        animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_TOP);
                                    } else {
                                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.preparing_link));
                                        new PreparingLinkForSidebarAsyncTask(new PreparingLinkForSidebarAsyncTaskResponder() {
                                            @Override
                                            public void preparingLinkLoading() {
                                            }

                                            @Override
                                            public void preparingLinkCancelled() {
                                            }

                                            @Override
                                            public void preparingLinkLoaded(Boolean bool) {
                                                InfoLink il = CacheData.getInfoLinkCaches(prevLink);
                                                if (il != null) {
                                                    showSidebarLink(il, posfinal);
                                                    animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
                                                    animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_TOP);
                                                    animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_TOP);
                                                } else {
                                                    Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.error_general));
                                                }
                                            }
                                        }).execute(prevLink);
                                    }
                                }
                            } else {
                                search = false;
                                Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_links));
                            }
                        } else {
                            search = false;
                            Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_links));
                        }
                    }


                    //} else if (posLink>=links.size()-1) {
                } else {
                    InfoLink il = CacheData.getInfoLinkCaches(links.get(posLink - 1));
                    if (il != null) {
                        showSidebarLink(il, pos);
                        animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
                        animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_TOP);
                        animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_TOP);
                    } else {
                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.preparing_link));
                    }
                }
            }
        }

    }

    private void nextSidebarLink() {

        int pos = -1;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            pos = mPositionSelectedTweetConversation;
        } else {
            pos = mPositionSelectedTweet;
        }

        InfoTweet it = getCurrentInfoTweet();
        if (it != null) {

            ArrayList<String> links = Utils.pullLinksHTTP(it.getText());
            int posLink = -1;

            if (links.contains(mCurrentInfoLink.getOriginalLink())) {
                posLink = links.indexOf(mCurrentInfoLink.getOriginalLink());
            }

            if (posLink >= 0) {

                if (posLink >= links.size() - 1) {

                    boolean search = true;

                    while (search) {
                        pos++;
                        //Log.d(Utils.TAG, pos+"");
                        if (!isEOFTweet(pos)) {
                            InfoTweet nextIt = getInfoTweet(pos);
                            if (nextIt != null) {
                                ArrayList<String> nextLinks = Utils.pullLinksHTTP(nextIt.getText());
                                if (nextLinks.size() > 0) {
                                    search = false;
                                    final String nextLink = nextLinks.get(0);
                                    final int posfinal = pos;
                                    //Log.d(Utils.TAG, "encontrado "+nextLink);
                                    InfoLink il = CacheData.getInfoLinkCaches(nextLink);
                                    if (il != null) {
                                        showSidebarLink(il, pos);
                                        animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_BOTTOM);
                                        animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_BOTTOM);
                                        animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_BOTTOM);
                                    } else {
                                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.preparing_link));
                                        new PreparingLinkForSidebarAsyncTask(new PreparingLinkForSidebarAsyncTaskResponder() {
                                            @Override
                                            public void preparingLinkLoading() {
                                            }

                                            @Override
                                            public void preparingLinkCancelled() {
                                            }

                                            @Override
                                            public void preparingLinkLoaded(Boolean bool) {
                                                InfoLink il = CacheData.getInfoLinkCaches(nextLink);
                                                if (il != null) {
                                                    showSidebarLink(il, posfinal);
                                                    animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_BOTTOM);
                                                    animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_BOTTOM);
                                                    animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_BOTTOM);
                                                } else {
                                                    Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.error_general));
                                                }
                                            }
                                        }).execute(nextLink);
                                    }
                                }
                            } else {
                                search = false;
                                Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_links));
                            }
                        } else {
                            search = false;
                            Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_links));
                        }
                    }


                    //} else if (posLink>=links.size()-1) {
                } else {
                    InfoLink il = CacheData.getInfoLinkCaches(links.get(posLink + 1));
                    if (il != null) {
                        showSidebarLink(il, pos);
                        animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_BOTTOM);
                        animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_BOTTOM);
                        animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_BOTTOM);
                    } else {
                        Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.preparing_link));
                    }
                }
            }
        }


    }

    private void prevSidebarTweet() {
        boolean todo = false;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mPositionSelectedTweetConversation > 0) {
                mPositionSelectedTweetConversation--;
                todo = true;
            }
        } else {
            if (mPositionSelectedTweet > 0) {
                mPositionSelectedTweet--;
                todo = true;
            }
        }
        if (todo) {
            showSidebarTweet();
            animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
            animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_TOP);
            animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_TOP);
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_tweets));
        }
    }

    private void nextSidebarTweet() {
        boolean todo = false;
        if (mTypeList == TYPE_LIST_CONVERSATION) {
            if (mPositionSelectedTweetConversation < mAdapterStatusList.getCount() - 1) {
                mPositionSelectedTweetConversation++;
                todo = true;
            }
        } else {
            if (mPositionSelectedTweet < mAdapterResponseList.getCount() - 1) {
                mPositionSelectedTweet++;
                todo = true;
            }
        }
        if (todo) {
            showSidebarTweet();
            animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_BOTTOM);
            animationLayoutIn(mSidebarContent, Utils.TYPE_ANIM_BOTTOM);
            animationLayoutIn(mSidebarMenu, Utils.TYPE_ANIM_BOTTOM);
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_more_tweets));
        }
    }


    AsyncTask<String, Void, InfoUsers> loadSidebarUser;

    public void loadSidebarUser(String user) {

        mTypeSidebar = TYPE_SIDEBAR_USER;

        InfoUsers iu = CacheData.getCacheUser(user);

        if (iu != null) {
            showSidebarUser(iu);
            return;
        }

        mSidebarHead.removeAllViews();
        mSidebarHead.setVisibility(View.GONE);

        mSidebarMenu.removeAllViews();
        mSidebarMenu.setVisibility(View.GONE);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
        ll.gravity = Gravity.CENTER;

        mSidebarContent.setVisibility(View.VISIBLE);
        mSidebarContent.removeAllViews();
        mSidebarContent.setLayoutParams(ll);
        mSidebarContent.addView(Sidebar.getLoadingView(mTweetTopics), ll);

        mSidebarFoot.removeAllViews();
        mSidebarFoot.setVisibility(View.GONE);

        openSidebar();

        loadSidebarUser = new LoadUserAsyncTask(mTweetTopics, new LoadUserAsyncAsyncTaskResponder() {

            @Override
            public void userLoading() {
            }

            @Override
            public void userCancelled() {
            }

            @Override
            public void userLoaded(InfoUsers iu) {
                showSidebarUser(iu);
            }

        });
        loadSidebarUser.execute(user);


    }

    public void showSidebarUser(InfoUsers iu) {

        if (iu == null) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            closeSidebar();
        } else {

            CacheData.addCacheUsers(iu);

            mUserNameSelected = iu;

            mSidebarHead.removeAllViews();
            mSidebarHead.setVisibility(View.VISIBLE);
            mSidebarHead.addView(Sidebar.getViewUserHeadSidebar(mTweetTopics, this, iu));

            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
            ll.gravity = Gravity.CENTER;

            mSidebarContent.removeAllViews();
            mSidebarContent.setVisibility(View.VISIBLE);
            mSidebarContent.setLayoutParams(ll);
            mSidebarContent.addView(Sidebar.getViewUserContentSidebar(mTweetTopics, this, iu), ll);

            mSidebarMenu.removeAllViews();
            mSidebarMenu.setVisibility(View.GONE);

            mSidebarFoot.removeAllViews();
            mSidebarFoot.setVisibility(View.VISIBLE);
            mSidebarFoot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            mSidebarFoot.addView(Sidebar.getViewUserFootSidebar(mTweetTopics, this, iu), new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if (!openSidebar()) {
                animationLayoutIn(mSidebarHead, Utils.TYPE_ANIM_TOP);
                animationLayoutIn(mSidebarContent);
                animationLayoutIn(mSidebarFoot, Utils.TYPE_ANIM_BOTTOM);
            }
        }
    }

    private void refreshTitle() {
        if (mTypeList == TYPE_LIST_COLUMNUSER) {
            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e != null) {
                mEntityUser = new EntityTweetUser(e.getId(), mTypeLastList);
            }

            if (mTypeLastColumn == TIMELINE) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.timeline));
                addFooter();
            } else if (mTypeLastColumn == MENTIONS) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.mentions));
                addFooter();
            } else if (mTypeLastColumn == FAVORITES) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.favorites));
            } else if (mTypeLastColumn == DIRECTMESSAGES) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.direct_messages));
                addFooter();
            }
        } else if (mTypeList == TYPE_LIST_READAFTER) {
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.tweets_saved));
        } else if (mTypeList == TYPE_LIST_SEARCH || mTypeList == TYPE_LIST_SEARCH_NOTIFICATIONS) {
            if (mEntitySearch != null) {
                mTweetTopics.setTitle(mEntitySearch.getString("name"));
            }
        } else if (mTypeList == TYPE_LIST_USERS) {
            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e != null) {
                mEntityUser = new EntityTweetUser(e.getId(), mTypeLastList);
            }
            if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.FOLLOWERS) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.followers_of) + " " + mCurrentTextTypeStatus);
            } else if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.FRIENDS) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.friends_of) + " " + mCurrentTextTypeStatus);
            }
        } else if (mTypeList == TYPE_LIST_RETWEETS) {
            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e != null) {
                mEntityUser = new EntityTweetUser(e.getId(), mTypeLastList);
            }
            if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.RETWEETED_BYME) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_byme));
            } else if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.RETWEETED_TOME) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_tome));
            } else if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.RETWEETED_OFME) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_ofme));
            }
        } else if (mTypeList == TYPE_LIST_LISTUSERS) {
            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e != null) {
                mEntityUser = new EntityTweetUser(e.getId(), mTypeLastList);
            }
            mTweetTopics.setTitle(mCurrentTextTypeStatus);
        }
        refreshButtonsColumns();
    }

    private void backToSearchFromConversation() {
        CacheData.clearChace_Others();
        listIsConversation = false;

        if (mTypeLastList == TYPE_LIST_COLUMNUSER) {

            setTypeList(TYPE_LIST_COLUMNUSER);

            if (mTypeLastColumn == TIMELINE) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.timeline));
                addFooter();
            } else if (mTypeLastColumn == MENTIONS) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.mentions));
                addFooter();
            } else if (mTypeLastColumn == FAVORITES) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.favorites));
            } else if (mTypeLastColumn == DIRECTMESSAGES) {
                mTweetTopics.setTitle(mTweetTopics.getString(R.string.direct_messages));
                addFooter();
            }

            if (mAdapterResponseList != null) {
                mListView.setAdapter(mAdapterResponseList);
                mListView.setSelection(mPositionSelectedTweet);
            }

        } else if (mTypeLastList == TYPE_LIST_READAFTER) {
            toDoReadAfter();
        } else {
            setTypeList(TYPE_LIST_SEARCH);
            if (mAdapterResponseList != null) {
                if (mEntitySearch != null) {
                    mTweetTopics.setTitle(mEntitySearch.getString("name"));
                    if (mEntitySearch.getInt("notifications") == 1) {
                        setTypeList(TYPE_LIST_SEARCH_NOTIFICATIONS);
                    } else {
                        setTypeList(TYPE_LIST_SEARCH);
                    }
                }
                mListView.setAdapter(mAdapterResponseList);
                mListView.setSelection(mPositionSelectedTweet);
            }
        }
        closeSidebar();
    }


    /*
     *
     * Carga grid busquedas
     *
     */

    public void loadGridSearch() {
    }

    private void editSearch() {
        Intent newsearch = new Intent(mTweetTopics, TabNewEditSearch.class);
        newsearch.putExtra(DataFramework.KEY_ID, mAdapterSearch.getItem(mPositionSelectedSearch).getId());
        mTweetTopics.startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void editCurrenSearch() {
        Intent newsearch = new Intent(mTweetTopics, TabNewEditSearch.class);
        newsearch.putExtra(DataFramework.KEY_ID, mCurrentId);
        mTweetTopics.startActivityForResult(newsearch, ACTIVITY_NEWEDITSEARCH);
    }

    public void exportCurrenSearch() {
        mTweetTopics.showDialog(DIALOG_CURRENT_EXPORT);
    }

    private void deleteSearch() {
        Entity ent = new Entity("search", mAdapterSearch.getItem(mPositionSelectedSearch).getId());
        ent.delete();
        loadGridSearch();
        Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.delete_correct));
    }

    /*
     *
     * retweet status
     *
     */

    public void sendRetweet(String users, long tweet_id) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 3);
        ent.setValue("reply_tweet_id", tweet_id);
        ent.save();

        mTweetTopics.startService(new Intent(mTweetTopics, ServiceUpdateStatus.class));
    }

    public void retweetStatus(final long tweet_id) {

        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendRetweet(ents.get(0).getId() + "", tweet_id);

        } else {

            final UsersAdapter adapter = new UsersAdapter(mTweetTopics, ents);

            AlertDialog builder = new AlertDialog.Builder(mTweetTopics)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRetweet(adapter.getItem(which).getId() + "", tweet_id);
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();

        }

    }


    /*
     *
     * Busquedas en Twitter
     *
     */

    public void reload() {
        if (!app.isReloadUserTwitter()) {
            boolean gotoreload = false;
            if ((mTypeList == TYPE_LIST_CONVERSATION) || (mTypeList == TYPE_LIST_READAFTER)) {

            }
            if (mTypeList == TYPE_LIST_COLUMNUSER) {
                gotoreload = true;
                reloadColumnUser(true);
            }
            if ((mTypeList == TYPE_LIST_SEARCH_NOTIFICATIONS)) {
                gotoreload = true;
                reloadSearch(true, true);
            }
            if (mTypeList == TYPE_LIST_SEARCH) {
                gotoreload = true;
                search();
            }
            if (!gotoreload) {
                mListView.onRefreshComplete();
            }
        }
    }

    protected void toDoSearch(long id) {

        if (searchTask != null || app.isReloadUserTwitter()) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.wait_moment));
            return;
        }

        // antes de cambiar marcar como ultimo leido el primero visible
        if (markPositionLastReadAsLastReadId()) {
            reloadNewMsgInAllColumns();
        }

        setTypeList(TYPE_LIST_SEARCH);
        isToDoSearch = true;
        mCurrentId = id;
        closeSidebar();

    }

    private AsyncTask<EntitySearch, Void, SearchAsyncTask.SearchResult> searchTask;

    protected void search() {

        if (searchTask != null || app.isReloadUserTwitter()) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.wait_moment));
            return;
        }

        refreshButtonsColumns();

        if (mCurrentId > 0) {

            removeFooter();
            try {
                mEntitySearch = new EntitySearch(mCurrentId);
                mEntitySearch.setValue("last_modified", Utils.now());
                mEntitySearch.setValue("use_count", mEntitySearch.getInt("use_count") + 1);
                mEntitySearch.save();

                CacheData.clearChace_Others();

                exetuteSearchAsyncTask();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
                Utils.showMessage(mTweetTopics, R.string.no_server);
            }
        }

    }

    public void exetuteSearchAsyncTask() {

        listIsConversation = false;

        if (mEntitySearch.getInt("notifications") == 1) {
            ArrayList<Entity> tweets = DataFramework.getInstance().getEntityList("tweets", "search_id = " + mEntitySearch.getId() + " AND favorite = 0", "date desc");

            ArrayList<RowResponseList> response = new ArrayList<RowResponseList>();
            int pos = 0;
            boolean found = false;
            for (int i = 0; i < tweets.size(); i++) {
                boolean delete = false;
                if (i > 0) {
                    if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                        delete = true;
                    }
                }
                if (delete) {
                    try {
                        tweets.get(i).delete();
                    } catch (Exception er) {
                    }
                } else {
                    RowResponseList r = new RowResponseList(tweets.get(i));
                    if (!found && mEntitySearch.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                        r.setLastRead(true);
                        pos = i;
                        found = true;
                    }
                    if (i >= tweets.size() - 1 && !found) {
                        r.setLastRead(true);
                        pos = i;
                        found = true;
                    }
                    r.setRead(found);
                    response.add(r);
                }
            }

            mAdapterResponseList = new ResponseListAdapter(mTweetTopics, this, response, mEntitySearch.getValueLastId());

            mAdapterResponseList.setLastReadPosition(pos);
            mListView.setAdapter(mAdapterResponseList);

            mListView.setSelection(pos);

            reloadSearch(true, false);
        } else {
            setLayoutLoading();
            reloadSearch(false, false);
        }
    }

    public void reloadSearch(boolean isNotification, boolean firstIsLastPosition) {
        if (isNotification) {
            if (firstIsLastPosition) {
                mAdapterResponseList.firtsItemIsLastRead();
            }
            searchTask = new SearchAsyncTask(this, mTweetTopics).execute(mEntitySearch);
        } else {
            searchTask = new SearchAsyncTask(this, mTweetTopics).execute(mEntitySearch);
        }
    }

    @Override
    public void searchCancelled() {
    }

    @Override
    public void searchLoaded(SearchResult searchResult) {
        setLayoutListView();
        searchTask = null;

        if (searchResult.info.getError() == Utils.UNKNOWN_ERROR) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
        } else if (searchResult.info.getError() == Utils.LIMIT_ERROR) {
            Date date = searchResult.info.getRate().getResetTime();
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.limit_server) + " " + date.toLocaleString());
        } else {

            mTweetTopics.setTitle(mEntitySearch.getString("name"));

            if (mEntitySearch.getInt("notifications") == 1) {

                if (searchResult.info.getNewMessages() > 0) {
                    int pos = mListView.getFirstVisiblePosition();
                    String where = "search_id =" + mEntitySearch.getId() + " AND favorite = 0 AND tweet_id >= '" + Utils.fillZeros("" + searchResult.info.getOlderId()) + "'";
                    ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets", where, "date desc");
                    int count = mAdapterResponseList.appendNewer(ents, -1);
                    mListView.setSelection(pos + count);
                    mAdapterResponseList.setLastReadPosition(mAdapterResponseList.getLastReadPosition() + count);
                }

                // meter publicidad
                if (Utils.isLite(mTweetTopics) && mAdapterResponseList.getCount() > 0 && !mAdapterResponseList.hasAd()) {
                    try {
                        int pos = mAdapterResponseList.getLastReadPosition();
                        if (mAdapterResponseList.getCount() < pos) {
                            pos = 1;
                        }
                        if (pos == 0) pos = 1;
                        mAdapterResponseList.generateAd();
                        mAdapterResponseList.insert(new RowResponseList(RowResponseList.TYPE_PUB), pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (mAdapterResponseList.getCount() > 0) {
                    // guardar el ultimo id

                    if (mEntitySearch != null) {
                        mEntitySearch.setValue("new_tweets_count", 0);
                        mEntitySearch.setValue("last_tweet_id", mAdapterResponseList.getItem(0).getEntity().getLong("tweet_id") + "");
                        mEntitySearch.setValue("last_tweet_id_notifications", mAdapterResponseList.getItem(0).getEntity().getLong("tweet_id") + "");
                        mEntitySearch.save();
                    }

                } else {
                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_found_tweets));
                }
            } else {

                boolean refreshInLastRead = mTweetTopics.getPreference().getBoolean("prf_refresh_in_last_read", true);

                mAdapterResponseList = searchResult.response;

                mListView.setAdapter(mAdapterResponseList);

                if (mAdapterResponseList.getCount() > 0) {
                    int pos = 0;
                    try {
                        if (refreshInLastRead) {
                            boolean found = false;
                            for (int i = 0; i < mAdapterResponseList.getCount(); i++) {
                                if (mAdapterResponseList.getItem(i).getType() == RowResponseList.TYPE_TWEET) {
                                    if (mAdapterResponseList.getItem(i).getTweet().getId() <= mEntitySearch.getLong("last_tweet_id")) {
                                        found = true;
                                        pos = i;
                                        mAdapterResponseList.getItem(i).setLastRead(true);
                                        mAdapterResponseList.setLastReadPosition(i);
                                        i = mAdapterResponseList.getCount();
                                    }
                                    mAdapterResponseList.getItem(i).setRead(found);
                                }
                            }
                            if (!found) {
                                pos = mAdapterResponseList.getCount();
                                mAdapterResponseList.getItem(mAdapterResponseList.getCount() - 1).setLastRead(true);
                                mAdapterResponseList.setLastReadPosition(mAdapterResponseList.getCount() - 1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mListView.setSelection(pos);

                    // guardar el ultimo id

                    if (mEntitySearch != null) {
                        mEntitySearch.setValue("last_tweet_id", mAdapterResponseList.getItem(0).getTweetId() + "");
                        mEntitySearch.save();
                    }

                    if (mEntitySearch != null) {
                        if (!mEntitySearch.getErrorLastQuery().equals("")) {
                            Utils.showMessage(mTweetTopics, mEntitySearch.getErrorLastQuery());
                        }
                    }

                } else {
                    if (mEntitySearch.getString("lang").equals("")) {
                        Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_found_tweets));
                    } else {
                        if (isGenericSearch) {
                            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_found_tweets));
                        } else {
                            String lang = mEntitySearch.getString("lang");
                            String selectedLang = "";
                            String[] langNames = mTweetTopics.getResources().getStringArray(R.array.languages);
                            String[] langValues = mTweetTopics.getResources().getStringArray(R.array.languages_values);
                            for (int i = 0; i < langValues.length; i++) {
                                if (lang.equals(langValues[i])) {
                                    selectedLang = langNames[i];
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
                            builder.setTitle(R.string.no_result);
                            builder.setMessage(mTweetTopics.getString(R.string.search_no_found_change_lang) + selectedLang + mTweetTopics.getString(R.string.search_no_found_change_lang2));
                            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    isGenericSearch = true;
                                    reload();
                                }
                            });
                            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    }
                }

                // meter publicidad

                if (Utils.isLite(mTweetTopics) && mAdapterResponseList.getCount() > 0) {
                    try {
                        int pos = mAdapterResponseList.getLastReadPosition();
                        if (mAdapterResponseList.getCount() < pos) {
                            pos = 1;
                        }
                        if (pos == 0) pos = 1;
                        mAdapterResponseList.generateAd();
                        mAdapterResponseList.insert(new RowResponseList(RowResponseList.TYPE_PUB), pos);
                        mAdapterResponseList.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                isGenericSearch = false;

            }

            this.loadGridSearch();

        }

    }

    @Override
    public void searchLoading() {
    }


    /*
     *
     * Conversacion
     *
     */

    public void showConversationLinks(String tweet_text) {
        final ArrayList<String> tweet_links = Utils.pullLinks(tweet_text);

        if (tweet_links.size() > 0) {
            CharSequence[] link_list = new CharSequence[tweet_links.size()];

            for (int i = 0; i < tweet_links.size(); i++) {
                link_list[i] = tweet_links.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
            builder.setTitle(R.string.links);
            builder.setItems(link_list, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selected_link = tweet_links.get(which);

                    if (selected_link.startsWith("@")) { // User
                        loadSidebarUser(selected_link);
                    } else if (selected_link.startsWith("#")) { // Hashtag
                        TweetTopicsCore.this.showDialogHashTag(selected_link);
                    } else {
                        TweetTopicsCore.this.showSidebarLink(selected_link);
                    }
                }
            });

            builder.create();
            builder.show();
        }
    }

    public void loadConversation() {
        setTypeList(TYPE_LIST_CONVERSATION);
        listIsConversation = true;
        removeFooter();
        mPositionSelectedTweet = mListView.getFirstVisiblePosition();

        CacheData.clearChace_Others();
        mTweetTopics.setTitle(mTweetTopics.getString(R.string.conversation));

        /*ArrayList<Status> status_list = new ArrayList<Status>();
        ListView conversation_listview = (ListView)mSidebarMenu.findViewById(R.id.list_conversation_tweets);
        ListAdapter adapter = conversation_listview.getAdapter();

        for(int i = 0; i < adapter.getCount(); i++)
            status_list.add((Status)adapter.getItem(i));*/

        if (mConversationStatusList.size() > 0) {
            mAdapterStatusList = new StatusListAdapter(mTweetTopics, this, mConversationStatusList);
            mListView.setAdapter(mAdapterStatusList);
            mListView.setSelection(0);
        }

        closeSidebar();
    }

    private AsyncTask<Long, GetConversationAsyncTask.GetConversationResult, Boolean> getConversationTask;

    public void getFullConversation(long conversation_id) {
        LinearLayout loading_progress = (LinearLayout) mSidebarMenu.findViewById(R.id.load_progress);
        loading_progress.setVisibility(View.VISIBLE);

        LinearLayout buttons_foot = (LinearLayout) mSidebarMenu.findViewById(R.id.buttons_foot);
        buttons_foot.setVisibility(View.GONE);

        getConversationTask = new GetConversationAsyncTask(this, mTweetTopics).execute(conversation_id);
    }

    @Override
    public void getFullConversationCancelled() {
    }

    @Override
    public void getFullConversationProgressUpdate(GetConversationAsyncTask.GetConversationResult searchResult) {
        if (searchResult.error) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
        } else {
            //ListView list_view = (ListView)mSidebarMenu.findViewById(R.id.list_conversation_tweets);
            mConversationStatusList.add(searchResult.conversation_status);
            Sidebar.addRowSidebarConversationAdapter(searchResult.conversation_status);
            //list_view.setAdapter(new RowSidebarConversationAdapter(mTweetTopics, this, mConversationStatusList));
            //list_view.setSelection(0);
        }
    }

    @Override
    public void getFullConversationLoaded(Boolean result) {
        if (result != null) {
            LinearLayout loading_progress = (LinearLayout) mSidebarMenu.findViewById(R.id.load_progress);
            loading_progress.setVisibility(View.GONE);

            LinearLayout buttons_foot = (LinearLayout) mSidebarMenu.findViewById(R.id.buttons_foot);
            buttons_foot.setVisibility(View.VISIBLE);

            Button btnLoadConversation = (Button) mSidebarMenu.findViewById(R.id.bt_load_conversation);
            btnLoadConversation.setVisibility(View.VISIBLE);

            Button btnConversation = (Button) mSidebarMenu.findViewById(R.id.bt_view_conversation);
            btnConversation.setVisibility(View.GONE);
        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
        }
    }

    @Override
    public void getFullConversationLoading() {
    }

    /*
     *
     * Leer luego
     *
     */

    public void toDoReadAfter() {
        isToDoSearch = true;
        setTypeList(TYPE_LIST_READAFTER);
        closeSidebar();
    }

    public void readAfter() {
        setTypeList(TYPE_LIST_READAFTER);

        removeFooter();

        CacheData.clearChace_Others();

        mTweetTopics.setTitle(mTweetTopics.getString(R.string.tweets_saved));

        closeSidebar();
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("saved_tweets", "", "date desc");

        ArrayList<RowResponseList> response = new ArrayList<RowResponseList>();
        for (int i = 0; i < ents.size(); i++) {
            RowResponseList r = new RowResponseList(ents.get(i));
            response.add(r);
        }

        mAdapterResponseList = new ResponseListAdapter(mTweetTopics, this, response, -1);

        mListView.setAdapter(mAdapterResponseList);
        mListView.setSelection(0);
        if (ents.size() <= 0) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_saved_tweet));
        }

    }

    /*
     *
     * Cargar Timeline y Menciones
     *
     */

    public void changeUser(long id) {

        CacheData.clearChace_Users();

        if (app.isReloadUserTwitter()) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.wait_moment));
            return;
        }

        markPositionLastReadAsLastReadId();

        twitter = ConnectionManager.getInstance().getTwitter(id, true);

        columnUser(TIMELINE, true);

    }

    public void columnUser(int column) {
        columnUser(column, false);
    }

    public void columnUser(int column, boolean isChangeUser) {

        if (loadMoreTask != null) loadMoreTask.cancel(true);

        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {

            // antes de cambiar marcar como ultimo leido el primero visible
            if (!isChangeUser) markPositionLastReadAsLastReadId();

            // ir a las menciones en el caso de que el usuario no guarde el timeline
            // y sea un cambio de usuario
            if (isChangeUser && e.getInt("no_save_timeline") == 1) {
                column = MENTIONS;
            }

            mTypeLastColumn = column;
            setTypeList(TYPE_LIST_COLUMNUSER);

            refreshButtonsColumns();

            if (column == FAVORITES) {

                loadTypeStatus(LoadTypeStatusAsyncTask.FAVORITES);

            } else if (column == TIMELINE && e.getInt("no_save_timeline") == 1) { // no se guarda el timeline

                addFooter();

                reloadNewMsgInAllColumns();

                mEntityUser = new EntityTweetUser(e.getId(), column);

                loadTypeStatus(LoadTypeStatusAsyncTask.TIMELINE);


            } else { // todas las demas

                addFooter();

                reloadNewMsgInAllColumns();

                mEntityUser = new EntityTweetUser(e.getId(), column);

                closeSidebar();

                String whereType = "";

                if (column == TIMELINE) {
                    Utils.fillHide();
                    mTweetTopics.setTitle(mTweetTopics.getString(R.string.timeline));
                    //Entity d = DataFramework.getInstance().getTopEntity("tweets_user", "user_tt_id = " + mEntityUser.getId() + " AND type_id = " + TIMELINE, "date asc");
                    //whereType = " AND date >= '" + d.getString("date") + "' AND (type_id = " + TIMELINE + " OR type_id = " + MENTIONS + " OR type_id = " + DIRECTMESSAGES + ")";
                    whereType = " AND type_id = " + TIMELINE;
                } else if (column == MENTIONS) {
                    mTweetTopics.setTitle(mTweetTopics.getString(R.string.mentions));
                    whereType = " AND type_id = " + MENTIONS;
                } else if (column == FAVORITES) {
                    mTweetTopics.setTitle(mTweetTopics.getString(R.string.favorites));
                    whereType = " AND type_id = " + FAVORITES;
                } else if (column == DIRECTMESSAGES) {
                    mTweetTopics.setTitle(mTweetTopics.getString(R.string.direct_messages));
                    whereType = " AND (type_id = " + DIRECTMESSAGES + " OR type_id = " + SENT_DIRECTMESSAGES + ")";
                }


                ArrayList<Entity> tweets;
                try {
                    tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + mEntityUser.getId() + whereType, "date desc, has_more_tweets_down asc");
                } catch (OutOfMemoryError er) {
                    tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + mEntityUser.getId() + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
                }

                ArrayList<RowResponseList> response = new ArrayList<RowResponseList>();
                int pos = 0;
                int count = 0;
                boolean found = false;
                int countHide = 0;
                for (int i = 0; i < tweets.size(); i++) {
                    boolean delete = false;
                    if (i > 0) {
                        if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                            delete = true;
                        }
                    }
                    if (delete) {
                        try {
                            tweets.get(i).delete();
                        } catch (Exception er) {
                        }
                    } else {

                        if (column == TIMELINE && Utils.hideUser.contains(tweets.get(i).getString("username").toLowerCase())) { // usuario
                            countHide++;
                        } else if (column == TIMELINE && Utils.isHideWordInText(tweets.get(i).getString("text").toLowerCase())) { // palabra
                            countHide++;
                        } else if (column == TIMELINE && Utils.isHideSourceInText(tweets.get(i).getString("source").toLowerCase())) { // fuente
                            countHide++;
                        } else {
                            RowResponseList r = new RowResponseList(tweets.get(i));
                            if (!found && mEntityUser.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                                r.setLastRead(true);
                                pos = count;
                                found = true;
                            }
                            if (i >= tweets.size() - 1 && !found) {
                                r.setLastRead(true);
                                pos = count;
                                found = true;
                            }
                            r.setRead(found);
                            try {
                                response.add(r);
                                if (r.hasMoreTweetDown()) {
                                    response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                                }
                                count++;
                            } catch (OutOfMemoryError er) {
                                i = tweets.size();
                            }
                        }
                    }

                }

                //pos++;

                mAdapterResponseList = new ResponseListAdapter(mTweetTopics, this, response, mEntityUser.getValueLastId());

                mAdapterResponseList.setHideMessages(countHide);

                mAdapterResponseList.setLastReadPosition(pos);
                mPositionLastRead = pos;

                mListView.setAdapter(mAdapterResponseList);

                mListView.setSelection(pos);

                if (column == TIMELINE) { //&& mEntityUser.getValueNewCount()<20
                    // actualizar automaticamente si hace X minutos del ultimo tweets
                    if (count > 0) {
                        int minutes = mTweetTopics.getIntPreference("prf_time_refresh", 10);//Integer.parseInt(mTweetTopics.getPreference().getString("prf_time_refresh", "10"));
                        if (minutes > 0) {
                            int miliseconds = minutes * 60 * 1000;
                            Date d = new Date(mAdapterResponseList.getItem(0).getEntity().getLong("date") + miliseconds); //600000
                            if (new Date().after(d)) {
                                reloadColumnUser(false);
                            } else {
                                showHideMessage();
                            }
                        } else {
                            showHideMessage();
                        }
                    } else {
                        reloadColumnUser(false);
                    }
                } else {
                    if (mAdapterResponseList.getCount() > 0) {
                        mEntityUser.saveValueLastIdFromDB();
                    }
                }

            }

        }

    }


    public void reloadColumnUser(boolean firstIsLastPosition) {
        if (!app.isReloadUserTwitter()) {

            if (mTypeLastColumn == TIMELINE && mEntityUser != null && mEntityUser.getInt("no_save_timeline") == 1) { // no se guarda el timeline
                columnUser(TIMELINE);
            } else if (mTypeLastColumn == FAVORITES) {
                columnUser(FAVORITES);
            } else {
                if (firstIsLastPosition) {
                    mAdapterResponseList.firtsItemIsLastRead();
                }
                app.reloadUserTwitter();

            }

        }
    }

    @Override
    public void OnFinishTwitterDownload(TwitterUserResult searchResult, int witch) {

        if (witch == 2) { // es la segunda vez
            mLayoutInfoBackground.setVisibility(View.GONE);
        } else {
            if (searchResult.infoTimeline != null) mLayoutInfoBackground.setVisibility(View.VISIBLE);
        }

        InfoSaveTweets info = null;

        if (mTypeLastColumn == TIMELINE) {
            info = searchResult.infoTimeline;
        }
        if (mTypeLastColumn == MENTIONS) {
            info = searchResult.infoMentions;
        }
        if (mTypeLastColumn == DIRECTMESSAGES) {
            info = searchResult.infoDM;
        }

        reloadNewMsgInAllColumns(searchResult.infoTimeline != null, searchResult.infoMentions != null, searchResult.infoDM != null);

        if (info != null) {
            mListView.onRefreshComplete();
            if (info.getError() == Utils.UNKNOWN_ERROR) {
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            } else if (info.getError() == Utils.LIMIT_ERROR) {
                Date date = info.getRate().getResetTime();
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.limit_server) + " " + date.toLocaleString());
            } else {

                // comprobamos si hay mensajes por encima del primero mensaje puesto en la lista y si es asi los mostramos

                int newMsg = info.getNewMessages();
                long oldId = info.getOlderId();
                String oper = ">=";
                long id = mAdapterResponseList.getFirstId();
                if (id > 0) {
                    newMsg = mEntityUser.getValueCountFromId(id);
                    oldId = id;
                    oper = ">";
                }

                if (newMsg > 0) {
                    int pos = mListView.getFirstVisiblePosition();
                    String where = "user_tt_id=" + searchResult.user_id + " AND tweet_id " + oper + "'" + Utils.fillZeros("" + oldId) + "'";
                    if (mTypeLastColumn == TIMELINE) {
                        //where += " AND ( type_id = " + TIMELINE + " OR type_id = " + MENTIONS + " OR type_id = " + DIRECTMESSAGES + ")";
                        where += " AND type_id = " + TIMELINE;
                    } else if (mTypeLastColumn == MENTIONS) {
                        where += " AND type_id = " + MENTIONS;
                    } else if (mTypeLastColumn == FAVORITES) {
                        where += " AND type_id = " + FAVORITES;
                    } else if (mTypeLastColumn == DIRECTMESSAGES) {
                        where += " AND (type_id = " + DIRECTMESSAGES + " OR type_id = " + SENT_DIRECTMESSAGES + ")";
                    }

                    ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", where, "date desc");
                    int count = mAdapterResponseList.appendNewer(ents, mTypeLastColumn);
                    //mListView.setSelection(mAdapterResponseList.getLastReadPosition() + ents.size());
                    mListView.setSelection(pos + count);
                    mAdapterResponseList.setLastReadPosition(mAdapterResponseList.getLastReadPosition() + count);
                    mPositionLastRead += count;
                }

                if (mAdapterResponseList.getCount() > 0) {
                    mEntityUser.saveValueLastIdFromDB();
                }

                // comprobamos si hay mensajes ocultos y mostramos el toast

                showHideMessage();

            }

        }

    }

    public void showHideMessage() {
        boolean show = mTweetTopics.getPreference().getBoolean("prf_quiet_show_msg", true);

        if (show && mAdapterResponseList.getHideMessages() > 0) {
            Utils.showMessage(mTweetTopics, mAdapterResponseList.getHideMessages() + " " + mTweetTopics.getString(R.string.tweets_hidden));
        }
    }

    /*
     *
     * Buscar locations trends
     *
     */

    private AsyncTask<Void, Void, ResponseList<Location>> trendsLocationsTask;

    public void showLocationsTrends() {

        closeSidebar();

        if (PreferenceUtils.getWoeidTT(mTweetTopics) > 0) {
            showTrends(PreferenceUtils.getWoeidTT(mTweetTopics));
        } else {

            progressDialog = new ProgressDialog(mTweetTopics);

            progressDialog.setTitle(R.string.loading_title_trends);
            progressDialog.setMessage(mTweetTopics.getResources().getString(R.string.loading_description_trends));

            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    if (trendsLocationsTask != null) trendsLocationsTask.cancel(true);
                }

            });

            progressDialog.show();

            trendsLocationsTask = new TrendsLocationAsyncTask(this).execute();

        }


    }

    @Override
    public void trendsLocationCancelled() {
    }

    @Override
    public void trendsLocationLoaded(ResponseList<Location> locations) {
        progressDialog.dismiss();

        if (locations == null || locations.size() <= 0) {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_server));
            return;
        }

        try {
            Collections.sort(locations, new Comparator<Location>() {

                public int compare(Location o1, Location o2) {
                    Location l1 = (Location) o1;
                    Location l2 = (Location) o2;
                    return l1.getName().compareToIgnoreCase(l2.getName());
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayList<Integer> woeid = new ArrayList<Integer>();

        CharSequence[] c = new CharSequence[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            c[i] = locations.get(i).getName();
            woeid.add(locations.get(i).getWoeid());
        }

        final CheckBox cb = new CheckBox(mTweetTopics);
        cb.setText(R.string.use_default);
        cb.setTextColor(Color.GRAY);

        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setView(cb);
        builder.setTitle(R.string.loading_title_trends);
        builder.setItems(c, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cb.isChecked()) PreferenceUtils.setWoeidTT(mTweetTopics, woeid.get(which));
                showTrends(woeid.get(which));
            }


        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void trendsLocationLoading() {

    }

    /*
     *
     * Mostrar trends
     *
     */

    private AsyncTask<Integer, Void, Trend[]> trendsTask;

    public void showTrends(int woeid) {

        closeSidebar();

        progressDialog = new ProgressDialog(mTweetTopics);

        progressDialog.setTitle(R.string.loading_title_trends);
        progressDialog.setMessage(mTweetTopics.getResources().getString(R.string.loading_description_trends));

        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                if (trendsTask != null) trendsTask.cancel(true);
            }

        });

        progressDialog.show();

        if (woeid < 0) {
            trendsTask = new TrendsAsyncTask(this).execute();
        } else {
            trendsTask = new TrendsAsyncTask(this).execute(woeid);
        }


    }

    @Override
    public void trendsCancelled() {

    }

    @Override
    public void trendsLoaded(Trend[] trends) {

        progressDialog.dismiss();

        if (trends != null && trends.length > 0) {
            final CharSequence[] c = new CharSequence[trends.length];
            for (int i = 0; i < trends.length; i++) {
                c[i] = trends[i].getName();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
            builder.setTitle(R.string.loading_title_trends);
            builder.setItems(c, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Entity ent = new Entity("search");
                    ent.setValue("date_create", Utils.now());
                    ent.setValue("last_modified", Utils.now());
                    ent.setValue("use_count", 0);
                    ent.setValue("is_temp", 1);
                    ent.setValue("icon_id", 1);
                    ent.setValue("icon_big", "drawable/letter_hash");
                    ent.setValue("icon_small", "drawable/letter_hash_small");
                    ent.setValue("name", c[which]);
                    ent.setValue("words_and", c[which]);
                    ent.save();
                    deleteTempSearch();
                    toDoSearch(ent.getId());
                }


            });
            if (PreferenceUtils.getWoeidTT(mTweetTopics) > 0) {
                builder.setNeutralButton(R.string.change_country, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PreferenceUtils.setWoeidTT(mTweetTopics, 0);
                        showLocationsTrends();
                    }
                });
            }
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }
    }

    @Override
    public void trendsLoading() {

    }

    /*
     *
     * Buscar listas
     *
     */

    private AsyncTask<String, Void, UserListsResult> userListTask;

    public void showListUser(int type) {
        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {
            showListUser(e.getString("name"), type);
        }
    }

    public void showListUser(String user, int type) {

        closeSidebar();

        progressDialog = new ProgressDialog(mTweetTopics);

        progressDialog.setTitle(R.string.loading_title_lists);
        progressDialog.setMessage(mTweetTopics.getResources().getString(R.string.loading_description_lists));

        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                if (userListTask != null) userListTask.cancel(true);
            }

        });

        progressDialog.show();

        if (type == UserListsAsyncTask.SHOW_TWEETS) {
            userListTask = new UserListsAsyncTask(this, type, "").execute(user);
        } else {
            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e != null) {
                userListTask = new UserListsAsyncTask(this, type, user).execute(e.getString("name"));
            }
        }


    }

    @Override
    public void userListsCancelled() {
    }

    @Override
    public void userListsLoaded(UserListsResult result) {
        progressDialog.dismiss();

        if (result != null) {

            final ResponseList<UserList> userLists = result.response;
            final int type = result.type;
            final String userAdd = result.userAdd;

            if (userLists != null && userLists.size() > 0) {

                ArrayList<UserList> ar = new ArrayList<UserList>();

                for (UserList u : result.response) {
                    ar.add(u);
                }

                UserListsAdapter adapter = new UserListsAdapter(mTweetTopics, ar);

                AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
                builder.setTitle(R.string.loading_title_lists);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                        if (e != null) {
                            if (type == UserListsAsyncTask.ADD_USER) {
                                String[] l = new String[1];
                                l[0] = userAdd;
                                try {
                                    twitter.addUserListMembers(userLists.get(which).getId(), l);
                                    Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.user_in_list));
                                } catch (TwitterException e1) {
                                    e1.printStackTrace();
                                }

                            } else {
                                loadTypeListStatus(e.getString("name"), userLists.get(which));
                            }
                        }
                    }


                });
                builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create();
                builder.show();

            } else {
                Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.no_list));
            }

        } else {
            Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.error_general));
        }

    }

    @Override
    public void userListsLoading() {

    }

    /*
     *
     * Mostrar tweet antiguos
     *
     */

    private int mColumnOldTweets;
    private AsyncTask<Void, Void, ArrayList<RowResponseList>> loadMoreTask;

    private void loadOlderTweets() {
        if (mAdapterResponseList != null && mAdapterResponseList.getCount() > 0) {
            mColumnOldTweets = mTypeLastColumn;
            mFooterView.showProgress();
            loadMoreTask = new LoadMoreAsyncTask(mTweetTopics, this, this.mAdapterResponseList.getLastId(), mTypeList, mTypeLastColumn).execute();
        }
    }

    @Override
    public void loadingMoreStatuses() {

    }

    @Override
    public void statusesLoaded(ArrayList<RowResponseList> result) {
        if (mColumnOldTweets == mTypeLastColumn) {
            if ((mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn != FAVORITES) || mTypeList == TYPE_LIST_LISTUSERS) {
                mFooterView.hideProgress();
                mAdapterResponseList.appendOlder(result);
            }
        }
    }

    /*
     *
     * Mostrar tweets en medio del timeline
     *
     */

    private int whichMoreTweetDown = 0;

    public void showDialogMoreTweetDown() {
        whichMoreTweetDown = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
        builder.setTitle(R.string.download_msg);
        //builder.setMessage(mTweetTopics.getString(R.string.follow_tweettopics_msg));
        builder.setSingleChoiceItems(R.array.select_dialog_download_msg_timeline, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                whichMoreTweetDown = whichButton;
            }
        });
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int count = 20;
                if (whichMoreTweetDown == 0) {
                    count = -1;
                } else if (whichMoreTweetDown == 1) {
                    count = 100;
                } else if (whichMoreTweetDown == 2) {
                    count = 60;
                } else if (whichMoreTweetDown == 3) {
                    count = 40;
                } else if (whichMoreTweetDown == 4) {
                    count = 20;
                }
                //mAdapterResponseList.showProgressMoreTweetsView(mPositionSelectedTweet);
                moreTweetDown(mPositionSelectedTweet, mAdapterResponseList.getItem(mPositionSelectedTweet + 1).getTweetId(),
                        mAdapterResponseList.getItem(mPositionSelectedTweet - 1).getTweetId(), count);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moreTweetDown(int pos, long since, long max, int count) {
        if (mAdapterResponseList != null && mAdapterResponseList.getCount() > 0) {
            //Utils.showShortMessage(mTweetTopics, R.string.moretweetdown_loaded_msg);
            new LoadMoreTweetDownAsyncTask(this, since, max, pos, count).execute();
            progressDialog = ProgressDialog.show(
                    mTweetTopics,
                    mTweetTopics.getResources().getString(R.string.loading),
                    mTweetTopics.getResources().getString(R.string.loading_description)
            );
        }
    }

    @Override
    public void loadingMoreTweetDown() {

    }

    @Override
    public void statusesMoreTweetDown(LoadMoreTweetDownResult result) {
        if (progressDialog != null) progressDialog.dismiss();
        if (mTypeLastColumn == TIMELINE) {
            if (mAdapterResponseList != null && result != null && result.response != null) {
                try {
                    //if (result.hasMoreTweets) mAdapterResponseList.showProgressMoreTweetsView(result.pos);
                    if (result.response.size() > 0) {
                        mAdapterResponseList.appendPosition(result.response, result.pos, result.hasMoreTweets);
                        mListView.setSelection(result.pos + result.response.size());
                        reloadNewMsgInAllColumns();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //if (mAdapterResponseList!=null && result!=null) mAdapterResponseList.hideProgressMoreTweetsView(result.pos);
                    Utils.showMessage(mTweetTopics, R.string.no_server);
                }
            } else {
                //mAdapterResponseList.hideProgressMoreTweetsView(result.pos);
                Utils.showMessage(mTweetTopics, R.string.no_server);
            }
        }
    }

    /*
     *
     * Update status
     *
     */

    public void sendStatus(String users, String text) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("text", text);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 1);
        ent.setValue("username_direct", "");
        ent.setValue("photos", "");
        ent.setValue("mode_tweetlonger", NewStatus.MODE_TL_NONE);
        ent.setValue("reply_tweet_id", "-1");
        ent.setValue("use_geo", PreferenceUtils.getGeo(mTweetTopics) ? "1" : "0");
        ent.save();

        mTweetTopics.startService(new Intent(mTweetTopics, ServiceUpdateStatus.class));
    }


    public void updateStatus(final String text) {
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendStatus(ents.get(0).getId() + "", text);

        } else {
            final UsersAdapter adapter = new UsersAdapter(mTweetTopics, ents);

            AlertDialog builder = new AlertDialog.Builder(mTweetTopics)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendStatus(adapter.getItem(which).getId() + "", text);
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();
        }
    }
    /*
     *
     * Mostrar status por tipo
     *
     */

    //private AsyncTask<String, Void, ArrayList<RowResponseList>> loadTypeStatusTask;
    private int mCurrentTypeStatus = 0;
    private String mCurrentTextTypeStatus = "";

    private void loadTypeStatus(int type) {
        loadTypeStatus(type, "");
    }

    protected void loadTypeListStatus(String user, UserList list) {
        mCurrentList = list;
        loadTypeStatus(LoadTypeStatusAsyncTask.LIST, user, list);
    }

    public void loadTypeStatus(int type, String text) {
        loadTypeStatus(type, text, null);
    }

    public void loadTypeStatus(int type, String text, UserList list) {

        closeSidebar();

        mCurrentTypeStatus = type;
        mCurrentTextTypeStatus = text;

        setLayoutLoading();

        String idListUser = "";

        if (type == LoadTypeStatusAsyncTask.FAVORITES) {
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.favorites));
        } else if (type == LoadTypeStatusAsyncTask.SEARCH_USERS) {
            setTypeList(TYPE_LIST_USERS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.users) + ": " + text);
        } else if (type == LoadTypeStatusAsyncTask.RETWEETED_BYME) {
            setTypeList(TYPE_LIST_RETWEETS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_byme));
        } else if (type == LoadTypeStatusAsyncTask.RETWEETED_TOME) {
            setTypeList(TYPE_LIST_RETWEETS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_tome));
        } else if (type == LoadTypeStatusAsyncTask.RETWEETED_OFME) {
            setTypeList(TYPE_LIST_RETWEETS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.retweets_ofme));
        } else if (type == LoadTypeStatusAsyncTask.FOLLOWERS) {
            setTypeList(TYPE_LIST_USERS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.followers_of) + " " + text);
        } else if (type == LoadTypeStatusAsyncTask.FRIENDS) {
            setTypeList(TYPE_LIST_USERS);
            refreshButtonsColumns();
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.friends_of) + " " + text);
        } else if (type == LoadTypeStatusAsyncTask.TIMELINE) {
            mTweetTopics.setTitle(mTweetTopics.getString(R.string.timeline));
        } else if (type == LoadTypeStatusAsyncTask.LIST) {
            setTypeList(TYPE_LIST_LISTUSERS);
            refreshButtonsColumns();
            idListUser = list.getId() + "";
            text = list.getUser().getScreenName();
            mTweetTopics.setTitle(list.getFullName());
            mCurrentTextTypeStatus = list.getFullName();
        }

        new LoadTypeStatusAsyncTask(this, type).execute(text, idListUser);
    }

    @Override
    public void loadingTypeStatus() {

    }

    @Override
    public void typeStatusLoaded(ArrayList<RowResponseList> result) {

        setLayoutListView();

        try {
            if (mCurrentTypeStatus == LoadTypeStatusAsyncTask.LIST) {
                addFooter();
            } else {
                removeFooter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapterResponseList = new ResponseListAdapter(mTweetTopics, this, result, -1);
        mListView.setAdapter(mAdapterResponseList);

    }

    /*
     *
     * GESTOS
     *
     */

    //prevSidebarTweet

    public void gestureUp() {
        if (mTypeSidebar == TYPE_SIDEBAR_TWEET) {
            nextSidebarTweet();
        } else if (mTypeSidebar == TYPE_SIDEBAR_LINK) {
            nextSidebarLink();
        }
    }

    public void gestureDown() {
        if (mTypeSidebar == TYPE_SIDEBAR_TWEET) {
            prevSidebarTweet();
        } else if (mTypeSidebar == TYPE_SIDEBAR_LINK) {
            prevSidebarLink();
        }
    }

    public void gestureLeft() {

    }

    public void gestureRight() {
        closeSidebar();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > 500) {
            if (e1.getX() < e2.getX()) { // derecha
                gestureRight();
            } else { // izquierda
                gestureLeft();
            }
        } else {
            if (Math.abs(velocityY) > 500) {
                if (e1.getY() < e2.getY()) { // abajo
                    gestureDown();
                } else { // arriba
                    gestureUp();
                }
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }


}
