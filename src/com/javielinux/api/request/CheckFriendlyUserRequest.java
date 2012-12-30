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

import com.javielinux.infos.InfoUsers;

public class CheckFriendlyUserRequest implements BaseRequest {

    private String user = "";
    private String userCheck = "";
    private InfoUsers infoUsers;

    public CheckFriendlyUserRequest(InfoUsers infoUsers, String user, String userCheck) {
        this.infoUsers = infoUsers;
        this.user = user;
        this.userCheck = userCheck;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String userCheck) {
        this.userCheck = userCheck;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user.replace("@", "");
    }

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }

    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }
}
