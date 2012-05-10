package task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageAutoCompleteAsyncTask extends AsyncTask<String, Void, Bitmap> {

	public interface LoadImageAutoCompleteAsyncTaskResponder {
		public void imageAutoCompleteLoading();
		public void imageAutoCompleteLoadCancelled();
		public void imageAutoCompleteLoaded(Bitmap bmp);
	}

	private LoadImageAutoCompleteAsyncTaskResponder responder;
		
	public LoadImageAutoCompleteAsyncTask(LoadImageAutoCompleteAsyncTaskResponder responder) {
		this.responder = responder;
	}
	

	@Override
	protected Bitmap doInBackground(String... arg) {
		Bitmap bmp = null;
		try {
			URL url = new URL(arg[0]);
			bmp = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));	
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmp;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.imageAutoCompleteLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.imageAutoCompleteLoadCancelled();
	}

	@Override
	protected void onPostExecute(Bitmap bmp) {
		super.onPostExecute(bmp);
		responder.imageAutoCompleteLoaded(bmp);
	}

}
