package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.GetConversationRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.GetConversationResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class GetConversationLoader extends AsynchronousLoader<BaseResponse> {

    private GetConversationRequest request;

    public GetConversationLoader(Context context, GetConversationRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            GetConversationResponse response = new GetConversationResponse();

            ConnectionManager.getInstance().open(getContext());
            twitter4j.Status status = ConnectionManager.getInstance().getUserForSearchesTwitter().showStatus(request.getId());

            InfoTweet infoTweet = new InfoTweet(status);
            response.setConversationTweet(infoTweet);

            // TODO : ver como funciona el publish progress en los loaders
            /*
            publishProgress(result);

            while (status.getInReplyToStatusId() > 0) {
                status = ConnectionManager.getInstance().getTwitter().showStatus(status.getInReplyToStatusId());

                result.conversation_status = status;
                publishProgress(result);
            }             */

            return response;
        } catch (TwitterException exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        }

    }

}
