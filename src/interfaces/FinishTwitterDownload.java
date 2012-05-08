package interfaces;

import task.TwitterUserAsyncTask.TwitterUserResult;

public interface FinishTwitterDownload {
    public abstract void OnFinishTwitterDownload(TwitterUserResult searchResult, int witch);
}