package task;

import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.Utils;
import infos.InfoLink;
import infos.InfoWeb;

public class LoadLinkAsyncTask extends AsyncTask<InfoLink, Void, InfoLink> {

	public interface LoadLinkAsyncAsyncTaskResponder {
		public void linkLoading();
		public void linkCancelled();
		public void linkLoaded(InfoLink il);
	}

	private LoadLinkAsyncAsyncTaskResponder responder;
	private Context mContext;

	public LoadLinkAsyncTask(Context cnt, LoadLinkAsyncAsyncTaskResponder responder) {
		this.responder = responder;
		mContext = cnt;
	}

	@Override
	protected InfoLink doInBackground(InfoLink... args) {
		InfoLink il = args[0];
		
		if (il.getType()==2) { // es un link
			try {
				InfoWeb info = new InfoWeb(il.getLink());
	
				if (!info.getTitle().equals("")) {
					il.setTitle(info.getTitle());
				}
				if (!info.getDescription().equals("")) {
					il.setDescription(info.getDescription());
				}
				if (info.getImageBitmap()!=null) {
					il.setBitmapThumb(info.getImageBitmap());
				}
				
				il.setExtensiveInfo(true);
			} catch (Exception e) {}

		} else {
			int h = Utils.dip2px(mContext, Utils.HEIGHT_IMAGE);
			if (il.getType()==1) h = Utils.dip2px(mContext, Utils.HEIGHT_VIDEO);
			il.setBitmapLarge(Utils.getBitmap(il.getLinkImageLarge(), h));
			il.setExtensiveInfo(true);
		}
		
		return il;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.linkLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.linkCancelled();
	}

	@Override
	protected void onPostExecute(InfoLink il) {
		super.onPostExecute(il);
		responder.linkLoaded(il);
	}

}
