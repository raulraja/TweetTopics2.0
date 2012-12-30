/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.updatestatus;

import android.app.Service;
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
import com.javielinux.task.*;
import com.javielinux.task.DirectMessageAsyncTask.DirectMessageAsyncTaskResponder;
import com.javielinux.task.ImageUploadAsyncTask.ImageUploadAsyncTaskResponder;
import com.javielinux.task.ImageUploadAsyncTask.ImageUploadResult;
import com.javielinux.task.UploadStatusAsyncTask.UploadStatusAsyncTaskResponder;
import com.javielinux.task.UploadTwitlongerAsyncTask.UploadTwitlongerAsyncTaskResponder;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.*;
import twitter4j.Twitter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ServiceUpdateStatus extends Service implements UploadStatusAsyncTaskResponder, ImageUploadAsyncTaskResponder,
        DirectMessageAsyncTaskResponder, RetweetStatusAsyncTask.RetweetStatusAsyncTaskResponder {

    private ArrayList<Long> usersId;
    private HashMap<Long,String> usersNetwork;

    private ArrayList<String> photos;
    private ArrayList<String> photosRemoved;

    private Entity entityStatus = null;

    private long currentIdUser = 0;

    public static Twitter twitter;

    private String text = "";

    private String baseURLImage = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * For all versión, lower 2.0
     * @param intent
     * @param startId
     */

    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public void handleCommand(Intent intent) {

        usersId = new ArrayList<Long>();
        usersNetwork = new HashMap<Long, String>();
        photos = new ArrayList<String>();
        photosRemoved = new ArrayList<String>();

        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionManager.getInstance().open(this);

        Log.d(Utils.TAG, "Creando nuevo estado");

        entityStatus = DataFramework.getInstance().getTopEntity("send_tweets", "is_sent = 0", "");

        if (entityStatus !=null) {

            ArrayList<Long> auxUsersTwitter = new ArrayList<Long>();
            ArrayList<Long> auxUsersOthers = new ArrayList<Long>();

            String users = entityStatus.getString("users");

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
                usersId.add(user);
                usersNetwork.put(user, ConstantUtils.NETWORK_TWITTER_NAME);
            }
            for (long user : auxUsersOthers) {
                usersId.add(user);
                usersNetwork.put(user, ConstantUtils.NETWORK_FACEBOOK_NAME);
            }

            text = entityStatus.getString("text");

            String photos = entityStatus.getString("photos");

            if (!photos.equals("")) {

                int type = Integer.parseInt(Utils.getPreference(this).getString("prf_service_image", "1"));
                if (type==1) {
                    baseURLImage = NewStatusActivity.URL_BASE_YFROG;
                } else if (type==2) {
                    baseURLImage = NewStatusActivity.URL_BASE_TWITPIC;
                } else if (type==3) {
                    baseURLImage = NewStatusActivity.URL_BASE_LOCKERZ;
                }

                StringTokenizer tokens = new StringTokenizer(photos, "--");

                while(tokens.hasMoreTokens()) {
                    String photo = tokens.nextToken();
                    if (!photo.equals("")) {
                        this.photos.add(photo);
                    }
                }

            }

            // empezamos a enviar

            try {
                sendNotification(this.getString(R.string.update_status_uploading_msg), false);
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
        if (photos.size()>0) {
            twitter = ConnectionManager.getInstance().getTwitter(usersId.get(0));
            try {
                sendNotification(this.getString(R.string.update_status_uploading_image), false);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadImage(photos.get(0));
        } else {
            if (usersId.size()>0) {
                sendTweet(usersId.get(0));
            } else {
                deleteTweet();
                try {
                    sendNotification(this.getString(R.string.update_status_correct), false);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NotificationUtils.cancelNotification(this);
                Intent update = new Intent();
                update.putExtra("refresh-column", TweetTopicsUtils.TWEET_TYPE_TIMELINE);
                update.setAction(Intent.ACTION_VIEW);
                sendOrderedBroadcast(update, null);
            }
        }
    }

    private void sendTweet(long id) {

        currentIdUser = id;


        if (usersNetwork.get(id).equals("facebook")) {
            updateStatusFacebook();
        } else {
            twitter = ConnectionManager.getInstance().getTwitter(id);

            if (entityStatus.getInt("type_id") == 3) { // retweet
                retweetMessage();
            } else {
                int shortURLLength = PreferenceUtils.getShortURLLength(this);
                int shortURLLengthHttps = PreferenceUtils.getShortURLLengthHttps(this);

                if (Utils.getLenghtTweet(text, shortURLLength, shortURLLengthHttps)>140) {
                    if (entityStatus.getInt("type_id") == 1) { // normal
                        if (entityStatus.getInt("mode_tweetlonger") == NewStatusActivity.MODE_TL_TWITLONGER) { // twitlonger
                            updateTwitlonger();
                        } else {
                            updateStatus(NewStatusActivity.MODE_TL_N_TWEETS);
                        }
                    } else { // directo
                        directMessage(NewStatusActivity.MODE_TL_N_TWEETS);
                    }
                } else {
                    if (entityStatus.getInt("type_id") == 1) { // normal
                        updateStatus(NewStatusActivity.MODE_TL_NONE);
                    } else { // directo
                        directMessage(NewStatusActivity.MODE_TL_NONE);
                    }
                }
            }
        }

    }

    private void deleteCurrentId() {
        usersId.remove(currentIdUser);
    }

    private void setErrorTweet() {
        if (entityStatus.getLong("tweet_programmed_id")>0) {
            Entity ent = entityStatus.getEntity("tweet_programmed_id");
            ent.setValue("is_sent", 2);
            ent.save();
        }
        entityStatus.setValue("is_sent", 1);
        entityStatus.save();
    }

    private void deleteTweet() {
        if (entityStatus.getLong("tweet_programmed_id")>0) {
            Entity ent = entityStatus.getEntity("tweet_programmed_id");
            ent.setValue("is_sent", 1);
            ent.save();
        }
        if (entityStatus.getLong("tweet_draft_id")>0) {
            Entity ent = entityStatus.getEntity("tweet_draft_id");
            ent.delete();
        }
        for (String photo : photosRemoved) {
            File f = new File(Utils.appUploadImageDirectory+photo);
            if (f.exists()) f.delete();
        }
        try {
//            try {
//                DataFramework.getInstance().open(this, Utils.packageName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            entityStatus.delete();
//            DataFramework.getInstance().close();
        } catch (IllegalStateException e) {
        } catch (Exception e) {}
    }

    /*
    *
    * Update status Facebook
    *
    */

    public void updateStatusFacebook() {
        FacebookHandler fbh = new FacebookHandler(null);
        Facebook facebook = fbh.loadUser(currentIdUser);
        if (facebook!=null) {
            Bundle params = new Bundle();
            params.putString("message", text);

            String stream = "me/feed";

            if (photosRemoved.size()>0) {

                stream = "me/photos";

                byte[] data = null;

                Bitmap bi = BitmapFactory.decodeFile(Utils.appUploadImageDirectory+ photosRemoved.get(0));
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
        new UploadStatusAsyncTask(this, this, twitter, modeTL).execute(text, entityStatus.getString("reply_tweet_id"), entityStatus.getString("use_geo"));
    }

    @Override
    public void uploadStatusCancelled() {

    }

    @Override
    public void uploadStatusLoaded(boolean error) {
        if (error) {
            setErrorTweet();
            try {
                sendNotification(this.getString(R.string.update_status_error_msg), true);
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
                        sendNotification(ServiceUpdateStatus.this.getString(R.string.update_status_error_msg), true);
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
        }, twitter).execute(text, entityStatus.getString("reply_tweet_id"), entityStatus.getString("use_geo"));

    }

    /*
    *
    * retweet
    *
    */

    public void retweetMessage() {
        new RetweetStatusAsyncTask(this, twitter).execute(entityStatus.getLong("reply_tweet_id"));
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
                sendNotification(this.getString(R.string.update_status_error_msg), true);
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
        new DirectMessageAsyncTask(this, twitter, modeTL).execute(text, entityStatus.getString("username_direct"));
    }

    @Override
    public void directMessageStatusCancelled() {

    }

    @Override
    public void directMessageStatusLoaded(boolean error) {
        if (error) {
            setErrorTweet();
            try {
                sendNotification(this.getString(R.string.update_status_error_msg), true);
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

                String base = baseURLImage + name;

                //Log.d(Utils.TAG, "Reemplazando " + base + " por " + imageUploadResult.url);

                text = text.replace(base, imageUploadResult.url);

                photos.remove(imageUploadResult.file);
                photosRemoved.add(imageUploadResult.file);

            } else {
                error = true;
            }


        }

        if (error) {
            setErrorTweet();
            try {
                sendNotification(this.getString(R.string.update_status_error_image), true);
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

    private void sendNotification(String text, boolean tryAgain) {
        if (entityStatus.getLong("tweet_programmed_id")<=0) { // enviar sólo si no es un tweet programado
            NotificationUtils.sendNotification(this, getString(R.string.app_name), text, "", true, false);
        }
    }

}
