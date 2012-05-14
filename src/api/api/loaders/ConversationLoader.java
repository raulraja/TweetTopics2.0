package api.api.loaders;


import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class ConversationLoader extends AsynchronousLoader<APIResult> {

    private long id = 0;

    public ConversationLoader(Context context, Bundle bundle) {
        super(context);
        id = bundle.getLong("id");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

        try {
            ConnectionManager.getInstance().open(getContext());
            ArrayList<Status> tweets = new ArrayList<twitter4j.Status>();
            twitter4j.Status st = ConnectionManager.getInstance().getTwitter().showStatus(id);

            tweets.add(st);
            while (st.getInReplyToStatusId()>0) {
                st = ConnectionManager.getInstance().getTwitter().showStatus(st.getInReplyToStatusId());
                tweets.add(st);
            }
            out.addArrayStatusParameter("tweets", tweets);
            return out;
        } catch (TwitterException e) {
            e.printStackTrace();
            out.setError(e, e.getMessage());
            return out;
        }

    }

}
