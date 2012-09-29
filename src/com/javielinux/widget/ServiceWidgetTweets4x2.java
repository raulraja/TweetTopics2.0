package com.javielinux.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.LinksUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ServiceWidgetTweets4x2 extends Service {
	
	public  Twitter twitter;
	
	public static int TIMELINE = 0;
	public static int MENTIONS = 1;
	public static int SEARCH = 2;

	private  int mType = TIMELINE;
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	public  long UPDATE_WIDGET = 15000;
	
	public boolean blocked = false;
	
	public Context lastContext;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {  
        	if (!Utils.isLite(context) ) {
	        	try {
	        		if (intent.getExtras().containsKey("button")) {
	        			onHandleAction(context, (Uri)intent.getExtras().get("button"));
	        			return;
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
	
    private int MAX_TWEET = 10;
    
	private AppWidgetManager manager;
	private ComponentName thisWidget;
	
	private List<InfoTweet> mTweets = new ArrayList<InfoTweet>();
	
	private int mCurrentTweet = 0;
	private long mCurrentSearch = 1;
		
	private AsyncTask<String, Void,LoadImageWidgetAsyncTask.ImageData> latestLoadTask;
	
	private  ArrayList<InfoImagesTweet> imagesTweet = new ArrayList<InfoImagesTweet>();

	private  InfoImagesTweet getImagesTweets(int pos) {
		for (int i=0; i<imagesTweet.size(); i++) {
			if (imagesTweet.get(i).getPosition()==pos)
				return imagesTweet.get(i);
		}
		return null;
	}
	
	public  void clearImagesTweets() {
		imagesTweet.clear();
	}
	
	public AppWidgetManager getManager() {
		if (manager==null) manager = AppWidgetManager.getInstance(this);
		return manager;
	}
	
	public ComponentName getWidget() {
		if (thisWidget==null) thisWidget = new ComponentName(this, WidgetTweets4x2.class);
		return thisWidget;
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
    	
        twitter = ConnectionManager.getInstance().getAnonymousTwitter();
    	
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
    		
    		List<Entity> e = DataFramework.getInstance().getEntityList("users", "active=1");
	    	if (e.size()==1) {
	    		
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
	    		
	    		if (mType==TIMELINE || mType==MENTIONS) {
	    			List<Entity> e = DataFramework.getInstance().getEntityList("users", "active=1");
	    	    	if (e.size()==1) {
	    	    		String title = "";
	    	    		if (mType==TIMELINE) {
	    	    			title = context.getString(R.string.timeline) + ": " + e.get(0).getString("name");
	    	    		}
	    	    		if (mType==MENTIONS) {
	    	    			title = context.getString(R.string.mentions) + ": " + e.get(0).getString("name");
	    	    		}
	    	    		mRemoteView.setTextViewText(R.id.w_title, title);
	    	    		Bitmap bmp = ImageUtils.getBitmapAvatar(e.get(0).getId(), Utils.AVATAR_LARGE);
	    	    		if (bmp==null) {
	    	    			mRemoteView.setImageViewResource(R.id.w_icon, R.drawable.avatar);
	    	    		} else {
	    	    			mRemoteView.setImageViewBitmap(R.id.w_icon, bmp);
	    	    		}
	    	    			
	    	    	}
	    		} else {
		    		if (mCurrentSearch>0) {
			    		Entity ent = new Entity ("search", mCurrentSearch);
			    		
			    		Bitmap icon = ((BitmapDrawable)Utils.getDrawable(context, ent.getString("icon_small"))).getBitmap();
			    		mRemoteView.setImageViewBitmap(R.id.w_icon, icon);
			    		
			    		mRemoteView.setTextViewText(R.id.w_title, ent.getString("name"));
		    		}
	    		}
	    		
	    		mRemoteView.setTextViewText(R.id.w_tweet_user_name_text, mTweets.get(mCurrentTweet).getUsername());
	    		mRemoteView.setTextViewText(R.id.w_tweet_text, Html.fromHtml(Utils.toHTML(context, mTweets.get(mCurrentTweet).getText())));
				mRemoteView.setTextViewText(R.id.w_tweet_date,  Utils.timeFromTweet(this, mTweets.get(mCurrentTweet).getDate()) );
				
				ArrayList<String> links = LinksUtils.pullLinksHTTP(mTweets.get(mCurrentTweet).getText());
							
				for (int i=0; i<5; i++) {
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
				}
				
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
				
			} catch (Exception e) {
				e.printStackTrace();
				mRemoteView.setTextViewText(R.id.w_tweet_text, "Error");
			}
	    	
	    	return mRemoteView;
	    	
    	 }

    }
    
     private PendingIntent makeControlPendingIntent(Context context, String command) {
    	Intent active = new Intent();
    	active.setAction(Utils.ACTION_WIDGET_CONTROL);
    	active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);    	
    	Uri data = Uri.parse(Utils.URI_SCHEME + "://command/"+command);
    	active.setData(data);
    	return(PendingIntent.getBroadcast(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }
    
     public void update(Context cnt) {
    	   	
    	blocked = true;
    	
    	mCurrentTweet = 0;
    	
    	RemoteViews mRemoteLoading = new RemoteViews(cnt.getPackageName(), R.layout.widget_loading);
    	getManager().updateAppWidget(getWidget(), mRemoteLoading);
    	
    	mTweets.clear();
    	clearImagesTweets();
    	
    	mType = PreferenceUtils.getTypeWidget(cnt);
    	mCurrentSearch = PreferenceUtils.getIdSearchWidget(cnt);
    	
    	if (mType==TIMELINE || mType==MENTIONS) {
    		List<Entity> e = DataFramework.getInstance().getEntityList("users", "active=1");
	    	if (e.size()==1) {
	    		loadUser(e.get(0).getId());
	    		ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "type_id="+mType+" and user_tt_id="+e.get(0).getId(), "date desc");
	    		for (int i=0; i<ents.size();i++) {
	    			mTweets.add(new InfoTweet(ents.get(i)));
	    		}
	    	}
    	}    	
    	
    	if (mType==SEARCH) {
    		
    		if (mCurrentSearch>0) {
    				    	
		    	try {
			    	Log.d(Utils.TAG_WIDGET, "Cargando busqueda con id " + mCurrentSearch);	    	
			    	EntitySearch mEntitySearch = new EntitySearch(mCurrentSearch);
			    	if (mEntitySearch.getInt("notifications")==1) {
			    		ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets", "favorite=0 and search_id="+mEntitySearch.getId(), "date desc");
			    		for (int i=0; i<ents.size();i++) {
			    			mTweets.add(new InfoTweet(ents.get(i)));
			    		}
			    	} else {
			    		QueryResult result = twitter.search(mEntitySearch.getQuery(cnt));
						ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
						for (int i=0; i<tweets.size();i++) {
			    			mTweets.add(new InfoTweet(tweets.get(i)));
			    		}
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
				Intent i = new Intent(context, TweetTopicsActivity.class);
				i.setAction(Intent.ACTION_VIEW);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    	if (mCurrentTweet>0) {
			mCurrentTweet--;
		} else {
			mCurrentTweet = MAX_TWEET-1;
		}
    }
    
    private void next() {
    	if (mCurrentTweet<MAX_TWEET-1) {
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

    		//handler.postDelayed(this, UPDATE_WIDGET);
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
