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

package com.javielinux.api.response;

import twitter4j.UserList;

import java.util.ArrayList;

public class GetUserListResponse implements BaseResponse {

    private boolean ready;
    private long nextCursor = -1;
    private ArrayList<UserList> userListArrayList = new ArrayList<UserList>();

    public boolean getReady() {
        return this.ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public long getNextCursor() {
        return this.nextCursor;
    }

    public void setNextCursor(long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public ArrayList<UserList> getUserListArrayList() {
        return this.userListArrayList;
    }

    public void setUserListArrayList(ArrayList<UserList> userListArrayList) {
        this.userListArrayList = userListArrayList;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
