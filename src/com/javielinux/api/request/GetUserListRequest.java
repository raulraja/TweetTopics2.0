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

public class GetUserListRequest implements BaseRequest {

    private long userId;
    private String screenName;
    private int userListType;
    private long cursor;

    public GetUserListRequest(long userId, String screenName, int userListType, long cursor) {
        this.userId = userId;
        this.screenName = screenName;
        this.userListType = userListType;
        this.cursor = cursor;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getUserListType() {
        return userListType;
    }
    public void setUserListType(int userListType) {
        this.userListType = userListType;
    }

    public long getCursor() {
        return cursor;
    }
    public void setCursor(long cursor) {
        this.cursor = cursor;
    }
}
