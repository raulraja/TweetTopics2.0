package task;


import infos.InfoSaveTweets;

import java.util.ArrayList;

import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Tweet;
import twitter4j.TwitterException;
import adapters.ResponseListAdapter;
import adapters.RowResponseList;
import android.content.Context;
import android.os.AsyncTask;

import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;
import com.javielinux.twitter.ConnectionManager;

import database.EntitySearch;

public class SearchAsyncTask extends AsyncTask<EntitySearch, Void, SearchAsyncTask.SearchResult> {
	
	public interface SearchAsyncTaskResponder {
		public void searchLoading();
		public void searchCancelled();
		public void searchLoaded(SearchAsyncTask.SearchResult searchResult);
	}
	
	public class SearchResult {
		public ResponseListAdapter response;
		public InfoSaveTweets info = null;
		public SearchResult() {
			super();
		}
	}

	private SearchAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;
	private Context mContext;
	
	public SearchAsyncTask(SearchAsyncTaskResponder responder, Context context) {
		this.responder = responder;
		this.mTweetTopicsCore = (TweetTopicsCore)responder;
		this.mContext = context;
	}

	@Override
	protected SearchAsyncTask.SearchResult doInBackground(EntitySearch... args) {
		SearchResult sr = new SearchResult();
    	try {
    		ConnectionManager.getInstance().open(mContext);
    		EntitySearch mEntitySearch = args[0];
    		if (mEntitySearch.getInt("notifications")==1) {
    			mTweetTopicsCore.setTypeList(TweetTopicsCore.TYPE_LIST_SEARCH_NOTIFICATIONS);
    			sr.info = mEntitySearch.saveTweets(mContext, ConnectionManager.getInstance().getTwitter(), false);
    			/*
    	    	ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets", "favorite=0 and search_id="+mEntitySearch.getId(), "date desc");
    	    	ArrayList<RowResponseList> response = new ArrayList<RowResponseList>(); 
    	    	for (int i=0; i<ents.size(); i++) {
    	    		RowResponseList r = new RowResponseList(ents.get(i));
    	    		response.add(r);
    	    	}
    	    	sr.response = new ResponseListAdapter(mTweetTopics, response, mEntitySearch.getLong("last_tweet_id"));
    	    	*/
    		} else {
    			mTweetTopicsCore.setTypeList(TweetTopicsCore.TYPE_LIST_SEARCH);
    			InfoSaveTweets info = new InfoSaveTweets();
				sr.info = info;
				ArrayList<RowResponseList> response = new ArrayList<RowResponseList>(); 
				if (mEntitySearch.isUser()) { // la búsqueda es de un usuario, así que buscamos en twitter directamente
					ResponseList<twitter4j.Status> statuses = ConnectionManager.getInstance().getTwitter().getUserTimeline(mEntitySearch.getString("from_user"));
					for (twitter4j.Status st : statuses) {
						RowResponseList r = new RowResponseList(st);
	    	    		response.add(r);
					}
				} else {
	    			QueryResult result = ConnectionManager.getInstance().getTwitter().search(mEntitySearch.getQuery(mContext));
	    			ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
	    	    	for (int i=0; i<tweets.size(); i++) {
	    	    		RowResponseList r = new RowResponseList(tweets.get(i));
	    	    		response.add(r);
	    	    	}
				}
    	    	sr.response = new ResponseListAdapter(mContext, mTweetTopicsCore, response, mEntitySearch.getLong("last_tweet_id"));
    		}
    	} catch (TwitterException e) {
    		e.printStackTrace();
			if (sr.info==null) {
				InfoSaveTweets info = new InfoSaveTweets();
				sr.info = info;
			}
    		RateLimitStatus rate = e.getRateLimitStatus();
    		if (rate!=null) {
    			sr.info.setError(Utils.LIMIT_ERROR);
    			sr.info.setRate(rate);
    		} else {
    			sr.info.setError(Utils.UNKNOWN_ERROR);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
			if (sr.info==null) {
				InfoSaveTweets info = new InfoSaveTweets();
				info.setError(Utils.UNKNOWN_ERROR);
				sr.info = info;
			} else {
				sr.info.setError(Utils.UNKNOWN_ERROR);
			}
    	}
		return sr;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.searchLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.searchCancelled();
	}

	@Override
	protected void onPostExecute(SearchAsyncTask.SearchResult searchResult) {
		super.onPostExecute(searchResult);
		responder.searchLoaded(searchResult);
	}

}
