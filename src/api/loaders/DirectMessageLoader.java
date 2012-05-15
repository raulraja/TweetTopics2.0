package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.DirectMessageRequest;
import api.response.BaseResponse;
import api.response.DirectMessageResponse;
import api.response.ErrorResponse;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class DirectMessageLoader extends AsynchronousLoader<BaseResponse> {

    private int modeTweetLonger = 0;
    private String text = "";
    private String user = "";

    public DirectMessageLoader(Context context, DirectMessageRequest request) {
        super(context);
        modeTweetLonger = request.getModeTweetLonger();
        text = request.getText();
        user = request.getUser();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            DirectMessageResponse response = new DirectMessageResponse();
            if (modeTweetLonger == NewStatus.MODE_TL_NONE) {
                ConnectionManager.getInstance().getTwitter().sendDirectMessage(user, text);
            } else {
                ArrayList<String> ar = Utils.getDivide140(text, "");
                for (String t : ar) {
                    ConnectionManager.getInstance().getTwitter().sendDirectMessage(user, t);
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