package api.loaders;


import android.content.Context;
import api.AsynchronousLoader;
import api.request.CheckConversationRequest;
import api.response.BaseResponse;
import api.response.CheckConversationResponse;
import api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager;
import infos.InfoTweet;
import twitter4j.TwitterException;

public class CheckConversationLoader extends AsynchronousLoader<BaseResponse> {

    private int from = 0;
    private long conversation = 0;
    
    public CheckConversationLoader(Context context, CheckConversationRequest request) {
        super(context);
        from = request.getFrom();
        conversation = request.getConversation();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            CheckConversationResponse response = new CheckConversationResponse();
            ConnectionManager.getInstance().open(getContext());
            if (from == InfoTweet.FROM_STATUS) {
                response.setStatus(ConnectionManager.getInstance().getTwitter().showStatus(conversation));
            } else {
                long i = ConnectionManager.getInstance().getTwitter().showStatus(conversation).getInReplyToStatusId();
                if (i>0) {
                    response.setStatus(ConnectionManager.getInstance().getTwitter().showStatus(i));
                }
            }
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}
