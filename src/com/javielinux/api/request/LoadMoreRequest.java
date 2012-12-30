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

public class LoadMoreRequest implements BaseRequest {

    private long targetId;
    private int typeList;
    private int typeLastColumn;

    public LoadMoreRequest(long targetId, int typeList, int typeLastColumn) {
        this.targetId = targetId;
        this.typeList = typeList;
        this.typeLastColumn = typeLastColumn;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public int getTypeList() {
        return typeList;
    }

    public void setTypeList(int typeList) {
        this.typeList = typeList;
    }

    public int getTypeLastColumn() {
        return typeLastColumn;
    }

    public void setTypeLastColumn(int typeLastColumn) {
        this.typeLastColumn = typeLastColumn;
    }
}
