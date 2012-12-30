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

package com.javielinux.api.loaders;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.UploadTwitlongerRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.UploadTwitlongerResponse;
import com.javielinux.utils.LocationUtils;
import com.javielinux.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class UploadTwitlongerLoader extends AsynchronousLoader<BaseResponse> {

    private Twitter twitter;
    private String tweet_text;
    private long tweet_id;
    private boolean use_geolocation;

    public UploadTwitlongerLoader(Context context, UploadTwitlongerRequest request) {
        super(context);

        this.twitter = request.getTwitter();
        this.tweet_text = request.getTweetText();
        this.tweet_id = request.getTweetId();
        this.use_geolocation = request.getUseGeolocation();
    }

    @Override
    public BaseResponse loadInBackground() {

        //TODO: Comprobar el valor devuelto con el valor esperado (error - ready) y el parámetro user_geolocation
        try {
            Log.d(Utils.TAG, "Enviando a twitlonger: " + tweet_text);

            String textTwitLonger = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.twitlonger.com/api_post");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("application", "tweettopics"));
                nameValuePairs.add(new BasicNameValuePair("api_key", "f7y8lgz31srR46sr"));
                nameValuePairs.add(new BasicNameValuePair("username", twitter.getScreenName()));
                nameValuePairs.add(new BasicNameValuePair("message", tweet_text));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse httpResponse = httpclient.execute(httppost);

                String xml = EntityUtils.toString(httpResponse.getEntity());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser x = factory.newPullParser();

                x.setInput(new StringReader(xml));

                String error = "";

                int eventType = x.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (x.getName().equals("error")) {
                            error = x.nextText();
                        }
                        if (x.getName().equals("content")) {
                            textTwitLonger = x.nextText();
                            Log.d(Utils.TAG, "Enviando a twitter: " + textTwitLonger);
                        }
                    }
                    eventType = x.next();
                }

                if (!error.equals("")) {
                    Log.d(Utils.TAG, "Error: " + error);
                    ErrorResponse response = new ErrorResponse();
                    response.setError(error);
                    return response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ErrorResponse response = new ErrorResponse();
                response.setError(e, e.getMessage());
                return response;
            }

            UploadTwitlongerResponse response = new UploadTwitlongerResponse();

            if (!textTwitLonger.equals("")) {
                StatusUpdate statusUpdate = new StatusUpdate(textTwitLonger);
                if (use_geolocation) {
                    Location loc = LocationUtils.getLastLocation(getContext());
                    GeoLocation gl = new GeoLocation(loc.getLatitude(), loc.getLongitude());
                    statusUpdate.setLocation(gl);
                }
                if (tweet_id>0) statusUpdate.inReplyToStatusId(tweet_id);
                twitter.updateStatus(statusUpdate);

                response.setReady(true);
            } else {
                response.setReady(false);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
