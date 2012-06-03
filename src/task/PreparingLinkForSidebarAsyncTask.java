package task;

import android.os.AsyncTask;
import com.javielinux.utils.Utils;
import infos.CacheData;

public class PreparingLinkForSidebarAsyncTask extends AsyncTask<String, Void, Boolean> {

	public interface PreparingLinkForSidebarAsyncTaskResponder {
		public void preparingLinkLoading();
		public void preparingLinkCancelled();
		public void preparingLinkLoaded(Boolean bool);
	}

	private PreparingLinkForSidebarAsyncTaskResponder responder;

	public PreparingLinkForSidebarAsyncTask(PreparingLinkForSidebarAsyncTaskResponder responder) {
		this.responder = responder;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		String link = args[0];
		try {
			CacheData.putCacheImages(link, Utils.getThumbTweet(link));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.preparingLinkLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.preparingLinkCancelled();
	}

	@Override
	protected void onPostExecute(Boolean bool) {
		super.onPostExecute(bool);
		responder.preparingLinkLoaded(bool);
	}

}
