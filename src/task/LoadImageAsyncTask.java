package task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.javielinux.utils.LinksUtils;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;

import java.io.File;
import java.util.ArrayList;

public class LoadImageAsyncTask extends AsyncTask<String, Void, Void> {

	public interface LoadImageAsyncTaskResponder {
		public void imageLoading();
		public void imageLoadCancelled();
		public void imageLoaded(Void v);
	}

	private LoadImageAsyncTaskResponder responder;
	private ArrayList<String> mSearchAvatars;
	private ArrayList<String> mSearchImages;
    private Context context;
	
	public LoadImageAsyncTask(Context context, LoadImageAsyncTaskResponder responder, ArrayList<String> searchAvatars, ArrayList<String> searchImages) {
		this.responder = responder;
		mSearchAvatars = searchAvatars;
		mSearchImages = searchImages;
        this.context = context;
	}
	
	public Bitmap downloadImage(String u) {
		Bitmap bmp = null;

		try {

            File file = Utils.getFileForSaveURL(context, u);
            if (!file.exists()) {
                bmp = Utils.saveAvatar(u, file);
            } else {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            }


		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Utils.TAG, "Could not load image.", e);
		}
		
		return bmp;

	}

	@Override
	protected Void doInBackground(String... arg) {
		
		for (String avatar : mSearchAvatars) {
			Bitmap bmp = downloadImage(avatar);
			
			if (bmp!=null) {
				CacheData.putCacheAvatars(avatar, bmp);
			}
		}
		
		for (String image : mSearchImages) {
			InfoLink il = LinksUtils.getInfoTweet(image);
			if (il!=null) {
				//int type = Integer.parseInt(Utils.preference.getString("prf_links", "3"));
				//if ( type == 3 ) {
				CacheData.putCacheImages(image, il);	
				/*} else if ( type == 2 && (il.getType()  == 0 || il.getType()  == 1) ) {
					TweetListItem.putCacheImages(image, il);
				}*/				
			}
		}
		
		return null;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.imageLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.imageLoadCancelled();
	}

	@Override
	protected void onPostExecute(Void v) {
		super.onPostExecute(v);
		responder.imageLoaded(v);
	}

}
