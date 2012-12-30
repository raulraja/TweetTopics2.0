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

package com.javielinux.api.request;

public class DirectMessageRequest implements BaseRequest {

    private long userId = 0;
    private int modeTweetLonger = 0;
    private String text = "";
    private String user = "";

    public DirectMessageRequest(long userId, int modeTweetLonger, String user, String text) {
        this.modeTweetLonger = modeTweetLonger;
        this.userId = userId;
        this.user = user;
        this.text = text;
    }


    public int getModeTweetLonger() {
        return modeTweetLonger;
    }

    public void setModeTweetLonger(int modeTweetLonger) {
        this.modeTweetLonger = modeTweetLonger;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
