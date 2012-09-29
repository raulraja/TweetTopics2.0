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