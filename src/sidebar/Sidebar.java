package sidebar;

import adapters.RowSidebarConversationAdapter;
import adapters.RowSidebarRetweetersAdapter;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.*;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import infos.InfoLink;
import infos.InfoTweet;
import infos.InfoUsers;
import task.LoadImageWidgetAsyncTask;
import task.LoadImageWidgetAsyncTask.LoadImageWidgetAsyncTaskResponder;
import task.LoadLinkAsyncTask;
import task.LoadLinkAsyncTask.LoadLinkAsyncAsyncTaskResponder;
import task.LoadTypeStatusAsyncTask;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Sidebar {

	public static View getLoadingView(TweetTopics mTweetTopics, String text) {
		View v = mTweetTopics.getLayoutInflater().inflate(R.layout.loading, null);
		if (!text.equals("")) ((TextView)v.findViewById(R.id.text_loading)).setText(text);
		return v;
	}
	
	public static View getLoadingView(TweetTopics mTweetTopics) {
		return getLoadingView(mTweetTopics, "");
	}
	
    /*
     * 
     * TWEET
     * 
     */
	
    public static View getViewTweetHeadSidebar(TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore) {
    	
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_head, null);
    	
    	final InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
    	
    	if (it != null) {
    		final ImageView avatar_sidebar = (ImageView) v.findViewById(R.id.user_avatar_sidebar);
    		avatar_sidebar.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				mTweetTopicsCore.loadSidebarUser(it.getUsername());
    			}
    			
    		}); 
    		String urlAvatar = it.getUrlAvatar();
    		String name = it.getUsername();
    		String fullname = it.getFullname();
    		if (it.isRetweet()) {
    			name = it.getUsernameRetweet();
    			urlAvatar = it.getUrlAvatarRetweet();
    			fullname = it.getFullnameRetweet();
    		}    		

    		File file = Utils.getFileForSaveURL(mTweetTopics, urlAvatar);
    		if (file.exists()) {
    			avatar_sidebar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
    		} else {
        		new LoadImageWidgetAsyncTask(new LoadImageWidgetAsyncTaskResponder() {
    				@Override
    				public void imageWidgetLoadCancelled() {}
    				@Override
    				public void imageWidgetLoaded(LoadImageWidgetAsyncTask.ImageData data) {
    					try {
    						avatar_sidebar.setImageBitmap(data.bitmap);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				@Override
    				public void imageWidgetLoading() {}

    			}).execute(urlAvatar);
    		}
    		
    		TextView username_sidebar = (TextView) v.findViewById(R.id.tweet_user_name_text_sidebar);
    		username_sidebar.setText( name + ((fullname.equals(""))?"":" (" + it.getFullname() + ")") );
    		
    		TextView date_sidebar = (TextView) v.findViewById(R.id.tweet_date_sidebar);
   			date_sidebar.setText(Utils.timeFromTweetExtended(mTweetTopics, it.getDate()));
    		
   			TextView text_tweet_sidebar = (TextView) v.findViewById(R.id.tweet_text_sidebar);
            String html = it.getTextHTMLFinal();
            if (html.equals("")) html = Utils.toHTML(mTweetTopics, it.getText());
    		text_tweet_sidebar.setText(Html.fromHtml(html));
    		//text_tweet_sidebar.setText(Html.fromHtml(Utils.toHTML(mTweetTopics, it.getText())));
    		
    		
    	}
    	
    	return v;
    }

    /*
     *
     * RETWEETERS
     *
     */
     public static View getViewRetweetersSidebar(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final ResponseList<User> retweeters_list) {

        View v = View.inflate(mTweetTopics, R.layout.sidebar_retweeters, null);

    	if (retweeters_list != null) {
            RowSidebarRetweetersAdapter rowSidebarRetweetersAdapter = new RowSidebarRetweetersAdapter(mTweetTopics, mTweetTopicsCore, retweeters_list);
            ListView list_view = (ListView)v.findViewById(R.id.list_retweeters);

            list_view.addFooterView(View.inflate(mTweetTopics, R.layout.sidebar_foot_retweeters, null));

            list_view.setAdapter(rowSidebarRetweetersAdapter);
    		list_view.setSelection(0);

            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    User user = (User) adapterView.getItemAtPosition(position);

                    mTweetTopicsCore.loadSidebarUser(user.getScreenName());
                }
            });

            Button btnSendMessage = (Button)v.findViewById(R.id.bt_reply);
            btnSendMessage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String users_reply = "";

                    for (int i = 0; i < retweeters_list.size(); i++) {
                        users_reply = users_reply.concat("@" + retweeters_list.get(i).getScreenName() + " ");
                    }

                    mTweetTopicsCore.updateStatus(NewStatus.TYPE_NORMAL, users_reply, null);
                }
            });

    		Button btnBack = (Button) v.findViewById(R.id.bt_back_to_menu);

    		btnBack.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
		    		mTweetTopicsCore.backToMenuSidebar();
				}

			});

    	}

    	return v;
    }

    /*
     * 
     * CONVERSATION
     * 
     */

    public static RowSidebarConversationAdapter rowSidebarConversationAdapter;

    public static void addRowSidebarConversationAdapter(Status st) {
         if (rowSidebarConversationAdapter!=null) {
             rowSidebarConversationAdapter.add(st);
             rowSidebarConversationAdapter.notifyDataSetChanged();
         }
    }
	
    public static View getViewConversationSidebar(TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final Status mStatusConversation) {
    	
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_conversation, null);
    	
    	if (mStatusConversation != null) {
            ArrayList<Status> tweets = new ArrayList<twitter4j.Status>();
            tweets.add(mStatusConversation);
            rowSidebarConversationAdapter = new RowSidebarConversationAdapter(mTweetTopics, mTweetTopicsCore, tweets);
            ListView list_view = (ListView)v.findViewById(R.id.list_conversation_tweets);

            list_view.addFooterView(View.inflate(mTweetTopics, R.layout.sidebar_foot_conversation, null));

            list_view.setAdapter(rowSidebarConversationAdapter);
    		list_view.setSelection(0);

            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Status tweet_status = (Status) adapterView.getItemAtPosition(position);

                    mTweetTopicsCore.showConversationLinks(tweet_status.getText());
                }
            });
            LinearLayout loading_progress = (LinearLayout)v.findViewById(R.id.load_progress);
            loading_progress.setVisibility(View.GONE);

            Button btnLoadConversation = (Button)v.findViewById(R.id.bt_load_conversation);
            btnLoadConversation.setVisibility(View.GONE);
            btnLoadConversation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mTweetTopicsCore.loadConversation();
                }
            });

    		Button btnConversation = (Button)v.findViewById(R.id.bt_view_conversation);
            btnConversation.setBackgroundResource(R.drawable.button_background);
            btnConversation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mStatusConversation != null && mStatusConversation.getInReplyToStatusId() > 0) {
                        mTweetTopicsCore.getFullConversation(mStatusConversation.getInReplyToStatusId());
                    } else {
                        Utils.showMessage(mTweetTopicsCore.getTweetTopics(), R.string.no_more_tweets);
                    }
                }
            });
    		
    		Button btnBack = (Button) v.findViewById(R.id.bt_back_to_menu);

    		btnBack.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
		    		mTweetTopicsCore.backToMenuSidebar();
				}
				
			});
    		
    	}
    	
    	return v;
    }
    
    /*
     * 
     * TRANSLATE
     * 
     */
	
    public static View getViewTranslateSidebar(TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoUsers iu) {
    	
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_translate, null);
    	
    	if (iu != null) {

    		TextView translate_text_sidebar = (TextView) v.findViewById(R.id.translate_text_sidebar);
    		translate_text_sidebar.setText(Html.fromHtml(Utils.toHTML(mTweetTopics, iu.getTextTweetTranslate())));
    		
    		Button btnTweet = (Button) v.findViewById(R.id.bt_create_tweet);

    		btnTweet.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					InfoTweet it = mTweetTopicsCore.getCurrentInfoTweet();
			    	if (it != null) {
			    		mTweetTopicsCore.updateStatus(NewStatus.TYPE_RETWEET, iu.getTextTweetTranslate(), it);
			    	}
				}
				
			});

    		Button btnBack = (Button) v.findViewById(R.id.bt_back_to_menu);
    		

    		btnBack.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
		    		mTweetTopicsCore.backToMenuSidebar();
				}
				
			});
    		
    	}
    	
    	return v;
    }
    
    /*
     * 
     * USER
     * 
     */

    
    public static View getViewUserHeadSidebar(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoUsers iu) {
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_head_user, null);
    	
    	ImageView avatar = (ImageView) v.findViewById(R.id.user_avatar_sidebar);
    	
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
			
		});    	
    	
    	TextView user_name_text_sidebar = (TextView) v.findViewById(R.id.user_name_text_sidebar);
    	TextView user_url_sidebar = (TextView) v.findViewById(R.id.user_url_sidebar);
    	TextView user_location_sidebar = (TextView) v.findViewById(R.id.user_location_sidebar);
    	
    	user_name_text_sidebar.setText(iu.getName() + " (" + iu.getFullname() + ")");
    	user_url_sidebar.setText(iu.getUrl());
    	user_location_sidebar.setText(iu.getLocation());
		avatar.setImageBitmap(iu.getAvatar());
    	
    	return v;
    }
    
    public static View getViewUserContentSidebar(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoUsers iu) {
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_user, null);

    	TextView text_n_tweets = (TextView) v.findViewById(R.id.text_n_tweets);
    	text_n_tweets.setText(iu.getTweets()+"");
    	
    	TextView text_followers = (TextView) v.findViewById(R.id.text_followers);
    	text_followers.setText(iu.getFollowers()+"");
    	
    	TextView text_following = (TextView) v.findViewById(R.id.text_following);
    	text_following.setText(iu.getFollowing()+"");
    	
    	TextView text_description = (TextView) v.findViewById(R.id.text_description);
    	text_description.setText(iu.getBio());
    	    	
    	String txt_name_user_twitter = "";
    	final String txt_name_user_select = iu.getName();

    	ConnectionManager.getInstance().open(mTweetTopics);
    	
    	try {
    		txt_name_user_twitter = ConnectionManager.getInstance().getTwitter().getScreenName();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LinearLayout btTweets = (LinearLayout) v.findViewById(R.id.bt_tweets);
		
		btTweets.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTweetTopicsCore.createSearchUser(iu, 0);
			}
		});
		
		LinearLayout btFollowers = (LinearLayout) v.findViewById(R.id.bt_followers);
		
		btFollowers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTweetTopicsCore.loadTypeStatus(LoadTypeStatusAsyncTask.FOLLOWERS, txt_name_user_select);
			}
		});
		
		LinearLayout btFollowing = (LinearLayout) v.findViewById(R.id.bt_following);
		
		btFollowing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTweetTopicsCore.loadTypeStatus(LoadTypeStatusAsyncTask.FRIENDS, txt_name_user_select);
			}
		});
		
		Button btnFollow = (Button) v.findViewById(R.id.btn_user_follow);
		
		LinearLayout layout_connect_users = (LinearLayout) v.findViewById(R.id.layout_connect_users);
		
		if (txt_name_user_twitter.equals(txt_name_user_select)) {
			layout_connect_users.setVisibility(View.GONE);
			btnFollow.setVisibility(View.GONE);
		} else {
		
			layout_connect_users.setVisibility(View.VISIBLE);
			btnFollow.setVisibility(View.VISIBLE);
			
			TextView name_user_twitter = (TextView) v.findViewById(R.id.name_user_twitter);
			name_user_twitter.setText(txt_name_user_twitter);
			
			String connects = "connects_" + (iu.isFriend()?"on":"off") + "_" + (iu.isFollower()?"on":"off");
			
			int id = mTweetTopics.getResources().getIdentifier(Utils.packageName+":drawable/"+connects, null, null);
			ImageView connects_users = (ImageView) v.findViewById(R.id.connects_users);
			connects_users.setImageResource(id);
	    	
	    	TextView name_user_select = (TextView) v.findViewById(R.id.name_user_select);
	    	name_user_select.setText(txt_name_user_select);
	    	
	    	
	    	if (iu.isFriend()) {
	    		btnFollow.setText(R.string.unfollow);
	    	} else {
	    		btnFollow.setText(R.string.follow);
	    	}
	    	btnFollow.setTag(iu);
	    	btnFollow.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					//hideImageLayout();
					
			    	try {
			    		InfoUsers iu = (InfoUsers)v.getTag();
			    		ConnectionManager.getInstance().open(mTweetTopics);
			    		if (iu.isFriend()) {
			    			((Button)v).setText(R.string.follow);
			    			ConnectionManager.getInstance().getTwitter().destroyFriendship(iu.getName());
							String text = "";
							Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
					    	if (e!=null) {
					    		text = e.getString("name");
					    	}
					    	text += " " + mTweetTopics.getString(R.string.txt_unfollow_user) + " " + iu.getName();
							Utils.showMessage(mTweetTopics, text);
			    		} else {
			    			((Button)v).setText(R.string.unfollow);
			    			ConnectionManager.getInstance().getTwitter().createFriendship(iu.getName());
							String text = "";
							Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
					    	if (e!=null) {
					    		text = e.getString("name");
					    	}
					    	text += " " + mTweetTopics.getString(R.string.txt_follow_user) + " " + iu.getName();
							Utils.showMessage(mTweetTopics, text);
			    		}
			    		iu.setFriend(!iu.isFriend());
					} catch (TwitterException e) {
						e.printStackTrace();
					}
	
				}
				
			});

		}
		
    	return v;
    }
    
    public static View getViewUserFootSidebar(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoUsers iu) {
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_foot_user, null);
    	v.setBackgroundColor(Color.parseColor("#"+new ThemeManager(mTweetTopics).getStringColor("color_bottom_bar")));
    	Button bt_show_tweets = (Button) v.findViewById(R.id.bt_show_tweets);
    	bt_show_tweets.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTweetTopics.showDialog(TweetTopicsCore.DIALOG_USER_TWEETS);
			}
    	});
    	
    	Button bt_actions = (Button) v.findViewById(R.id.bt_actions);
    	bt_actions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTweetTopics.showDialog(TweetTopicsCore.DIALOG_USER_ACTIONS);
			}
    	});
    	
    	Button bt_close = (Button) v.findViewById(R.id.bt_close);
    	bt_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTweetTopicsCore.closeSidebar();
			}
    	});
    	
    	return v;
    }
    
    /*
     * 
     * LINK
     * 
     */
    
    public static View getViewLinkHeadSidebar(TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, InfoLink link) {
    	View v = View.inflate(mTweetTopics, R.layout.sidebar_head_link, null);
    	ImageView icon_link_sidebar = (ImageView) v.findViewById(R.id.icon_link_sidebar);
    	TextView type_link_sidebar = (TextView) v.findViewById(R.id.type_link_sidebar);
    	TextView url_link_sidebar = (TextView) v.findViewById(R.id.url_link_sidebar);
    	if (link.getType() == 0) { // imagen
    		type_link_sidebar.setText(mTweetTopics.getString(R.string.sidebar_image, link.getService()));
    		icon_link_sidebar.setImageDrawable(mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_sidebar_photo, ThemeManager.TYPE_NORMAL));
    	} else if (link.getType() == 1) { // video
    		type_link_sidebar.setText(mTweetTopics.getString(R.string.sidebar_video, link.getService()));
    		icon_link_sidebar.setImageDrawable(mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_sidebar_video, ThemeManager.TYPE_NORMAL));
    	} else if (link.getType() == 2) { // enlace
    		type_link_sidebar.setText(mTweetTopics.getString(R.string.sidebar_link));
    		icon_link_sidebar.setImageDrawable(mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_sidebar_link, ThemeManager.TYPE_NORMAL));
    	}
    	url_link_sidebar.setText(link.getOriginalLink());
    	
    	ImageView icon_view_tweet = (ImageView) v.findViewById(R.id.icon_goto_tweet);
    	icon_view_tweet.setImageDrawable(mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_view_tweet, ThemeManager.TYPE_NORMAL));
    	icon_view_tweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	    		mTweetTopicsCore.showSidebarTweet();	
			}
    	});
    	
    	return v;
    }
    
    private static void showDialogActionsImage(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoLink link) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
		builder.setTitle(R.string.actions);
		builder.setItems(R.array.items_actions_image, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface v, int whichButton) {
            	if (whichButton==0) {
            		String url = link.getOriginalLink();
					mTweetTopicsCore.updateStatus(NewStatus.TYPE_NORMAL, url, null);
            	} else if (whichButton==1) {
            		try {
						int count = 0;
						String filename = "photoTweetTopics_"+count+".jpg";
						String f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + filename;
						File file = new File(f);
						while (file.exists()) {
							count++;
							filename = "photoTweetTopics_"+count+".jpg";
							f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + filename;
							file = new File(f);	
						}
						
						Log.d(Utils.TAG, "Grabando imagen en " + f);
						
						FileOutputStream out = new FileOutputStream(f);
						
						link.getBitmapLarge().compress(Bitmap.CompressFormat.JPEG, 95, out);
						
						//mTweetTopics.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
						
						ContentValues image = new ContentValues();

						image.put(Images.Media.TITLE, "Download from TweetTopics");
						image.put(Images.Media.DISPLAY_NAME, "Download from TweetTopics");
						image.put(Images.Media.DESCRIPTION, link.getOriginalLink());
						image.put(Images.Media.DATE_ADDED, Calendar.getInstance().getTimeInMillis());
						image.put(Images.Media.DATE_TAKEN, Calendar.getInstance().getTimeInMillis());
						image.put(Images.Media.DATE_MODIFIED, Calendar.getInstance().getTimeInMillis());
						image.put(Images.Media.MIME_TYPE, "image/jpeg");
						image.put(Images.Media.ORIENTATION, 0);

						File parent = file.getParentFile();
						String path = parent.toString().toLowerCase();
						String name = parent.getName().toLowerCase();
						image.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
						image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
						image.put(Images.Media.SIZE, file.length());
						image.put(Images.Media.DATA, file.getAbsolutePath());

						mTweetTopics.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
						
						Utils.showShortMessage(mTweetTopics, mTweetTopics.getString(R.string.save_image_download));
						
            		} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
            	} else if (whichButton==2) {
					try {
						String f = Utils.appDirectory+"image_tweettopics_"+ System.currentTimeMillis() +".jpg";
						FileOutputStream out = new FileOutputStream(f);
						link.getBitmapLarge().compress(Bitmap.CompressFormat.JPEG, 95, out);
	            		Intent msg=new Intent(Intent.ACTION_SEND);
	        			msg.setType("image/jpeg");
	        			msg.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+f));
	        			mTweetTopics.startActivity(Intent.createChooser(msg, mTweetTopics.getString(R.string.share)));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
            	} else if (whichButton==3) {
            		Intent msg=new Intent(Intent.ACTION_SEND);
                    msg.putExtra(Intent.EXTRA_TEXT, link.getOriginalLink());
                    msg.setType("text/plain");
                    mTweetTopics.startActivity(msg);
            	} else if (whichButton==4) {
            		Intent wallpaper = new Intent(mTweetTopics, AdjustImage.class);
                	wallpaper.putExtra("url", link.getLinkImageLarge());
                	mTweetTopics.startActivityForResult(wallpaper, TweetTopicsCore.ACTIVITY_WALLPAPER);
            	} else if (whichButton==5) {
            		Intent intent = new Intent(mTweetTopics, QRCode.class);
            		String title = "";
            		if (link.getType() == 0) { // imagen
            			title = mTweetTopics.getString(R.string.sidebar_image, link.getService());
                	} else if (link.getType() == 1) { // video
                		title = mTweetTopics.getString(R.string.sidebar_video, link.getService());
                	} else if (link.getType() == 2) { // enlace
                		title = mTweetTopics.getString(R.string.sidebar_link);
                	}
        	    	intent.putExtra("title_qr", title);
        	    	intent.putExtra("url_qr", link.getOriginalLink());
        	    	mTweetTopics.startActivity(intent);
            	} else if (whichButton==6) {
            		ClipboardManager clipboard = (ClipboardManager) mTweetTopics.getSystemService(Context.CLIPBOARD_SERVICE);
            		clipboard.setText(link.getOriginalLink());
            		Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.copied_to_clipboard));
            	}
			}

			
		});
		builder.setPositiveButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private static void showDialogActionsLink(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoLink link) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(mTweetTopics);
		builder.setTitle(R.string.actions);
		builder.setItems(R.array.items_actions_links, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface v, int whichButton) {
            	if (whichButton==0) {
            		String url = link.getOriginalLink();
					mTweetTopicsCore.updateStatus(NewStatus.TYPE_NORMAL, url, null);
            	} else if (whichButton==1) {
            		Intent msg=new Intent(Intent.ACTION_SEND);
                    msg.putExtra(Intent.EXTRA_TEXT, link.getTitle() + " " + link.getOriginalLink());
                    msg.setType("text/plain");
                    mTweetTopics.startActivity(msg);
            	} else if (whichButton==2) {
            		Intent intent = new Intent(mTweetTopics, QRCode.class);
            		String title = "";
            		if (link.getType() == 0) { // imagen
            			title = mTweetTopics.getString(R.string.sidebar_image, link.getService());
                	} else if (link.getType() == 1) { // video
                		title = mTweetTopics.getString(R.string.sidebar_video, link.getService());
                	} else if (link.getType() == 2) { // enlace
                		title = mTweetTopics.getString(R.string.sidebar_link);
                	}
        	    	intent.putExtra("title_qr", title);
        	    	intent.putExtra("url_qr", link.getOriginalLink());
        	    	mTweetTopics.startActivity(intent);
            	} else if (whichButton==3) {
            		ClipboardManager clipboard = (ClipboardManager) mTweetTopics.getSystemService(Context.CLIPBOARD_SERVICE);
            		clipboard.setText(link.getOriginalLink());
            		Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.copied_to_clipboard));
            	}
			}

			
		});
		builder.setPositiveButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

		AlertDialog alert = builder.create();
		alert.show();
    }
    
    public static View getViewLinkFootSidebar(final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoLink link) {
    	if (link.getType() == 0) { // imagen
	    	View v = View.inflate(mTweetTopics, R.layout.sidebar_foot_image, null);
	    	v.setBackgroundColor(Color.parseColor("#"+new ThemeManager(mTweetTopics).getStringColor("color_bottom_bar")));
	    	Button bt_visit = (Button) v.findViewById(R.id.bt_visit);
	    	bt_visit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTweetTopicsCore.goToLink(link.getOriginalLink());
					/*
					String url = link.getOriginalLink();
		    		String u = url;
		    		if (url.startsWith("www")) {
		    			u = "http://"+url;
		    		}
		    		Uri uri = Uri.parse(u);
		    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		    		mTweetTopics.startActivity(intent);	
		    		*/
				}
	    	});
	    	
	    	Button bt_actions = (Button) v.findViewById(R.id.bt_actions);
	    	bt_actions.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialogActionsImage(mTweetTopics, mTweetTopicsCore, link);
				}
	    	});
	    	
	    	Button bt_close = (Button) v.findViewById(R.id.bt_close);
	    	bt_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTweetTopicsCore.closeSidebar();
				}
	    	});
	    	
	    	return v;
    	} else { // otros
	    	View v = View.inflate(mTweetTopics, R.layout.sidebar_foot_link, null);
	    	v.setBackgroundColor(Color.parseColor("#"+new ThemeManager(mTweetTopics).getStringColor("color_bottom_bar")));
	    	Button bt_visit = (Button) v.findViewById(R.id.bt_visit);
	    	bt_visit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTweetTopicsCore.goToLink(link.getOriginalLink());
					/*
					String url = link.getOriginalLink();
		    		String u = url;
		    		if (url.startsWith("www")) {
		    			u = "http://"+url;
		    		}
		    		Uri uri = Uri.parse(u);
		    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		    		mTweetTopics.startActivity(intent);	*/
				}
	    	});
	    	
	    	Button bt_actions = (Button) v.findViewById(R.id.bt_actions);
	    	bt_actions.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialogActionsLink(mTweetTopics, mTweetTopicsCore, link);
				}
	    	});
	    	
	    	Button bt_close = (Button) v.findViewById(R.id.bt_close);
	    	bt_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTweetTopicsCore.closeSidebar();
				}
	    	});
	    	
	    	return v;
    	}
    }
    
    public static View getViewLinkContentSidebar(final TweetTopics mTweetTopics, TweetTopicsCore mTweetTopicsCore, final InfoLink link) {
    	    	
    	if (link.getType() == 0) { // imagen
    		if (link.isExtensiveInfo()) {
    			View v = View.inflate(mTweetTopics, R.layout.sidebar_image, null);
	    		ImageView im = (ImageView) v.findViewById(R.id.link_image);
	    		/*FrameLayout.LayoutParams ll = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    		ll.gravity = Gravity.TOP;
	    		im.setLayoutParams(ll);
	    		im.setAdjustViewBounds(true);
	    		im.setBackgroundColor(Color.WHITE);*/
	    		im.setImageBitmap(link.getBitmapLarge());
	    		ImageView bt = (ImageView) v.findViewById(R.id.bt_zoom);
	    		bt.setOnClickListener(new OnClickListener() {
	    			@Override
					public void onClick(View v) {
	    				if (link.getBitmapLarge()!=null) {
							try {
								FileOutputStream out = new FileOutputStream(Utils.appDirectory + "image_large.jpg");
								link.getBitmapLarge().compress(Bitmap.CompressFormat.JPEG, 90, out);
			    				Intent intent = new Intent(mTweetTopics, ShowImage.class);
					    		mTweetTopics.startActivity(intent);	
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.problem_image));
							} catch (Exception e) {
								e.printStackTrace();
								Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.problem_image));
							}
	    				} else {
	    					Utils.showMessage(mTweetTopics, mTweetTopics.getString(R.string.problem_image));
	    				}
					}
	    		});
	    		return v;
    		} else {
    			seachLink(mTweetTopics, mTweetTopicsCore, link);
    			return getLoadingView(mTweetTopics);
    		}
    	} else if (link.getType() == 1) { // video
    		if (link.isExtensiveInfo()) {
    			View v = View.inflate(mTweetTopics, R.layout.sidebar_video, null);
    			TextView txtTitle = (TextView) v.findViewById(R.id.text_title);
    			txtTitle.setText(link.getTitle());
    			
    			TextView txtSubtitle = (TextView) v.findViewById(R.id.text_subtitle);
    			
    			if (link.getDurationVideo()>0) {
    				txtSubtitle.setVisibility(View.VISIBLE);
    				txtSubtitle.setText(mTweetTopics.getString(R.string.duration) + ": " + Utils.seconds2Time(link.getDurationVideo(), false));
    			} else {
    				txtSubtitle.setVisibility(View.GONE);
    			}
    			
    			ImageView image = (ImageView) v.findViewById(R.id.link_image);
    			/*LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(Utils.dip2px(mTweetTopics, 400), LayoutParams.WRAP_CONTENT);
    			image.setLayoutParams(ll);
    			image.setAdjustViewBounds(true);*/
    			image.setImageBitmap(link.getBitmapLarge());
    			return v;
    		} else {
    			seachLink(mTweetTopics, mTweetTopicsCore, link);
    			return getLoadingView(mTweetTopics);
    		}
    	} else if (link.getType() == 2) { // enlace
    		if (link.isExtensiveInfo()) {
    			View v = View.inflate(mTweetTopics, R.layout.sidebar_link, null);
            	ImageView image = (ImageView) v.findViewById(R.id.link_image);
            	TextView txtDescription = (TextView) v.findViewById(R.id.text_description);
            	
            	TextView txtTitle = (TextView) v.findViewById(R.id.text_title);
    			txtTitle.setText(link.getTitle());
    			
    			TextView txtLink = (TextView) v.findViewById(R.id.text_link);
    			if (!link.getLink().equals(link.getOriginalLink())) {
    				txtLink.setVisibility(View.VISIBLE);
    				String t = link.getLink();
    				if (t.length()>50) t = link.getLink().substring(0, 47)+"...";
    				txtLink.setText(t);
    			} else {
    				txtLink.setVisibility(View.GONE);
    			}
            	
            	if (link.getDescription().equals("")) {
        			txtDescription.setVisibility(View.GONE);
        		} else {
        			txtDescription.setText(link.getDescription());
        			txtDescription.setVisibility(View.VISIBLE);
        		}
        		image.setImageBitmap(link.getBitmapThumb());

            	return v;
    		} else {
    			seachLink(mTweetTopics, mTweetTopicsCore, link);
    			return getLoadingView(mTweetTopics);
    		}
    	} else if (link.getType() == 3) { // QR TweetTopics
    		return getLoadingView(mTweetTopics);
    	}

    	return null;
    }
    
    // buscar informacion del enlace
    
    private static AsyncTask<InfoLink, Void, InfoLink> loadLinkTask;  
    
    public static void seachLink (final TweetTopics mTweetTopics, final TweetTopicsCore mTweetTopicsCore, final InfoLink il) {
    	

		loadLinkTask = new LoadLinkAsyncTask(mTweetTopics, new LoadLinkAsyncAsyncTaskResponder() {
			@Override
			public void linkLoading() {
			}

			@Override
			public void linkCancelled() {	
			}

			@Override
			public void linkLoaded(InfoLink newIl) {
				mTweetTopicsCore.refreshLink(newIl);
			}
		}).execute(il);
    					

    }
    
    public static void cancelLoadLinkAsyncTask() {
    	if (loadLinkTask!=null) loadLinkTask.cancel(true);
    }

    
}
