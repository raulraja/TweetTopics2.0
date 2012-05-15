package api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.AsynchronousLoader;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.TwitterResponse;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import database.EntityTweetUser;
import infos.InfoSaveTweets;

public class TwitterLoader extends AsynchronousLoader<BaseResponse> {

    private Context context;
    private Entity currentEntity;

    private int column = 0;
    private long userId = 0;

    private InfoSaveTweets info = null;

    public TwitterLoader(Context context, Bundle bundle) {
        super(context);
        this.context = context;
        userId = bundle.getLong("userId");
        column = bundle.getInt("column");
    }

    @Override
    public BaseResponse loadInBackground() {

        ConnectionManager.getInstance().open(context);

        currentEntity = new Entity("users", userId);

        TwitterResponse response = new TwitterResponse();

        response.setUserId(userId);
        response.setColumn(column);

        if (column == TweetTopicsCore.TIMELINE) {
            saveTimeline();
        }
        if (column == TweetTopicsCore.MENTIONS) {
            saveMentions();
        }
        if (column == TweetTopicsCore.DIRECTMESSAGES) {
            saveDirects();
        }

        response.setInfo(info);

        if (info.getError()!=Utils.NOERROR) {
            ErrorResponse error = new ErrorResponse();
            error.setError(null, "");
            error.setTypeError(info.getError());
            error.setRateError(info.getRate());
            return error;
        }


        return response;

    }

    private void saveTimeline() {
        // timeline

        try {

            if (currentEntity.getInt("no_save_timeline")!=1) {
                EntityTweetUser etu = new EntityTweetUser(userId, TweetTopicsCore.TIMELINE);
                info = etu.saveTweets(context, ConnectionManager.getInstance().getTwitter(), false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (info==null) {
                info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                info.setError(Utils.UNKNOWN_ERROR);
            }
        }

    }

    private void saveMentions() {
        // mentions
        try {
            EntityTweetUser etu = new EntityTweetUser(userId, TweetTopicsCore.MENTIONS);
            info = etu.saveTweets(context, ConnectionManager.getInstance().getTwitter(), false);

        } catch (Exception e) {
            e.printStackTrace();
            if (info==null) {
                info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                info.setError(Utils.UNKNOWN_ERROR);
            }
        }
    }

    private void saveDirects() {
        // directos

        try {

            EntityTweetUser etu = new EntityTweetUser(userId, TweetTopicsCore.DIRECTMESSAGES);
            info = etu.saveTweets(context, ConnectionManager.getInstance().getTwitter(), false);

            // enviados directos
            EntityTweetUser etu_send = new EntityTweetUser(userId, TweetTopicsCore.SENT_DIRECTMESSAGES);
            info = etu_send.saveTweets(context, ConnectionManager.getInstance().getTwitter(), false);

        } catch (Exception e) {
            e.printStackTrace();
            if (info==null) {
                info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                info.setError(Utils.UNKNOWN_ERROR);
            }
        }

    }

}