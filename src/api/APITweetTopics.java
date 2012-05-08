package api;


import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;

public class APITweetTopics {
    
    public static final int KEY_CHECK_CONVERSATION = 0;

    public static void executeCheckConversation(Context context, LoaderManager loaderManager, APIDelegate delegate, int from, long conversation) {
        
        APILoader api = new APILoader(context, loaderManager, delegate, KEY_CHECK_CONVERSATION);
        
        Bundle params=new Bundle();
        params.putInt("from", from);
        params.putLong("conversation", conversation);

        api.execute(params);

    }

}
