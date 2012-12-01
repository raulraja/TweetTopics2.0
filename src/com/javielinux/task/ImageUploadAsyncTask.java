package com.javielinux.task;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.NetworkConfig;
import com.javielinux.twitter.NetworkConfigParser;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import java.io.File;
import java.util.List;

public class ImageUploadAsyncTask extends AsyncTask<String, Void, ImageUploadAsyncTask.ImageUploadResult> {
	
	public interface ImageUploadAsyncTaskResponder {
		public void imageUploadLoading();
		public void imageUploadCancelled();
		public void imageUploadLoaded(ImageUploadAsyncTask.ImageUploadResult imageUploadResult);
	}
	
	public class ImageUploadResult {
		public String url;
		public String file;
		public boolean error = true;
		public Bitmap bmp;
		public ImageUploadResult() {
			super();
		}
	}

	private ImageUploadAsyncTaskResponder responder;
	private Twitter twitter;
	private Context mContext;
	private String consumerKey;
	private String consumerSecretKey;

	public ImageUploadAsyncTask(Context responder, Twitter twitter) {
		mContext = responder;
		this.responder = (ImageUploadAsyncTaskResponder) responder;
		this.twitter = twitter;
	}

	@Override
	protected ImageUploadAsyncTask.ImageUploadResult doInBackground(String... args) {
		ImageUploadResult iur = new ImageUploadResult();
    	try {
    		
    		String f = Utils.appUploadImageDirectory+args[0];
    		
    		iur.file = args[0];
    		/*
    		int size = Integer.parseInt(Utils.getPreference(mContext).getString("prf_size_photo", "2"));
    		
    		if (size == 1) {
    			Bitmap resizedBitmap = Utils.getResizeBitmapFromFile(f, Utils.HEIGHT_PHOTO_SIZE_SMALL);
    			if (resizedBitmap!=null) {
    				FileOutputStream out = new FileOutputStream(f);
    				resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
    			}
    		} else if (size == 2) {
    			Bitmap resizedBitmap = Utils.getResizeBitmapFromFile(f, Utils.HEIGHT_PHOTO_SIZE_MIDDLE);
    			if (resizedBitmap!=null) {
    				FileOutputStream out = new FileOutputStream(f);
    				resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
    			}
    		}
    		*/
    		iur.bmp = ImageUtils.getBitmapFromFile(f, Utils.HEIGHT_THUMB_NEWSTATUS, true);
    		    		
    		File file = new File(f);
    		
    		
    		int type = Integer.parseInt(Utils.getPreference(mContext).getString("prf_service_image", "1"));
    		
    		MediaProvider mp = MediaProvider.YFROG;
    		
    		if (type==1) {
    			mp = MediaProvider.YFROG;
    		} else if (type==2) {
    			mp = MediaProvider.TWITPIC;
    		} else if (type==3) {
    			mp = MediaProvider.PLIXI;
    		}

    		
    		loadConsumerKeys();
    		
    		ConfigurationBuilder confBuild = new ConfigurationBuilder();
    		confBuild.setOAuthConsumerKey(consumerKey);
    		confBuild.setOAuthConsumerSecret(consumerSecretKey);
    		confBuild.setOAuthAccessToken(twitter.getOAuthAccessToken().getToken());
    		confBuild.setOAuthAccessTokenSecret(twitter.getOAuthAccessToken().getTokenSecret());
    		if (type==1) {
    		} else if (type==2) {
    			confBuild.setMediaProviderAPIKey("e3533af853e8f63a0018a2c63d7ee69f");
    		} else if (type==3) {
    			confBuild.setMediaProviderAPIKey("57438faa-51e4-43a5-9e15-0c63b6f73950");
    		}
    		
    		confBuild.setMediaProvider(mp.name());
    		Configuration config = confBuild.build();
    		
            ImageUpload upload = new ImageUploadFactory(config).getInstance(mp);
            if (upload!=null) {
    			iur.url = upload.upload(file);
    			iur.error = false;
    		} else {
    			iur.error = true;	
    		}
    		
    	} catch (TwitterException e) {
    		e.printStackTrace();
    		iur.error = true;
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		iur.error = true;
    	}
		return iur;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.imageUploadLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.imageUploadCancelled();
	}

	@Override
	protected void onPostExecute(ImageUploadAsyncTask.ImageUploadResult imageUploadResult) {
		super.onPostExecute(imageUploadResult);
		responder.imageUploadLoaded(imageUploadResult);
	}
	
	private void loadConsumerKeys() {

        NetworkConfigParser parser = new NetworkConfigParser();
        List<NetworkConfig> networkConfigs = parser.parse(mContext.getResources().getXml(R.xml.network_config));
        NetworkConfig config = null;
        for (NetworkConfig c : networkConfigs) {
            if(c.getName().equals("twitter.com")) {
                config = c;
                break;
            }
        }

        consumerKey = config.getConsumerKey();
        consumerSecretKey = config.getConsumerSecret();

	}

}
