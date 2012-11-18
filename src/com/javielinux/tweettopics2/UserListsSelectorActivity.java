package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RowUserListsAdapter;
import com.javielinux.adapters.UsersAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.GetUserListLoader;
import com.javielinux.api.request.GetUserListRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetUserListResponse;
import com.javielinux.preferences.Preferences;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.UserList;

import java.io.File;
import java.util.ArrayList;

public class UserListsSelectorActivity extends BaseActivity implements APIDelegate<BaseResponse> {

    public static String KEY_ACTIVE_USER_ID = "KEY_ACTIVE_USER_ID";
    public static String KEY_LIST_ID = "KEY_LIST_ID";

    private RowUserListsAdapter userListsAdapter;
    private ArrayList<UserList> userListArrayList;
    private Entity userEntity;
    private long nextCursor;
    private boolean loadingMoreUserList;

    private ThemeManager themeManager;

    private LinearLayout mLayoutBackgroundApp;
    private LinearLayout viewNoLists;
    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private GridView viewUserLists;

    private RelativeLayout viewUserInfo;
    private ImageView userIcon;
    private TextView userFullname;
    private TextView userName;

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

        userEntity = null;

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
            userEntity = DataFramework.getInstance().getTopEntity("users","","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userEntity == null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        nextCursor = -1;
        loadingMoreUserList = false;

        themeManager = new ThemeManager(this);
        themeManager.setTheme();

        setContentView(R.layout.userlistsselector_activity);

        mLayoutBackgroundApp = (LinearLayout) this.findViewById(R.id.user_lists_parent);
        layoutActionBar = (RelativeLayout) findViewById(R.id.user_list_bar_action);
        titlePage = (TextView) this.findViewById(R.id.user_list_bar_title);

        refreshTheme();

        viewNoLists = (LinearLayout) this.findViewById(R.id.user_lists_view_no_lists);
        viewLoading = (LinearLayout) this.findViewById(R.id.user_lists_view_loading);
        viewNoInternet = (LinearLayout) this.findViewById(R.id.user_lists_view_no_internet);
        viewUserLists = (GridView) this.findViewById(R.id.grid_userlist);
        viewUserInfo = (RelativeLayout) this.findViewById(R.id.user_info);

        userIcon = (ImageView)this.findViewById(R.id.user_icon);
        userFullname = (TextView)this.findViewById(R.id.user_fullname);
        userName = (TextView)this.findViewById(R.id.user_name);

        viewUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Entity> userEntityList = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

                final UsersAdapter adapter = new UsersAdapter(UserListsSelectorActivity.this, userEntityList);

                AlertDialog builder = new AlertDialog.Builder(UserListsSelectorActivity.this)
                        .setCancelable(true)
                        .setTitle(R.string.users)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userEntity = adapter.getItem(which);
                                loadUserInfo();
                                reload();
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
        });

        findViewById(R.id.user_list_bar_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListsSelectorActivity.this.finish();
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
                Intent intent = getIntent();
                intent.putExtra(KEY_ACTIVE_USER_ID, userEntity.getId());
                intent.putExtra(KEY_LIST_ID, userList.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        gridUserList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                return true;
            }
        });

        loadUserInfo();
        reload();
    }

    private void loadUserInfo() {
        if (userEntity != null) {
            userIcon.setImageBitmap(ImageUtils.getBitmapAvatar(userEntity.getId(), Utils.AVATAR_LARGE));
            userFullname.setText(userEntity.getString("fullname"));
            userName.setText(userEntity.getString("name"));
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
        userListArrayList.clear();

        GetUserListResponse result = (GetUserListResponse)response;
        userListArrayList.addAll(result.getUserListArrayList());
        userListsAdapter.notifyDataSetChanged();

        if (userListsAdapter.getCount() == 0) {
            showNoLists();
        } else {
            showUserLists();
            viewUserLists.setSelection(userListsAdapter.getCount() - 1);
        }
    }

    public void reload() {
        showLoading();

        GetUserListRequest getUserListRequest = new GetUserListRequest(userEntity.getId(), userEntity.getString("name"), GetUserListLoader.OWN_LISTS, -1);
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