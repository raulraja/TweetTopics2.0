package com.javielinux.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoImagesTweet;
import com.javielinux.infos.InfoTweet;
import com.javielinux.task.LoadImageWidgetAsyncTask;
import com.javielinux.task.LoadImageWidgetAsyncTask.LoadImageWidgetAsyncTaskResponder;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.*;
import twitter4j.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ServiceWidgetTweets4x2 extends Service {
	
	public  Twitter twitter;

    private int MAX_TWEET = 10;
    public long UPDATE_WIDGET = 15000;

	public static int TIMELINE = 0;
	public static int MENTIONS = 1;
	public static int SEARCH = 2;

	private int mType = TIMELINE;
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Entity column;
    private int mCurrentTweet = 0;
    private long mCurrentSearch = 1;
	
	public boolean blocked = false;
	
	public Context lastContext;
    private AppWidgetManager manager;
    private ComponentName thisWidget;

    private List<InfoTweet> mTweets = new ArrayList<InfoTweet>();
    private  ArrayList<InfoImagesTweet> imagesTweet = new ArrayList<InfoImagesTweet>();
    private AsyncTask<String, Void,LoadImageWidgetAsyncTask.ImageData> latestLoadTask;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (!Utils.isLite(context) ) {
	        	try {
	        		if (intent.getExtras().containsKey("button")) {
	        			onHandleAction(context, (Uri)intent.getExtras().get("button"));
	        			return;
	        		}

                    if (intent.getExtras().containsKey("column_id")) {
                        long column_id = intent.getExtras().getLong("column_id");
                        column = new Entity("columns", column_id);

                        if (column == null) {
                            column = DBUtils.widgetFirstColumn();
                            column_id = column.getId();
                        }

                        PreferenceUtils.setWidgetColumn(context, column_id);
                    }

	        		if (intent.getExtras().containsKey("id_search")) {
	        			mCurrentSearch = intent.getExtras().getLong("id_search");
	        			mType = SEARCH;
                        PreferenceUtils.setIdSearchWidget(context, mCurrentSearch);
                        PreferenceUtils.setTypeWidget(context, mType);
	        		}
	        		if (intent.getExtras().containsKey("id_user")) {
	        			mType = intent.getExtras().getInt("id_user");
                        PreferenceUtils.setTypeWidget(context, mType);
	        		}

	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		Log.d(Utils.TAG_WIDGET, "Error al recibir el parametro");
	        	}
	        	update(context);
	        	getManager().updateAppWidget(getWidget(), getRemoteView(context));
        	}
        }
    };

    public  void clearImagesTweets() {
        imagesTweet.clear();
    }

    private Bitmap getIconItem(Context context) {
        int column_type = column.getInt("type_id");
        Bitmap bitmap = null;
        switch (column_type) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
            case TweetTopicsUtils.COLUMN_MENTIONS:
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
            case TweetTopicsUtils.COLUMN_SENT_DIRECT_MESSAGES:
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
            case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
            case TweetTopicsUtils.COLUMN_FOLLOWERS:
            case TweetTopicsUtils.COLUMN_FOLLOWINGS:
            case TweetTopicsUtils.COLUMN_FAVORITES:
                bitmap = ImageUtils.getBitmapAvatar(column.getEntity("user_id").getId(), Utils.AVATAR_LARGE);
                break;
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity search_entity = new Entity("search", column.getLong("search_id"));
                Drawable drawable = Utils.getDrawable(context, search_entity.getString("icon_big"));
                if (drawable == null) drawable = context.getResources().getDrawable(R.drawable.letter_az);
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, Utils.AVATAR_LARGE, Utils.AVATAR_LARGE, true);
                break;
            case TweetTopicsUtils.COLUMN_LIST_USER:
                bitmap = ImageUtils.getBitmapAvatar(column.getEntity("userlist_id").getEntity("user_id").getId(), Utils.AVATAR_LARGE);
                break;
        }

        return bitmap;
    }

	private  InfoImagesTweet getImagesTweets(int position) {
		for (int i=0; i<imagesTweet.size(); i++) {
			if (imagesTweet.get(i).getPosition() == position)
				return imagesTweet.get(i);
		}

		return null;
	}
	
	public AppWidgetManager getManager() {
		if (manager==null) manager = AppWidgetManager.getInstance(this);
		return manager;
	}

    private int getTypeResource(String linkForImage) {
        int res = 0;
        if (LinksUtils.isLinkImage(linkForImage)) {
            if (LinksUtils.isLinkVideo(linkForImage)) {
                res = R.drawable.icon_tweet_video;
            } else {
                res = R.drawable.icon_tweet_image;
            }
        } else {
            if (linkForImage.startsWith("@")) {
                res = R.drawable.icon_tweet_user;
            } else if (linkForImage.startsWith("#")) {
                res = R.drawable.icon_tweet_hashtag ;
            } else {
                res = R.drawable.icon_tweet_link;
            }
        }
        return res;
    }

    public String getTitleItem() {
        int type_column = column.getInt("type_id");
        switch (type_column) {
            case TweetTopicsUtils.COLUMN_SEARCH:
                Entity ent = new Entity("search", column.getLong("search_id"));
                return ent.getString("name");
            case TweetTopicsUtils.COLUMN_LIST_USER:
                Entity list_user_entity = new Entity("user_lists", column.getLong("userlist_id"));
                return list_user_entity.getString("name");
            case TweetTopicsUtils.COLUMN_TRENDING_TOPIC:
                return column.getEntity("type_id").getString("title") + " " + column.getString("description");
            default:
                return column.getEntity("type_id").getString("title");
        }

    }

	public ComponentName getWidget() {
		if (thisWidget==null) thisWidget = new ComponentName(this, WidgetTweets4x2.class);
		return thisWidget;
	}

    private PendingIntent makeControlPendingIntent(Context context, String command) {
        Intent active = new Intent();
        active.setAction(Utils.ACTION_WIDGET_CONTROL);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri data = Uri.parse(Utils.URI_SCHEME + "://command/"+command);
        active.setData(data);
        return(PendingIntent.getBroadcast(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	
    	this.registerReceiver(receiver, new IntentFilter(Intent.ACTION_VIEW));
    	
    	try {
	    	if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
	    		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	    	}
    	} catch (NullPointerException e) {
    		e.printStackTrace();
    	}

        PreferenceUtils.setTypeWidget(this, TIMELINE);
        PreferenceUtils.setIdSearchWidget(this, 1);

    	ConnectionManager.getInstance().open(this);
    	
        twitter = ConnectionManager.getInstance().getUserForSearchesTwitter();
    	
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		    	
		if ( Utils.isLite(this) ) {
    		RemoteViews mRemoteLoading = new RemoteViews(this.getPackageName(), R.layout.widget_lite);
    		
    		Uri uri = Uri.parse("market://search?q=pname:com.javielinux.tweettopics.pro");
            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent configurePendingIntent = PendingIntent.getActivity(this, 0, buyProIntent, 0);
            mRemoteLoading.setOnClickPendingIntent(R.id.btn_w_lite, configurePendingIntent);
    		
            getManager().updateAppWidget(getWidget(), mRemoteLoading);
    	} else {

            /*List<Entity> columns = DataFramework.getInstance().getEntityList("columns", "id=" + column);

            if (columns.size() == 1) {
                update(this);
                handler.postDelayed(runnable, UPDATE_WIDGET);
                getManager().updateAppWidget(getWidget(), getRemoteView(this));
            } else {
                RemoteViews mRemoteLoading = new RemoteViews(this.getPackageName(), R.layout.widget_no_user);
                getManager().updateAppWidget(getWidget(), mRemoteLoading);
            }*/

    		List<Entity> e = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");
	    	if (e.size()>0) {
	    		
	    		if (DataFramework.getInstance().getEntityList("search").size()>0) {
	    			mCurrentSearch = DataFramework.getInstance().getEntityList("search").get(0).getId();
	    		} else {
	    			mCurrentSearch = -1;
	    		}
    			update(this);
    			handler.postDelayed(runnable, UPDATE_WIDGET);
    			getManager().updateAppWidget(getWidget(), getRemoteView(this));
    			
	    	} else {
	    		
	    		RemoteViews mRemoteLoading = new RemoteViews(this.getPackageName(), R.layout.widget_no_user);
	    			    		
	    		getManager().updateAppWidget(getWidget(), mRemoteLoading);
	    		
	    	}
    	}        

    }
    
    public RemoteViews getRemoteView(Context context) {

        if ( Utils.isLite(context) ) {
            RemoteViews mRemoteLoading = new RemoteViews(this.getPackageName(), R.layout.widget_lite);

            Uri uri = Uri.parse("market://search?q=pname:com.javielinux.tweettopics.pro");
            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent configurePendingIntent = PendingIntent.getActivity(this, 0, buyProIntent, 0);
            mRemoteLoading.setOnClickPendingIntent(R.id.btn_w_lite, configurePendingIntent);

            return mRemoteLoading;
        } else {
            lastContext = context;

            RemoteViews mRemoteView = new RemoteViews(context.getPackageName(), R.layout.widget_main);

            try {
                if (column != null) {
                    Bitmap avatar = getIconItem(context);
                    String title = getTitleItem();

                    mRemoteView.setTextViewText(R.id.w_title, title);

                    if (avatar == null) {
                        mRemoteView.setImageViewResource(R.id.w_icon, R.drawable.avatar);
                    } else {
                        mRemoteView.setImageViewBitmap(R.id.w_icon, avatar);
                    }
                }

                mRemoteView.setTextViewText(R.id.w_tweet_user_name_text, mTweets.get(mCurrentTweet).getUsername());
                mRemoteView.setTextViewText(R.id.w_tweet_source, Html.fromHtml(mTweets.get(mCurrentTweet).getSource()).toString());
                mRemoteView.setTextViewText(R.id.w_tweet_text, Html.fromHtml(Utils.toHTML(context, mTweets.get(mCurrentTweet).getText())));
                mRemoteView.setTextViewText(R.id.w_tweet_date,  Utils.timeFromTweet(this, mTweets.get(mCurrentTweet).getDate()) );

                if (mTweets.get(mCurrentTweet).hasGeoLocation()) {
                    mRemoteView.setViewVisibility(R.id.tag_map, View.VISIBLE);
                } else {
                    mRemoteView.setViewVisibility(R.id.tag_map, View.GONE);
                }

                if (mTweets.get(mCurrentTweet).hasConversation()) {
                    mRemoteView.setViewVisibility(R.id.tag_conversation, View.VISIBLE);
                } else {
                    mRemoteView.setViewVisibility(R.id.tag_conversation, View.GONE);
                }
                /*
				ArrayList<String> links = LinksUtils.pullLinksHTTP(mTweets.get(mCurrentTweet).getText());

                String linkForImage = mTweets.get(mCurrentTweet).getBestLink();

                if (links.size() == 0) {
                    mRemoteView.setViewVisibility(R.id.tweet_photo_img_container, View.GONE);
                    mRemoteView.setViewVisibility(R.id.tweet_photo_multi_img_container, View.GONE);
                } else {
                    int tweet_photo_id = -1;
                    if (links.size() == 1) {
                        tweet_photo_id = R.id.tweet_photo_img;
                        mRemoteView.setViewVisibility(R.id.tweet_photo_img, View.VISIBLE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_img_container, View.VISIBLE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_multi_img, View.GONE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_multi_img_container, View.GONE);

                        Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(0)));
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
                        mRemoteView.setOnClickPendingIntent(R.id.tweet_photo_img, pendingIntent);
                    } else {
                        tweet_photo_id = R.id.tweet_photo_multi_img;
                        mRemoteView.setViewVisibility(R.id.tweet_photo_img, View.GONE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_img_container, View.GONE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_multi_img, View.VISIBLE);
                        mRemoteView.setViewVisibility(R.id.tweet_photo_multi_img_container, View.VISIBLE);

                        Intent configureIntent = new Intent(context, WidgetTweetsLinks4x2.class);
                        configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        configureIntent.putExtra(GlobalsWidget.WIDGET_LINKS, links);
                        PendingIntent configurePendingIntent = PendingIntent.getActivity(context, 0, configureIntent, 0);
                        mRemoteView.setOnClickPendingIntent(R.id.tweet_photo_multi_img, configurePendingIntent);
                    }

                    InfoLink infoLink = null;

                    if (CacheData.existCacheInfoLink(linkForImage)) {
                        infoLink = CacheData.getCacheInfoLink(linkForImage);
                    }

                    int typeResource = getTypeResource(linkForImage);
                    AQuery aQuery = new AQuery(context);

                    if (infoLink!=null) {

                        String thumb = infoLink.getLinkImageThumb();

                        if (thumb.equals("")) {
                            mRemoteView.setImageViewResource(tweet_photo_id,typeResource);
                        } else {
                            Bitmap image = aQuery.getCachedImage(infoLink.getLinkImageThumb());

                            if (image!=null) {
                                mRemoteView.setImageViewBitmap(tweet_photo_id,image);
                            } else {
                                aQuery.id(tweet_photo_id).image(infoLink.getLinkImageThumb(), true, true, 0, typeResource, aQuery.getCachedImage(typeResource), 0);
                            }
                        }

                    } else { // si no tenemos InfoLink en cache
                        mRemoteView.setImageViewResource(tweet_photo_id,typeResource);
                    }
                }
                */
				/*for (int i=0; i<5; i++) {
					int id = 0;
					if (i==0) id = R.id.widget_link_1;
					else if (i==1) id = R.id.widget_link_2;
					else if (i==2) id = R.id.widget_link_3;
					else if (i==3) id = R.id.widget_link_4;
					else if (i==4) id = R.id.widget_link_5;
					if (id>0) {
						if (i<links.size()) {
							mRemoteView.setViewVisibility(id, View.VISIBLE);
							Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(i)));
			    	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
			    	        mRemoteView.setOnClickPendingIntent(id, pendingIntent);
						} else {
							mRemoteView.setViewVisibility(id, View.GONE);
						}
					}
				}*/
				
				if (null != latestLoadTask) {
					latestLoadTask.cancel(true);
				}
				
				InfoImagesTweet iit = getImagesTweets(mCurrentTweet);
				
				if (iit==null) { // si no esta en cache RAM se busca en el telefono
					String urlAvatar = mTweets.get(mCurrentTweet).getUrlAvatar();

					File file = Utils.getFileForSaveURL(context, urlAvatar);
		
					if (file.exists()) {
						Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
						if (bmp != null) {
							iit = new InfoImagesTweet(mCurrentTweet); 
							iit.setBmpAvatar(bmp);
							imagesTweet.add(iit);
						}
					}
				}
	
	
				
				if (iit==null) { // no estan las imagenes en cache
					mRemoteView.setImageViewResource(R.id.w_user_avatar, R.drawable.avatar);
					latestLoadTask = new LoadImageWidgetAsyncTask(new LoadImageWidgetAsyncTaskResponder() {
	
						@Override
						public void imageWidgetLoadCancelled() {
							
						}
	
						@Override
						public void imageWidgetLoaded(LoadImageWidgetAsyncTask.ImageData data) {
							try {
								InfoImagesTweet iit = new InfoImagesTweet(mCurrentTweet); 
								iit.setBmpAvatar(data.bitmap);
								imagesTweet.add(iit);
								getManager().updateAppWidget(getWidget(), getRemoteView(lastContext));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
	
						@Override
						public void imageWidgetLoading() {
							
						}
	
	
						
					}).execute(mTweets.get(mCurrentTweet).getUrlAvatar());
				} else { // si estan las imagenes en cache
	
					mRemoteView.setImageViewBitmap(R.id.w_user_avatar, iit.getBmpAvatar());
	
				}
				
				mRemoteView.setOnClickPendingIntent(R.id.w_tweet_layout, makeControlPendingIntent(context, "app"));
				mRemoteView.setOnClickPendingIntent(R.id.btn_w_refresh, makeControlPendingIntent(context, "refresh"));
				mRemoteView.setOnClickPendingIntent(R.id.btn_w_next, makeControlPendingIntent(context, "next"));
				mRemoteView.setOnClickPendingIntent(R.id.btn_w_prev, makeControlPendingIntent(context, "prev"));
				
	    		Intent configureIntent = new Intent(context, WidgetTweetsConf4x2.class);
	    		configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	            PendingIntent configurePendingIntent = PendingIntent.getActivity(context, 0, configureIntent, 0);
	            mRemoteView.setOnClickPendingIntent(R.id.btn_w_configure, configurePendingIntent);

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                column = DBUtils.widgetFirstColumn();
                PreferenceUtils.setWidgetColumn(context, column.getId());
                mRemoteView.setTextViewText(R.id.w_tweet_text, "Error");
			} catch (Exception e) {
				e.printStackTrace();
                column = DBUtils.widgetFirstColumn();
                PreferenceUtils.setWidgetColumn(context, column.getId());
				mRemoteView.setTextViewText(R.id.w_tweet_text, "Error");
			}
	    	
	    	return mRemoteView;
	    	
    	 }

    }

    public void update(Context context) {
    	   	
        blocked = true;
    	
    	mCurrentTweet = 0;
    	
    	RemoteViews mRemoteLoading = new RemoteViews(context.getPackageName(), R.layout.widget_loading);
    	getManager().updateAppWidget(getWidget(), mRemoteLoading);
    	
    	mTweets.clear();
    	clearImagesTweets();
    	
    	mType = PreferenceUtils.getTypeWidget(context);
    	mCurrentSearch = PreferenceUtils.getIdSearchWidget(context);
    	long column_id = PreferenceUtils.getWidgetColumn(context);

        try {
            column = new Entity("columns", column_id);
        } catch (CursorIndexOutOfBoundsException e) {
        } catch (Exception e) { }

        if (column != null) {

            ArrayList<Entity> tweetList = new ArrayList<Entity>();

            switch (column.getInt("type_id")) {
                case TweetTopicsUtils.COLUMN_TIMELINE:
                    //loadUser(columns.get(0).getEntity("user_id").getId());
                    tweetList.addAll(DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + TweetTopicsUtils.TWEET_TYPE_TIMELINE + " and user_tt_id=" + column.getEntity("user_id").getId(), "date desc"));

                    for (int i=0; i<tweetList.size();i++) {
                        mTweets.add(new InfoTweet(tweetList.get(i)));
                    }
                    break;
                case TweetTopicsUtils.COLUMN_MENTIONS:
                    //loadUser(columns.get(0).getEntity("user_id").getId());
                    tweetList.addAll(DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + TweetTopicsUtils.TWEET_TYPE_MENTIONS + " and user_tt_id=" + column.getEntity("user_id").getId(), "date desc"));

                    for (int i=0; i<tweetList.size();i++) {
                        mTweets.add(new InfoTweet(tweetList.get(i)));
                    }
                    break;
                case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                    //loadUser(columns.get(0).getEntity("user_id").getId());
                    tweetList.addAll(DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " and user_tt_id=" + column.getEntity("user_id").getId(), "date desc"));

                    for (int i=0; i<tweetList.size();i++) {
                        mTweets.add(new InfoTweet(tweetList.get(i)));
                    }
                    break;
                case TweetTopicsUtils.COLUMN_SEARCH:
                    try {
                        EntitySearch entitySearch = new EntitySearch(column.getLong("search_id"));
                        QueryResult result = twitter.search(entitySearch.getQuery(context));
                        ArrayList<Status> tweets = (ArrayList<Status>)result.getTweets();

                        for (int i=0; i<tweets.size(); i++) {
                            mTweets.add(new InfoTweet(tweets.get(i)));
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_OTHERS:
                    // TODO retweet
                    try {
                        ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).getRetweetsOfMe();

                        for (int i=0; i<statii.size(); i++) {
                            mTweets.add(new InfoTweet(statii.get(i)));
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TweetTopicsUtils.COLUMN_RETWEETS_BY_YOU:
                    // TODO retweet
//                    try {
//                        ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).getRetweetedByMe();
//
//                        for (int i=0; i<statii.size(); i++) {
//                            mTweets.add(new InfoTweet(statii.get(i)));
//                        }
//                    } catch (TwitterException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    break;
                case TweetTopicsUtils.COLUMN_FAVORITES:
                    try {
                        ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).getFavorites();

                        for (int i=0; i<statii.size(); i++) {
                            mTweets.add(new InfoTweet(statii.get(i)));
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TweetTopicsUtils.COLUMN_FOLLOWERS:
                    try {
                        IDs followers_ids = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).getFollowersIDs(column.getEntity("user_id").getString("name"), -1);

                        ResponseList<User> users = null;

                        if (followers_ids.getIDs().length <= 100) {
                            users = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(followers_ids.getIDs());
                        } else {
                            int hundred_count = followers_ids.getIDs().length / 100;

                            for (int i=0; i < hundred_count; i++) {
                                if (users == null)
                                    users = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(), i * 100, (i + 1) * 100 - 1));
                                else
                                    users.addAll(ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(),i*100,(i+1)*100-1)));
                            }

                            if (followers_ids.getIDs().length % 100 > 0)
                                users.addAll(ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(),hundred_count*100 + 1,followers_ids.getIDs().length-1)));
                        }

                        for (User user : users) {
                            InfoTweet row = new InfoTweet(user);
                            mTweets.add(row);
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TweetTopicsUtils.COLUMN_FOLLOWINGS:
                    try {
                        IDs friends_ids = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).getFriendsIDs(column.getEntity("user_id").getString("name"), -1);

                        ResponseList<User> users = null;

                        if (friends_ids.getIDs().length <= 100) {
                            users = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(friends_ids.getIDs());
                        } else {
                            int hundred_count = friends_ids.getIDs().length / 100;

                            for (int i=0; i < hundred_count; i++) {
                                if (users == null)
                                    users = ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(), i * 100, (i + 1) * 100 - 1));
                                else
                                    users.addAll(ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),i*100,(i+1)*100-1)));
                            }

                            if (friends_ids.getIDs().length % 100 > 0)
                                users.addAll(ConnectionManager.getInstance().getTwitter(column.getEntity("user_id").getId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),hundred_count*100 + 1,friends_ids.getIDs().length-1)));
                        }

                        for (User user : users) {
                            InfoTweet row = new InfoTweet(user);
                            mTweets.add(row);
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TweetTopicsUtils.COLUMN_LIST_USER:
                    try {
                        Entity user_list_entity = new Entity("user_lists", Long.parseLong(column.getValue("userlist_id").toString()));

                        ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter(user_list_entity.getLong("user_id")).getUserListStatuses(user_list_entity.getInt("userlist_id"), new Paging(1));
                        for (int i=0; i<statii.size(); i++) {
                            mTweets.add(new InfoTweet(statii.get(i)));
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    	
    	blocked = false;

    }
        
    public void onHandleAction(Context context, Uri data) {
    	    	
    	if (!blocked) {
    		Log.d(Utils.TAG_WIDGET, "onHandleAction");
	    	if (data.equals(Uri.parse(Utils.URI_SCHEME + "://command/refresh"))) {
	    		Log.d(Utils.TAG_WIDGET, "update");
	    		update(context);
	    	}
	    	
	    	if (data.equals(Uri.parse(Utils.URI_SCHEME + "://command/prev"))) {
	    		Log.d(Utils.TAG_WIDGET, "prev");
	    		prev();
	    	}
	    	
	    	if (data.equals(Uri.parse(Utils.URI_SCHEME + "://command/next"))) {
	    		Log.d(Utils.TAG_WIDGET, "next");
	    		next();
	    	}
	    	
	    	if (data.equals(Uri.parse(Utils.URI_SCHEME + "://command/app"))) {
//				Intent i = new Intent(context, TweetTopicsActivity.class);
//				i.setAction(Intent.ACTION_VIEW);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//              i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_POSITION, column.getLong("position"));
//              i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_TWEET_ID, mTweets.get(mCurrentTweet).getId());
                Intent i = new Intent(context, TweetActivity.class);
                i.setAction(Intent.ACTION_VIEW);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TweetActivity.KEY_EXTRAS_TWEET, mTweets.get(mCurrentTweet));
                i.putExtra(Utils.KEY_ACTIVITY_ANIMATION, Utils.ACTIVITY_ANIMATION_RIGHT);
                i.putExtra(Utils.KEY_EXTRAS_INFO, bundle);
				context.startActivity(i);
	    	}
	    	
	    	refreshHandler();    	
	    	getManager().updateAppWidget(getWidget(), getRemoteView(context));
    	} else {
        	Log.d(Utils.TAG_WIDGET, "Botones bloqueados");
    	}
    }
    
    private void refreshHandler() {
    	handler.removeCallbacks(runnable);
    	handler.postDelayed(runnable, UPDATE_WIDGET);
    }

	public void loadUser(long id) {
		twitter = ConnectionManager.getInstance().getTwitter(id);
	}
    
    private void prev() {
    	if (mCurrentTweet > 0) {
			mCurrentTweet--;
		} else {
			mCurrentTweet = MAX_TWEET-1;
		}
    }
    
    private void next() {
    	if (mCurrentTweet < MAX_TWEET-1) {
			mCurrentTweet++;
		} else {
			mCurrentTweet = 0;
		}
    }
    
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {

    	public void run() {

    		try {
    			next();
    			getManager().updateAppWidget(getWidget(), getRemoteView(lastContext));
    			refreshHandler();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}

    };

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
		DataFramework.getInstance().close();
	}

}