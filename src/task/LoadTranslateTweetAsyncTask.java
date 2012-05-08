package task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.javielinux.tweettopics.Utils;
import com.javielinux.twitter.ConnectionManager;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import infos.InfoUsers;
import twitter4j.TwitterException;

public class LoadTranslateTweetAsyncTask extends AsyncTask<Long, Void, InfoUsers> {

	public interface LoadTranslateTweetAsyncAsyncTaskResponder {
		public void translateLoading();
		public void translateCancelled();
		public void translateLoaded(InfoUsers iu);
	}

	private Context mContext;
	private LoadTranslateTweetAsyncAsyncTaskResponder responder;

	public LoadTranslateTweetAsyncTask(Context cnt, LoadTranslateTweetAsyncAsyncTaskResponder responder) {
		mContext = cnt;
		this.responder = responder;
	}

	@Override
	protected InfoUsers doInBackground(Long... args) {
		InfoUsers iu = new InfoUsers();
		try {
			ConnectionManager.getInstance().open(mContext);
			
			twitter4j.Status status = ConnectionManager.getInstance().getTwitter().showStatus(args[0]);
			iu.setName(status.getUser().getScreenName());
			iu.setFollowers(status.getUser().getFollowersCount());
			iu.setFollowing(status.getUser().getFriendsCount());
			iu.setTweets(status.getUser().getStatusesCount());
			iu.setTextTweet(status.getText());
			iu.setDateTweet(status.getCreatedAt());
			
			String lang = Utils.preference.getString("prf_translate", "es");
			
			//String text = Translate.translate(status.getText(), "", lang);
			//if (text==null) text = Translate.translate(status.getText(), "en", lang);

            Translate.setKey("2EFDAEA6BE06919111E8FA1FB505BF7A2FC6161B");
            Language l = Language.ENGLISH;
            if (lang.equals("en")) {
               l = Language.ENGLISH;
            } else if (lang.equals("es")) {
               l = Language.SPANISH;
            } else if (lang.equals("fr")) {
               l = Language.FRENCH;
            } else if (lang.equals("de")) {
               l = Language.GERMAN;
            } else if (lang.equals("ja")) {
               l = Language.JAPANESE;
            } else if (lang.equals("pt")) {
               l = Language.PORTUGUESE;
            } else if (lang.equals("it")) {
               l = Language.ITALIAN;
            } else if (lang.equals("ru")) {
               l = Language.RUSSIAN;
            } else if (lang.equals("id")) {
                l = Language.INDONESIAN;
            }
            String text = Translate.execute(status.getText(), l);

			iu.setTextTweetTranslate(text);
			
			try {
				Bitmap bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(status.getUser().getProfileImageURL().openStream()));
				iu.setAvatar(Bitmap.createScaledBitmap(bmp, 48, 48, true));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return iu;
		} catch (TwitterException e1) {
			e1.printStackTrace();
			iu = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iu;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.translateLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.translateCancelled();
	}

	@Override
	protected void onPostExecute(InfoUsers iu) {
		super.onPostExecute(iu);
		responder.translateLoaded(iu);
	}

}
