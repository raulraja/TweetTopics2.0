package com.javielinux.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.notifications.OnAlarmReceiver;
import com.javielinux.tweettopics2.AdjustImage;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.DialogUtils.PersonalDialogBuilder;
import com.javielinux.utils.FileUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Preferences extends PreferenceActivity {

    public static final String IMAGE_WALLPAPER = Utils.appDirectory + "wallpaper.jpg";

    private static final int DIALOG_SELECT_IMAGE = 0;

    private static final int ACTIVITY_SELECTIMAGE = 0;
    private static final int ACTIVITY_CAMERA = 1;
    private static final int ACTIVITY_WALLPAPER = 2;


    private static String FILE_BACKUP = "/sdcard/backup_tweettopics.xml";

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SELECT_IMAGE:
                return new AlertDialog.Builder(this)
                        .setTitle(R.string.select_action)
                        .setItems(R.array.select_type_image, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    File f = new File(IMAGE_WALLPAPER);
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
                        })
                        .setNeutralButton(R.string.delete_background, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File f = new File(Preferences.IMAGE_WALLPAPER);
                                if (f.exists()) {
                                    f.delete();
                                }
                                Utils.showShortMessage(Preferences.this, Preferences.this.getString(R.string.correct_delete_background));
                            }

                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }

                        })
                        .create();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case ACTIVITY_CAMERA:
                if (resultCode != 0) {
                    createWallpaper();
                }
                break;
            case ACTIVITY_SELECTIMAGE:
                if (resultCode != 0) {
                    Cursor c = managedQuery(intent.getData(), null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        String media_path = c.getString(1);
                        try {
                            File f = new File(IMAGE_WALLPAPER);
                            if (f.exists()) f.delete();
                            FileUtils.copy(media_path, IMAGE_WALLPAPER);
                            createWallpaper();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    c.close();
                }
                break;
        }
    }

    private void createWallpaper() {
        Intent wallpaper = new Intent(this, AdjustImage.class);
        wallpaper.putExtra("file", IMAGE_WALLPAPER);
        startActivityForResult(wallpaper, ACTIVITY_WALLPAPER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ThemeManager mThemeManager = new ThemeManager(this);

        colors = mThemeManager.getColors();

        if (!Utils.isDev(this)) {
            CheckBoxPreference forceLitePref = (CheckBoxPreference) findPreference("prf_force_lite");
            forceLitePref.setEnabled(false);
        }

        Preference timePrefTl = (Preference) findPreference("prf_time_notifications");

        timePrefTl.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                OnAlarmReceiver.callAlarmTimeline(Preferences.this, Integer.parseInt(newValue.toString()));
                return true;
            }

        });

        Preference timePrefOthers = (Preference) findPreference("prf_time_notifications_mentions_dm");

        timePrefOthers.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                OnAlarmReceiver.callAlarmOthers(Preferences.this, Integer.parseInt(newValue.toString()));
                return true;
            }

        });

        Preference aboutPref = (Preference) findPreference("prf_about");

        aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                String file = "about.txt";
                if (Locale.getDefault().getLanguage().equals("es")) {
                    file = "about_es.txt";
                }

                try {
                    AlertDialog builder = PersonalDialogBuilder.create(Preferences.this, Preferences.this.getString(R.string.about), file);
                    builder.show();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            }

        });

        Preference submenuTweet = (Preference) findPreference("prf_submenutweet");

        submenuTweet.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent newuser = new Intent(Preferences.this, SubMenuTweet.class);
                startActivity(newuser);
                return false;
            }

        });

        Preference aboutChangeLog = (Preference) findPreference("prf_changelog");

        aboutChangeLog.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                String file = "changelog_en.txt";
                if (Locale.getDefault().getLanguage().equals("es")) {
                    file = "changelog_es.txt";
                }

                try {
                    AlertDialog builder = PersonalDialogBuilder.create(Preferences.this, Preferences.this.getString(R.string.changelog), file);
                    builder.show();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            }

        });

        Preference colorsApp = (Preference) findPreference("prf_colors_app");

        colorsApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent colorsApp = new Intent(Preferences.this, ColorsApp.class);
                startActivity(colorsApp);
                return false;
            }

        });

        Preference programmed = (Preference) findPreference("prf_tweetprogrammed");

        programmed.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent newuser = new Intent(Preferences.this, TweetProgrammed.class);
                startActivity(newuser);
                return false;
            }

        });

        Preference drafts = (Preference) findPreference("prf_draft");

        drafts.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent newuser = new Intent(Preferences.this, TweetDraft.class);
                startActivity(newuser);
                return false;
            }

        });

        Preference colorTweets = (Preference) findPreference("prf_color_tweets");

        colorTweets.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent newuser = new Intent(Preferences.this, Colors.class);
                startActivity(newuser);
                return false;
            }

        });

        Preference colorMentions = (Preference) findPreference("prf_color_mentions");

        colorMentions.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                selectColor(0);
                return false;
            }

        });

        Preference colorFavorites = (Preference) findPreference("prf_color_favorite");

        colorFavorites.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                selectColor(1);
                return false;
            }

        });

        Preference createBackup = (Preference) findPreference("prf_create_backup");

        createBackup.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(R.string.title_question_backup);
                builder.setMessage(R.string.question_backup);
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        backup();
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create();
                builder.show();

                return false;
            }

        });

        Preference createRestore = (Preference) findPreference("prf_create_restore");

        createRestore.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(R.string.title_question_restore);
                builder.setMessage(R.string.question_restore);
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        restore();
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create();
                builder.show();
                return false;
            }

        });

        Preference adwLauncherSettings = (Preference) findPreference("prf_adw_launcher_configure");

        adwLauncherSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent adw = new Intent(Preferences.this, IntegrationADW.class);
                startActivity(adw);
                return false;
            }

        });

        Preference serviceBitLy = (Preference) findPreference("prf_service_bitly");

        serviceBitLy.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater factory = LayoutInflater.from(Preferences.this);
                final View textEntryView = factory.inflate(R.layout.alert_dialog_username, null);
                ((TextView) textEntryView.findViewById(R.id.username_edit)).setText(PreferenceUtils.getUsernameBitly(Preferences.this));
                ((TextView) textEntryView.findViewById(R.id.key_edit)).setText(PreferenceUtils.getKeyBitly(Preferences.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(R.string.bitly_key);
                builder.setView(textEntryView);
                builder.setNeutralButton(R.string.goto_web, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("http://bit.ly/a/account");
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PreferenceUtils.setUsernameBitly(Preferences.this, ((TextView) textEntryView.findViewById(R.id.username_edit)).getText().toString());
                        PreferenceUtils.setKeyBitly(Preferences.this, ((TextView) textEntryView.findViewById(R.id.key_edit)).getText().toString());
                    }
                });
                builder.create();
                builder.show();
                return false;
            }

        });

        Preference serviceKarmacracy = (Preference) findPreference("prf_service_karmacracy");

        serviceKarmacracy.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater factory = LayoutInflater.from(Preferences.this);
                final View textEntryView = factory.inflate(R.layout.alert_dialog_username, null);
                ((TextView) textEntryView.findViewById(R.id.username_edit)).setText(PreferenceUtils.getUsernameKarmacracy(Preferences.this));
                ((TextView) textEntryView.findViewById(R.id.key_edit)).setText(PreferenceUtils.getKeyKarmacracy(Preferences.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(R.string.karmacracy_key);
                builder.setView(textEntryView);
                builder.setNeutralButton(R.string.goto_web, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("http://karmacracy.com/settings?t=connections");
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PreferenceUtils.setUsernameKarmacracy(Preferences.this, ((TextView) textEntryView.findViewById(R.id.username_edit)).getText().toString());
                        PreferenceUtils.setKeyKarmacracy(Preferences.this, ((TextView) textEntryView.findViewById(R.id.key_edit)).getText().toString());
                    }
                });
                builder.create();
                builder.show();
                return false;
            }

        });

        Preference quietSettings = (Preference) findPreference("prf_quiet");

        quietSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent quiet = new Intent(Preferences.this, QuietWords.class);
                startActivity(quiet);
                return false;
            }

        });

        Preference tweetQuick = (Preference) findPreference("prf_tweetquick");

        tweetQuick.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent tq = new Intent(Preferences.this, TweetQuick.class);
                startActivity(tq);
                return false;
            }

        });

        Preference typesRetweets = (Preference) findPreference("prf_types_retweet");

        typesRetweets.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent tq = new Intent(Preferences.this, RetweetsTypes.class);
                startActivity(tq);
                return false;
            }

        });

        Preference deleteTweetsPref = (Preference) findPreference("prf_delete_tweets");

        deleteTweetsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(R.string.title_question_delete);
                builder.setMessage(R.string.question_delete_generic);
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteTweets();
                    }
                });
                builder.setNegativeButton(R.string.alert_dialog_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create();
                builder.show();


                return false;
            }

        });
        /*
        Preference deleteCachePref = (Preference) findPreference("prf_delete_cache");
        
        deleteCachePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
	    		builder.setTitle(R.string.title_question_delete);
	    		builder.setMessage(R.string.question_delete_generic);
	            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	deleteCache();
	                }
	            });
	            builder.setNegativeButton(R.string.alert_dialog_close, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            });
	            builder.create();
	            builder.show();
				

		        
				return false;
			}
        	
        });
         */
        Preference imageBackground = (Preference) findPreference("prf_image_background");

        imageBackground.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_SELECT_IMAGE);

                return false;
            }

        });


        CheckBoxPreference use_divider_tweet = (CheckBoxPreference) findPreference("prf_use_divider_tweet");
        use_divider_tweet.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                Utils.showShortMessage(Preferences.this, Preferences.this.getString(R.string.out_app));
                return true;
            }
        });

        // hay que hacer esto por la forma de guardar los colores de los themas antiguamente

        Preference selectTheme = (Preference) findPreference("prf_theme");

        selectTheme.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                builder.setTitle(Preferences.this.getString(R.string.restarts_colors));
                builder.setMessage(Preferences.this.getString(R.string.desc_restarts_colors));
                builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ColorsApp.restartColors(Preferences.this);
                        Utils.showShortMessage(Preferences.this, Preferences.this.getString(R.string.refresh_theme));
                    }

                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.showShortMessage(Preferences.this, Preferences.this.getString(R.string.refresh_theme));
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }


        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backup() {
        try {
            File f = new File(FILE_BACKUP);
            if (f.exists()) f.delete();
            DataFramework.getInstance().backup(FILE_BACKUP);
            Utils.showMessage(this, this.getString(R.string.backup_correct));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Utils.showMessage(this, this.getString(R.string.error_general));
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showMessage(this, this.getString(R.string.error_general));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showMessage(this, this.getString(R.string.error_general));
        }
    }

    private void restore() {
        File f = new File(FILE_BACKUP);
        if (f.exists()) {
            try {
                DataFramework.getInstance().emptyTablesBackup();
                DataFramework.getInstance().restore(FILE_BACKUP);
                Utils.showMessage(this, this.getString(R.string.restore_correct));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Utils.showMessage(this, this.getString(R.string.error_general));
            } catch (IOException e) {
                e.printStackTrace();
                Utils.showMessage(this, this.getString(R.string.error_general));
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showMessage(this, this.getString(R.string.error_general));
            }
        } else {
            Utils.showMessage(this, this.getString(R.string.restore_nofile));
        }
    }

    /*
   private void deleteCache() {

       String path = Utils.filesDirPath;
       File dir = new File(path);
       if (dir.isDirectory()) {
           String [] files = dir.list();
           Log.d(Utils.TAG, "Borrar " + files.length + " avatares");
           for (int i = 0; i < files.length; i++) {
               if (!files[i].equals(".nomedia")) {
                   File file = new File(Utils.filesDirPath + "/" + files[i]);
                   if (file.exists()) file.delete();
               }
           }
       }
   }
    */
    private void deleteTweets() {
        try {
            DataFramework.getInstance().open(Preferences.this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataFramework.getInstance().emptyTable("tweets_user");

        for (Entity ent : DataFramework.getInstance().getEntityList("users")) {
            ent.setValue("last_timeline_id", 0);
            ent.setValue("last_mention_id", 0);
            ent.setValue("last_direct_id", 0);
            ent.save();
        }

        DataFramework.getInstance().close();

        Utils.showMessage(Preferences.this, getString(R.string.delete_correct_generic));
    }

    private static View dialogColor;
    private ArrayList<String> colors = new ArrayList<String>();
    private int mColorSelected = 0;

    private void selectedColor(int color) {
        ImageButton c1 = (ImageButton) dialogColor.findViewById(R.id.color1);
        c1.setBackgroundResource((color == 0) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c2 = (ImageButton) dialogColor.findViewById(R.id.color2);
        c2.setBackgroundResource((color == 1) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c3 = (ImageButton) dialogColor.findViewById(R.id.color3);
        c3.setBackgroundResource((color == 2) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c4 = (ImageButton) dialogColor.findViewById(R.id.color4);
        c4.setBackgroundResource((color == 3) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c5 = (ImageButton) dialogColor.findViewById(R.id.color5);
        c5.setBackgroundResource((color == 4) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c6 = (ImageButton) dialogColor.findViewById(R.id.color6);
        c6.setBackgroundResource((color == 5) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c7 = (ImageButton) dialogColor.findViewById(R.id.color7);
        c7.setBackgroundResource((color == 6) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        ImageButton c8 = (ImageButton) dialogColor.findViewById(R.id.color8);
        c8.setBackgroundResource((color == 7) ? R.drawable.btn_default_selected : R.drawable.btn_default_normal);
        mColorSelected = color;
    }

    /*
    private String getColorSelected() {
        try {
            return colors.get(mColorSelected);
        } catch (Exception e) {
        }
        return colors.get(0);
    }
    */
    private void loadColors() {
        ImageButton c1 = (ImageButton) dialogColor.findViewById(R.id.color1);
        Bitmap bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(0)));
        c1.setImageBitmap(bmp);
        c1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(0);
            }

        });

        ImageButton c2 = (ImageButton) dialogColor.findViewById(R.id.color2);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(1)));
        c2.setImageBitmap(bmp);
        c2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(1);
            }

        });

        ImageButton c3 = (ImageButton) dialogColor.findViewById(R.id.color3);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(2)));
        c3.setImageBitmap(bmp);
        c3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(2);
            }

        });
        ImageButton c4 = (ImageButton) dialogColor.findViewById(R.id.color4);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(3)));
        c4.setImageBitmap(bmp);
        c4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(3);
            }

        });
        ImageButton c5 = (ImageButton) dialogColor.findViewById(R.id.color5);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(4)));
        c5.setImageBitmap(bmp);
        c5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(4);
            }

        });
        ImageButton c6 = (ImageButton) dialogColor.findViewById(R.id.color6);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(5)));
        c6.setImageBitmap(bmp);
        c6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(5);
            }

        });
        ImageButton c7 = (ImageButton) dialogColor.findViewById(R.id.color7);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(6)));
        c7.setImageBitmap(bmp);
        c7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(6);
            }

        });
        ImageButton c8 = (ImageButton) dialogColor.findViewById(R.id.color8);
        bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.RGB_565);
        cv = new Canvas(bmp);
        cv.drawColor(Color.parseColor(colors.get(7)));
        c8.setImageBitmap(bmp);
        c8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedColor(7);
            }

        });
    }

    private void selectColor(int type) {
        final int idType = type; // 0 mentions 1 favorites
        dialogColor = LayoutInflater.from(this).inflate(R.layout.alert_dialog_select_color, null);

        loadColors();

        if (idType == 0) {
            selectedColor(PreferenceUtils.getColorMentions(Preferences.this));
        } else {
            selectedColor(PreferenceUtils.getColorFavorited(Preferences.this));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_dialog_color);
        builder.setView(dialogColor);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (idType == 0) {
                    PreferenceUtils.setColorMentions(Preferences.this, mColorSelected);
                } else {
                    PreferenceUtils.setColorFavorited(Preferences.this, mColorSelected);
                }
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

}
