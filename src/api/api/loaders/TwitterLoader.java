package api.api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import database.EntityTweetUser;
import infos.InfoSaveTweets;

public class TwitterLoader extends AsynchronousLoader<APIResult> {

    private Context context;
    private Entity currentEntity;
    private APIResult out = new APIResult();

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
    public APIResult loadInBackground() {

        ConnectionManager.getInstance().open(context);

        currentEntity = new Entity("users", userId);

        out.addParameter("user_id", currentEntity.getId());
        out.addParameter("column", column);

        if (column == TweetTopicsCore.TIMELINE) {
            saveTimeline();
        }
        if (column == TweetTopicsCore.MENTIONS) {
            saveMentions();
        }
        if (column == TweetTopicsCore.DIRECTMESSAGES) {
            saveDirects();
        }

        out.addParameter("info", info);

        if (info.getError()!=Utils.NOERROR) {
            out.setError(null, "");
            out.setTypeError(info.getError());
            out.setRateError(info.getRate());
        }


        return out;

    }

    private void saveTimeline() {
        // timeline

        try {

            if (currentEntity.getInt("no_save_timeline")!=1) {
                EntityTweetUser etu = new EntityTweetUser(out.getLong("user_id"), TweetTopicsCore.TIMELINE);
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
            EntityTweetUser etu = new EntityTweetUser(out.getLong("user_id"), TweetTopicsCore.MENTIONS);
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

            EntityTweetUser etu = new EntityTweetUser(out.getLong("user_id"), TweetTopicsCore.DIRECTMESSAGES);
            info = etu.saveTweets(context, ConnectionManager.getInstance().getTwitter(), false);

            // enviados directos
            EntityTweetUser etu_send = new EntityTweetUser(out.getLong("user_id"), TweetTopicsCore.SENT_DIRECTMESSAGES);
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