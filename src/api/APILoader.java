package api;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import api.api.loaders.CheckConversationLoader;

public class APILoader implements LoaderManager.LoaderCallbacks {

    private Context context;
    private APIDelegate delegate;
    private LoaderManager loaderManager;
    private int id;

    public APILoader(Context context, LoaderManager loaderManager, APIDelegate apiDelegate, int id) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.delegate = apiDelegate;
        this.id = id;
    }
    
    public void execute(Bundle params) {
        if (loaderManager.getLoader(id)==null) {
            loaderManager.initLoader(id, params, this);
        } else {
            loaderManager.restartLoader(id, params, this);
        }
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case APITweetTopics.KEY_CHECK_CONVERSATION:
                return new CheckConversationLoader(context, bundle);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        APIResult result = (APIResult) o;

        if (result.hasError()) {
            delegate.onError(result);
        } else {
            delegate.onResults(result);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
