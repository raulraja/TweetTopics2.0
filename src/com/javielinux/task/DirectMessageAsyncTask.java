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

import android.os.AsyncTask;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class DirectMessageAsyncTask extends AsyncTask<String, Void, Boolean> {

	public interface DirectMessageAsyncTaskResponder {
		public void directMessageStatusLoading();
		public void directMessageStatusCancelled();
		public void directMessageStatusLoaded(boolean error);
	}

	private DirectMessageAsyncTaskResponder responder;
	private Twitter twitter;
	private int mModeTweetLonger = NewStatusActivity.MODE_TL_NONE;

	public DirectMessageAsyncTask(DirectMessageAsyncTaskResponder responder, Twitter twitter, int modeTweetLonger) {
		this.responder = responder;
		this.twitter = twitter;
		mModeTweetLonger = modeTweetLonger;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		try {
			String text = args[0];
			String user = args[1];
			if (mModeTweetLonger == NewStatusActivity.MODE_TL_NONE) {
				twitter.sendDirectMessage(user, text);
			} else {
				ArrayList<String> ar = Utils.getDivide140(text, "");
				for (String t : ar) {
					twitter.sendDirectMessage(user, t);	
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.directMessageStatusLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.directMessageStatusCancelled();
	}

	@Override
	protected void onPostExecute(Boolean error) {
		super.onPostExecute(error);
		responder.directMessageStatusLoaded(error);
	}

}
