package com.javielinux.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.MyActivityAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.ProfileImageLoader;
import com.javielinux.api.request.ProfileImageRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.ProfileImageResponse;
import com.javielinux.dialogs.AlertDialogFragment;
import com.javielinux.dialogs.CreateDefaultColumnsUserDialogFragment;
import com.javielinux.dialogs.SelectImageDialogFragment;
import com.javielinux.facebook.FacebookHandler;
import com.javielinux.tweettopics2.*;
import com.javielinux.twitter.AuthorizationActivity;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MyActivityFragment extends Fragment {

    public static final int ACTIVITY_NEW_TWITTER_USER = 0;
    public static final int ACTIVITY_EDIT_TWITTER_USER = 1;
    public static final int ACTIVITY_SHOW_USER_LISTS = 2;

    private MyActivityAdapter adapter;

    private ListView listUsers;
    private TextView lblEmpty;
    private ProgressDialog progressDialog;

    private long idUser = 0;

    private ThemeManager themeManager;

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MyActivityAdapter(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.my_activity_fragment, null);

        themeManager = new ThemeManager(getActivity());
        themeManager.setTheme();

        BitmapDrawable bmp = (BitmapDrawable) getActivity().getResources().getDrawable(themeManager.getResource("search_tile"));
        if (bmp != null) {
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            view.setBackgroundDrawable(bmp);
        }

        Button btTwitter = (Button) view.findViewById(R.id.bt_twitter);
        btTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newUserTwitter();
            }

        });

        Button btFacebook = (Button) view.findViewById(R.id.bt_facebook);
        btFacebook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newUserFacebook();
            }

        });

        listUsers = (ListView) view.findViewById(R.id.my_activity_users);

        listUsers.setAdapter(adapter);

        lblEmpty = (TextView) view.findViewById(R.id.my_activity_empty);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case ACTIVITY_NEW_TWITTER_USER:
                if (resultCode == Activity.RESULT_OK) {
                    fillData();
                    // se hace en un handler porque sino da el siguiente error
                    // "Can not perform this action after onSaveInstanceState"
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            createColumnsLastUser();
                        }
                    });
                }
                break;
            case ACTIVITY_EDIT_TWITTER_USER:
                if (resultCode == Activity.RESULT_OK) {
                    fillData();
                }
            case SelectImageDialogFragment.ACTIVITY_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    changeAvatar();
                }
                break;
            case SelectImageDialogFragment.ACTIVITY_SELECTIMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Cursor cursor = getActivity().managedQuery(intent.getData(), null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String media_path = cursor.getString(1);

                            try {
                                if (idUser > 0) {
                                    Bitmap new_avatar = BitmapFactory.decodeFile(media_path);
                                    String file = getURLNewAvatar();

                                    FileOutputStream out = new FileOutputStream(file);
                                    new_avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    new_avatar.recycle();
                                    changeAvatar();
                                }
                            } catch (FileNotFoundException exception) {
                                exception.printStackTrace();
                            }
                        }
                        cursor.close();
                    } else {
                        Utils.showMessage(getActivity(), R.string.other_gallery);
                    }
                }
                break;
            case ACTIVITY_SHOW_USER_LISTS:
                if (resultCode == Activity.RESULT_OK) {
                    final int position = intent.getIntExtra("position", 0);

                    Handler myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((TweetTopicsActivity)getActivity()).getFragmentPagerAdapter().refreshColumnList();
                            ((TweetTopicsActivity)getActivity()).getViewPager().setCurrentItem(position, false);
                        }
                    }, 100);
                }
                break;
        }
    }

    public void clickUser(Entity user) {
        idUser = user.getId();
        if (user.getString("service").equals("facebook")) {
            showFacebookDialog();
        } else {
            showTwitterDialog();
        }
    }

    public void clickSearch(Entity clickSearch) {
        final ArrayList<Entity> created_column_list = DataFramework.getInstance().getEntityList("columns", "search_id=" + clickSearch.getId());

        if (created_column_list.size() == 0) {
            final int position = DataFramework.getInstance().getEntityListCount("columns", "") + 1;

            Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_SEARCH);
            Entity search = new Entity("columns");
            search.setValue("description", type.getString("description"));
            search.setValue("type_id", type);
            search.setValue("position", position);
            search.setValue("search_id", clickSearch.getId());
            search.save();
            Toast.makeText(getActivity(), getString(R.string.column_created, clickSearch.getString("name")), Toast.LENGTH_LONG).show();

            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((TweetTopicsActivity)getActivity()).getFragmentPagerAdapter().refreshColumnList();
                    ((TweetTopicsActivity)getActivity()).getViewPager().setCurrentItem(position, false);
                }
            }, 100);
        } else {
            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((TweetTopicsActivity)getActivity()).getViewPager().setCurrentItem(created_column_list.get(0).getInt("position"), false);
                }
            }, 100);

        }
    }

    public void fillData() {
        adapter.refresh();
        adapter.notifyDataSetChanged();

        if (listUsers.getCount() <= 0) {
            listUsers.setVisibility(View.GONE);
            lblEmpty.setVisibility(View.VISIBLE);
        } else {
            listUsers.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE);
        }

    }

    public String getURLNewAvatar() {
        return Utils.appDirectory + "aux_avatar_" + idUser + ".jpg";
    }

    public void newUserTwitter() {
        if (Utils.isLite(getActivity())) {
            if (DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size() < 1) {
                startAuthorization(Utils.NETWORK_TWITTER);
            } else {
                showDialogBuyPro();
                Utils.showMessage(getActivity(), getString(R.string.max_users_lite));
            }
        } else {
            startAuthorization(Utils.NETWORK_TWITTER);
        }
    }

    public void newUserFacebook() {
        int nUserTwitter = DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size();
        if (nUserTwitter <= 0) {
            Utils.showMessage(getActivity(), getString(R.string.first_twitter_user));
        } else {
            if (Utils.isLite(getActivity())) {
                if (DataFramework.getInstance().getEntityList("users", "service = \"facebook\"").size() < 1) {
                    startAuthorization(Utils.NETWORK_FACEBOOK);
                } else {
                    showDialogBuyPro();
                    Utils.showMessage(getActivity(), getString(R.string.max_users_lite));
                }
            } else {
                startAuthorization(Utils.NETWORK_FACEBOOK);
            }
        }
    }

    private void startAuthorization(int network) {
        if (network == Utils.NETWORK_TWITTER) {
            Intent intent = new Intent(getActivity(), AuthorizationActivity.class);
            startActivityForResult(intent, ACTIVITY_NEW_TWITTER_USER);
        }
        if (network == Utils.NETWORK_FACEBOOK) {
            FacebookHandler fbh = new FacebookHandler(getActivity());
            // TODO
            //fbh.setUsersActivity(getActivity());
            fbh.newUser();
        }
    }

    public void showDialogBuyPro() {
        // TODO
    }

    public void deleteUser() {
        if (idUser > 0) {
            Entity ent = new Entity("users", idUser);

            String sqlTweetsDelete = "DELETE FROM tweets_user WHERE user_tt_id=" + ent.getId();
            DataFramework.getInstance().getDB().execSQL(sqlTweetsDelete);

            String sqlColumnsDelete = "DELETE FROM columns WHERE user_id=" + ent.getId();
            DataFramework.getInstance().getDB().execSQL(sqlColumnsDelete);

            ent.delete();

            fillData();
        }
    }

    public void editUser() {
        if (idUser > 0) {
            Intent edit = new Intent(getActivity(), EditUserTwitter.class);
            edit.putExtra(DataFramework.KEY_ID, idUser);
            startActivityForResult(edit, ACTIVITY_EDIT_TWITTER_USER);
        }
    }

    public void refreshAvatar() {
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(this.getResources().getString(R.string.update_avatar_loading));

        progressDialog.setCancelable(false);

        progressDialog.show();

        APITweetTopics.execute(getActivity(), getLoaderManager(), new APIDelegate<ProfileImageResponse>() {
            @Override
            public void onResults(ProfileImageResponse result) {
                progressDialog.cancel();

                fillData();
                if (result.getReady())
                    Utils.showMessage(getActivity(), getActivity().getString(R.string.refresh_avatar_correct));

            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.cancel();
                Utils.showMessage(getActivity(), getActivity().getString(R.string.refresh_avatar_no_correct));
            }
        }, new ProfileImageRequest(ProfileImageLoader.REFRESH_AVATAR, idUser));

    }

    public void showUserLists() {
        if (idUser > 0) {
            Intent userLists = new Intent(getActivity(), UserListsActivity.class);
            userLists.putExtra(DataFramework.KEY_ID, idUser);
            startActivityForResult(userLists, ACTIVITY_SHOW_USER_LISTS);
        }
    }

    public void changeAvatar() {

        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(this.getResources().getString(R.string.change_avatar_loading));

        progressDialog.setCancelable(false);

        progressDialog.show();

        APITweetTopics.execute(getActivity(), getLoaderManager(), new APIDelegate<ProfileImageResponse>() {
            @Override
            public void onResults(ProfileImageResponse result) {
                progressDialog.cancel();

                fillData();
                if (result.getReady())
                    Utils.showMessage(getActivity(), getActivity().getString(R.string.change_avatar_correct));

            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.cancel();
                Utils.showMessage(getActivity(), getActivity().getString(R.string.change_avatar_no_correct));
            }
        }, new ProfileImageRequest(ProfileImageLoader.CHANGE_AVATAR, idUser));
    }

    private void showFacebookDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.actions);
        args.putBoolean("has_positive_button", false);
        args.putBoolean("cancelable", false);
        args.putInt("array_items", R.array.actions_users_facebook);
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
                    showDeleteUserDialog();
                }
            }
        });
        frag.show(getFragmentManager(), "dialog");

    }


    private void showTwitterDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.actions);
        args.putBoolean("has_positive_button", false);
        args.putBoolean("cancelable", false);
        args.putInt("array_items", R.array.actions_users);
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
                    editUser();
                } else if (which == 1) {
                    showSelectImageDialog();
                } else if (which == 2) {
                    refreshAvatar();
                } else if (which == 3) {
                    showDeleteUserDialog();
                } else if (which == 4) {
                    showUserLists();
                }
            }
        });
        frag.show(getFragmentManager(), "dialog");

    }

    private void showDeleteUserDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.title_question_delete);
        args.putInt("message", R.string.question_delete);
        frag.setArguments(args);
        frag.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
            @Override
            public void OnAlertButtonOk() {
                deleteUser();
            }

            @Override
            public void OnAlertButtonCancel() {
            }

            @Override
            public void OnAlertButtonNeutral() {
            }

            @Override
            public void OnAlertItems(int witch) {
            }
        });
        frag.show(getFragmentManager(), "dialog");

    }

    private void showSelectImageDialog() {

        SelectImageDialogFragment frag = new SelectImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.actions);
        args.putString("file", getURLNewAvatar());
        frag.setArguments(args);
        frag.show(getFragmentManager(), "dialog");

    }

    public void createColumnsLastUser() {

        long lastIdUser = DataFramework.getInstance().getTopEntity("users", "", DataFramework.KEY_ID + " desc").getId();

        CreateDefaultColumnsUserDialogFragment frag = new CreateDefaultColumnsUserDialogFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", lastIdUser);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "dialog");
    }

}