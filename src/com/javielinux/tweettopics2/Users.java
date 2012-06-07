package com.javielinux.tweettopics2;

import adapters.RowUserAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import api.APIDelegate;
import api.APITweetTopics;
import api.loaders.ProfileImageLoader;
import api.request.ProfileImageRequest;
import api.response.ErrorResponse;
import api.response.ProfileImageResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.facebook.FacebookHandler;
import com.javielinux.twitter.AuthorizationActivity;
import com.javielinux.twitter.ConnectionManager2;
import com.javielinux.utils.DialogUtils.BuyProDialogBuilder;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetTopicsConstants;
import com.javielinux.utils.Utils;
import preferences.Preferences;
import twitter4j.TwitterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Users extends BaseActivity {

    public static final int ACTIVITY_NEWUSER = 0;
    public static final int ACTIVITY_EDITUSER = 1;
    private static final int ACTIVITY_SELECTIMAGE = 2;
    private static final int ACTIVITY_CAMERA = 3;

    RowUserAdapter adapter;

    private long idUser = -1;

    private long idUserAux = -1;

    private ListView mListView;
    private TextView mEmpty;

    private LinearLayout mLayoutBackgroundApp;

    private ThemeManager mThemeManager;

    private ProgressDialog progressDialog;


    public String getURLNewAvatar() {
        return Utils.appDirectory + "aux_avatar_" + idUser + ".jpg";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThemeManager = new ThemeManager(this);
        mThemeManager.setTheme();

        if (savedInstanceState != null && savedInstanceState.containsKey("user_id"))
            idUser = savedInstanceState.getLong("user_id");

        setContentView(R.layout.users_list);

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionManager2.getInstance().open(this);

        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {
            idUserAux = e.getId();
        }

        Button btTwitter = (Button) findViewById(R.id.bt_twitter);
        btTwitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                newUserTwitter();
            }

        });

        Button btFacebook = (Button) findViewById(R.id.bt_facebook);
        btFacebook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                newUserFacebook();
            }

        });


        mLayoutBackgroundApp = (LinearLayout) findViewById(R.id.layout_background_app);

        mListView = (ListView) this.findViewById(R.id.list_users);

        mEmpty = (TextView) this.findViewById(R.id.empty);

        refreshTheme();

        fillData();
    }

    public void refreshTheme() {
        File f = new File(Preferences.IMAGE_WALLPAPER);
        if (f.exists()) {
            BitmapDrawable bmp = (BitmapDrawable) BitmapDrawable.createFromPath(Preferences.IMAGE_WALLPAPER);
            bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            mLayoutBackgroundApp.setBackgroundDrawable(bmp);
        } else {
            if (mThemeManager.getTheme() == ThemeManager.THEME_DEFAULT) {
                mLayoutBackgroundApp.setBackgroundResource(R.drawable.background_user);
            } else {
                mLayoutBackgroundApp.setBackgroundResource(R.drawable.background_user_dark);
            }
        }
        mThemeManager.setColors();
        refreshColorsBars();
        refreshColorsListView();

    }

    private void refreshColorsBars() {

    }


    private void refreshColorsListView() {
        mListView.setDivider(ImageUtils.createDividerDrawable(this, new ThemeManager(this).getColor("color_divider_tweet")));
        if (Utils.getPreference(this).getBoolean("prf_use_divider_tweet", true)) {
            mListView.setDividerHeight(2);
        } else {
            mListView.setDividerHeight(0);
        }
    }

    private void showSelectImageDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.select_action);
        alert.setItems(R.array.select_type_image, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    File f = new File(getURLNewAvatar());
                    if (f.exists()) f.delete();

                    Intent intendCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intendCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    intendCapture.putExtra("return-data", true);
                    startActivityForResult(intendCapture, ACTIVITY_CAMERA);
                } else if (which == 1) {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(i, ACTIVITY_SELECTIMAGE);
                }
            }
        });
        alert.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void showDeleteUserDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.title_question_delete);
        alert.setMessage(R.string.question_delete);
        alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteUser();
            }
        });
        alert.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void showFacebookDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.actions));
        alert.setItems(R.array.actions_users_facebook, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showDeleteUserDialog();
                }
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    private void showTwitterDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.actions));
        alert.setItems(R.array.actions_users, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editUser();
                } else if (which == 1) {
                    showSelectImageDialog();
                } else if (which == 2) {
                    refreshAvatar();
                } else if (which == 3) {
                    showDeleteUserDialog();
                }
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.setCancelable(false);
        alert.show();
    }


    public void fillData() {
        try {

            adapter = new RowUserAdapter(this, DataFramework.getInstance().getEntityList("users"));

            mListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int pos, long id2) {
                    Entity e = (Entity) adapter.getItem(pos);
                    idUser = e.getId();
                    if (e.getString("service").equals("facebook")) {
                        showFacebookDialog();
                    } else {
                        showTwitterDialog();
                    }
                }
            });

            mListView.setAdapter(adapter);

            if (mListView.getCount() <= 0) {
                mEmpty.setVisibility(View.VISIBLE);
            } else {
                mEmpty.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

    }

    public void editUser() {
        if (idUser > 0) {
            Intent edit = new Intent(this, EditUserTwitter.class);
            edit.putExtra(DataFramework.KEY_ID, idUser);
            startActivityForResult(edit, ACTIVITY_EDITUSER);
        }
    }

    public void refreshAvatar() {
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(R.string.user_list);
        progressDialog.setMessage(this.getResources().getString(R.string.update_avatar_loading));

        progressDialog.setCancelable(false);

        progressDialog.show();

        APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<ProfileImageResponse>() {
            @Override
            public void onResults(ProfileImageResponse result) {
                progressDialog.cancel();

                fillData();
                if (result.getReady())
                    Utils.showMessage(Users.this, Users.this.getString(R.string.refresh_avatar_correct));

            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.cancel();
                Utils.showMessage(Users.this, Users.this.getString(R.string.refresh_avatar_no_correct));
            }
        }, new ProfileImageRequest(ProfileImageLoader.REFRESH_AVATAR, idUser));

    }

    public void updateAvatar() {

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle(R.string.user_list);
        progressDialog.setMessage(this.getResources().getString(R.string.change_avatar_loading));

        progressDialog.setCancelable(false);

        progressDialog.show();

        APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<ProfileImageResponse>() {
            @Override
            public void onResults(ProfileImageResponse result) {
                progressDialog.cancel();

                fillData();
                if (result.getReady())
                    Utils.showMessage(Users.this, Users.this.getString(R.string.change_avatar_correct));

            }

            @Override
            public void onError(ErrorResponse error) {
                progressDialog.cancel();
                Utils.showMessage(Users.this, Users.this.getString(R.string.change_avatar_no_correct));
            }
        }, new ProfileImageRequest(ProfileImageLoader.CHANGE_AVATAR, idUser));
    }

    public void deleteUser() {
        if (idUser > 0) {
            Entity ent = new Entity("users", idUser);

            String sqlTweetsDelete = "DELETE FROM tweets_user WHERE user_tt_id=" + ent.getId();
            DataFramework.getInstance().getDB().execSQL(sqlTweetsDelete);

            String sqlColumsnDelete = "DELETE FROM columns WHERE user_id=" + ent.getId();
            DataFramework.getInstance().getDB().execSQL(sqlColumsnDelete);

            ent.delete();

            fillData();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showDialogBuyPro() {
        try {
            AlertDialog builder = BuyProDialogBuilder.create(this);
            builder.show();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newUserTwitter() {
        if (Utils.isLite(this)) {
            if (DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size() < 1) {
                startAuthorization(Utils.NETWORK_TWITTER);
            } else {
                showDialogBuyPro();
                Utils.showMessage(this, getString(R.string.max_users_lite));
            }
        } else {
            startAuthorization(Utils.NETWORK_TWITTER);
        }
    }

    public void newUserFacebook() {
        int nUserTwitter = DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size();
        if (nUserTwitter <= 0) {
            Utils.showMessage(this, getString(R.string.first_twitter_user));
        } else {
            if (Utils.isLite(this)) {
                if (DataFramework.getInstance().getEntityList("users", "service = \"facebook\"").size() < 1) {
                    startAuthorization(Utils.NETWORK_FACEBOOK);
                } else {
                    showDialogBuyPro();
                    Utils.showMessage(this, getString(R.string.max_users_lite));
                }
            } else {
                startAuthorization(Utils.NETWORK_FACEBOOK);
            }
        }
    }

    public void createColumnsCurrentUser() {
        final Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {
            CharSequence[] choices = new CharSequence[3];
            choices[0] = getString(R.string.timeline);
            choices[1] = getString(R.string.mentions);
            choices[2] = getString(R.string.direct_messages);

            final boolean[] isChoices = new boolean[]{true, true, true};

            LinearLayout llTitle = new LinearLayout(this);
            llTitle.setOrientation(LinearLayout.VERTICAL);
            final CheckBox boxInvite = new CheckBox(this);
            boxInvite.setText(R.string.follow_tweettopics);
            boxInvite.setChecked(true);
            llTitle.addView(boxInvite);
            TextView txtTitle = new TextView(this);
            txtTitle.setText(R.string.create_columns);
            txtTitle.setTextSize(25);
            llTitle.addView(txtTitle);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCustomTitle(llTitle);
            builder.setMultiChoiceItems(choices, isChoices,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                            isChoices[whichButton] = isChecked;
                        }
                    });
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // choices
                    int count = DataFramework.getInstance().getEntityListCount("columns", "") + 1;
                    if (isChoices[0]) {
                        Entity type = new Entity("type_columns", (long)TweetTopicsConstants.COLUMN_TIMELINE);
                        Entity timeline = new Entity("columns");
                        timeline.setValue("description", type.getString("description"));
                        timeline.setValue("type_id", type);
                        timeline.setValue("position", count);
                        timeline.setValue("user_id", e.getId());
                        timeline.save();
                        count++;
                    }
                    if (isChoices[1]) {
                        Entity type = new Entity("type_columns", (long)TweetTopicsConstants.COLUMN_MENTIONS);
                        Entity mentions = new Entity("columns");
                        mentions.setValue("description", type.getString("description"));
                        mentions.setValue("type_id", type);
                        mentions.setValue("position", count);
                        mentions.setValue("user_id", e.getId());
                        mentions.save();
                        count++;
                    }
                    if (isChoices[2]) {
                        Entity type = new Entity("type_columns", (long)TweetTopicsConstants.COLUMN_DIRECT_MESSAGES);
                        Entity dms = new Entity("columns");
                        dms.setValue("description", type.getString("description"));
                        dms.setValue("type_id", type);
                        dms.setValue("position", count);
                        dms.setValue("user_id", e.getId());
                        dms.save();
                    }

                    // create friend
                    if (boxInvite.isChecked()) {
                        try {
                            ConnectionManager2.getInstance().getTwitter(e.getId()).createFriendship("tweettopics_app");
                        } catch (TwitterException e1) {
                            e1.printStackTrace();
                        }
                        Utils.showMessage(Users.this, Users.this.getString(R.string.thanks));
                    }
                }

            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void startAuthorization(int network) {
        if (network == Utils.NETWORK_TWITTER) {
            Intent intent = new Intent(this, AuthorizationActivity.class);
            startActivityForResult(intent, ACTIVITY_NEWUSER);
        }
        if (network == Utils.NETWORK_FACEBOOK) {
            FacebookHandler fbh = new FacebookHandler(this);
            fbh.setUsersActivity(this);
            fbh.newUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case ACTIVITY_NEWUSER:
                fillData();
                createColumnsCurrentUser();
                break;
            case ACTIVITY_EDITUSER:
                fillData();
            case ACTIVITY_CAMERA:
                if (resultCode != 0) {
                    updateAvatar();
                }
                break;
            case ACTIVITY_SELECTIMAGE:
                if (resultCode != 0) {
                    Cursor cursor = managedQuery(intent.getData(), null, null, null, null);
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
                                    updateAvatar();
                                }
                            } catch (FileNotFoundException exception) {
                                exception.printStackTrace();
                            }
                        }
                        cursor.close();
                    } else {
                        Utils.showMessage(this, R.string.other_gallery);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
        //ConnectionManager.destroyInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.saveStatusWorkApp(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.saveStatusWorkApp(this, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("user_id", idUser);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void back() {
        boolean changed = true;
        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        if (e != null) {
            if (idUserAux == e.getId()) {
                changed = false;
            }
        }
        setResult(changed ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

}
