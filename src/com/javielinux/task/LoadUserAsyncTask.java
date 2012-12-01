package com.javielinux.task;

import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.infos.InfoUsers;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;
import twitter4j.User;

public class LoadUserAsyncTask extends AsyncTask<String, Void, InfoUsers> {

	public interface LoadUserAsyncAsyncTaskResponder {
		public void userLoading();
		public void userCancelled();
		public void userLoaded(InfoUsers iu);
	}

	private LoadUserAsyncAsyncTaskResponder responder;
	private Context mContext;

	public LoadUserAsyncTask(Context cnt, LoadUserAsyncAsyncTaskResponder responder) {
		mContext = cnt;
		this.responder = responder;
	}

	@Override
	protected InfoUsers doInBackground(String... args) {

		try {

			ConnectionManager.getInstance().open(mContext);
			
			User u = ConnectionManager.getInstance().getUserForSearchesTwitter().showUser(args[0]);

			return new InfoUsers(u);
		} catch (TwitterException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.userLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.userCancelled();
	}

	@Override
	protected void onPostExecute(InfoUsers iu) {
		super.onPostExecute(iu);
		responder.userLoaded(iu);
	}

}
