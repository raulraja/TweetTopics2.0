package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.DirectMessageRequest;
import api.response.BaseResponse;
import api.response.DirectMessageResponse;
import api.response.ErrorResponse;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.twitter.ConnectionManager2;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class DirectMessageLoader extends AsynchronousLoader<BaseResponse> {

    private DirectMessageRequest request;

    public DirectMessageLoader(Context context, DirectMessageRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            DirectMessageResponse response = new DirectMessageResponse();
            if (request.getModeTweetLonger() == NewStatusActivity.MODE_TL_NONE) {
                ConnectionManager2.getInstance().getTwitter(request.getUserId()).sendDirectMessage(request.getUser(), request.getText());
            } else {
                ArrayList<String> ar = Utils.getDivide140(request.getText(), "");
                for (String t : ar) {
                    ConnectionManager2.getInstance().getTwitter(request.getUserId()).sendDirectMessage(request.getUser(), t);
                }
            }
            response.setSent(true);
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setError(e, e.getMessage());
            return error;
        }

    }

}