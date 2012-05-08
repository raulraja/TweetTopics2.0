package com.javielinux.tweettopics;

import adapters.RowUserAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.cyrilmottier.android.greendroid.R;
import com.javielinux.facebook.FacebookHandler;
import com.javielinux.tweettopics.Utils.BuyProDialogBuilder;
import com.javielinux.twitter.AuthorizationActivity;
import com.javielinux.twitter.ConnectionManager;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import preferences.Preferences;
import task.ProfileImageAsyncTask;
import task.ProfileImageAsyncTask.Params;
import twitter4j.TwitterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class Users extends GDActivity implements ProfileImageAsyncTask.ProfileImageAsyncTaskResponder {

	public static final int ACTIVITY_NEWUSER = 0;
	public static final int ACTIVITY_EDITUSER = 1;
    private static final int ACTIVITY_SELECTIMAGE = 2;
    private static final int ACTIVITY_CAMERA = 3;
	
	RowUserAdapter adapter;
	
	private static final int DIALOG_DELETE = 0;
	private static final int DIALOG_MENU_TWITTER = 1;
	private static final int DIALOG_SAVETIMELINE = 2;
	private static final int DIALOG_MENU_FACEBOOK = 3;
    private static final int DIALOG_SELECT_IMAGE = 4;
	
	private long idUser = -1;
	
	private long idUserAux = -1;
	
	private ListView mListView;
	private TextView mEmpty;
	
	private LinearLayout mLayoutBackgroundApp;
	
	private ThemeManager mThemeManager;
	
	private ActionBar mActionBar;

    private ProgressDialog progressDialog;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_MENU_FACEBOOK:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.actions_users_facebook, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	showDialog(DIALOG_DELETE);
                    }
                }
            })
            .create();
        case DIALOG_MENU_TWITTER:
            return new AlertDialog.Builder(this)
            .setTitle(R.string.actions)
            .setItems(R.array.actions_users, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0) {
                    	//loadUserAndFinish(idUser);
                    	editUser();
                    } else if (which==1) {
                        showDialog(DIALOG_SELECT_IMAGE);
                    } else if (which==2) {
                        refreshAvatar();
                    } else if (which==3) {
                    	showDialog(DIALOG_SAVETIMELINE);
                    } else if (which==4) {
                    	showDialog(DIALOG_DELETE);
                    }
                }
            })
            .create();
        case DIALOG_SAVETIMELINE:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.title_no_save_timeline)
                .setMessage(R.string.text_no_save_timeline)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	Entity ent = new Entity ("users", idUser);
                    	ent.setValue("no_save_timeline", 0);
                    	ent.save();
                    	fillData();
                    }
                })
                .setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	Entity ent = new Entity ("users", idUser);
                    	ent.setValue("no_save_timeline", 1);
                    	ent.setValue("last_timeline_id", 0);
                    	ent.save();
                    	String sqldelete = "DELETE FROM tweets_user WHERE user_tt_id="+idUser + " AND type_id = " + TweetTopicsCore.TIMELINE;
						DataFramework.getInstance().getDB().execSQL(sqldelete);
                    	fillData();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        case DIALOG_DELETE:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.title_question_delete)
                .setMessage(R.string.question_delete)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	deleteUser();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        case DIALOG_SELECT_IMAGE:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.select_action)
                .setItems(R.array.select_type_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                        	File f = new File(getURLNewAvatar());
        	    			if( f.exists() ) f.delete();

                        	Intent intendCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        	intendCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        	intendCapture.putExtra("return-data", true);
                        	startActivityForResult(intendCapture, ACTIVITY_CAMERA);
                        } else if (which==1) {
                        	Intent i = new Intent(Intent.ACTION_PICK) ;
                        	i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        					 MediaStore.Images.Media.CONTENT_TYPE);
                        	startActivityForResult(i, ACTIVITY_SELECTIMAGE);
                        }
                    }
                })
                .create();
        }
        return null;
    }

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

        setActionBarContentView(R.layout.users_list);
        
        mActionBar = this.getGreenDroidActionBar();
        
        mActionBar.setTitle(getString(R.string.user_list));
        
        ImageView mIconActivity = (ImageView) mActionBar.findViewById(R.id.gd_action_bar_home_item);
        
        mIconActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				back();
			}
        	
        });
        
        
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ConnectionManager.getInstance().open(this);
                
        Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
    	if (e!=null) {
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
    		if (mThemeManager.getTheme()==ThemeManager.THEME_DEFAULT) {
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
        mActionBar.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_top_bar")));
    }
	
	
    private void refreshColorsListView() {
    	mListView.setDivider(Utils.createDividerDrawable(this, new ThemeManager(this).getColor("color_divider_tweet")));
    	if (Utils.getPreference(this).getBoolean("prf_use_divider_tweet", true)) {
    		mListView.setDividerHeight(2);
    	} else {
    		mListView.setDividerHeight(0);
    	}
    }
    
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		return super.onHandleActionBarItemClick(item, position);
	}
	
    public void fillData() {
    	try {

    		adapter = new RowUserAdapter(this, DataFramework.getInstance().getEntityList("users"));
    		
    		mListView.setOnItemClickListener(new OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> av, View v, int pos, long id2) {
    				Entity e = (Entity)adapter.getItem(pos);
    				idUser = e.getId();
    				if (e.getString("service").equals("facebook")) {
    					showDialog(DIALOG_MENU_FACEBOOK);
    				} else {
    					showDialog(DIALOG_MENU_TWITTER);
    				}
    			}
            });

    		mListView.setAdapter(adapter);
    		
    		if (mListView.getCount()<=0) {
    			mEmpty.setVisibility(View.VISIBLE);
    		} else {
    			mEmpty.setVisibility(View.GONE);
    		}
	        
	        	        
    	} catch (Exception e) {
    		System.out.println("ERROR: "+e.getMessage());
    	}
    	        
    }
    
    public void editUser() {
    	if (idUser>0) {
    		Intent edit = new Intent(this, EditUserTwitter.class);
    		edit.putExtra(DataFramework.KEY_ID, idUser);
        	startActivityForResult(edit, ACTIVITY_EDITUSER);
    	}
    }
    
    public void refreshAvatar() {
        final ProfileImageAsyncTask task = new ProfileImageAsyncTask(this);

        Params params = task.new Params();
        params.action = ProfileImageAsyncTask.REFRESH_AVATAR;
        params.idUser = idUser;

        task.execute(params);

		progressDialog = new ProgressDialog(this);

		progressDialog.setTitle(R.string.user_list);
		progressDialog.setMessage(this.getResources().getString(R.string.update_avatar_loading));

		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						if (task!=null) task.cancel(true);
					}

				});

		progressDialog.show();

    	/*if (idUser>0) {
			try {
				Entity ent = new Entity("users", idUser);
				User user = ConnectionManager.getInstance().getTwitter().showUser(ent.getInt("user_id"));
				Bitmap avatar = BitmapFactory.decodeStream(new Utils.FlushedInputStream(user.getProfileImageURL().openStream()));
				String file = Utils.getFileAvatar(idUser);
				
				FileOutputStream out = new FileOutputStream(file);
				avatar.compress(Bitmap.CompressFormat.JPEG, 90, out);
		
				avatar.recycle();
				
				fillData();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_correct));
			} catch (NullPointerException e) { 
				e.printStackTrace();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
			} catch (OutOfMemoryError e) { 
				e.printStackTrace();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
			} catch (TwitterException e) {
				e.printStackTrace();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
			} catch (IOException e) {
				e.printStackTrace();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
			} catch (Exception e) {
				e.printStackTrace();
				Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
			}		

    	}*/
    	
    }

    public void updateAvatar() {
        final ProfileImageAsyncTask task = new ProfileImageAsyncTask(this);

        Params params = task.new Params();
        params.action = ProfileImageAsyncTask.CHANGE_AVATAR;
        params.idUser = idUser;

        task.execute(params);

		progressDialog = new ProgressDialog(this);

		progressDialog.setTitle(R.string.user_list);
		progressDialog.setMessage(this.getResources().getString(R.string.change_avatar_loading));

		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						if (task!=null) task.cancel(true);
					}

				});

		progressDialog.show();
    }
    
    public void deleteUser() {
    	if (idUser>0) {
    		Entity ent = new Entity("users", idUser);
    		boolean loaduser = false;
    		if (ent.getInt("active")==1) {
    			loaduser = true;
    			idUserAux = -1;
    		}
    		
    		String sqldelete = "DELETE FROM tweets_user WHERE user_tt_id="+ent.getId();
			DataFramework.getInstance().getDB().execSQL(sqldelete);
			
			ent.delete();
    		
    		if (loaduser) {
    			List<Entity> listUser = DataFramework.getInstance().getEntityList("users");
    			if (listUser.size()>0) {
    				loadUser(((Entity)listUser.get(0)).getId());
    			}
    		}
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
    
    public void loadUser(long id) {
    	ConnectionManager.getInstance().getTwitter(id, true);
    	fillData();
    }
    
    /*
    public void loadUserAndFinish(long id) {
    	app.loadUser(id, true);
    	back();
    }
    */
    /*
    public void newUser() {
    	if (Utils.isLite(this)) {
    		if (DataFramework.getInstance().getEntityList("users").size()<1) {
    			beginAuthorization();        			
    		} else {
    			Utils.showMessage(this, getString(R.string.max_users_lite));
    		}
    	} else {
    		beginAuthorization();
    	}
    }
    */
    
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
    		if (DataFramework.getInstance().getEntityList("users", "service = \"twitter.com\" or service is null").size()<1) {
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
    	if (nUserTwitter<=0) {
    		Utils.showMessage(this, getString(R.string.first_twitter_user));
    	} else {
	    	if (Utils.isLite(this)) {
	    		if (DataFramework.getInstance().getEntityList("users", "service = \"facebook\"").size()<1) {
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
    
    public void inviteFollowTweetTopics() {
    	final Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
    	if (e!=null) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(getString(R.string.follow_tweettopics));
    		builder.setMessage(getString(R.string.follow_tweettopics_msg));
    		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				loadUser(e.getId());
    				try {
    					ConnectionManager.getInstance().getTwitter().createFriendship("tweettopics_app");
					} catch (TwitterException e1) {
						e1.printStackTrace();
					}
    				Utils.showMessage(Users.this, Users.this.getString(R.string.thanks));
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
    /*
    private void beginAuthorization() {
    	if(!Utils.isOnline(this)) {
    		Utils.showMessage(this, "No internet connection available!");
    	} else {	

    		CharSequence[] networks = new CharSequence[2];
    		networks[0] = this.getString(R.string.twitter_network);
    		networks[1] = this.getString(R.string.facebook_network);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.select_network);
    		builder.setItems(networks, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int itemIndex) {
    		    	startAuthorization(itemIndex);
    		    }
    		});
    		
    		AlertDialog alert = builder.create();
    		alert.show();
    	}
    }
    */
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
    /*
    private void beginAuthorization1() {
    	Intent intent = new Intent(this, AuthorizationActivity.class);
    	startActivityForResult(intent, ACTIVITY_NEWUSER);
    }
    
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        switch (requestCode){
	    	case ACTIVITY_NEWUSER:
	    		fillData();
    			inviteFollowTweetTopics();
	    	break;
	    	case ACTIVITY_EDITUSER:
	    		fillData();
            case ACTIVITY_CAMERA:
                if( resultCode != 0 ) {
                    updateAvatar();
                }
                break ;
            case ACTIVITY_SELECTIMAGE:
                if( resultCode != 0 ) {
                    Cursor cursor = managedQuery(intent.getData(),null,null,null,null);
                    if (cursor!=null) {
                        if( cursor.moveToFirst() ) {
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
    /*
	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		
		fillData();
		
		Uri uri = intent.getData();
		if(uri != null) {
			try {
				ConnectionManager.getInstance().finalizeOAuthentication(uri);
							    
				inviteFollowTweetTopics();
		        
			} catch (TwitterException e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
        //ConnectionManager.destroyInstance();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Utils.saveStatusWorkApp(this, true);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveStatusWorkApp(this, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("user_id", idUser);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void back() {
    	boolean changed = true; 
    	Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
    	if (e!=null) {
    		if (idUserAux==e.getId()) {
    			changed = false; 
    		}
    	}
    	setResult(changed?RESULT_OK:RESULT_CANCELED);
    	finish();
    }

    @Override
    public void profileImageLoading() {}

    @Override
    public void profileImageLoadCancelled() {}

    @Override
    public void profileImageLoaded(ProfileImageAsyncTask.Result result) {
        progressDialog.cancel();
        if (result.ok) {
            fillData();
            if (result.action == ProfileImageAsyncTask.CHANGE_AVATAR)
                Utils.showMessage(this, this.getString(R.string.change_avatar_correct));
            else
                Utils.showMessage(this, this.getString(R.string.refresh_avatar_correct));
        } else {
            if (result.action == ProfileImageAsyncTask.CHANGE_AVATAR)
                Utils.showMessage(this, this.getString(R.string.change_avatar_no_correct));
            else
                Utils.showMessage(this, this.getString(R.string.refresh_avatar_no_correct));
        }
    }
}
