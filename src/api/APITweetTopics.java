package api;


import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;

public class APITweetTopics {
    
    public static final int KEY_CHECK_CONVERSATION = 0;
    public static final int KEY_CONVERSATION = 1;
    public static final int KEY_DIRECT_MESSAGE = 2;

    public static void checkConversation(Context context, LoaderManager loaderManager, APIDelegate delegate, int from, long conversation) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_CHECK_CONVERSATION);

        Bundle params=new Bundle();
        params.putInt("from", from);
        params.putLong("conversation", conversation);

        api.execute(params);

    }

    public static void conversation(Context context, LoaderManager loaderManager, APIDelegate delegate, long id) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_CONVERSATION);

        Bundle params=new Bundle();
        params.putLong("id", id);

        api.execute(params);

    }

    public static void directMessage(Context context, LoaderManager loaderManager, APIDelegate delegate, int modeTweetLonger, String user, String text) {

        APILoader api = new APILoader(context, loaderManager, delegate, KEY_DIRECT_MESSAGE);

        Bundle params=new Bundle();
        params.putInt("modeTweetLonger", modeTweetLonger);
        params.putString("user", user);
        params.putString("text", text);

        api.execute(params);

    }

}
