package task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.javielinux.tweettopics.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageWidgetAsyncTask extends AsyncTask<String, Void, LoadImageWidgetAsyncTask.ImageData> {

	public interface LoadImageWidgetAsyncTaskResponder {
		public void imageWidgetLoading();
		public void imageWidgetLoadCancelled();
		public void imageWidgetLoaded(ImageData data);
	}
    
    public class ImageData {
        public Bitmap bitmap;
        public String url;
    }

	private LoadImageWidgetAsyncTaskResponder responder;
	
	public LoadImageWidgetAsyncTask(LoadImageWidgetAsyncTaskResponder responder) {
		this.responder = responder;
	}
	

	@Override
	protected ImageData doInBackground(String... arg) {
        ImageData out = new ImageData();
        out.url = arg[0];
		try {
			URL url = new URL(arg[0]);
            out.bitmap = BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream()));
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.imageWidgetLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.imageWidgetLoadCancelled();
	}

	@Override
	protected void onPostExecute(ImageData data) {
		super.onPostExecute(data);
		responder.imageWidgetLoaded(data);
	}

}
