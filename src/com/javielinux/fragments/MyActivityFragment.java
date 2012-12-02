package com.javielinux.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.MyActivityAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.ProfileImageLoader;
import com.javielinux.api.request.ProfileImageRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.ProfileImageResponse;
import com.javielinux.dialogs.*;
import com.javielinux.facebook.FacebookHandler;
import com.javielinux.preferences.Preferences;
import com.javielinux.tweettopics2.*;
import com.javielinux.twitter.AuthorizationActivity;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetActions;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.Callable;

public class MyActivityFragment extends Fragment {

    public static final int ACTIVITY_NEW_TWITTER_USER = 0;
    public static final int ACTIVITY_EDIT_TWITTER_USER = 1;

    private MyActivityAdapter adapter;

    private ListView listUsers;
    private TextView lblEmpty;
    private ProgressDialog progressDialog;

    private Entity userSelected = null;

    private long idUser = 0;

    private ThemeManager themeManager;

    private Handler handler = new Handler();

    public MyActivityFragment() {
        super();
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
                    Cursor cursor = getActivity().getContentResolver().query(intent.getData(), null, null, null, null);
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

        }
    }

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

        themeManager.setColors();

        view.findViewById(R.id.layout_foot).setBackgroundColor(themeManager.getColor("color_bottom_bar"));

        BitmapDrawable bmp = (BitmapDrawable) getActivity().getResources().getDrawable(themeManager.getResource("search_tile"));
        if (bmp != null) {
            bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            view.setBackgroundDrawable(bmp);
        }

        view.findViewById(R.id.my_activity_add_user).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogSocialNetworks();
            }

        });

        view.findViewById(R.id.my_activity_add_search).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((TweetTopicsActivity)getActivity()).newSearch();
            }

        });

        view.findViewById(R.id.my_activity_add_trending).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenuMoreActions(v);
            }

        });

        view.findViewById(R.id.my_activity_more_options).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenuOptions(v);
            }

        });

        listUsers = (ListView) view.findViewById(R.id.my_activity_users);

        listUsers.setAdapter(adapter);

        lblEmpty = (TextView) view.findViewById(R.id.my_activity_empty);

        return view;
    }

    private void showMenuMoreActions(View v) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.getMenuInflater().inflate(R.menu.my_activity_more_actions, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popupmenu_more_actions_saved_tweet) {
                        openSavedTweetColumn();
                    } else if (item.getItemId() == R.id.popupmenu_more_actions_trending_topics) {
                        newTrending();
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
            args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.popupmenu_my_activity_more_actions);
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
                        openSavedTweetColumn();
                    } else if (which == 1) {
                        newTrending();
                    }
                }
            });
            frag.show(getFragmentManager(), "dialog");
        }
    }

    private void showThemeDialog() {
        ThemeManagerDialogFragment frag = new ThemeManagerDialogFragment(new Callable() {
            @Override
            public Object call() throws Exception {
                if (DataFramework.getInstance().getEntityList("themes").size()>0) {
                    LoadThemeDialogFragment frag = new LoadThemeDialogFragment(new Callable() {
                        @Override
                        public Object call() throws Exception {
                            ((TweetTopicsActivity)getActivity()).refreshTheme();
                            return null;
                        }
                    });
                    frag.show(getFragmentManager(), "dialog");
                } else {
                    Utils.showMessage(getActivity(), getString(R.string.no_themes));
                }

                return null;
            }
        });
        frag.show(getFragmentManager(), "dialog");
    }

    private void showMenuOptions(View v) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.getMenuInflater().inflate(R.menu.my_activity_more_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popupmenu_more_options_size) {
                        showSizeText();
                    } else if (item.getItemId() == R.id.popupmenu_more_options_theme) {
                        showThemeDialog();
                    } else if (item.getItemId() == R.id.popupmenu_more_options_preferences) {
                        Intent i = new Intent(getActivity(), Preferences.class);
                        startActivity(i);
                    } else if (item.getItemId() == R.id.popupmenu_more_options_exit) {
                        showDialogExit();
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
            args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.popupmenu_my_activity_more_options);
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
                        showSizeText();
                    } else if (which == 1) {
                        Intent i = new Intent(getActivity(), Preferences.class);
                        startActivity(i);
                    } else if (which == 2) {
                        showThemeDialog();
                    }  else if (which == 3) {
                        showDialogExit();
                    }
                }
            });
            frag.show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void showDialogSocialNetworks() {
        TypeSocialNetworksDialogFragment frag = new TypeSocialNetworksDialogFragment(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0) {
                    newUserTwitter();
                } else if (i==1) {
                    newUserFacebook();
                }
            }
        });
        Bundle args = new Bundle();
        args.putInt("title", R.string.select_network);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "dialog");
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


    public void clickUser(Entity user) {
        userSelected = user;
        idUser = user.getId();
        if (user.getString("service").equals("facebook")) {
            showFacebookDialog();
        } else {
            showTwitterDialog();
        }
    }

    public void createColumnsLastUser() {

        long lastIdUser = DataFramework.getInstance().getTopEntity("users", "", DataFramework.KEY_ID + " desc").getId();

        CreateDefaultColumnsUserDialogFragment frag = new CreateDefaultColumnsUserDialogFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", lastIdUser);
        frag.setArguments(args);
        frag.show(getFragmentManager(), "dialog");
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

            Handler myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((TweetTopicsActivity) getActivity()).getFragmentPagerAdapter().refreshColumnList();
                    ((TweetTopicsActivity) getActivity()).refreshActionBarColumns();
                }
            }, 100);
        }
    }

    public void saveForUseForSearches() {
        if (idUser > 0) {
            String sqlUpdate = "UPDATE users SET use_for_searches=" + 0;
            DataFramework.getInstance().getDB().execSQL(sqlUpdate);
            Entity user = new Entity("users", idUser);
            user.setValue("use_for_searches", 1);
            user.save();
        }
    }

    public void showUser() {
        if (getActivity() instanceof BaseLayersActivity) {
            Bundle bundle = new Bundle();
            bundle.putString(UserActivity.KEY_EXTRAS_USER, userSelected.getString("name"));
            ((BaseLayersActivity)getActivity()).startAnimationActivity(UserActivity.class, bundle);
        }
    }

    public void editUser() {
        if (idUser > 0) {
            Intent edit = new Intent(getActivity(), EditUserTwitter.class);
            edit.putExtra(DataFramework.KEY_ID, idUser);
            startActivityForResult(edit, ACTIVITY_EDIT_TWITTER_USER);
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
                }
            } else {
                startAuthorization(Utils.NETWORK_FACEBOOK);
            }
        }
    }


    public void newTrending() {
        ((TweetTopicsActivity)getActivity()).newTrending();
    }

    public void openSavedTweetColumn() {
        ((TweetTopicsActivity)getActivity()).openSavedTweetColumn();
    }

    public void openUserColumn(long userId, int typeId) {
        ((TweetTopicsActivity)getActivity()).openUserColumn(userId, typeId);
    }

    public void createUserFavoritesColumn() {
        if (idUser > 0)
            openUserColumn(idUser, TweetTopicsUtils.COLUMN_FAVORITES);
    }

    public void createUserRetweetByUserColumn() {
        if (idUser > 0)
            openUserColumn(idUser, TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU);
    }

    public void createUserRetweetByOtherColumn() {
        if (idUser > 0)
            openUserColumn(idUser, TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS);
    }

    public void newUserTwitter() {
        if (Utils.isLite(getActivity())) {
            if (DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size() < 1) {
                startAuthorization(Utils.NETWORK_TWITTER);
            } else {
                showDialogBuyPro();
            }
        } else {
            startAuthorization(Utils.NETWORK_TWITTER);
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
                progressDialog.dismiss();

                fillData();
                if (result.getReady())
                    Utils.showMessage(getActivity(), getActivity().getString(R.string.refresh_avatar_correct));

            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.dismiss();
                Utils.showMessage(getActivity(), getActivity().getString(R.string.refresh_avatar_no_correct));
            }
        }, new ProfileImageRequest(ProfileImageLoader.REFRESH_AVATAR, idUser));

    }

    private void showDeleteUserDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.title_question_delete);
        args.putInt(AlertDialogFragment.KEY_ALERT_MESSAGE, R.string.question_delete);
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

    public void showDialogBuyPro() {
        BuyProDialogFragment frag = new BuyProDialogFragment();
        frag.show(getFragmentManager(), "dialog");
    }

    private void showFacebookDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.actions);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_POSITIVE_BUTTON, false);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_CANCELABLE, false);
        args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.actions_users_facebook);
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

    private void showSelectImageDialog() {

        SelectImageDialogFragment frag = new SelectImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.actions);
        args.putString("file", getURLNewAvatar());
        frag.setArguments(args);
        frag.show(getFragmentManager(), "dialog");

    }

    private void showTwitterDialog() {

        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.actions);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_POSITIVE_BUTTON, false);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_CANCELABLE, false);
        args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.actions_users);
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
                    showUser();
                } else if (which == 1) {
                    editUser();
                } else if (which == 2) {
                    showSelectImageDialog();
                } else if (which == 3) {
                    refreshAvatar();
                } else if (which == 4) {
                    showDeleteUserDialog();
                } else if (which == 5) {
                    showUserLists();
                } else if (which == 6) {
                    createUserFavoritesColumn();
                } else if (which == 7) {
                    createUserRetweetByUserColumn();
                } else if (which == 8) {
                    createUserRetweetByOtherColumn();
                } else if (which == 9) {
                    saveForUseForSearches();
                }

            }
        });
        frag.show(getFragmentManager(), "dialog");

    }

    public void showUserLists() {
        if (idUser > 0) {
            ((TweetTopicsActivity)getActivity()).createUserList(idUser);
        }
    }

    private void startAuthorization(int network) {
        if (network == Utils.NETWORK_TWITTER) {
            Intent intent = new Intent(getActivity(), AuthorizationActivity.class);
            startActivityForResult(intent, ACTIVITY_NEW_TWITTER_USER);
        }
        if (network == Utils.NETWORK_FACEBOOK) {
            FacebookHandler fbh = new FacebookHandler(getActivity());
            fbh.setMyActivityFragment(this);
            fbh.newUser();
        }
    }


    public void showSizeText() {

        final int minValue = 6;

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View sizesFontView = factory.inflate(R.layout.alert_dialog_sizes_font, null);

        ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(getActivity()) + ")");
        ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(getActivity()) + ")");

        SeekBar sbSizeTitles = (SeekBar) sizesFontView.findViewById(R.id.sb_size_titles);

        sbSizeTitles.setMax(18);
        sbSizeTitles.setProgress(PreferenceUtils.getSizeTitles(getActivity()) - minValue);

        sbSizeTitles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeTitles(getActivity(), progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_titles)).setText(getString(R.string.size_title) + " (" + PreferenceUtils.getSizeTitles(getActivity()) + ")");
                // TODO notificar al adapter el cambio de texto
                //mAdapterResponseList.notifyDataSetChanged();
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
        sbSizeText.setProgress(PreferenceUtils.getSizeText(getActivity()) - minValue);

        sbSizeText.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += minValue;
                PreferenceUtils.setSizeText(getActivity(), progress);
                //seekBar.setProgress(progress);
                ((TextView) sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeText(getActivity()) + ")");
                // TODO notificar al adapter el cambio de texto
                //mAdapterResponseList.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.font_size);
        builder.setView(sizesFontView);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    private void showDialogExit() {

        int minutes = Integer.parseInt(Utils.getPreference(getActivity()).getString("prf_time_notifications", "15"));

        if (minutes > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_exit);
            builder.setMessage(R.string.dialog_exit_msg);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    PreferenceUtils.saveNotificationsApp(getActivity(), false);
                    getActivity().finish();
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        } else {
            getActivity().finish();
        }
    }

    public void showDialogSamples() {
        final boolean[] samplesChecked = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};

        final CheckBox cb = new CheckBox(getActivity());
        cb.setText(R.string.samples_search_lang);
        cb.setTextColor(Color.GRAY);

        AlertDialog builder = new AlertDialog.Builder(getActivity())
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
                        String[] names = getResources().getStringArray(R.array.actions_samples_search);
                        String lang = "";
                        if (cb.isChecked()) {
                            String[] langs = getResources().getStringArray(R.array.languages_values);
                            for (int l=0; l<langs.length; l++) {
                                if (langs[l].equals(Locale.getDefault().getLanguage())) {
                                    lang = Locale.getDefault().getLanguage();
                                }
                            }
                        }
                        for (int i=0; i<samplesChecked.length; i++) {
                            if (samplesChecked[i]) {
                                Entity ent = new Entity("search");
                                ent.setValue("name", names[i]);
                                ent.setValue("date_create", Utils.now());
                                ent.setValue("last_modified", Utils.now());
                                ent.setValue("use_count", 0);
                                if (i==0) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i==1) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("words_or", "juego juegos videojuegos videjuego game games");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i==2) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "android");
                                    ent.setValue("words_or", "app aplicacion aplication");
                                    ent.setValue("icon_id", 2);
                                    ent.setValue("icon_big", "drawable/icon_android");
                                    ent.setValue("icon_small", "drawable/icon_android_small");
                                } else if (i==3) {
                                    ent.setValue("words_and", "tilt shift");
                                    ent.setValue("filter", 2);
                                    ent.setValue("icon_id", 17);
                                    ent.setValue("icon_big", "drawable/icon_photo");
                                    ent.setValue("icon_small", "drawable/icon_photo_small");
                                } else if (i==4) {
                                    ent.setValue("words_and", "hdr");
                                    ent.setValue("filter", 2);
                                    ent.setValue("icon_id", 18);
                                    ent.setValue("icon_big", "drawable/icon_photo2");
                                    ent.setValue("icon_small", "drawable/icon_photo2_small");
                                } else if (i==5) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "true blood");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_t");
                                    ent.setValue("icon_small", "drawable/letter_t_small");
                                } else if (i==6) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_and", "walking dead");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_w");
                                    ent.setValue("icon_small", "drawable/letter_w_small");
                                } else if (i==7) {
                                    ent.setValue("words_and", "4 8 15 16 23 42");
                                    ent.setValue("icon_id", 1);
                                    ent.setValue("icon_big", "drawable/letter_n");
                                    ent.setValue("icon_small", "drawable/letter_n_small");
                                } else if (i==8) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_or", "geek \"humor geek\"");
                                    ent.setValue("filter", 5);
                                    ent.setValue("icon_id", 14);
                                    ent.setValue("icon_big", "drawable/icon_news");
                                    ent.setValue("icon_small", "drawable/icon_news_small");
                                } else if (i==9) {
                                    ent.setValue("lang", lang);
                                    ent.setValue("words_or", "receta recipe");
                                    ent.setValue("filter", 5);
                                    ent.setValue("icon_id", 14);
                                    ent.setValue("icon_big", "drawable/icon_news");
                                    ent.setValue("icon_small", "drawable/icon_news_small");
                                } else if (i==10) {
                                    ent.setValue("words_and", "slow motion");
                                    ent.setValue("filter", 3);
                                    ent.setValue("icon_id", 3);
                                    ent.setValue("icon_big", "drawable/icon_cinema");
                                    ent.setValue("icon_small", "drawable/icon_cinema_small");
                                } else if (i==11) {
                                    ent.setValue("words_and", "stop motion");
                                    ent.setValue("filter", 3);
                                    ent.setValue("icon_id", 4);
                                    ent.setValue("icon_big", "drawable/icon_cinema2");
                                    ent.setValue("icon_small", "drawable/icon_cinema2_small");
                                }
                                ent.save();
                            }
                        }
                        fillData();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        builder.show();
    }

    /*public void showOptionsColumns(int positionX, int index) {


        int x = positionX - (layoutOptionsColumns.getWidth()/2);
        if (x<0) x = 0;
        if (x>widthScreen-layoutOptionsColumns.getWidth()) x = widthScreen-layoutOptionsColumns.getWidth();
        int y = (int)getResources().getDimension(R.dimen.actionbar_columns_height) - Utils.dip2px(this, 20);

        int xCenterView = x;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(x, y, 0, 0);
        layoutOptionsColumns.setLayoutParams(params);

        layoutMainOptionsColumns.setVisibility(View.VISIBLE);

        btnOptionsColumnsMain.setTag(index);
        btnOptionsColumnsDelete.setTag(index);

        ObjectAnimator translationX = ObjectAnimator.ofFloat(layoutOptionsColumns, "translationX", xCenterView-x, 0f);
        translationX.setDuration(150);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleX", 0f, 1f);
        scaleX.setDuration(150);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleY", 0f, 1f);
        scaleY.setDuration(150);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutOptionsColumns, "alpha", 0f, 1f);
        fadeAnim.setDuration(150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, scaleX, scaleY, fadeAnim);
        animatorSet.start();

    }

    public void hideOptionsColumns() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleX", 1f, 0f);
        scaleX.setDuration(150);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutOptionsColumns, "scaleY", 1f, 0f);
        scaleY.setDuration(150);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(layoutOptionsColumns, "alpha", 1f, 0f);
        fadeAnim.setDuration(150);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layoutMainOptionsColumns.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();

    }*/

    public void editSearch(Entity search) {
        ((TweetTopicsActivity)getActivity()).editSearch(search);
    }

    public void openSearchColumn(Entity search) {
        ((TweetTopicsActivity)getActivity()).openSearchColumn(search);
    }

    public void longClickSearch(final Entity search) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.actions);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_POSITIVE_BUTTON, false);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_CANCELABLE, false);
        args.putInt(AlertDialogFragment.KEY_ALERT_ARRAY_ITEMS, R.array.actions_search);
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
                    openSearchColumn(search);
                } else if (which == 1) {
                    editSearch(search);
                } else if (which == 2) {
                    shareSearch(search);
                } else if (which == 3) {
                    showDialogDeleteSearch(search);
                }
            }
        });
        frag.show(getFragmentManager(), "dialog");

    }

    private void deleteSearch(Entity search) {
        search.delete();
        (((TweetTopicsActivity)getActivity())).deleteSearchInColumn(search.getId());
        (((TweetTopicsActivity)getActivity())).refreshActionBarColumns();
        fillData();
        Utils.showMessage(getActivity(), getString(R.string.delete_correct));
    }

    private void showDialogDeleteSearch(final Entity search) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AlertDialogFragment.KEY_ALERT_TITLE, R.string.title_question_delete);
        args.putInt(AlertDialogFragment.KEY_ALERT_MESSAGE, R.string.question_delete);
        args.putBoolean(AlertDialogFragment.KEY_ALERT_HAS_NEGATIVE_BUTTON, true);
        args.putInt(AlertDialogFragment.KEY_ALERT_POSITIVE_LABEL, R.string.alert_dialog_ok);
        args.putInt(AlertDialogFragment.KEY_ALERT_NEGATIVE_LABEL, R.string.alert_dialog_cancel);
        alertDialogFragment.setArguments(args);
        alertDialogFragment.setAlertButtonListener(new AlertDialogFragment.AlertButtonListener() {
            @Override
            public void OnAlertButtonOk() {
                deleteSearch(search);
            }

            @Override
            public void OnAlertButtonCancel() {
            }

            @Override
            public void OnAlertButtonNeutral() {
            }

            @Override
            public void OnAlertItems(int which) {
            }
        });
        alertDialogFragment.show(getFragmentManager(), "dialog");

    }

    private void shareSearch(Entity search) {
        String name = search.getString("name");
        String text = Utils.HASHTAG_SHARE + " " + Utils.exportSearch(getActivity(), search.getId()) + " " + name;
        TweetActions.updateStatus(getActivity(), text);
    }
}