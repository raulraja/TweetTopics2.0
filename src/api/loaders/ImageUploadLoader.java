package api.loaders;


import android.content.Context;
import api.AsynchronousLoader;
import api.request.ImageUploadRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.ImageUploadResponse;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class ImageUploadLoader extends AsynchronousLoader<BaseResponse> {

    public ImageUploadRequest request;
    private String consumerKey;
    private String consumerSecretKey;

    public ImageUploadLoader(Context context, ImageUploadRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            ImageUploadResponse response = new ImageUploadResponse();

            String f = Utils.appUploadImageDirectory+request.getFilename();

            response.setFile(request.getFilename());
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
            response.setBmp(Utils.getBitmapFromFile(f, Utils.HEIGHT_THUMB_NEWSTATUS, true));

            File file = new File(f);


            int type = Integer.parseInt(Utils.getPreference(getContext()).getString("prf_service_image", "1"));

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
            confBuild.setOAuthAccessToken(ConnectionManager.getInstance().getTwitter().getOAuthAccessToken().getToken());
            confBuild.setOAuthAccessTokenSecret(ConnectionManager.getInstance().getTwitter().getOAuthAccessToken().getTokenSecret());
            if (type==1) {
            } else if (type==2) {
                confBuild.setMediaProviderAPIKey("e3533af853e8f63a0018a2c63d7ee69f");
            } else if (type==3) {
                confBuild.setMediaProviderAPIKey("57438faa-51e4-43a5-9e15-0c63b6f73950");
            }

            confBuild.setMediaProvider(mp.getName());
            Configuration config = confBuild.build();

            ImageUpload upload = new ImageUploadFactory(config).getInstance(mp);
            if (upload!=null) {
                response.setUrl(upload.upload(file));
                return response;
            } else {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("Image upload is null");
                return errorResponse;
            }

        } catch (TwitterException exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
        }

    }

    private void loadConsumerKeys() {
        try {
            Properties props = new Properties();
            InputStream stream = getContext().getResources().openRawResource(R.raw.oauth);
            props.load(stream);
            consumerKey = (String)props.get("consumer_key");
            consumerSecretKey = (String)props.get("consumer_secret_key");
        } catch (Exception e) {
        }
    }

}
