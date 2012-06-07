package api.loaders;


import android.content.Context;
import api.AsynchronousLoader;
import api.request.CheckConversationRequest;
import api.response.BaseResponse;
import api.response.CheckConversationResponse;
import api.response.ErrorResponse;
import com.javielinux.twitter.ConnectionManager2;
import infos.InfoTweet;
import twitter4j.TwitterException;

public class CheckConversationLoader extends AsynchronousLoader<BaseResponse> {

    private CheckConversationRequest request;
    
    public CheckConversationLoader(Context context, CheckConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            CheckConversationResponse response = new CheckConversationResponse();
            ConnectionManager2.getInstance().open(getContext());
            if (request.getFrom() == InfoTweet.FROM_STATUS) {
                response.setStatus(ConnectionManager2.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()));
            } else {
                long i = ConnectionManager2.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()).getInReplyToStatusId();
                if (i>0) {
                    response.setStatus(ConnectionManager2.getInstance().getTwitter(request.getUserId()).showStatus(i));
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
