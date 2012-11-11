package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RowUserListsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.GetUserListRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.preferences.Preferences;
import com.javielinux.utils.DBUtils;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class UserListsActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    private static int SHOW_TWEETS = 1;
    private static int SHOW_TWEETS_FOLLOWINGLIST = 2;

    private Entity user_entity;
    private RowUserListsAdapter userListsAdapter;
    private ArrayList<Entity> userlist_entities;
    private int type_id;

    private ThemeManager themeManager;

    private LinearLayout mLayoutBackgroundApp;
    private LinearLayout viewNoLists;
    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private GridView viewUserLists;

    private RelativeLayout layoutActionBar;

    private TextView titlePage;

    public void refreshTheme() {
        boolean hasWallpaper = false;
        File f = new File(Preferences.IMAGE_WALLPAPER);
        if (f.exists()) {
            try {
                BitmapDrawable bmp = (BitmapDrawable) BitmapDrawable.createFromPath(Preferences.IMAGE_WALLPAPER);
                bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                mLayoutBackgroundApp.setBackgroundDrawable(bmp);
                hasWallpaper = true;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        if (!hasWallpaper) {
            mLayoutBackgroundApp.setBackgroundColor(Color.parseColor("#" + themeManager.getStringColor("color_background_new_status")));
        }

        themeManager.setColors();
        layoutActionBar.setBackgroundDrawable(ImageUtils.createBackgroundDrawable(this, themeManager.getColor("color_top_bar"), false, 0));

        titlePage.setTextColor(themeManager.getColor("color_indicator_text"));
        titlePage.setTextSize(getResources().getDimension(R.dimen.text_size_title_page));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long user_id = -1;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DataFramework.KEY_ID))
                user_id = savedInstanceState.getLong(DataFramework.KEY_ID);
        } else {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                if (extras.containsKey(DataFramework.KEY_ID)) user_id = extras.getLong(DataFramework.KEY_ID);
            }
        }

        if (user_id < 0) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        user_entity = new Entity("users", user_id);

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.userlists_activity);

        mLayoutBackgroundApp = (LinearLayout) this.findViewById(R.id.user_lists_parent);
        layoutActionBar = (RelativeLayout) findViewById(R.id.user_list_bar_action);
        titlePage = (TextView) this.findViewById(R.id.user_list_bar_title);

        refreshTheme();

        viewNoLists = (LinearLayout) this.findViewById(R.id.user_lists_view_no_lists);
        viewLoading = (LinearLayout) this.findViewById(R.id.user_lists_view_loading);
        viewNoInternet = (LinearLayout) this.findViewById(R.id.user_lists_view_no_internet);
        viewUserLists = (GridView) this.findViewById(R.id.grid_userlist);

        findViewById(R.id.user_list_selection).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = SHOW_TWEETS;
                fillGridUserLists();
            }

        });

        findViewById(R.id.user_following_list_selection).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = SHOW_TWEETS_FOLLOWINGLIST;
                fillGridUserLists();
            }

        });

        findViewById(R.id.user_list_more_options).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenuOptions(v);
            }

        });

        findViewById(R.id.user_list_bar_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListsActivity.this.finish();
            }
        });

        userlist_entities = new ArrayList<Entity>();
        userListsAdapter = new RowUserListsAdapter(this, userlist_entities);
        GridView gridUserList = (GridView)this.findViewById(R.id.grid_userlist);
        gridUserList.setAdapter(userListsAdapter);

        gridUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Entity userList = userListsAdapter.getItem(pos);

                createUserListsColumn(userList);
            }
        });

        gridUserList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                return true;
            }
        });

        type_id = SHOW_TWEETS;
        fillGridUserLists();

        if (userlist_entities.size() == 0) {
            showLoading();
            reload();
        }
    }

    private void update() {
        new AlertDialog.Builder(UserListsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.user_list)
                .setMessage(R.string.user_list_update_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoading();
                        reload();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showMenuOptions(View view) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.list_users_more_actions, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popupmenu_list_user_update) {
                        update();
                    }
                    return true;
                }
            });
            popupMenu.show();
        } else {
            AlertDialogFragment frag = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.actions);
            args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_POSITIVE_BUTTON, false);
            args.putBoolean(AlertDialogFragment.KEY_ALERT_CANCELABLE, false);
            args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.popupmenu_list_users_more_options);
            frag.setArguments(args);
            frag.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
                @Override
                public void OnAlertButtonOk() {
                }

                @Override
                public void OnAlertButtonCancel() {
                }

                @Override
                public void OnAlertButtonNeutral() {
                }

                @Override
                public void OnAlertItems(int which) {
                    if (which == 0) {
                        update();
                    }
                }
            });
            frag.show(getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        showNoInternet();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onResults(BaseResponse result) {
        if (!result.isError())
            fillGridUserLists();
    }

    private void createUserListsColumn(Entity userList) {

        ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "userlist_id=" + userList.getId());

        int position = 0;

        if (created_column_list.size() == 0) {
            position = DBUtils.nextPositionColumn();

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_LIST_USER);
            Entity user_list = new Entity("columns");
            user_list.setValue("description", type.getString("description"));
            user_list.setValue("type_id", type);
            user_list.setValue("position", position);
            user_list.setValue("userlist_id", userList.getId());
            user_list.save();
            Toast.makeText(this, getString(R.string.column_created, userList.getString("name")), Toast.LENGTH_LONG).show();
        } else {
            position = created_column_list.get(0).getInt("position");
        }

        Intent intent = getIntent();
        intent.putExtra("position", position);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void fillGridUserLists() {

        userlist_entities.clear();

        ArrayList<Entity> userlist_data;

        if (type_id == SHOW_TWEETS) {
            userlist_data = DataFramework.getInstance().getEntityList("user_lists", "user_id=" + user_entity.getId() + " AND type_id=1", "");
        } else {
            userlist_data = DataFramework.getInstance().getEntityList("user_lists", "user_id=" + user_entity.getId() + " AND type_id=2", "");
        }

        for (int i = 0; i < userlist_data.size(); i++)
            userlist_entities.add(userlist_data.get(i));

        userListsAdapter.notifyDataSetChanged();

        if (userListsAdapter.getCount() == 0) {
            showNoLists();
        } else {
            showUserLists();
        }
    }

    public void reload() {

        GetUserListRequest getUserListRequest = new GetUserListRequest(user_entity);
        APITweetTopics.execute(this, getSupportLoaderManager(), this, getUserListRequest);
    }

    public void showNoLists() {
        viewNoLists.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.GONE);
    }

    public void showLoading() {
        viewNoLists.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewNoLists.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        viewUserLists.setVisibility(View.GONE);
    }

    public void showUserLists() {
        viewNoLists.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.VISIBLE);
    }
}
