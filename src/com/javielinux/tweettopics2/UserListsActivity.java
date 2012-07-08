package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class UserListsActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    private static int SHOW_TWEETS = 1;
    private static int SHOW_TWEETS_FOLLOWINGLIST = 2;

    private Entity user_entity;
    private RowUserListsAdapter userListsAdapter;
    private ArrayList<Entity> userlist_entities;
    private int type_id;

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private GridView viewUserLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.setActivity(this);

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

        ThemeManager mThemeManager = new ThemeManager(this);
        mThemeManager.setTheme();

        setContentView(R.layout.userlists);

        viewLoading = (LinearLayout) this.findViewById(R.id.user_lists_view_loading);
        viewNoInternet = (LinearLayout) this.findViewById(R.id.user_lists_view_no_internet);
        viewUserLists = (GridView) this.findViewById(R.id.grid_userlist);

        TextView userOwnList = (TextView) this.findViewById(R.id.user_list_selection);
        userOwnList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = SHOW_TWEETS;
                fillGridUserLists();
            }

        });

        TextView userFollowingList = (TextView) this.findViewById(R.id.user_following_list_selection);
        userFollowingList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = SHOW_TWEETS_FOLLOWINGLIST;
                fillGridUserLists();
            }

        });

        TextView userListUpdate = (TextView) this.findViewById(R.id.user_list_update);
        userListUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

        });

        TextView userListClose = (TextView) this.findViewById(R.id.user_list_close);
        userListClose.setOnClickListener(new View.OnClickListener() {

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

                if (createUserListsColumn(userList))
                    UserListsActivity.this.finish();
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

    private boolean createUserListsColumn(Entity userList) {

        int user_lists_count = DataFramework.getInstance().getEntityListCount("columns", "userlist_id=" + userList.getId());

        if (user_lists_count == 0) {
            int count = DataFramework.getInstance().getEntityListCount("columns", "") + 1;

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_LIST_USER);
            Entity user_list = new Entity("columns");
            user_list.setValue("description", type.getString("description"));
            user_list.setValue("type_id", type);
            user_list.setValue("position", count);
            user_list.setValue("userlist_id", userList.getId());
            user_list.save();
            Toast.makeText(this, getString(R.string.column_created, userList.getString("name")), Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, getString(R.string.column_exists, userList.getString("name")), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        viewUserLists.setVisibility(View.GONE);
    }

    public void showUserLists() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.VISIBLE);
    }

    public void reload() {

        GetUserListRequest getUserListRequest = new GetUserListRequest(user_entity);
        APITweetTopics.execute(this, getSupportLoaderManager(), this, getUserListRequest);
    }

    private void fillGridUserLists() {

        if (type_id == SHOW_TWEETS) {
            userlist_entities = DataFramework.getInstance().getEntityList("user_lists", "user_id=" + user_entity.getId() + " AND type_id=1", "");
        } else {
            userlist_entities = DataFramework.getInstance().getEntityList("user_lists", "user_id=" + user_entity.getId() + " AND type_id=2", "");
        }

        userListsAdapter.clear();
        for (Entity entity : userlist_entities) {
            userListsAdapter.add(entity);
        }
        userListsAdapter.notifyDataSetChanged();

        showUserLists();

        if (userListsAdapter.getCount() == 0) {
            Utils.showMessage(this, "El usuario no tiene listas.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    @Override
    public void onResults(BaseResponse result) {
        if (!result.isError())
            fillGridUserLists();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        showNoInternet();
    }
}
