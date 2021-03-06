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

public class LoadTypeStatusRequest implements BaseRequest {

    private long userId = 0;
    private int type = 0;
    private String user_search_text = "";
    private String user = "";
    private int list_id = 0;
    private long[] userIdList;

    public LoadTypeStatusRequest(long userId, int type, String user, String user_search_text, int list_id, long[] userIdList) {
        this.userId = userId;
        this.type = type;
        this.user = user;
        this.user_search_text = user_search_text;
        this.list_id = list_id;
        this.userIdList = userIdList;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getUserSearchText() {
        return user_search_text;
    }
    public void setUserSearchText(String user_search_text) {
        this.user_search_text = user_search_text;
    }

    public int getListId() {
        return list_id;
    }
    public void setListId(int list_id) {
        this.list_id = list_id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long[] getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(long[] userIdList) {
        this.userIdList = userIdList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadTypeStatusRequest that = (LoadTypeStatusRequest) o;

        if (list_id != that.list_id) return false;
        if (type != that.type) return false;
        if (userId != that.userId) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (user_search_text != null ? !user_search_text.equals(that.user_search_text) : that.user_search_text != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + type;
        result = 31 * result + (user_search_text != null ? user_search_text.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + list_id;
        return result;
    }
}
