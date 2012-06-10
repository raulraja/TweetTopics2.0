package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.TwitterUserRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.TwitterUserResponse;
import com.javielinux.twitter.ConnectionManager2;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import database.EntityTweetUser;
import infos.InfoSaveTweets;

public class TwitterUserLoader extends AsynchronousLoader<BaseResponse> {

    private TwitterUserRequest request;

    public TwitterUserLoader(Context context, TwitterUserRequest request) {
        super(context);

        this.request = request;

    }

    private InfoSaveTweets saveTimeline(long user_id) {

        InfoSaveTweets infoSaveTweets = null;

        try {
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_TIMELINE);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager2.getInstance().getTwitter(request.getUserId()), false);
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
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_MENTIONS);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager2.getInstance().getTwitter(request.getUserId()), false);

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
            EntityTweetUser entityTweetUser = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser.saveTweets(getContext(), ConnectionManager2.getInstance().getTwitter(request.getUserId()), false);

            EntityTweetUser entityTweetUser_send = new EntityTweetUser(user_id, TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES);
            infoSaveTweets = entityTweetUser_send.saveTweets(getContext(), ConnectionManager2.getInstance().getTwitter(request.getUserId()), false);

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

            ConnectionManager2.getInstance().open(getContext());

            response.setUserId(request.getUserId());
            response.setColumn(request.getColumn());

            if (request.getColumn() == TweetTopicsUtils.COLUMN_TIMELINE) {
               response.setInfo(saveTimeline(response.getUserId()));
            }
            if (request.getColumn() == TweetTopicsUtils.COLUMN_MENTIONS) {
                response.setInfo(saveMentions(response.getUserId()));
            }
            if (request.getColumn() == TweetTopicsUtils.COLUMN_DIRECT_MESSAGES) {
                response.setInfo(saveDirects(response.getUserId()));
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }
    }
}
