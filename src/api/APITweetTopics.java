package api;


import android.app.LoaderManager;
import android.content.Context;
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
    public static final int KEY_LOAD_TRANSLATE_TWEET = 16;
    public static final int KEY_LOAD_TYPE_STATUS = 17;
    public static final int KEY_LOAD_USER = 18;
    public static final int KEY_PREPARING_LINK_FOR_SIDEBAR = 19;
    public static final int KEY_PROFILE_IMAGE = 20;
    public static final int KEY_RETWEET_STATUS = 21;
    public static final int KEY_SAVE_FIRST_TWEETS = 22;
    public static final int KEY_STATUS_RETWEETEERS = 24;

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
        } else if (request instanceof ImageUploadRequest) {
            key = KEY_IMAGE_UPLOAD;
        } else if (request instanceof ListUserTwitterRequest) {
            key = KEY_LIST_USER_TWITTER;
        } else if (request instanceof LoadImageRequest) {
            key = KEY_LOAD_IMAGE;
        } else if (request instanceof LoadTranslateTweetRequest) {
            key = KEY_LOAD_TRANSLATE_TWEET;
        } else if (request instanceof LoadTypeStatusRequest) {
            key = KEY_LOAD_TYPE_STATUS;
        } else if (request instanceof LoadUserRequest) {
            key = KEY_LOAD_USER;
        } else if (request instanceof PreparingLinkForSidebarRequest) {
            key = KEY_PREPARING_LINK_FOR_SIDEBAR;
        }

        APILoader api = new APILoader(context, loaderManager, delegate, key);

        api.execute(request);

    }

    /*
    public static void checkConversation(Context context, LoaderManager loaderManager, APIDelegate delegate, CheckConversationRequest request) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_CHECK_CONVERSATION);

        api.execute(request);

    }


    public static void conversation(Context context, LoaderManager loaderManager, APIDelegate delegate, ConversationRequest request) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_CONVERSATION);

        api.execute(request);

    }

    public static void directMessage(Context context, LoaderManager loaderManager, APIDelegate delegate, int modeTweetLonger, String user, String text) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_DIRECT_MESSAGE);

        api.execute(new DirectMessageRequest(modeTweetLonger, user, text));

    }

    public static void export2HTML(Context context, LoaderManager loaderManager, APIDelegate delegate, ArrayList<InfoTweet> tweets) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_EXPORT_HTML);

        api.execute(new Export2HTMLRequest(tweets));

    }

    public static void getConversation(Context context, LoaderManager loaderManager, APIDelegate delegate, ArrayList<InfoTweet> tweets) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_EXPORT_HTML);

        api.execute(new Export2HTMLRequest(tweets));

    }

    public static void loadTranslateTweet(Context context, LoaderManager loaderManager, APIDelegate delegate, long id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_LOAD_TRANSLATE_TWEET);

        Bundle params=new Bundle();
        params.putLong("id", id);

        //api.execute(params);

    }

    public static void loadTypeStatus(Context context, LoaderManager loaderManager, APIDelegate delegate, int type, String user, int userlist_id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_LOAD_TYPE_STATUS);

        Bundle params=new Bundle();
        params.putInt("type", type);
        params.putString("user", user);
        params.putInt("userlist_id", userlist_id);

        //api.execute(params);

    }

    public static void loadTypeStatus(Context context, LoaderManager loaderManager, APIDelegate delegate, String user) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_LOAD_USER);

        Bundle params=new Bundle();
        params.putString("user", user);

        //api.execute(params);

    }

    public static void preparingLinkForSidebar(Context context, LoaderManager loaderManager, APIDelegate delegate, String link) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_PREPARING_LINK_FOR_SIDEBAR);

        Bundle params=new Bundle();
        params.putString("link", link);

        //api.execute(params);

    }

    public static void profileImage(Context context, LoaderManager loaderManager, APIDelegate delegate, int action, long user_id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_PROFILE_IMAGE);

        Bundle params=new Bundle();
        params.putInt("action", action);
        params.putLong("user_id", user_id);

        //api.execute(params);

    }

    public static void retweetStatus(Context context, LoaderManager loaderManager, APIDelegate delegate, long id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_RETWEET_STATUS);

        Bundle params=new Bundle();
        params.putLong("id", id);

        //api.execute(params);

    }

    public static void saveFirstTweets(Context context, LoaderManager loaderManager, APIDelegate delegate, long id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_RETWEET_STATUS);

        Bundle params=new Bundle();
        params.putLong("id", id);

        //api.execute(params);

    }

    public static void statusRetweeters(Context context, LoaderManager loaderManager, APIDelegate delegate, long id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_STATUS_RETWEETEERS);

        Bundle params=new Bundle();
        params.putLong("id", id);

        //api.execute(params);

    }
    */
}
