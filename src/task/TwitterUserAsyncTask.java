package task;

import infos.InfoSaveTweets;
import android.content.Context;
import android.os.AsyncTask;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;
import com.javielinux.twitter.ConnectionManager;

import database.EntityTweetUser;

public class TwitterUserAsyncTask extends AsyncTask<Integer, Void, TwitterUserAsyncTask.TwitterUserResult> {

	public interface TwitterUserAsyncTaskResponder {
		public void twitterUserLoading();
		public void twitterUserCancelled();
		public void twitterUserLoaded(TwitterUserAsyncTask.TwitterUserResult searchResult);
	}
	
	public class TwitterUserResult {
		public long user_id = -1;
		public boolean loadOtherColumns = false;
		public InfoSaveTweets infoTimeline = null;
		public InfoSaveTweets infoMentions = null;
		public InfoSaveTweets infoDM = null;
		public TwitterUserResult() {
			super();
		}
	}

	private TwitterUserAsyncTaskResponder responder;
	private Context mContext;
	private TwitterUserResult mTwitterUserResult;
	private Entity mCurrentEntity;
	
	// si esta variable esta a false carga sólo la columna activa y a true
	// carga las otras menos la activa
	private boolean mLoadOtherColumns = false;
	private int mColumn = 0;

	public TwitterUserAsyncTask(TwitterUserAsyncTaskResponder responder, Context context, int column, boolean loadOtherColumns) {
		this.responder = responder;
		this.mContext = context;
		mColumn = column;
		mLoadOtherColumns = loadOtherColumns;
	}

	@Override
	protected TwitterUserAsyncTask.TwitterUserResult doInBackground(Integer... args) {
		
		ConnectionManager.getInstance().open(mContext);
		
		mTwitterUserResult = new TwitterUserResult();
		
		mCurrentEntity = DataFramework.getInstance().getTopEntity("users", "active=1", "");
		mTwitterUserResult.user_id = mCurrentEntity.getId();
		
		mTwitterUserResult.loadOtherColumns = mLoadOtherColumns;
	
		if (mColumn == TweetTopicsCore.TIMELINE) {
			if (mLoadOtherColumns) {
				saveMentions();
				saveDirects();
			} else {
				saveTimeline();
			}
		}
		if (mColumn == TweetTopicsCore.MENTIONS) {
			if (!mLoadOtherColumns) {
				saveMentions();
			}
		}
		if (mColumn == TweetTopicsCore.DIRECTMESSAGES) {
			if (!mLoadOtherColumns) {
				saveDirects();
			};
		}
		
		return mTwitterUserResult;

	}
	
	private void saveTimeline() {
		// timeline
		
		try {
			
			if (mCurrentEntity.getInt("no_save_timeline")!=1) {
				
				EntityTweetUser etu = new EntityTweetUser(mTwitterUserResult.user_id, TweetTopicsCore.TIMELINE);
				mTwitterUserResult.infoTimeline = etu.saveTweets(mContext, ConnectionManager.getInstance().getTwitter(), false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (mTwitterUserResult.infoTimeline==null) {
				InfoSaveTweets info = new InfoSaveTweets();
				info.setError(Utils.UNKNOWN_ERROR);
				mTwitterUserResult.infoTimeline = info;
			} else {
				mTwitterUserResult.infoTimeline.setError(Utils.UNKNOWN_ERROR);
			}
		}
			
	}
	
	private void saveMentions() {
		// mentions
		try {
			EntityTweetUser etu = new EntityTweetUser(mTwitterUserResult.user_id, TweetTopicsCore.MENTIONS);
			mTwitterUserResult.infoMentions = etu.saveTweets(mContext, ConnectionManager.getInstance().getTwitter(), false);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (mTwitterUserResult.infoMentions==null) {
				InfoSaveTweets info = new InfoSaveTweets();
				info.setError(Utils.UNKNOWN_ERROR);
				mTwitterUserResult.infoMentions = info;
			} else {
				mTwitterUserResult.infoMentions.setError(Utils.UNKNOWN_ERROR);
			}
		}
	}
	
	private void saveDirects() {
		// directos
		
		try {
			
			EntityTweetUser etu = new EntityTweetUser(mTwitterUserResult.user_id, TweetTopicsCore.DIRECTMESSAGES);
			mTwitterUserResult.infoDM = etu.saveTweets(mContext, ConnectionManager.getInstance().getTwitter(), false);
			
			// enviados directos
			EntityTweetUser etu_send = new EntityTweetUser(mTwitterUserResult.user_id, TweetTopicsCore.SENT_DIRECTMESSAGES);
			mTwitterUserResult.infoDM = etu_send.saveTweets(mContext, ConnectionManager.getInstance().getTwitter(), false);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (mTwitterUserResult.infoDM==null) {
				InfoSaveTweets info = new InfoSaveTweets();
				info.setError(Utils.UNKNOWN_ERROR);
				mTwitterUserResult.infoDM = info;
			} else {
				mTwitterUserResult.infoDM.setError(Utils.UNKNOWN_ERROR);
			}
		}
		
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.twitterUserLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.twitterUserCancelled();
	}

	@Override
	protected void onPostExecute(TwitterUserAsyncTask.TwitterUserResult searchResult) {
		super.onPostExecute(searchResult);
		responder.twitterUserLoaded(searchResult);
	}

}
