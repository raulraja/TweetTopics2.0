package api.api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class DirectMessageLoader extends AsynchronousLoader<APIResult> {

    private int modeTweetLonger = 0;
    private String text = "";
    private String user = "";

    public DirectMessageLoader(Context context, Bundle bundle) {
        super(context);
        modeTweetLonger = bundle.getInt("modeTweetLonger");
        text = bundle.getString("text");
        user = bundle.getString("user");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

        try {
            if (modeTweetLonger == NewStatus.MODE_TL_NONE) {
                ConnectionManager.getInstance().getTwitter().sendDirectMessage(user, text);
            } else {
                ArrayList<String> ar = Utils.getDivide140(text, "");
                for (String t : ar) {
                    ConnectionManager.getInstance().getTwitter().sendDirectMessage(user, t);
                }
            }
            return out;
        } catch (TwitterException e) {
            e.printStackTrace();
            out.setError(e, e.getMessage());
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            out.setError(e, e.getMessage());
            return out;
        }

    }

}