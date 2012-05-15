package api.loaders;


import android.content.Context;
import api.AsynchronousLoader;
import api.request.ConversationRequest;
import api.response.BaseResponse;
import api.response.ConversationResponse;
import api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class ConversationLoader extends AsynchronousLoader<BaseResponse> {

    private long id = 0;

    public ConversationLoader(Context context, ConversationRequest request) {
        super(context);
        id = request.getId();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            ConversationResponse response = new ConversationResponse();

            ConnectionManager.getInstance().open(getContext());
            ArrayList<Status> tweets = new ArrayList<twitter4j.Status>();
            twitter4j.Status st = ConnectionManager.getInstance().getTwitter().showStatus(id);

            tweets.add(st);
            while (st.getInReplyToStatusId()>0) {
                st = ConnectionManager.getInstance().getTwitter().showStatus(st.getInReplyToStatusId());
                tweets.add(st);
            }
            response.setTweets(tweets);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
