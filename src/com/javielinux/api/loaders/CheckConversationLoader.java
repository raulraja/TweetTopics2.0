package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.CheckConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.CheckConversationResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
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
            ConnectionManager.getInstance().open(getContext());
            if (request.getFrom() == InfoTweet.FROM_STATUS) {
                response.setStatus(ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()));
            } else {
                long i = ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(request.getConversation()).getInReplyToStatusId();
                if (i>0) {
                    response.setStatus(ConnectionManager.getInstance().getTwitter(request.getUserId()).showStatus(i));
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
