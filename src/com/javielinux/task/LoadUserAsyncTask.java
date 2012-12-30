/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
