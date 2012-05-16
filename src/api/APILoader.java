package api;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import api.loaders.*;
import api.request.*;
import api.response.BaseResponse;
import api.response.ErrorResponse;

public class APILoader implements LoaderManager.LoaderCallbacks {

    private Context context;
    private APIDelegate delegate;
    private LoaderManager loaderManager;
    private int id;
    private BaseRequest baseRequest;

    public APILoader(Context context, LoaderManager loaderManager, APIDelegate apiDelegate, int id) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.delegate = apiDelegate;
        this.id = id;
    }
    
    public void execute(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
        if (loaderManager.getLoader(id)==null) {
            loaderManager.initLoader(id, null, this);
        } else {
            loaderManager.restartLoader(id, null, this);
        }
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case APITweetTopics.KEY_CHECK_CONVERSATION:
                return new CheckConversationLoader(context, (CheckConversationRequest)baseRequest);
            case APITweetTopics.KEY_CONVERSATION:
                return new ConversationLoader(context, (ConversationRequest)baseRequest);
            case APITweetTopics.KEY_DIRECT_MESSAGE:
                return new DirectMessageLoader(context, (DirectMessageRequest)baseRequest);
            case APITweetTopics.KEY_EXPORT_HTML:
                return new Export2HTMLLoader(context, (Export2HTMLRequest)baseRequest);
            case APITweetTopics.KEY_GET_CONVERSATION:
                return new GetConversationLoader(context, (GetConversationRequest)baseRequest);
            case APITweetTopics.KEY_IMAGE_UPLOAD:
                return new ImageUploadLoader(context, (ImageUploadRequest)baseRequest);
            case APITweetTopics.KEY_LIST_USER_TWITTER:
                return new ListUserTwitterLoader(context, (ListUserTwitterRequest)baseRequest);
            case APITweetTopics.KEY_LOAD_IMAGE:
                return new LoadImageLoader(context, (LoadImageRequest)baseRequest);
            case APITweetTopics.KEY_LOAD_TRANSLATE_TWEET:
                return new LoadTranslateTweetLoader(context, (LoadTranslateTweetRequest)baseRequest);
            case APITweetTopics.KEY_LOAD_TYPE_STATUS:
                return new LoadTypeStatusLoader(context, (LoadTypeStatusRequest)baseRequest);
            case APITweetTopics.KEY_LOAD_USER:
                return new LoadUserLoader(context, (LoadUserRequest)baseRequest);
            case APITweetTopics.KEY_PREPARING_LINK_FOR_SIDEBAR:
                return new PreparingLinkForSidebarLoader(context, (PreparingLinkForSidebarRequest)baseRequest);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        BaseResponse result = (BaseResponse) o;

        if (result instanceof ErrorResponse) {
            delegate.onError((ErrorResponse)result);
        } else {
            delegate.onResults(result);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
