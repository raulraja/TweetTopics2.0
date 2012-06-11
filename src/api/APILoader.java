package api;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
        try {
            if (loaderManager.getLoader(id)==null) {
                loaderManager.initLoader(id, null, this);
            } else {
                loaderManager.restartLoader(id, null, this);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        AsynchronousLoader<BaseResponse> loader = null;
        switch (i) {
            case APITweetTopics.KEY_CHECK_CONVERSATION:
                loader = new CheckConversationLoader(context, (CheckConversationRequest)baseRequest);
                break;
            case APITweetTopics.KEY_CONVERSATION:
                loader = new ConversationLoader(context, (ConversationRequest)baseRequest);
                break;
            case APITweetTopics.KEY_DIRECT_MESSAGE:
                loader = new DirectMessageLoader(context, (DirectMessageRequest)baseRequest);
                break;
            case APITweetTopics.KEY_EXPORT_HTML:
                loader = new Export2HTMLLoader(context, (Export2HTMLRequest)baseRequest);
                break;
            case APITweetTopics.KEY_GET_CONVERSATION:
                loader = new GetConversationLoader(context, (GetConversationRequest)baseRequest);
                break;
            case APITweetTopics.KEY_GET_USER_LIST:
                loader = new GetUserListLoader(context, (GetUserListRequest)baseRequest);
                break;
            case APITweetTopics.KEY_IMAGE_UPLOAD:
                loader = new ImageUploadLoader(context, (ImageUploadRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LIST_USER_TWITTER:
                loader = new ListUserTwitterLoader(context, (ListUserTwitterRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_IMAGE:
                loader = new LoadImageLoader(context, (LoadImageRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_IMAGE_AUTO_COMPLETE:
                loader = new LoadImageAutoCompleteLoader(context, (LoadImageAutoCompleteRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_IMAGE_WIDGET:
                loader = new LoadImageWidgetLoader(context, (LoadImageWidgetRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_LINK:
                loader = new LoadLinkLoader(context, (LoadLinkRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_MORE:
                loader = new LoadMoreLoader(context, (LoadMoreRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_MORE_TWEET_DOWNLOADER:
                loader = new LoadMoreTweetDownLoader(context, (LoadMoreTweetDownRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_TRANSLATE_TWEET:
                loader = new LoadTranslateTweetLoader(context, (LoadTranslateTweetRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_TYPE_STATUS:
                loader = new LoadTypeStatusLoader(context, (LoadTypeStatusRequest)baseRequest);
                break;
            case APITweetTopics.KEY_LOAD_USER:
                loader = new LoadUserLoader(context, (LoadUserRequest)baseRequest);
                break;
            case APITweetTopics.KEY_PREPARING_LINK_FOR_SIDEBAR:
                loader = new PreparingLinkForSidebarLoader(context, (PreparingLinkForSidebarRequest)baseRequest);
                break;
            case APITweetTopics.KEY_PROFILE_IMAGE:
                loader = new ProfileImageLoader(context, (ProfileImageRequest)baseRequest);
                break;
            case APITweetTopics.KEY_RETWEET_STATUS:
                loader = new RetweetStatusLoader(context, (RetweetStatusRequest)baseRequest);
                break;
            case APITweetTopics.KEY_SAVE_FIRST_TWEETS:
                loader = new SaveFirstTweetsLoader(context, (SaveFirstTweetsRequest)baseRequest);
                break;
            case APITweetTopics.KEY_SEARCH:
                loader = new SearchLoader(context, (SearchRequest)baseRequest);
                break;
            case APITweetTopics.KEY_STATUS_RETWEETEERS:
                loader = new StatusRetweetersLoader(context, (StatusRetweetersRequest)baseRequest);
                break;
            case APITweetTopics.KEY_TRENDS:
                loader = new TrendsLoader(context, (TrendsRequest)baseRequest);
                break;
            case APITweetTopics.KEY_TRENDS_LOCATION:
                loader = new TrendsLocationLoader(context, (TrendsLocationRequest)baseRequest);
                break;
            case APITweetTopics.KEY_TWITTER_USER:
                loader = new TwitterUserLoader(context, (TwitterUserRequest)baseRequest);
                break;
            case APITweetTopics.KEY_UPLOAD_STATUS:
                loader = new UploadStatusLoader(context, (UploadStatusRequest)baseRequest);
                break;
            case APITweetTopics.KEY_UPLOAD_TWIT_LONGER:
                loader = new UploadTwitlongerLoader(context, (UploadTwitlongerRequest)baseRequest);
                break;
            case APITweetTopics.KEY_USER_LISTS:
                loader = new UserListsLoader(context, (UserListsRequest)baseRequest);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        BaseResponse result = (BaseResponse) o;

        if (result.isError()) {
            delegate.onError((ErrorResponse)result);
        } else {
            delegate.onResults(result);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
