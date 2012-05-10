package com.javielinux.twitter;

import android.app.Application;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.TweetTopicsCore;
import error_reporter.ErrorReporter;
import interfaces.FinishTwitterDownload;
import task.TwitterUserAsyncTask;
import task.TwitterUserAsyncTask.TwitterUserAsyncTaskResponder;
import task.TwitterUserAsyncTask.TwitterUserResult;

public class TwitterApplication extends Application {
    private ErrorReporter mErrs;

/*
	public boolean isDev() {
		return getApplicationContext().getPackageName().equals("com.javielinux.tweettopics");
	}
	
	public boolean isLite() {
		if (getApplicationContext().getPackageName().equals("com.javielinux.tweettopics.lite")) {
			return true;
		} else {
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
			return preference.getBoolean("prf_force_lite", false);
		}
	}
	*/
	/*
	 * 
	 * Llamadas a Twitter en background
	 *
	 */
	
	private AsyncTask<Integer, Void, TwitterUserAsyncTask.TwitterUserResult> twitterUserTask = null;  
	private FinishTwitterDownload mFinishTwitterDownload = null; 
	
	public void setOnFinishTwitterDownload(FinishTwitterDownload f) {
		mFinishTwitterDownload = f;
	}
	
	public boolean isReloadUserTwitter() {
		if (twitterUserTask != null) return true;
		return false;
	}
	
	public void reloadUserTwitter() {
		firstLoad();
	}

	
	public int getLoad() {
		return mLoad;
	}
	
	private int mCurrentColumn = 0;
	private int mLoad = 1;
	
	public void firstLoad() {
		mLoad = 1;
		mCurrentColumn = TweetTopicsCore.mTypeLastColumn;
		
		if (twitterUserTask==null) {
			twitterUserTask = new TwitterUserAsyncTask(new TwitterUserAsyncTaskResponder() {

				@Override
				public void twitterUserLoading() {
				}

				@Override
				public void twitterUserCancelled() {
				}

				@Override
				public void twitterUserLoaded(TwitterUserResult searchResult) {
					twitterUserTask = null;
					if (mFinishTwitterDownload!=null) {
						mFinishTwitterDownload.OnFinishTwitterDownload(searchResult, mLoad);
						if (searchResult.infoTimeline != null) secondLoad();
					}
				}
				
			}, this.getBaseContext(), mCurrentColumn, false).execute();
		}
	}
	
	public void secondLoad() {
		mLoad = 2;
		if (twitterUserTask==null) {
			twitterUserTask = new TwitterUserAsyncTask(new TwitterUserAsyncTaskResponder() {

				@Override
				public void twitterUserLoading() {
				}

				@Override
				public void twitterUserCancelled() {
				}

				@Override
				public void twitterUserLoaded(TwitterUserResult searchResult) {
					twitterUserTask = null;
					if (mFinishTwitterDownload!=null) {
						mFinishTwitterDownload.OnFinishTwitterDownload(searchResult, mLoad);
					}
				}
				
			}, this.getBaseContext(), mCurrentColumn, true).execute();
		}
	}

}
