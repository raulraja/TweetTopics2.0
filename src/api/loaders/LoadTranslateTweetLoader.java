package api.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import infos.InfoUsers;
import twitter4j.Status;
import twitter4j.TwitterException;

public class LoadTranslateTweetLoader extends AsynchronousLoader<APIResult> {

    private long id = 0;

    public LoadTranslateTweetLoader(Context context, Bundle bundle) {
        super(context);

        this.id = bundle.getLong("id");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();
		InfoUsers infoUsers = new InfoUsers();

		try {
			ConnectionManager.getInstance().open(getContext());

			Status status = ConnectionManager.getInstance().getTwitter().showStatus(id);
			infoUsers.setName(status.getUser().getScreenName());
			infoUsers.setFollowers(status.getUser().getFollowersCount());
			infoUsers.setFollowing(status.getUser().getFriendsCount());
			infoUsers.setTweets(status.getUser().getStatusesCount());
			infoUsers.setTextTweet(status.getText());
			infoUsers.setDateTweet(status.getCreatedAt());

			String lang = Utils.preference.getString("prf_translate", "es");

            Translate.setKey("2EFDAEA6BE06919111E8FA1FB505BF7A2FC6161B");
            Language language = Language.ENGLISH;

            if (lang.equals("en")) {
               language = Language.ENGLISH;
            } else if (lang.equals("es")) {
               language = Language.SPANISH;
            } else if (lang.equals("fr")) {
               language = Language.FRENCH;
            } else if (lang.equals("de")) {
               language = Language.GERMAN;
            } else if (lang.equals("ja")) {
               language = Language.JAPANESE;
            } else if (lang.equals("pt")) {
               language = Language.PORTUGUESE;
            } else if (lang.equals("it")) {
               language = Language.ITALIAN;
            } else if (lang.equals("ru")) {
               language = Language.RUSSIAN;
            } else if (lang.equals("id")) {
               language = Language.INDONESIAN;
            }

            String text = Translate.execute(status.getText(), language);

			infoUsers.setTextTweetTranslate(text);

			try {
				Bitmap bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(status.getUser().getProfileImageURL().openStream()));
				infoUsers.setAvatar(Bitmap.createScaledBitmap(bmp, 48, 48, true));
			} catch (OutOfMemoryError exception) {
				exception.printStackTrace();
                out.setError(exception, exception.getMessage());
                return out;
			} catch (Exception exception) {
				exception.printStackTrace();
                out.setError(exception, exception.getMessage());
                return out;
			}

            out.addParameter("infoUsers", infoUsers);
            return out;

		} catch (TwitterException twitterException) {
            twitterException.printStackTrace();
            out.setError(twitterException, twitterException.getMessage());
            return out;
		} catch (Exception exception) {
			exception.printStackTrace();
            out.setError(exception, exception.getMessage());
            return out;
		}
    }
}
