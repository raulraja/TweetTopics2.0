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
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class RetweetStatusAsyncTask extends AsyncTask<Long, Void, Boolean> {

    public interface RetweetStatusAsyncTaskResponder {
        public void retweetStatusLoading();
        public void retweetStatusCancelled();
        public void retweetStatusLoaded(boolean error);
    }

    private RetweetStatusAsyncTaskResponder responder;
    private Twitter twitter;

    public RetweetStatusAsyncTask(RetweetStatusAsyncTaskResponder responder, Twitter twitter) {
        this.responder = responder;
        this.twitter = twitter;
    }

    @Override
    protected Boolean doInBackground(Long... args) {
        try {
            twitter.retweetStatus(args[0]);
        } catch (TwitterException e) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        responder.retweetStatusLoading();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        responder.retweetStatusCancelled();
    }

    @Override
    protected void onPostExecute(Boolean error) {
        super.onPostExecute(error);
        responder.retweetStatusLoaded(error);
    }

}