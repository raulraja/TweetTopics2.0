package api.loaders;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import api.AsynchronousLoader;
import api.request.UploadStatusRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.UploadStatusResponse;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.utils.LocationUtils;
import com.javielinux.utils.Utils;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class UploadStatusLoader extends AsynchronousLoader<BaseResponse> {

    private Twitter twitter;
    private int modeTweetLonger;
    private String tweet_text;
    private long tweet_id;
    private boolean use_geolocation;

    public UploadStatusLoader(Context context, UploadStatusRequest request) {
        super(context);

        this.twitter = request.getTwitter();
        this.modeTweetLonger = request.getModeTweetLonger();
        this.tweet_text = request.getTweetText();
        this.tweet_id = request.getTweetId();
        this.use_geolocation = request.getUseGeolocation();
    }

    private boolean updateText(String text, long tweet_id, boolean useGeo) {
        StatusUpdate statusUpdate = new StatusUpdate(text);
        if (useGeo) {
            Location loc = LocationUtils.getLastLocation(getContext());
            GeoLocation gl = new GeoLocation(loc.getLatitude(), loc.getLongitude());
            statusUpdate.setLocation(gl);
        }
        if (tweet_id > 0) statusUpdate.inReplyToStatusId(tweet_id);
        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public BaseResponse loadInBackground() {

        //TODO: Comprobar el valor devuelto con el valor esperado (error - ready) y el parámetro user_geolocation
        try {
            UploadStatusResponse response = new UploadStatusResponse();

            Log.d(Utils.TAG, "Enviando a twitter: " + tweet_text);

            if (modeTweetLonger == NewStatus.MODE_TL_NONE) {
                response.setReady(updateText(tweet_text, tweet_id, use_geolocation));
            } else {
                String replyuser = "";
                if (tweet_id > 0) {
                    replyuser =  tweet_text.substring(0, tweet_text.indexOf(" ")).trim();
                }
                ArrayList<String> token_list = Utils.getDivide140(tweet_text, replyuser);

                for (String token : token_list) {
                    response.setReady(updateText(token, tweet_id, use_geolocation));
                }
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
