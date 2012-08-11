package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.TweetListDraftAdapter;
import com.javielinux.adapters.TweetLongerAdapter;
import com.javielinux.components.AutoCompleteHashTagListItem;
import com.javielinux.components.AutoCompleteListItem;
import com.javielinux.infos.InfoUsers;
import com.javielinux.utils.DialogUtils.BuyProDialogBuilder;
import com.javielinux.utils.*;
import preferences.NewEditTweetProgrammed;
import preferences.Preferences;
import preferences.TweetDraft;
import task.LoadUserAsyncTask;
import tweetprogrammed.OnAlarmReceiverTweetProgrammed;
import updatestatus.ServiceUpdateStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class NewStatusActivity extends BaseActivity {
	
	private static final int MAX_RESULTS = 5;
	
	private LinearLayout mButtonsFoot;
	private LinearLayout mAutoCompleteDataFoot;
	private HorizontalScrollView mAutoCompleteFoot;
	
	private long mIdDeleteDraft = 0;
	
	private List<InfoUsers> mResultInfoUsers = new ArrayList<InfoUsers>();
    private List<String> mResultInfoHashTags = new ArrayList<String>();
	
	public static int CHARS_YFROG = 8;
	public static int CHARS_TWITPIC = 6;
	public static int CHARS_LOCKERZ = 9;
	
	public static int MODE_TL_NONE = -1;
	public static int MODE_TL_TWITLONGER = 0;
	public static int MODE_TL_N_TWEETS = 1;
	
	public static String URL_BASE_YFROG = "http://yfrog.com/";
	public static String URL_BASE_TWITPIC = "http://twitpic.com/";
	public static String URL_BASE_LOCKERZ = "http://lockerz.com/p/";
			
	private static final int DIALOG_SELECT_IMAGE = 0;
	private static final int DIALOG_NO_GEO = 1;
	
    private static final int ACTIVITY_SELECTIMAGE = 0;
    private static final int ACTIVITY_CAMERA = 1;
    public static final int ACTIVITY_USER = 2;

	private int mModeTweetLonger = MODE_TL_NONE;

	private static final int TAKEPHOTO_ID = Menu.FIRST;
	private static final int DEFAULTTEXT_ID = Menu.FIRST+1;
	private static final int NEW_DRAFT_ID = Menu.FIRST + 2;
	private static final int VIEW_DRAFT_ID = Menu.FIRST + 3;
	protected static final int SIZE_TEXT_ID = Menu.FIRST+4;
	
	protected ProgressDialog progressDialog;
	
	public static int TYPE_NORMAL = 0;
	public static int TYPE_REPLY = 1;
	public static int TYPE_RETWEET = 2;
	public static int TYPE_DIRECT_MESSAGE = 3;
	public static int TYPE_REPLY_ON_COPY = 4;
	
	private String mTextStatus = "";
	private EditText mText;
	

	private TextView mTxtType;
	
	private LinearLayout mDataUsers;
	
	private TextView mCounter;
	
	private TextView mRefUserName;
	private TextView mRefText;
	private ImageView mRefAvatar;
	
	private LinearLayout mLayoutMsg;
	
	private LinearLayout mReftweetLayout;
	private LinearLayout mLayoutImages;
	
	private Button mBtPro;
	
	private Button mSend;
	private ImageButton mGeo;
	private ImageButton mTimer;
	private ImageButton mShorter;
	
	private String mDMUsername;
	
	private ArrayList<UserStatus> mUsers = new ArrayList<UserStatus>();
	
	private long mReplyTweetId;
	private String mReplyScreenName;
	private String mReplyText;
	private String mReplyURLAvatar;
	private int mType = 0; // 0 normal - 1 - Reply - 2 - Retweet
	private String retweetPrev = "";
	
	private int mStartAutoComplete = -1;
	private int mEndAutoComplete = -1;
	private String mAuxText = "";
	
	private ThemeManager mThemeManager;
	
	private ArrayList<String> mImages = new ArrayList<String>();
	
	private LinearLayout mLayoutBackgroundApp;
	
	private static NewStatusActivity thisInstance;
	
	private int mShortURLLength = 19;
	private int mShortURLLengthHttps = 20;

		
    public void refreshTheme() {
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
    		mLayoutBackgroundApp.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_background_new_status")));
    		/*
    		if (mThemeManager.getTheme()==ThemeManager.THEME_DEFAULT) {
    			bmp = new BitmapDrawable(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_user));
    			//mLayoutBackgroundApp.setBackgroundResource(R.drawable.background_user);
    		} else {
    			bmp = new BitmapDrawable(BitmapFactory.decodeResource(this.getResources(), R.drawable.background_user_dark));
    			//mLayoutBackgroundApp.setBackgroundResource(R.drawable.background_user_dark);
    		}*/
    	}
    	
		
    	mThemeManager.setColors();
    	mButtonsFoot.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_bottom_bar")));
    	
    	//mTxtUsername.setBackgroundColor(Color.parseColor("#99"+(mThemeManager.getTheme()==1?"FFFFFF":"000000")));
    	mTxtType.setBackgroundColor(Color.parseColor("#99"+(mThemeManager.getTheme()==1?"FFFFFF":"000000")));
    	
    	if (PreferenceUtils.getGeo(this))
    		mGeo.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_SELECTED));
    	else
    		mGeo.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_NORMAL));
    	
    	mShorter.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.gd_action_bar_shorter, ThemeManager.TYPE_NORMAL));
    }
	
    private void deleteImages() {
    	for (int i=0; i<mImages.size(); i++) {			
			String image = mImages.get(i);
			File file = new File(Utils.appUploadImageDirectory+image);
			if (file.exists()) file.delete();
    	}
    }
    
    private void setModeTweetLonger(int mode) {
    	mModeTweetLonger = mode;
    }
    /*
	private void prepareQuickActions() {
		mQuicActionModeTweetLonger = new QuickActionBar(this);
		mQuicActionModeTweetLonger.addQuickAction(new TweetTopicsQuickAction(this, R.drawable.gd_action_bar_twitlonger, R.string.twitlonger));
		mQuicActionModeTweetLonger.addQuickAction(new TweetTopicsQuickAction(this, R.drawable.gd_action_bar_n_tweets, R.string.n_tweets));
		mQuicActionModeTweetLonger.setOnQuickActionClickListener(new OnQuickActionClickListener() {
            public void onQuickActionClicked(QuickActionWidget widget, int position) {
            	if (position==0) {
            		setModeTweetLonger(MODE_TL_TWITLONGER);
            	} else {
            		setModeTweetLonger(MODE_TL_N_TWEETS);
            	}

            }
        });
	}
	*/
	private void createThumbs() {
		mLayoutImages.removeAllViews();
		for (int i=0; i<mImages.size(); i++) {
			try {
				String image = mImages.get(i);
				/*
				Matrix matrix = null;
	
				try {
					ExifInterface exif = new ExifInterface(Utils.appUploadImageDirectory+image);
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
					matrix = new Matrix();
			        if (orientation==3) {
			        	matrix.postRotate(180);
			        } else if (orientation==6) {
			        	matrix.postRotate(90);
			        } else if (orientation==8) {
			        	matrix.postRotate(270);
			        }
				} catch (IOException e) {
					e.printStackTrace();
				}
		        */
				
				Bitmap bmp = Utils.getBitmapFromFile(Utils.appUploadImageDirectory+image, Utils.HEIGHT_THUMB_NEWSTATUS, true);
				//if (matrix!=null) bmp = Bitmap.createBitmap(bmp, 0, 0, Utils.HEIGHT_THUMB_NEWSTATUS, Utils.HEIGHT_THUMB_NEWSTATUS, matrix, true);
				
				ImageView aux = new ImageView(this);
				aux.setImageBitmap(bmp);
	
				aux.setPadding(3, 0, 3, 3);
				aux.setTag(i);
				aux.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						//int i = Integer.parseInt(v.getTag().toString());
						//if (i<mURLImages.size()) addURLImageInEditText( mURLImages.get(i) );
					}
		    		
		    	});
		    	    	
		    	mLayoutImages.addView(aux);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		

		
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SELECT_IMAGE:        	
            return new AlertDialog.Builder(this)
                .setTitle(R.string.select_action)
                .setItems(R.array.select_type_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                        	File f = new File(getURLCurrentImage());
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
        case DIALOG_NO_GEO:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.title_no_geo)
                .setMessage(R.string.text_no_geo)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	send();
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
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
             
        thisInstance = this;
        
        long userStart = -1;
        
        mImages = new ArrayList<String>();
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey("start_user_id")) userStart = Long.parseLong(savedInstanceState.getString("start_user_id"));
        	if (savedInstanceState.containsKey("text")) mTextStatus = savedInstanceState.getString("text");
        	if (savedInstanceState.containsKey("type")) mType = savedInstanceState.getInt("type");
        	if (savedInstanceState.containsKey("reply_tweetid")) mReplyTweetId = savedInstanceState.getLong("reply_tweetid");
        	if (savedInstanceState.containsKey("reply_avatar")) mReplyURLAvatar = savedInstanceState.getString("reply_avatar");
        	if (savedInstanceState.containsKey("reply_screenname")) mReplyScreenName = savedInstanceState.getString("reply_screenname");
        	if (savedInstanceState.containsKey("reply_text")) mReplyText = savedInstanceState.getString("reply_text");
        	if (savedInstanceState.containsKey("username_direct_message")) mDMUsername = savedInstanceState.getString("username_direct_message");
        	if (savedInstanceState.containsKey("retweet_prev")) {
        		if (savedInstanceState.getString("retweet_prev").length()>0) retweetPrev = savedInstanceState.getString("retweet_prev") + " ";        	
        	}
        	        	
        	if (savedInstanceState.containsKey("ar_images")) mImages = savedInstanceState.getStringArrayList("ar_images");

       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey("start_user_id")) userStart = Long.parseLong(extras.getString("start_user_id"));
       			if (extras.containsKey("text")) mTextStatus = extras.getString("text");
       			if (extras.containsKey("type")) mType = extras.getInt("type");
       			if (extras.containsKey("reply_tweetid")) mReplyTweetId = extras.getLong("reply_tweetid");
       			if (extras.containsKey("reply_avatar")) mReplyURLAvatar = extras.getString("reply_avatar");
       			if (extras.containsKey("reply_screenname")) mReplyScreenName = extras.getString("reply_screenname");
       			if (extras.containsKey("reply_text")) mReplyText = extras.getString("reply_text");
       			if (extras.containsKey("username_direct_message")) mDMUsername = extras.getString("username_direct_message");
       			if (extras.containsKey("retweet_prev")) {
       				if (extras.getString("retweet_prev").length()>0) retweetPrev = extras.getString("retweet_prev") + " ";
       			}
       			if (extras.containsKey("ar_images")) mImages = extras.getStringArrayList("ar_images");

       		}
       	}
        
        Utils.setActivity(this);
        
        Utils.saveApiConfiguration(this);
        
        mShortURLLength = PreferenceUtils.getShortURLLength(this);
        mShortURLLengthHttps = PreferenceUtils.getShortURLLengthHttps(this);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        
        String fileFromOtherApp = "";
        
        if (mImages.size()<=0) {
        
	        if (Intent.ACTION_SEND.equals(action)) {
	        	if (extras.containsKey(Intent.EXTRA_STREAM)) {
	        		try {
	        			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
	        			Cursor c = managedQuery(uri, null, "", null, null);
	        			if (c.getCount() > 0) {
	        				c.moveToFirst();
	        				int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	        				fileFromOtherApp = c.getString(dataIndex);
	        			}
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	
	        	}
	        	
	        	if ("text/plain".equals(intent.getType())) {
	        		mTextStatus = intent.getStringExtra(Intent.EXTRA_TEXT);
	        	}
	        }
	        
        }
        
        mThemeManager = new ThemeManager(this);
        mThemeManager.setTheme();
        
        setContentView(R.layout.new_status);


        mLayoutBackgroundApp = (LinearLayout) findViewById(R.id.layout_background_app);

        
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (mUsers.size()==0) {
        	loadUsers(userStart);
        }
        
        mLayoutMsg = (LinearLayout) findViewById(R.id.ll_msg);
        
        mBtPro = (Button) this.findViewById(R.id.bt_pro);
        mBtPro.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				try {
					AlertDialog builder = BuyProDialogBuilder.create(NewStatusActivity.this);
					builder.show();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				/*
				Uri uri = Uri.parse("market://search?q=pname:" + Utils.packageNamePRO);
	            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
	            NewStatusActivity.this.startActivity(buyProIntent);*/
			}
		});

        mButtonsFoot = (LinearLayout) findViewById(R.id.buttons_foot);
        mAutoCompleteDataFoot = (LinearLayout) findViewById(R.id.autocomplete_data_foot);
        mAutoCompleteFoot = (HorizontalScrollView) findViewById(R.id.autocomplete_foot);
        
        mDataUsers = (LinearLayout) this.findViewById(R.id.users_data);
        
    	mRefUserName = (TextView) this.findViewById(R.id.tweet_user_name_text);
    	mRefText = (TextView) this.findViewById(R.id.tweet_text);
    	mRefAvatar = (ImageView) this.findViewById(R.id.user_avatar);
        
    	mLayoutImages = (LinearLayout) this.findViewById(R.id.images);
    	
    	refreshUsers();

    	mReftweetLayout = (LinearLayout) this.findViewById(R.id.reftweet_layout);
    	
		mText = (EditText) findViewById(R.id.text);
		
		mText.setTextSize(PreferenceUtils.getSizeTextNewStatus(this));

		mText.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged (Editable s) {
					countChars();
				}
				public void beforeTextChanged (CharSequence s, int start, int count, int after) {
					//Log.d(Utils.TAG, "beforeTextChanged: " + start + " -- after: " + after+ " -- count: " + count+ " -- s: " + s.toString());
				}
				public void onTextChanged (CharSequence s, int start, int before, int count) {
					//Log.d(Utils.TAG, "onTextChanged: " + start + " -- before: " + before+ " -- count: " + count+ " -- s: " + s.toString());
					
					mAuxText = thisInstance.mText.getText().toString();
					boolean isUser = false;
                    boolean isHashTag = false;
                    boolean isDM = false;
					mStartAutoComplete = 0;
					mEndAutoComplete = thisInstance.mText.getSelectionStart();
					if (mAuxText.length()>0) {
						int pos = mEndAutoComplete;
						while (pos>0 && !mAuxText.substring(pos-1, pos).equals(" ")) {
                            if (mAuxText.substring(pos-1, pos).equals("@")) {
                                if (mAuxText.length()>=3 && mAuxText.substring(0, 3).toLowerCase().equals("d @")) {
                                    isDM = true;
                                }
								isUser = true;
								mStartAutoComplete = pos;
								pos = 0;
                            } else if (mAuxText.substring(pos-1, pos).equals("#")) {
								isHashTag = true;
								mStartAutoComplete = pos;
								pos = 0;
                            } else {
								pos--;
							}
						}
                        if (pos>0 && mAuxText.substring(pos-1, pos).equals(" ") && mAuxText.length()>3 && mAuxText.substring(0, 3).toLowerCase().equals("d @")) {
                            onItemClickDMComplete(mAuxText.substring(3), true);
                        }
					}

					if (isUser && mAuxText.substring(mStartAutoComplete, mEndAutoComplete).length()>0) {
						showUsers(mAuxText.substring(mStartAutoComplete, mEndAutoComplete), isDM);
                    } else if (isHashTag && mAuxText.substring(mStartAutoComplete, mEndAutoComplete).length()>0) {
						showHashTags(mAuxText.substring(mStartAutoComplete, mEndAutoComplete));
					} else {
						showFootButtons();
					}
					
					/*
					if (mStartAutoComplete>=0 && start>mStartAutoComplete) {
						mAuxText = mText.getText().toString();					
						mEndAutoComplete = start+count+1;
						if (mAuxText.length()<mEndAutoComplete) mEndAutoComplete--;
						String text = mAuxText.substring(mStartAutoComplete+1, mEndAutoComplete);
						showUsers(text);
					}
					if (s.toString().substring(start, start+count).equals("@")) {
						mStartAutoComplete = start;
					}
					if (s.toString().substring(start, start+count).equals(" ")) {
						mStartAutoComplete = -1;
						showFootButtons();
					}
					*/
				}
			}
		);
		
		mTxtType = (TextView) findViewById(R.id.txt_type);
				
		mSend = (Button) findViewById(R.id.bt_send);
		mSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// comprobar si tenemos geoposicion
				
				if (PreferenceUtils.getGeo(NewStatusActivity.this)) {
					Location loc = LocationUtils.getLastLocation(NewStatusActivity.this);
					if (loc == null) {
						showDialog(DIALOG_NO_GEO);
					} else {
						send();
					}
				} else {
					send();
				}					

			}
			
		});
		
		mShorter = (ImageButton) findViewById(R.id.bt_shorter);
		mShorter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String text = mText.getText().toString();
				int count = LinksUtils.pullLinksHTTP(text).size() - mImages.size();
				if (count>0) {
					mText.setText(LinksUtils.shortLinks(text, mImages));
					Utils.showShortMessage(NewStatusActivity.this, count + " " + NewStatusActivity.this.getString(R.string.txt_shorter_n));
				} else {
					Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_shorter_0));
				}

			}
			
		});
		
		mGeo = (ImageButton) findViewById(R.id.bt_geo);
		mGeo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PreferenceUtils.getGeo(NewStatusActivity.this)) {
					mGeo.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_NORMAL));
					Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_geoloc_off));
                    PreferenceUtils.setGeo(NewStatusActivity.this, false);
				} else {
					mGeo.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.gd_action_bar_geo, ThemeManager.TYPE_SELECTED));
                    PreferenceUtils.setGeo(NewStatusActivity.this, true);
					Utils.showShortMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.txt_geoloc_on));
				}
			}
			
		});
		
		mTimer = (ImageButton) findViewById(R.id.bt_timer);
		mTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialogProgrammedTweet();
			}
			
		});
		
		mCounter = (TextView) this.findViewById(R.id.bt_counter);

		refreshTheme();
		
		createThumbs();
		
		populateFields();
		
		if (!fileFromOtherApp.equals("")) {
			copyImage(fileFromOtherApp);
		}

        if (mType==TYPE_DIRECT_MESSAGE) onItemClickDMComplete(mDMUsername, false);

    }
    
    private void addUserInLayout(UserStatus user) {
		View v = View.inflate(this, R.layout.users_item_new_status, null);
		/*
		LinearLayout bg = (LinearLayout)v.findViewById(R.id.ll_bg);
		bg.setBackgroundResource(user.checked?R.drawable.button_on_background:R.drawable.button_off_background);
*/
		ImageView on = (ImageView)v.findViewById(R.id.bg_on);
		ImageView off = (ImageView)v.findViewById(R.id.bg_off);
		
		if (user.checked) {
			on.setVisibility(View.VISIBLE);
			off.setVisibility(View.GONE);
		} else {
			on.setVisibility(View.GONE);
			off.setVisibility(View.VISIBLE);
		}
		
		ImageView img = (ImageView)v.findViewById(R.id.icon);
        try {
        	if (user.avatarON!=null) {
        		img.setImageBitmap(user.checked?user.avatarON:user.avatarOFF);
        	} else {
        		img.setImageResource(R.drawable.avatar);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	img.setImageResource(R.drawable.avatar);
        }
        
        ImageView tag_network = (ImageView)v.findViewById(R.id.tag_network);
        
        if (user.service.equals("facebook")) {
        	tag_network.setImageResource(R.drawable.icon_facebook);
        } else {
        	tag_network.setImageResource(R.drawable.icon_twitter);
        }
        
        TextView username = (TextView) v.findViewById(R.id.username);
        username.setText(user.username);
        
        v.setTag(user.id);
        
        v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setChecked(Long.parseLong(v.getTag().toString()));
			}
        });

        mDataUsers.addView(v);
    }
    
    private void addInviteFacebookInLayout() {
		View v = View.inflate(this, R.layout.users_item_new_status, null);
		
		ImageView on = (ImageView)v.findViewById(R.id.bg_on);
		ImageView off = (ImageView)v.findViewById(R.id.bg_off);
		
		on.setVisibility(View.GONE);
		off.setVisibility(View.VISIBLE);
		
		ImageView img = (ImageView)v.findViewById(R.id.icon);
        try {
       		img.setImageResource(R.drawable.icon_facebook_large);
        } catch (Exception e) {
        	e.printStackTrace();
        	img.setImageResource(R.drawable.avatar);
        }
        
        ImageView tag_network = (ImageView)v.findViewById(R.id.tag_network);

        tag_network.setImageBitmap(null);
        
        TextView username = (TextView) v.findViewById(R.id.username);
        username.setText(R.string.add);

        /*
        v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newuser = new Intent(NewStatusActivity.this, Users.class);
				NewStatusActivity.this.startActivityForResult(newuser, ACTIVITY_USER);
			}
        });
        */
        mDataUsers.addView(v);
    }
    
    private void loadUsers(long userStart) {
    	mUsers.clear();
    	List<Entity> ents = DataFramework.getInstance().getEntityList("users");

    	for (Entity ent : ents) {
    		UserStatus user = new UserStatus();
    		user.id = ent.getId();
    		String n = ent.getString("name");
    		if (n.length()>11) n = n.substring(0, 9) + "...";
    		user.username = n;
    		user.service = ent.getString("service");
    		if (userStart<0) {
    			user.checked = (ent.getInt("active")==1);
    		} else {
    			user.checked = (ent.getId()==userStart);
    		}
    		Bitmap bmp = Utils.getBitmapAvatar(ent.getId(), Utils.AVATAR_LARGE);
    		if (bmp!=null) {
    			user.avatarON = bmp;
    			user.avatarOFF = Utils.toGrayscale(bmp);
    		}
    		
    		mUsers.add(user);
    	}
    }
    
    private void refreshUsers() {
    	
    	mDataUsers.removeAllViews();
    	
    	if (Utils.isLite(this)) {
    		mLayoutMsg.setVisibility(View.VISIBLE);
    		addUserInLayout(mUsers.get(0));
    		if (mUsers.size()>1) {
    			addUserInLayout(mUsers.get(1));	
    		} else {
    			addInviteFacebookInLayout();
    		}
    	} else {
    		mLayoutMsg.setVisibility(View.GONE);
    		for (UserStatus user : mUsers) {
        		addUserInLayout(user);
        	}
    	}   	
    	
    }
    
    private void setChecked(long id) {
    	boolean todo = true;
    	int countSelected = 0;
    	for (UserStatus user : mUsers) {
    		if (user.checked) {
    			countSelected++;
    		}
    	}
    	if (countSelected==1) {
    		for (UserStatus user : mUsers) {
    			if (user.checked && user.id == id) {
    				todo = false;
    			}
    		}
    	}
    	if (todo) {
	    	for (UserStatus user : mUsers) {
	    		if (user.id == id) user.checked = !user.checked;
	    	}
	    	refreshUsers();
    	} else {
    		Utils.showMessage(this, R.string.one_user_selected);
    	}
    }
    
    public boolean copyImage(String image) {
    	int type = Integer.parseInt(Utils.getPreference(this).getString("prf_service_image", "1"));
		int chars = 0;
		if (type==1) {
			chars = CHARS_YFROG;
		} else if (type==2) {
			chars = CHARS_TWITPIC;
		} else if (type==3) {
			chars = CHARS_LOCKERZ;
		}
		
		String ext = "";
		
		StringTokenizer tokens = new StringTokenizer(image, ".");  
    	
    	while(tokens.hasMoreTokens()) {  
    		ext = tokens.nextToken();
    	}
		
    	String name = System.currentTimeMillis()+"";
    	
    	if (name.length()>chars) {
    		name = name.substring(name.length()-chars, name.length());
    	} else if (name.length()<chars) {
    		String fill = "";
    		for (int i=0; i<chars-name.length();i++) {
    			fill +="0";
    		}
    		name = fill+name;
    	} 
    	
    	String file = name + "." + ext;
    	
    	try {
    		Log.d(Utils.TAG, "Copiar " + image + " a " + Utils.appUploadImageDirectory + file);
			FileUtils.copy(image, Utils.appUploadImageDirectory + file);
            Utils.savePhotoInScale(this, Utils.appUploadImageDirectory + file);
			mImages.add(file);
			createThumbs();
			addTextInEditText(getURLBase()+name);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

    }
    
    public String getURLCurrentImage() {
		return Utils.appDirectory+"aux_upload_" + mImages.size() + ".jpg";
	}
    
    private void send() {

        if (Utils.getLenghtTweet(mText.getText().toString(), mShortURLLength, mShortURLLengthHttps)>140
               && mModeTweetLonger == MODE_TL_NONE) {
           showDialogTweetLonger();
           return;
        }

		String photos = "";
		for (String p : mImages) {
			photos += p + "--";
		}
	
    	DataFramework.getInstance().emptyTable("send_tweets");
    	
    	boolean onlyFacebook = true;
    	
    	String users = "";
    	for (UserStatus user : mUsers) {
    		if (user.checked) {
    			users += user.id + ",";
    			if (!user.service.equals("facebook")) onlyFacebook = false;
    		}
    	}
    	
    	if (onlyFacebook && mImages.size()>0) {
    		Utils.showMessage(this, R.string.no_facebook_images);
    	} else {
    	
	    	Entity ent = new Entity("send_tweets");
	    	ent.setValue("users", users);
	    	ent.setValue("text", mText.getText());
	    	ent.setValue("is_sent", 0);
	    	ent.setValue("type_id", (mType==TYPE_DIRECT_MESSAGE)?2:1);
	    	ent.setValue("username_direct", mDMUsername);
	    	ent.setValue("photos", photos);
	    	ent.setValue("mode_tweetlonger", mModeTweetLonger);
	    	if (mIdDeleteDraft>0) ent.setValue("tweet_draft_id", mIdDeleteDraft);
	    	if (mType== NewStatusActivity.TYPE_REPLY || mType== NewStatusActivity.TYPE_REPLY_ON_COPY) {
	    		ent.setValue("reply_tweet_id",  Utils.fillZeros("" + mReplyTweetId));
	    	} else {
	    		ent.setValue("reply_tweet_id",  "-1");
	    	}
	    	
	    	ent.setValue("use_geo", PreferenceUtils.getGeo(this)?"1":"0");
	    	ent.save();
	    	
	    	startService(new Intent(this, ServiceUpdateStatus.class));
	    	
	    	finish();

    	}
		
    }

    private void showUsers(String user, final boolean isDM) {
    	List<String> names = new ArrayList<String>();
		
		try {
			List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "username like '" + user + "%'", "username asc");
			
			Log.d(Utils.TAG, "Searching by " + user + " con " + ents.size() + " resultados");
						
			int count = 0;
			
			mAutoCompleteDataFoot.removeAllViews();
			mResultInfoUsers.clear();
			
			for (int i=0; i<ents.size()&&count<MAX_RESULTS; i++) {
				if (!names.contains(ents.get(i).getString("username"))) {
					InfoUsers iu = new InfoUsers();
					iu.setName(ents.get(i).getString("username"));
					iu.setUrlAvatar(ents.get(i).getString("url_avatar"));
					names.add(ents.get(i).getString("username"));
					mResultInfoUsers.add(iu);
					AutoCompleteListItem v = (AutoCompleteListItem) View.inflate(this, R.layout.row_autocomplete_user, null);
					v.setRow(iu, user);
					v.setTag(count);
					v.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
                            if (isDM) {
                                onItemClickDMComplete(Integer.parseInt(v.getTag().toString()));
                            } else {
							    onItemClickAutoComplete(Integer.parseInt(v.getTag().toString()));
                            }
						}
					});
					mAutoCompleteDataFoot.addView(v);
					count++;
				}
			}
			
			if (count>0) {
				showFootAutoComplete();
			} else {
				showFootButtons();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void showHashTags(String ht) {
    	List<String> hashtags = new ArrayList<String>();

		try {
			List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "text like '%#%'", "");

			Log.d(Utils.TAG, "Searching hashtag by " + ht + " en " + ents.size() + " resultados");

			int count = 0;

			mAutoCompleteDataFoot.removeAllViews();
			mResultInfoHashTags.clear();

			for (int i=0; i<ents.size()&&count<MAX_RESULTS; i++) {
                ArrayList<String> hashs = LinksUtils.pullLinksHashTags(ents.get(i).getString("text"));
                for (String h : hashs) {
                    h = h.replace("#", "");
                    if (!hashtags.contains(h) && h.startsWith(ht)) {
                        hashtags.add(h);
                        mResultInfoHashTags.add(h);
                        AutoCompleteHashTagListItem v = (AutoCompleteHashTagListItem) View.inflate(this, R.layout.row_autocomplete_hashtag, null);
                        v.setRow(h, ht);
                        v.setTag(count);
                        v.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickHashTagAutoComplete(Integer.parseInt(v.getTag().toString()));
                            }
                        });
                        mAutoCompleteDataFoot.addView(v);
                        count++;
                    }
                }
			}

			if (count>0) {
				showFootAutoComplete();
			} else {
				showFootButtons();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void onItemClickDMComplete(int position) {
        //Log.d(Utils.TAG, "Texto: " + mAuxText + " " + mStartAutoComplete + " - " + mEndAutoComplete + " tam: " + mAuxText.length());
        onItemClickDMComplete(mResultInfoUsers.get(position).getName(), true);
    }

    private void onItemClickDMComplete(String user, final boolean fromAutocomplete) {

        mText.setText("");
        mStartAutoComplete = -1;
        showFootButtons();
        
        progressDialog = ProgressDialog.show(
                this,
                getResources().getString(R.string.verify_dm_title),
                getResources().getString(R.string.verify_dm_msg)
        );
        
        new LoadUserAsyncTask(this, new LoadUserAsyncTask.LoadUserAsyncAsyncTaskResponder() {

            @Override
            public void userLoading() {
            }

            @Override
            public void userCancelled() {
            }

            @Override
            public void userLoaded(InfoUsers iu) {
                progressDialog.dismiss();
                if (iu!=null) {
                    // TODO Mirar esto bien
                    if (iu.isFollower("")) {
                        Utils.showMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.verify_dm_yes, iu.getName()));
                        if (fromAutocomplete) {
                            mDMUsername = iu.getName();
                            mType = TYPE_DIRECT_MESSAGE;
                            populateFields();
                        }
                    } else {
                        Utils.showMessage(NewStatusActivity.this, NewStatusActivity.this.getString(R.string.no_is_follower));
                        if (!fromAutocomplete) {
                           finish();
                        }
                    }
                }
            }
        }).execute(user);
        
    }

    private void onItemClickAutoComplete(int position) {
		//Log.d(Utils.TAG, "Texto: " + mAuxText + " " + mStartAutoComplete + " - " + mEndAutoComplete + " tam: " + mAuxText.length());
		String out = mAuxText.substring(0, mStartAutoComplete);
		out += mResultInfoUsers.get(position).getName();
		int pos = out.length();
		if (mEndAutoComplete<mAuxText.length()) out += mAuxText.substring(mEndAutoComplete, mAuxText.length());
		mText.setText(out);
		mText.setSelection(pos);
		mStartAutoComplete = -1;
		showFootButtons();
	}

    private void onItemClickHashTagAutoComplete(int position) {
		//Log.d(Utils.TAG, "Texto: " + mAuxText + " " + mStartAutoComplete + " - " + mEndAutoComplete + " tam: " + mAuxText.length());
		String out = mAuxText.substring(0, mStartAutoComplete);
        out += mResultInfoHashTags.get(position);
		int pos = out.length();
		if (mEndAutoComplete<mAuxText.length()) out += mAuxText.substring(mEndAutoComplete, mAuxText.length());
		mText.setText(out);
		mText.setSelection(pos);
		mStartAutoComplete = -1;
		showFootButtons();
	}
    
    private void showFootButtons() {
    	mButtonsFoot.setVisibility(View.VISIBLE);
    	mAutoCompleteFoot.setVisibility(View.GONE);
    }
    private void showFootAutoComplete() {
    	mButtonsFoot.setVisibility(View.GONE);
    	mAutoCompleteFoot.setVisibility(View.VISIBLE);
    }
    
	private void populateFields() {
		if (mType==TYPE_NORMAL) {
			String def = PreferenceUtils.getDefaultTextInTweet(this);
			if (def.length()>0) {
				mText.setText(def+" "+mTextStatus);	
			} else {
				mText.setText(mTextStatus);	
			}
			mTxtType.setVisibility(View.GONE);
		} else if (mType==TYPE_REPLY) {
			mTxtType.setText(getString(R.string.txt_type_reply));
			mText.setText("@"+mReplyScreenName + " " + mTextStatus);
		} else if (mType==TYPE_REPLY_ON_COPY) {
			mTxtType.setText(getString(R.string.txt_type_reply));
			mText.setText("@"+mReplyScreenName + " " + mTextStatus);
			mText.setSelection(mReplyScreenName.length()+2);
		} else if (mType==TYPE_RETWEET) {
			mTxtType.setText(getString(R.string.txt_type_retweet));
			if (Utils.preference.getBoolean("prf_retweet_via", false)) {
				mText.setText(retweetPrev + mTextStatus + " (via @"+mReplyScreenName+")");
			} else {
				mText.setText(retweetPrev + "RT: @"+mReplyScreenName+": "+mTextStatus);
			}
		} else if (mType==TYPE_DIRECT_MESSAGE) {
            mTxtType.setVisibility(View.VISIBLE);
			mTxtType.setText(getString(R.string.txt_type_dm) + " " + mDMUsername);
		}
		
		if ( (mType==TYPE_REPLY) || (mType==TYPE_RETWEET) || (mType==TYPE_REPLY_ON_COPY) ) {
			mReftweetLayout.setVisibility(View.VISIBLE);
			//Log.d(Utils.TAG, "dentro: " + mReplyScreenName);
			mRefUserName.setText(mReplyScreenName);
			mRefText.setText(Html.fromHtml(Utils.toHTML(this, mReplyText)));
			try {
				File file = Utils.getFileForSaveURL(this, mReplyURLAvatar);
				Bitmap bmp = null;
				if (!file.exists()) {
                    bmp = Utils.saveAvatar(mReplyURLAvatar, file);
					/*URL url = new URL(mReplyURLAvatar);
					bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
					FileOutputStream out = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.PNG, 90, out);      */
				} else {
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
					/*if (bmp==null) { // lo intentamos de nuevo
						file.delete();
						URL url = new URL(mReplyURLAvatar);
						bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
						FileOutputStream out = new FileOutputStream(file);
						bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
					}*/
				}
				mRefAvatar.setImageBitmap(bmp);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				mRefAvatar.setImageResource(R.drawable.avatar);
				Log.d(Utils.TAG, "Could not load image.", e);
			} catch (Exception e) {
				e.printStackTrace();
				mRefAvatar.setImageResource(R.drawable.avatar);
				Log.d(Utils.TAG, "Could not load image.", e);
			}
		} else {
			mReftweetLayout.setVisibility(View.GONE);
		}
		
		mText.setSelection(mText.getText().toString().length());
		countChars();		
	}
    
	private void countChars() {
		int length = Utils.getLenghtTweet(mText.getText().toString(), mShortURLLength, mShortURLLengthHttps);
		
		int number = 140-length;
		//mCounter.setImageBitmap(Utils.getBitmapNumber(this, number, (number<0)?Color.RED:Color.GREEN, Utils.TYPE_BUBBLE, 15));
		mCounter.setText(number+"");
		
		if (number<0) {
			mCounter.setTextColor(Color.RED);
		} else {
			mCounter.setTextColor(Color.WHITE);
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, TAKEPHOTO_ID, 0,  R.string.take_photo)
			.setIcon(android.R.drawable.ic_menu_camera);
        menu.add(0, NEW_DRAFT_ID, 0,  R.string.new_draft)
			.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, VIEW_DRAFT_ID, 0,  R.string.view_draft)
			.setIcon(android.R.drawable.ic_menu_edit);
        menu.add(0, DEFAULTTEXT_ID, 0,  R.string.default_text)
			.setIcon(android.R.drawable.ic_menu_agenda);
        menu.add(0, SIZE_TEXT_ID, 0,  R.string.size)
			.setIcon(R.drawable.ic_menu_font_size);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case TAKEPHOTO_ID:
        	showDialogSelectImage();
            return true;
        case DEFAULTTEXT_ID:
        	showDialogDefaultText();
            return true;
        case NEW_DRAFT_ID:
        	saveDrafts();
            return true;
        case VIEW_DRAFT_ID:
        	showDialogDrafts();
            return true;
        case SIZE_TEXT_ID:
        	showSizeText();
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    public void showSizeText() {
    	
    	final int minValue = 10;
    	
    	LayoutInflater factory = LayoutInflater.from(this);
        final View sizesFontView = factory.inflate(R.layout.alert_dialog_sizes_newstatus, null);
                
        ((TextView)sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeTextNewStatus(this) + ")");
                
        SeekBar sbSizeText = (SeekBar)sizesFontView.findViewById(R.id.sb_size_text);
        sbSizeText.setMax(18);
        sbSizeText.setProgress(PreferenceUtils.getSizeTextNewStatus(this)-minValue);
    	
        sbSizeText.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progress += minValue;
                PreferenceUtils.setSizeTextNewStatus(NewStatusActivity.this, progress);
				//seekBar.setProgress(progress);
		        ((TextView)sizesFontView.findViewById(R.id.txt_size_text)).setText(getString(R.string.size_text) + " (" + PreferenceUtils.getSizeTextNewStatus(NewStatusActivity.this) + ")");
				mText.setTextSize(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}
    		
    	});
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.font_size);
        builder.setView(sizesFontView);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        	}
        });
        builder.create();
        builder.show();
        
    }

    public void showDialogTweetLonger() {

        Dialog dialog = new Dialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.actions);
        builder.setMessage(R.string.is_twitlonger_msg);

        ArrayList<TweetLongerAdapter.TypeTweetLonger> items = new ArrayList<TweetLongerAdapter.TypeTweetLonger>();

        TweetLongerAdapter.TypeTweetLonger type1 = new TweetLongerAdapter.TypeTweetLonger();

        if (mType!=TYPE_DIRECT_MESSAGE) {
            type1.mode = MODE_TL_TWITLONGER;
            type1.title = getString(R.string.twitlonger);
            type1.description = getString(R.string.twitlonger_msg);
            items.add(type1);
        }

        String replyuser = "";
        if (mType== NewStatusActivity.TYPE_REPLY || mType== NewStatusActivity.TYPE_REPLY_ON_COPY) {
            replyuser = "@"+mReplyScreenName;
        }


        TweetLongerAdapter.TypeTweetLonger type2 = new TweetLongerAdapter.TypeTweetLonger();
        type2.mode = MODE_TL_N_TWEETS;
        type2.title = getString(R.string.n_tweets);
        type2.description = getString(R.string.n_tweets_msg, Utils.getDivide140(mText.getText().toString(), replyuser).size());
        items.add(type2);

        ListView modeList = new ListView(this);
        modeList.setBackgroundColor(Color.WHITE);
        modeList.setCacheColorHint(Color.WHITE);
        final TweetLongerAdapter adapterTweetLonger = new TweetLongerAdapter(this, items);
        modeList.setAdapter(adapterTweetLonger);
        
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setModeTweetLonger( ((TweetLongerAdapter.TypeTweetLonger)adapterTweetLonger.getItem(i)).mode );
                send();
            }
        });

        builder.setView(modeList);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        dialog = builder.create();

        dialog.show();

    }
    
    int whichProgrammedTweet;
    
    public void showDialogProgrammedTweet() {
    	whichProgrammedTweet = 0;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.options);
		//builder.setMessage(mTweetTopics.getString(R.string.follow_tweettopics_msg));
		builder.setSingleChoiceItems(R.array.values_tweetprogrammed, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	whichProgrammedTweet = whichButton;
            }
        });
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Calendar calendar = Calendar.getInstance();
                
                
            	if (whichProgrammedTweet==0) {
            		calendar.add(Calendar.MINUTE, 5);
            	} else if (whichProgrammedTweet==1) {
            		calendar.add(Calendar.MINUTE, 15);
            	} else if (whichProgrammedTweet==2) {
            		calendar.add(Calendar.MINUTE, 30);
            	} else if (whichProgrammedTweet==3) {
            		calendar.add(Calendar.HOUR, 1);
            	} else if (whichProgrammedTweet==4) {
            		calendar.add(Calendar.HOUR, 2);
            	} else if (whichProgrammedTweet==5) {
            		calendar.add(Calendar.HOUR, 6);
            	} else if (whichProgrammedTweet==6) {
            		calendar.add(Calendar.HOUR, 12);
            	} else if (whichProgrammedTweet==7) {
            		calendar.add(Calendar.HOUR, 24);
            	} else if (whichProgrammedTweet==8) {
            		calendar.add(Calendar.HOUR, 24*7);
            	}
            	
            	
                long date = calendar.getTimeInMillis();
            	
            	String users = "";
            	for (UserStatus user : mUsers) {
    	    		if (user.checked) users += user.id + ",";
    	    	}
            	
    			Entity ent = new Entity("tweets_programmed");
    			ent.setValue("users", users);
    			ent.setValue("text", mText.getText().toString());
    			ent.setValue("date", date);
    			ent.setValue("type_id", (mType==TYPE_DIRECT_MESSAGE)?2:1);
    	    	ent.setValue("username_direct", mDMUsername);
    			ent.setValue("is_sent", 0);
    			ent.save();
    			OnAlarmReceiverTweetProgrammed.callNextAlarm(NewStatusActivity.this);
    			Utils.showMessage(NewStatusActivity.this, R.string.programmed_save);
    			finish();
            	
            }
        });
		builder.setNeutralButton(R.string.personalize, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	sendProgrammedTweet();
            }
        });

		AlertDialog alert = builder.create();
		alert.show();
    } 
    
    private void sendProgrammedTweet() {
    	Intent send = new Intent(this, NewEditTweetProgrammed.class);
    	send.putExtra("text", mText.getText().toString());
		startActivity(send);
    }
    
    private void saveDrafts() {
    	Entity ent = new Entity("tweets_draft");
    	ent.setValue("text", mText.getText().toString());
    	ent.save();
    	Utils.showMessage(this, this.getString(R.string.draft_save));
    }
    
    private void showDialogDrafts() {
    	final CheckBox cb = new CheckBox(this);
    	cb.setChecked(false);
    	cb.setText(R.string.delete_draft_sent);
    	cb.setTextColor(Color.GRAY);
    	final List<Entity> ents = DataFramework.getInstance().getEntityList("tweets_draft");
    	TweetListDraftAdapter drafts = new TweetListDraftAdapter(this, ents);
    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setView(cb)
            .setTitle(R.string.view_draft)
            .setAdapter(drafts, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mText.append(ents.get(which).getString("text"));
					if (cb.isChecked()) {
						mIdDeleteDraft = ents.get(which).getId();
					}
				}
            	
            })
            .setPositiveButton(R.string.view_draft, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	Intent send = new Intent(NewStatusActivity.this, TweetDraft.class);
                		startActivity(send);
                    }
                })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
           .create();  
    	builder.show();
    }
    
    public void showDialogDefaultText() {
    	final EditText et = new EditText(this);
    	et.setText(PreferenceUtils.getDefaultTextInTweet(this));
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.dialog_default_text));
		builder.setMessage(this.getString(R.string.dialog_default_text_msg));
		builder.setView(et);
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.setDefaultTextInTweet(NewStatusActivity.this, et.getText().toString());
			}
			
		});
		builder.setNeutralButton(R.string.clean, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
                PreferenceUtils.setDefaultTextInTweet(NewStatusActivity.this, "");
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
    
    public void showDialogSelectImage() {
   		showDialog(DIALOG_SELECT_IMAGE);
    }
    /*
    public void loadUser(long id) {
    	List<Entity> ents = DataFramework.getInstance().getEntityList("users", DataFramework.KEY_ID + " = " + id);
    	if (ents.size()==1) {
    		mUserOut = ents.get(0).getId();
	    	app.loadUser(id, false);
	    	twitter = app.getTwitter();
	    	try {
	    		mBtAvatar.setImageBitmap(Utils.getBitmapAvatar(ents.get(0).getId(), Utils.AVATAR_LARGE));
	    		mTxtUsername.setText(ents.get(0).getString("name"));
			} catch (Exception ex) {
				ex.printStackTrace();
				mBtAvatar.setImageResource(R.drawable.avatar);
			}
    	}
    }
    */
    private void verifyTextAndQuit() {
    	if (mText.getText().toString().equals("")) {
    		quit();
    	} else {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.actions);
    		builder.setMessage(R.string.quit_newstatus);
    		builder.setPositiveButton(R.string.save_draft, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	saveDrafts();
                	quit();                	
                }
            });
    		builder.setNeutralButton(R.string.discard, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	deleteImages();
                	quit();                	
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
    
    private void quit() {
   		setResult(RESULT_OK);
    	finish();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	verifyTextAndQuit();
        	return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode){
		case ACTIVITY_CAMERA:
    		if( resultCode != 0 ) {
    			copyImage(getURLCurrentImage());
    		}
    		break ;
    	case ACTIVITY_SELECTIMAGE:
    		if( resultCode != 0 ) {
	    		Cursor c = managedQuery(intent.getData(),null,null,null,null);
	    		if (c!=null) {
		    		if( c.moveToFirst() ) {
		    			String media_path = c.getString(1);
	    				copyImage(media_path);
		    		}
		    		c.close();
	    		} else {
	    			Utils.showMessage(this, R.string.other_gallery);
	    		}
    		}
    		break;
    	case ACTIVITY_USER: 
    		loadUsers(-1);
    		refreshUsers();    		
    		break;
		}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
    private void addTextInEditText(String text) {
		if (mText.getText().toString().equals("")) {
			mText.append(text+" ");
		} else {
			mText.append(" "+text);
		}
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
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("text", mTextStatus);
		outState.putInt("type", mType);
		outState.putLong("reply_tweetid", mReplyTweetId);
		outState.putString("reply_avatar", mReplyURLAvatar);
		outState.putString("reply_screenname", mReplyScreenName);
		outState.putString("reply_text", mReplyText);
		outState.putString("username_direct_message", mDMUsername);
		outState.putString("retweet_prev", retweetPrev);
		outState.putStringArrayList("ar_images", mImages);
		
		super.onSaveInstanceState(outState);
	}

	
    private String getURLBase() {
    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		
    	int type = Integer.parseInt(preference.getString("prf_service_image", "1"));
		
		if (type==1) {
			return URL_BASE_YFROG;
		} else if (type==2) {
			return URL_BASE_TWITPIC;
		} else if (type==3) {
			return URL_BASE_LOCKERZ;
		}
		
		return "http://service.com/";
    }
    
    class UserStatus {
    	public Bitmap avatarON = null;
    	public Bitmap avatarOFF = null;
    	public String username = "";
    	public boolean checked = false;
    	public String service = "";
    	public long id = 0;
    }
    
    
}
