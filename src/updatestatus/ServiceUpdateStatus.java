package updatestatus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.javielinux.facebook.FacebookHandler;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import task.*;
import task.DirectMessageAsyncTask.DirectMessageAsyncTaskResponder;
import task.ImageUploadAsyncTask.ImageUploadAsyncTaskResponder;
import task.ImageUploadAsyncTask.ImageUploadResult;
import task.UploadStatusAsyncTask.UploadStatusAsyncTaskResponder;
import task.UploadTwitlongerAsyncTask.UploadTwitlongerAsyncTaskResponder;
import twitter4j.Twitter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ServiceUpdateStatus extends Service implements UploadStatusAsyncTaskResponder, ImageUploadAsyncTaskResponder,
        DirectMessageAsyncTaskResponder, RetweetStatusAsyncTask.RetweetStatusAsyncTaskResponder {

    private static int ID_NOTIFICATION = 151515;

    private ArrayList<Long> mUsersId;

    private ArrayList<String> mPhotos;
    private ArrayList<String> mPhotosRemoved;

    private Entity mEntityStatus = null;

    private long mCurrentIdUser = 0;

    public static Twitter twitter;

    private String mText = "";

    private String mBaseURLImage = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        mUsersId = new ArrayList<Long>();
        mPhotos = new ArrayList<String>();
        mPhotosRemoved = new ArrayList<String>();

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionManager.getInstance().open(this);

        Log.d(Utils.TAG, "Creando nuevo estado");

        mEntityStatus = DataFramework.getInstance().getTopEntity("send_tweets", "is_sent = 0", "");

        if (mEntityStatus!=null) {

            ArrayList<Long> auxUsersTwitter = new ArrayList<Long>();
            ArrayList<Long> auxUsersOthers = new ArrayList<Long>();

            String users = mEntityStatus.getString("users");

            for (String user : users.split(",")) {
                try {
                    if (!user.equals("")) {
                        Entity e = new Entity("users", Long.parseLong(user));
                        if (e.getString("service").equals("") || e.getString("service").equals("twitter.com")) {
                            auxUsersTwitter.add(Long.parseLong(user));
                        } else {
                            auxUsersOthers.add(Long.parseLong(user));
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // ordenar poniendo los de twitter los primeros

            for (long user : auxUsersTwitter) {
                mUsersId.add(user);
            }
            for (long user : auxUsersOthers) {
                mUsersId.add(user);
            }

            mText = mEntityStatus.getString("text");

            String photos = mEntityStatus.getString("photos");

            if (!photos.equals("")) {

                int type = Integer.parseInt(Utils.getPreference(this).getString("prf_service_image", "1"));
                if (type==1) {
                    mBaseURLImage = NewStatus.URL_BASE_YFROG;
                } else if (type==2) {
                    mBaseURLImage = NewStatus.URL_BASE_TWITPIC;
                } else if (type==3) {
                    mBaseURLImage = NewStatus.URL_BASE_LOCKERZ;
                }

                StringTokenizer tokens = new StringTokenizer(photos, "--");

                while(tokens.hasMoreTokens()) {
                    String photo = tokens.nextToken();
                    if (!photo.equals("")) {
                        mPhotos.add(photo);
                    }
                }

            }

            // empezamos a enviar

            try {
                setMood(this.getString(R.string.update_status_uploading_msg), false);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            launchTasks();

        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }

    private void launchTasks() {
        if (mPhotos.size()>0) {
            twitter = ConnectionManager.getInstance().getTwitter(mUsersId.get(0), false);
            try {
                setMood(this.getString(R.string.update_status_uploading_image), false);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadImage(mPhotos.get(0));
        } else {
            if (mUsersId.size()>0) {
                sendTweet(mUsersId.get(0));
            } else {
                deleteTweet();
                ConnectionManager.getInstance().getTwitterForceActiveUser();
                try {
                    setMood(this.getString(R.string.update_status_correct), false);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID_NOTIFICATION);
                Intent update = new Intent();
                update.putExtra("refresh-column", TweetTopicsCore.TIMELINE);
                update.setAction(Intent.ACTION_VIEW);
                sendOrderedBroadcast(update, null);
            }
        }
    }

    private void sendTweet(long id) {
        //Log.d(Utils.TAG, "userId = " + id);

        mCurrentIdUser = id;

        Entity ent = new Entity("users", mCurrentIdUser);

        if (ent.getString("service").equals("facebook")) {
            updateStatusFacebook();
        } else {
            twitter = ConnectionManager.getInstance().getTwitter(id, false);

            if (mEntityStatus.getInt("type_id") == 3) { // retweet
                retweetMessage();
            } else {
                int shortURLLength = PreferenceUtils.getShortURLLength(this);
                int shortURLLengthHttps = PreferenceUtils.getShortURLLengthHttps(this);

                if (Utils.getLenghtTweet(mText, shortURLLength, shortURLLengthHttps)>140) {
                    if (mEntityStatus.getInt("type_id") == 1) { // normal
                        if (mEntityStatus.getInt("mode_tweetlonger") == NewStatus.MODE_TL_TWITLONGER) { // twitlonger
                            updateTwitlonger();
                        } else {
                            updateStatus(NewStatus.MODE_TL_N_TWEETS);
                        }
                    } else { // directo
                        directMessage(NewStatus.MODE_TL_N_TWEETS);
                    }
                } else {
                    if (mEntityStatus.getInt("type_id") == 1) { // normal
                        updateStatus(NewStatus.MODE_TL_NONE);
                    } else { // directo
                        directMessage(NewStatus.MODE_TL_NONE);
                    }
                }
            }
        }
    }

    private void deleteCurrentId() {
        mUsersId.remove(mCurrentIdUser);
    }

    private void setErrorTweet() {
        if (mEntityStatus.getLong("tweet_programmed_id")>0) {
            Entity ent = mEntityStatus.getEntity("tweet_programmed_id");
            ent.setValue("is_sent", 2);
            ent.save();
        }
        mEntityStatus.setValue("is_sent", 1);
        mEntityStatus.save();
    }

    private void deleteTweet() {
        if (mEntityStatus.getLong("tweet_programmed_id")>0) {
            Entity ent = mEntityStatus.getEntity("tweet_programmed_id");
            ent.setValue("is_sent", 1);
            ent.save();
        }
        if (mEntityStatus.getLong("tweet_draft_id")>0) {
            Entity ent = mEntityStatus.getEntity("tweet_draft_id");
            ent.delete();
        }
        for (String photo : mPhotosRemoved) {
            File f = new File(Utils.appUploadImageDirectory+photo);
            if (f.exists()) f.delete();
        }
        try {
            mEntityStatus.delete();
        } catch (IllegalStateException e) {}
    }

    /*
    *
    * Update status Facebook
    *
    */

    public void updateStatusFacebook() {
        FacebookHandler fbh = new FacebookHandler(null);
        Facebook facebook = fbh.loadUser(mCurrentIdUser);
        if (facebook!=null) {
            Bundle params = new Bundle();
            params.putString("message", mText);

            String stream = "me/feed";

            if (mPhotosRemoved.size()>0) {

                stream = "me/photos";

                byte[] data = null;

                Bitmap bi = BitmapFactory.decodeFile(Utils.appUploadImageDirectory+mPhotosRemoved.get(0));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                data = baos.toByteArray();

                params.putByteArray("picture", data);
            }


            AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
            mAsyncRunner.request(stream, params, "POST", new RequestListener() {

                @Override
                public void onMalformedURLException(MalformedURLException e, Object state) {

                }

                @Override
                public void onIOException(IOException e, Object state) {

                }

                @Override
                public void onFileNotFoundException(FileNotFoundException e, Object state) {

                }

                @Override
                public void onFacebookError(FacebookError e, Object state) {

                }

                @Override
                public void onComplete(String response, Object state) {
                    deleteCurrentId();
                    launchTasks();
                    /*ServiceUpdateStatus.this.runOnUiThread(new Runnable() {
                             public void run() {
                                 Utils.showMessage(ServiceUpdateStatus.this, "Mensaje publicado");
                             }
                         });*/
                }
            }, "foo");
        }
    }

    /*
    *
    * Update status
    *
    */

    public void updateStatus(int modeTL) {
        new UploadStatusAsyncTask(this, this, twitter, modeTL).execute(mText, mEntityStatus.getString("reply_tweet_id"), mEntityStatus.getString("use_geo"));
    }

    @Override
    public void uploadStatusCancelled() {

    }

    @Override
    public void uploadStatusLoaded(boolean error) {
        if (error) {
            setErrorTweet();
            try {
                setMood(this.getString(R.string.update_status_error_msg), true);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.stopSelf();
        } else {
            deleteCurrentId();
            launchTasks();
        }
    }

    @Override
    public void uploadStatusLoading() {

    }

    /*
    *
    * Update Twitlonger
    *
    */


    public void updateTwitlonger() {

        new UploadTwitlongerAsyncTask(this, new UploadTwitlongerAsyncTaskResponder() {
            @Override
            public void uploadTwitlongerLoading() {
            }
            @Override
            public void uploadTwitlongerCancelled() {
            }
            @Override
            public void uploadTwitlongerLoaded(boolean error) {
                if (error) {
                    setErrorTweet();
                    try {
                        setMood(ServiceUpdateStatus.this.getString(R.string.update_status_error_msg), true);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ServiceUpdateStatus.this.stopSelf();
                } else {
                    deleteCurrentId();
                    launchTasks();
                }
            }
        }, twitter).execute(mText, mEntityStatus.getString("reply_tweet_id"), mEntityStatus.getString("use_geo"));

    }

    /*
    *
    * retweet
    *
    */

    public void retweetMessage() {
        new RetweetStatusAsyncTask(this, twitter).execute(mEntityStatus.getLong("reply_tweet_id"));
    }

    @Override
    public void retweetStatusLoading() {
    }

    @Override
    public void retweetStatusCancelled() {
    }

    @Override
    public void retweetStatusLoaded(boolean error) {
        if (error) {
            setErrorTweet();
            try {
                setMood(this.getString(R.string.update_status_error_msg), true);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.stopSelf();
        } else {
            deleteCurrentId();
            launchTasks();
        }
    }


    /*
     * 
     * Direct message
     * 
     */

    public void directMessage(int modeTL) {
        new DirectMessageAsyncTask(this, twitter, modeTL).execute(mText, mEntityStatus.getString("username_direct"));
    }

    @Override
    public void directMessageStatusCancelled() {

    }

    @Override
    public void directMessageStatusLoaded(boolean error) {
        if (error) {
            setErrorTweet();
            try {
                setMood(this.getString(R.string.update_status_error_msg), true);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.stopSelf();
        } else {
            deleteCurrentId();
            launchTasks();
        }

    }

    @Override
    public void directMessageStatusLoading() {

    }

    /*
    *
    * Upload image
    *
    */

    public void uploadImage(String file) {
        new ImageUploadAsyncTask(this, twitter).execute(file);
    }

    @Override
    public void imageUploadCancelled() {

    }

    @Override
    public void imageUploadLoaded(ImageUploadResult imageUploadResult) {
        boolean error = false;
        if (imageUploadResult.error) {
            error = true;
        } else {

            if ( (imageUploadResult.url!=null) && !imageUploadResult.url.equals("") ) {

                String name = "";
                StringTokenizer tokens = new StringTokenizer(imageUploadResult.file, ".");
                if (tokens.hasMoreTokens()) {
                    name = tokens.nextToken();
                }

                String base = mBaseURLImage + name;

                //Log.d(Utils.TAG, "Reemplazando " + base + " por " + imageUploadResult.url);

                mText = mText.replace(base, imageUploadResult.url);

                mPhotos.remove(imageUploadResult.file);
                mPhotosRemoved.add(imageUploadResult.file);

            } else {
                error = true;
            }


        }

        if (error) {
            setErrorTweet();
            try {
                setMood(this.getString(R.string.update_status_error_image), true);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.stopSelf();
        } else {
            launchTasks();
        }

    }

    @Override
    public void imageUploadLoading() {

    }

    /*
    *
    * Notificaciones
    *
    */

    private void setMood(String text, boolean tryAgain) {
        if (mEntityStatus.getLong("tweet_programmed_id")<=0) { // enviar sólo si no es un tweet programado
            Notification notification = new Notification(R.drawable.ic_stat_send_tweet, text, System.currentTimeMillis());
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            Intent i = new Intent();
            if (tryAgain) i = new Intent(this, LaunchServiceUpdateStatus.class);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
            notification.setLatestEventInfo(this, this.getText(R.string.app_name), text, contentIntent);


            ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(ID_NOTIFICATION, notification);
        }
    }

}
