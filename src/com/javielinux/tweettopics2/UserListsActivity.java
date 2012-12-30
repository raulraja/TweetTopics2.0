/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.tweettopics2;

import android.app.ProgressDialog;
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
import com.javielinux.api.loaders.GetUserListLoader;
import com.javielinux.api.request.CreateUserListsRequest;
import com.javielinux.api.request.GetUserListRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserListResponse;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.dialogs.CreateListTwitterDialogFragment;
import com.javielinux.preferences.Preferences;
import com.javielinux.utils.ColumnsUtils;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import twitter4j.UserList;

import java.io.File;
import java.util.ArrayList;

public class UserListsActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    private Entity user_entity;
    private RowUserListsAdapter userListsAdapter;
    private ArrayList<UserList> userListArrayList;
    private int type_id;
    private long user_id;
    private String screenName;
    private long nextCursor;
    private boolean loadingMoreUserList;

    private ThemeManager themeManager;

    private LinearLayout mLayoutBackgroundApp;
    private LinearLayout viewNoLists;
    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewLoadMore;
    private LinearLayout viewLoadMoreLoading;
    private GridView viewUserLists;
    private Button buttonLoadMore;

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

        user_id = -1;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DataFramework.KEY_ID))
                user_id = savedInstanceState.getLong(DataFramework.KEY_ID);
            if (savedInstanceState.containsKey("screenName"))
                screenName = savedInstanceState.getString("screenName");
        } else {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                if (extras.containsKey(DataFramework.KEY_ID)) user_id = extras.getLong(DataFramework.KEY_ID);
                if (extras.containsKey("screenName")) screenName = extras.getString("screenName");
            }
        }

        if (user_id < 0 && screenName == "") {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        if (user_id >= 0) {
            user_entity = new Entity("users", user_id);
            screenName = user_entity.getString("name");
        }

        nextCursor = -1;
        loadingMoreUserList = false;

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.userlists_activity);

        mLayoutBackgroundApp = (LinearLayout) this.findViewById(R.id.user_lists_parent);
        layoutActionBar = (RelativeLayout) findViewById(R.id.user_list_bar_action);
        titlePage = (TextView) this.findViewById(R.id.user_list_bar_title);
        titlePage.setText(getString(R.string.user_list) + ": @" + screenName);

        refreshTheme();

        viewNoLists = (LinearLayout) this.findViewById(R.id.user_lists_view_no_lists);
        viewLoading = (LinearLayout) this.findViewById(R.id.user_lists_view_loading);
        viewNoInternet = (LinearLayout) this.findViewById(R.id.user_lists_view_no_internet);
        viewUserLists = (GridView) this.findViewById(R.id.grid_userlist);
        viewLoadMore = (LinearLayout) this.findViewById(R.id.user_lists_load_more);
        viewLoadMoreLoading = (LinearLayout) this.findViewById(R.id.user_lists_load_more_view_loading);
        buttonLoadMore = (Button) this.findViewById(R.id.but_user_lists_load_more);

        findViewById(R.id.user_list_selection).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = GetUserListLoader.OWN_LISTS;
                loadingMoreUserList = false;
                reload();
            }

        });

        findViewById(R.id.user_following_list_selection).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type_id = GetUserListLoader.MEMBERSHIP_LIST;
                loadingMoreUserList = false;
                reload();
            }

        });

        ImageView bOptions = (ImageView) findViewById(R.id.user_list_more_options);
        if (user_id >= 0) {
            bOptions.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showMenuOptions(v);
                }

            });
        } else {
            bOptions.setVisibility(View.GONE);
        }

        findViewById(R.id.user_list_bar_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListsActivity.this.finish();
            }
        });

        findViewById(R.id.but_user_lists_load_more).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingMoreUserList = true;
                reload();
            }

        });

        userListArrayList = new ArrayList<UserList>();
        userListsAdapter = new RowUserListsAdapter(this, userListArrayList);
        GridView gridUserList = (GridView)this.findViewById(R.id.grid_userlist);
        gridUserList.setAdapter(userListsAdapter);

        gridUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                UserList userList = userListsAdapter.getItem(pos);

                createUserListsColumn(userList);
            }
        });

        gridUserList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                return true;
            }
        });

        type_id = GetUserListLoader.OWN_LISTS;

        reload();
    }

    private void showDialogNewList() {
        CreateListTwitterDialogFragment frag = new CreateListTwitterDialogFragment(new CreateListTwitterDialogFragment.CreateListListener() {
            @Override
            public void onCreateList(String title, String description, boolean isPublic) {
                createNewList(title, description, isPublic);
            }
        });
        frag.show(getSupportFragmentManager(), "dialog");
    }

    private void createNewList(String title, String description, boolean isPublic) {
        final ProgressDialog progressDialog = ProgressDialog.show(
                this,
                this.getResources().getString(R.string.loading),
                this.getResources().getString(R.string.loading)
        );
        APITweetTopics.execute(UserListsActivity.this, getSupportLoaderManager(), new APIDelegate() {
            @Override
            public void onResults(BaseResponse result) {
                progressDialog.dismiss();
                type_id = GetUserListLoader.OWN_LISTS;
                loadingMoreUserList = false;
                reload();
                Utils.showMessage(UserListsActivity.this, R.string.created_list_message);
            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.dismiss();
                Utils.showMessage(UserListsActivity.this, R.string.error_general);
            }
        }, new CreateUserListsRequest(user_id, title, description, isPublic));
    }

    private void showMenuOptions(View view) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.list_users_more_actions, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popupmenu_list_user_update) {
                        showDialogNewList();
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
                        showDialogNewList();
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
        Utils.showMessage(this,error.getMsgError());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onResults(BaseResponse response) {
        if (!loadingMoreUserList) {
            userListArrayList.clear();
        }

        GetUserListResponse result = (GetUserListResponse)response;
        nextCursor = result.getNextCursor();
        userListArrayList.addAll(result.getUserListArrayList());

        viewLoadMoreLoading.setVisibility(View.GONE);

        if (nextCursor > 0) {
            showButtonLoadMore();
            userListsAdapter.setExistsMoreElements(true);
        } else {
            hideButtonLoadMore();
            userListsAdapter.setExistsMoreElements(false);
        }

        userListsAdapter.notifyDataSetChanged();

        if (userListsAdapter.getCount() == 0) {
            showNoLists();
        } else {
            showUserLists();
            viewUserLists.setSelection(userListsAdapter.getCount() - 1);
        }
    }

    private void createUserListsColumn(UserList userList) {

        ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "userlist_id=" + userList.getId());
        int position = 0;

        if (created_column_list.size() == 0) {
            position = ColumnsUtils.nextPositionColumn();

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_LIST_USER);
            Entity user_list = new Entity("columns");
            user_list.setValue("description", userList.getName());
            user_list.setValue("type_id", type);
            user_list.setValue("user_id", user_id);
            user_list.setValue("position", position);
            user_list.setValue("userlist_id", userList.getId());
            user_list.save();
            Toast.makeText(this, getString(R.string.column_created, userList.getName()), Toast.LENGTH_LONG).show();
        } else {
            position = created_column_list.get(0).getInt("position");
        }

        Intent intent = getIntent();
        intent.putExtra("position", position);

        setResult(RESULT_OK, intent);
        finish();

        /*ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "userlist_id=" + userList.getId());

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
        finish();*/
    }

    public void reload() {
        if (loadingMoreUserList) {
            showLoadingMore();
        } else {
            showLoading();
        }

        GetUserListRequest getUserListRequest = new GetUserListRequest(user_id, screenName, type_id, nextCursor);
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
        viewLoadMore.setVisibility(View.GONE);
    }

    public void showLoadingMore() {
        buttonLoadMore.setVisibility(View.GONE);
        viewLoadMoreLoading.setVisibility(View.VISIBLE);
    }

    public void showButtonLoadMore() {
        viewLoadMore.setVisibility(View.VISIBLE);
        buttonLoadMore.setVisibility(View.VISIBLE);
        viewLoadMoreLoading.setVisibility(View.GONE);
    }

    public void hideButtonLoadMore() {
        viewLoadMore.setVisibility(View.GONE);
        buttonLoadMore.setVisibility(View.GONE);
        viewLoadMoreLoading.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewNoLists.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        viewUserLists.setVisibility(View.GONE);
        viewLoadMore.setVisibility(View.GONE);
    }

    public void showUserLists() {
        viewNoLists.setVisibility(View.GONE);
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        viewUserLists.setVisibility(View.VISIBLE);
    }
}