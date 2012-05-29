package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.TwitterUserRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.TwitterUserResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.TweetTopicsConstants;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import database.EntityTweetUser;
import infos.InfoSaveTweets;

public class TwitterUserLoader extends AsynchronousLoader<BaseResponse> {

    private int column;
    private Entity current_user;

    public TwitterUserLoader(Context context, TwitterUserRequest request) {
        super(context);

        this.column = request.getColumn();
        this.current_user = request.getUser();

    }

    private InfoSaveTweets saveTimeline(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsConstants.TWEET_TYPE_TIMELINE);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(), false);
        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                infoSaveTweets = new InfoSaveTweets();
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;
    }

    private InfoSaveTweets saveMentions(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsConstants.TWEET_TYPE_MENTIONS);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(), false);

        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                InfoSaveTweets info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;
    }

    private InfoSaveTweets saveDirects(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            // TODO: Comprobar este código
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsConstants.TWEET_TYPE_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(), false);

            EntityTweetUser entityTweetUser_send = new EntityTweetUser(user_id, TweetTopicsConstants.TWEET_TYPE_SENT_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser_send.saveTweets(getContext(), ConnectionManager.getInstance().getTwitter(), false);

        } catch (Exception e) {
            e.printStackTrace();
            if (infoSaveTweets == null) {
                InfoSaveTweets info = new InfoSaveTweets();
                info.setError(Utils.UNKNOWN_ERROR);
            } else {
                infoSaveTweets.setError(Utils.UNKNOWN_ERROR);
            }
        }

        return infoSaveTweets;

    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            TwitterUserResponse response = new TwitterUserResponse();

            ConnectionManager.getInstance().open(getContext());

            response.setUserId(current_user.getId());
            response.setColumn(column);

            if (column == TweetTopicsConstants.COLUMN_TIMELINE) {
               response.setInfo(saveTimeline(response.getUserId()));
            }
            if (column == TweetTopicsConstants.COLUMN_MENTIONS) {
                response.setInfo(saveMentions(response.getUserId()));
            }
            if (column == TweetTopicsConstants.COLUMN_DIRECT_MESSAGES) {
                response.setInfo(saveDirects(response.getUserId()));
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
