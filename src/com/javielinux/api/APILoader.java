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

package com.javielinux.api;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import com.javielinux.api.loaders.*;
import com.javielinux.api.request.*;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.utils.Utils;

import java.util.concurrent.RejectedExecutionException;

public class APILoader implements LoaderManager.LoaderCallbacks {

    private Context context;
    private APIDelegate delegate;
    private LoaderManager loaderManager;
    private BaseRequest baseRequest;

    public APILoader(Context context, LoaderManager loaderManager, APIDelegate apiDelegate) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.delegate = apiDelegate;
    }
    
    public void execute(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
        try {
            if (loaderManager.getLoader(baseRequest.hashCode())==null) {
                Log.d(Utils.TAG, "initLoader: " + baseRequest.getClass().getName() + ": " + baseRequest.hashCode());
                loaderManager.initLoader(baseRequest.hashCode(), null, this);
            } else {
                Log.d(Utils.TAG, "restartLoader: " + baseRequest.getClass().getName() + ": " + baseRequest.hashCode());
                loaderManager.restartLoader(baseRequest.hashCode(), null, this);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        AsynchronousLoader<BaseResponse> loader = null;

        if (baseRequest instanceof CheckConversationRequest) {
            loader = new CheckConversationLoader(context, (CheckConversationRequest)baseRequest);
        } else if (baseRequest instanceof ConversationRequest) {
            loader = new ConversationLoader(context, (ConversationRequest)baseRequest);
        } else if (baseRequest instanceof DirectMessageRequest) {
            loader = new DirectMessageLoader(context, (DirectMessageRequest)baseRequest);
        } else if (baseRequest instanceof Export2HTMLRequest) {
            loader = new Export2HTMLLoader(context, (Export2HTMLRequest)baseRequest);
        } else if (baseRequest instanceof GetConversationRequest) {
            loader = new GetConversationLoader(context, (GetConversationRequest)baseRequest);
        } else if (baseRequest instanceof GetUserListRequest) {
            loader = new GetUserListLoader(context, (GetUserListRequest)baseRequest);
        } else if (baseRequest instanceof ImageUploadRequest) {
            loader = new ImageUploadLoader(context, (ImageUploadRequest)baseRequest);
        } else if (baseRequest instanceof ListUserTwitterRequest) {
            loader = new ListUserTwitterLoader(context, (ListUserTwitterRequest)baseRequest);
        } else if (baseRequest instanceof LoadImageAutoCompleteRequest) {
            loader = new LoadImageAutoCompleteLoader(context, (LoadImageAutoCompleteRequest)baseRequest);
        } else if (baseRequest instanceof LoadImageWidgetRequest) {
            loader = new LoadImageWidgetLoader(context, (LoadImageWidgetRequest)baseRequest);
        } else if (baseRequest instanceof LoadLinkRequest) {
            loader = new LoadLinkLoader(context, (LoadLinkRequest)baseRequest);;
        } else if (baseRequest instanceof LoadMoreRequest) {
            loader = new LoadMoreLoader(context, (LoadMoreRequest)baseRequest);
        } else if (baseRequest instanceof LoadMoreTweetDownRequest) {
            loader = new LoadMoreTweetDownLoader(context, (LoadMoreTweetDownRequest)baseRequest);
        } else if (baseRequest instanceof LoadTranslateTweetRequest) {
            loader = new LoadTranslateTweetLoader(context, (LoadTranslateTweetRequest)baseRequest);
        } else if (baseRequest instanceof LoadTypeStatusRequest) {
            loader = new LoadTypeStatusLoader(context, (LoadTypeStatusRequest)baseRequest);
        } else if (baseRequest instanceof LoadUserRequest) {
            loader = new LoadUserLoader(context, (LoadUserRequest)baseRequest);
        } else if (baseRequest instanceof PreparingLinkForSidebarRequest) {
            loader = new PreparingLinkForSidebarLoader(context, (PreparingLinkForSidebarRequest)baseRequest);
        } else if (baseRequest instanceof ProfileImageRequest) {
            loader = new ProfileImageLoader(context, (ProfileImageRequest)baseRequest);
        } else if (baseRequest instanceof RetweetStatusRequest) {
            loader = new RetweetStatusLoader(context, (RetweetStatusRequest)baseRequest);
        } else if (baseRequest instanceof SaveFirstTweetsRequest) {
            loader = new SaveFirstTweetsLoader(context, (SaveFirstTweetsRequest)baseRequest);
        } else if (baseRequest instanceof SearchRequest) {
            loader = new SearchLoader(context, (SearchRequest)baseRequest);
        } else if (baseRequest instanceof StatusRetweetersRequest) {
            loader = new StatusRetweetersLoader(context, (StatusRetweetersRequest)baseRequest);
        } else if (baseRequest instanceof TrendsRequest) {
            loader = new TrendsLoader(context, (TrendsRequest)baseRequest);
        } else if (baseRequest instanceof TrendsLocationRequest) {
            loader = new TrendsLocationLoader(context, (TrendsLocationRequest)baseRequest);
        } else if (baseRequest instanceof TwitterUserRequest) {
            loader = new TwitterUserLoader(context, (TwitterUserRequest)baseRequest);
        } else if (baseRequest instanceof UploadStatusRequest) {
            loader = new UploadStatusLoader(context, (UploadStatusRequest)baseRequest);
        } else if (baseRequest instanceof UploadTwitlongerRequest) {
            loader = new UploadTwitlongerLoader(context, (UploadTwitlongerRequest)baseRequest);
        } else if (baseRequest instanceof UserListsRequest) {
            loader = new UserListsLoader(context, (UserListsRequest)baseRequest);
        } else if (baseRequest instanceof TwitterUserDBRequest) {
            loader = new TwitterUserDBLoader(context, (TwitterUserDBRequest)baseRequest);
        } else if (baseRequest instanceof CheckFriendlyUserRequest) {
            loader = new CheckFriendlyUserLoader(context, (CheckFriendlyUserRequest)baseRequest);
        }  else if (baseRequest instanceof ExecuteActionUserRequest) {
            loader = new ExecuteActionUserLoader(context, (ExecuteActionUserRequest)baseRequest);
        } else if (baseRequest instanceof GetGeolocationAddressRequest) {
            loader = new GetGeolocationAddressLoader(context, (GetGeolocationAddressRequest)baseRequest);
        } else if (baseRequest instanceof UserMentionsRequest) {
            loader = new UserMentionsLoader(context, (UserMentionsRequest)baseRequest);
        } else if (baseRequest instanceof SearchContentInDBRequest) {
            loader = new SearchContentInDBLoader(context, (SearchContentInDBRequest)baseRequest);
        } else if (baseRequest instanceof GetUserFriendshipMembersRequest) {
            loader = new GetUserFriendshipMembersLoader(context, (GetUserFriendshipMembersRequest)baseRequest);
        } else if (baseRequest instanceof CreateUserListsRequest) {
            loader = new CreateUserListsLoader(context, (CreateUserListsRequest)baseRequest);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        BaseResponse result = (BaseResponse) o;

        if (result.isError()) {
            delegate.onError((ErrorResponse)result);
        } else {
            Log.d(Utils.TAG, "onLoadFinished: " + o.getClass().getName());
            delegate.onResults(result);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}
}
