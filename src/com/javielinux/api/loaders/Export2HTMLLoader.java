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
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.Export2HTMLRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.Export2HTMLResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Export2HTMLLoader extends AsynchronousLoader<BaseResponse> {

    private ArrayList<InfoTweet> tweets;

    public Export2HTMLLoader(Context context, Export2HTMLRequest request) {
        super(context);
        tweets = request.getTweets();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {

            Export2HTMLResponse response = new Export2HTMLResponse();

            if (tweets==null) return response;

            String f = Utils.appDirectory+"export_html_"+ System.currentTimeMillis() +".html";

            File file = new File(f);
            if (file.exists()) file.delete();
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fOut, "ISO-8859-1");

            osw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n"
                    + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "<title>Listado de tweets</title>\n"
                    + "<style type=\"text/css\">\n"
                    + "body {\n"
                    + "padding: 10px;\n"
                    + "background-color: #89ace5;\n"
                    + "}\n"
                    + "div.tweet {\n"
                    + "font-family: Helvetica, Geneva, Arial, sans-serif;\n"
                    + "border: 2px solid #286cd9;\n"
                    + "background-color: #ffffff;\n"
                    + "-webkit-border-radius: 10px;\n"
                    + "moz-border-radius: 10px;\n"
                    + "border-radius: 10px;\n"
                    + "padding: 5px;\n"
                    + "margin-bottom: 10px;\n"
                    + "}\n"
                    + "div.username {\n"
                    + "font-size: 13px;\n"
                    + "font-weight: bolder;\n"
                    + "}\n"
                    + "div.date {\n"
                    + "font-size: 11px;\n"
                    + "color: #999999;\n"
                    + "}\n"
                    + ".image img {\n"
                    + "width: 50px;\n"
                    + "height: 50px;\n"
                    + "margin: 5px;\n"
                    + "float: left;\n"
                    + "}\n"
                    + "p {\n"
                    + "margin-left: 10px;\n"
                    + "font-size: 14px;\n"
                    + "}\n"
                    + "</style>\n"
                    + "</head>\n"
                    + "<body>\n");


            for (int i=0; i<tweets.size(); i++) {
                InfoTweet info = tweets.get(i);
                String t = "<div class=\"tweet\">\n"
                        + "<div class=\"image\">\n";
                if (info.isRetweet()) {
                    t += "<img src=\""+info.getUrlAvatarRetweet()+"\" />\n";
                } else {
                    t += "<img src=\""+info.getUrlAvatar()+"\" />\n";
                }
                t += "</div>\n";
                if (info.isRetweet()) {
                    t +="<div class=\"username\">"+info.getUsernameRetweet()+ " - " + getContext().getString(R.string.retweet_by) + " " + info.getUsername()+ "</div>\n";
                } else {
                    t +="<div class=\"username\">"+info.getUsername()+"</div>\n";
                }
                t += "<p>"+Utils.toExportHTML(getContext(), info.getText())+"</p>\n"
                        + "<div class=\"date\">"+info.getDate().toLocaleString()+" - (<a href=\"" + info.getUrlTweet() + "\">twitter</a>) " + "</div>\n"
                        + "</div>\n";
                osw.append(t);
            }


            osw.append("</body>\n"
                    + "</html>");

            osw.flush();
            osw.close();

            response.setUrl(f);

            return response;

        } catch (FileNotFoundException exception) {
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

}