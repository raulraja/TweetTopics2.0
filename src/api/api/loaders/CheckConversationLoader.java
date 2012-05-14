package api.api.loaders;


import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.twitter.ConnectionManager;
import infos.InfoTweet;
import twitter4j.TwitterException;

public class CheckConversationLoader extends AsynchronousLoader<APIResult> {

    private int from = 0;
    private long conversation = 0;
    
    public CheckConversationLoader(Context context, Bundle bundle) {
        super(context);
        from = bundle.getInt("from");
        conversation = bundle.getInt("conversation");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

        try {
            ConnectionManager.getInstance().open(getContext());
            if (from == InfoTweet.FROM_STATUS) {
                out.addParameter("status", ConnectionManager.getInstance().getTwitter().showStatus(conversation));
            } else {
                long i = ConnectionManager.getInstance().getTwitter().showStatus(conversation).getInReplyToStatusId();
                if (i>0) {
                    out.addParameter("status", ConnectionManager.getInstance().getTwitter().showStatus(i));
                }
            }
            return out;
        } catch (TwitterException e) {
            e.printStackTrace();
            out.setError(e, e.getMessage());
            return out;
        }

    }

}
