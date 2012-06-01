package api;


import android.content.Context;
import android.support.v4.app.LoaderManager;
import api.request.*;

public class APITweetTopics {
    
    public static final int KEY_CHECK_CONVERSATION = 0;
    public static final int KEY_CONVERSATION = 1;
    public static final int KEY_DIRECT_MESSAGE = 2;
    public static final int KEY_EXPORT_HTML = 3;
    public static final int KEY_GET_CONVERSATION = 4;
    public static final int KEY_IMAGE_UPLOAD = 5;
    public static final int KEY_LIST_USER_TWITTER = 6;
    public static final int KEY_LOAD_IMAGE = 7;
    public static final int KEY_LOAD_IMAGE_AUTO_COMPLETE = 8;
    public static final int KEY_LOAD_IMAGE_WIDGET = 9;
    public static final int KEY_LOAD_LINK = 10;
    public static final int KEY_LOAD_MORE = 11;
    public static final int KEY_LOAD_MORE_TWEET_DOWNLOADER = 12;
    public static final int KEY_GET_USER_LIST= 13;
    public static final int KEY_LOAD_TRANSLATE_TWEET = 16;
    public static final int KEY_LOAD_TYPE_STATUS = 17;
    public static final int KEY_LOAD_USER = 18;
    public static final int KEY_PREPARING_LINK_FOR_SIDEBAR = 19;
    public static final int KEY_PROFILE_IMAGE = 20;
    public static final int KEY_RETWEET_STATUS = 21;
    public static final int KEY_SAVE_FIRST_TWEETS = 22;
    public static final int KEY_SEARCH = 23;
    public static final int KEY_STATUS_RETWEETEERS = 24;
    public static final int KEY_TRENDS = 25;
    public static final int KEY_TRENDS_LOCATION = 26;
    public static final int KEY_TWITTER_USER = 27;
    public static final int KEY_UPLOAD_STATUS = 28;
    public static final int KEY_UPLOAD_TWIT_LONGER = 29;
    public static final int KEY_USER_LISTS = 30;


    public static void execute(Context context, LoaderManager loaderManager, APIDelegate delegate, BaseRequest request) {

        int key = 0;

        if (request instanceof CheckConversationRequest) {
            key = KEY_CHECK_CONVERSATION;
        } else if (request instanceof ConversationRequest) {
            key = KEY_CONVERSATION;
        } else if (request instanceof DirectMessageRequest) {
            key = KEY_DIRECT_MESSAGE;
        } else if (request instanceof Export2HTMLRequest) {
            key = KEY_EXPORT_HTML;
        } else if (request instanceof GetConversationRequest) {
            key = KEY_GET_CONVERSATION;
        } else if (request instanceof GetUserListRequest) {
            key = KEY_GET_USER_LIST;
        } else if (request instanceof ImageUploadRequest) {
            key = KEY_IMAGE_UPLOAD;
        } else if (request instanceof ListUserTwitterRequest) {
            key = KEY_LIST_USER_TWITTER;
        } else if (request instanceof LoadImageRequest) {
            key = KEY_LOAD_IMAGE;
        } else if (request instanceof LoadImageAutoCompleteRequest) {
            key = KEY_LOAD_IMAGE_AUTO_COMPLETE;
        } else if (request instanceof LoadImageWidgetRequest) {
            key = KEY_LOAD_IMAGE_WIDGET;
        } else if (request instanceof LoadLinkRequest) {
            key = KEY_LOAD_LINK;
        } else if (request instanceof LoadMoreRequest) {
            key = KEY_LOAD_MORE;
        } else if (request instanceof LoadMoreTweetDownRequest) {
            key = KEY_LOAD_MORE_TWEET_DOWNLOADER;
        } else if (request instanceof LoadTranslateTweetRequest) {
            key = KEY_LOAD_TRANSLATE_TWEET;
        } else if (request instanceof LoadTypeStatusRequest) {
            key = KEY_LOAD_TYPE_STATUS;
        } else if (request instanceof LoadUserRequest) {
            key = KEY_LOAD_USER;
        } else if (request instanceof PreparingLinkForSidebarRequest) {
            key = KEY_PREPARING_LINK_FOR_SIDEBAR;
        } else if (request instanceof ProfileImageRequest) {
            key = KEY_PROFILE_IMAGE;
        } else if (request instanceof RetweetStatusRequest) {
            key = KEY_RETWEET_STATUS;
        } else if (request instanceof SaveFirstTweetsRequest) {
            key = KEY_SAVE_FIRST_TWEETS;
        } else if (request instanceof SearchRequest) {
            key = KEY_SEARCH;
        } else if (request instanceof StatusRetweetersRequest) {
            key = KEY_STATUS_RETWEETEERS;
        } else if (request instanceof TrendsRequest) {
            key = KEY_TRENDS;
        } else if (request instanceof TrendsLocationRequest) {
            key = KEY_TRENDS_LOCATION;
        } else if (request instanceof TwitterUserRequest) {
            key = KEY_TWITTER_USER;
        } else if (request instanceof UploadStatusRequest) {
            key = KEY_UPLOAD_STATUS;
        } else if (request instanceof UploadTwitlongerRequest) {
            key = KEY_UPLOAD_TWIT_LONGER;
        } else if (request instanceof UserListsRequest) {
            key = KEY_USER_LISTS;
        }

        APILoader api = new APILoader(context, loaderManager, delegate, key);

        api.execute(request);

    }

}
