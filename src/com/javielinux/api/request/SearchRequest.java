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

import com.javielinux.database.EntitySearch;

public class SearchRequest implements BaseRequest {

    private EntitySearch entitySearch = null;
    private long since_id = -1;

    public SearchRequest(EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }

    public EntitySearch getEntitySearch() {
        return entitySearch;
    }
    public void setEntitySearch(EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }

    public long getSinceId() {
        return since_id;
    }
    public void setSinceId(long since_id) {
        this.since_id = since_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchRequest that = (SearchRequest) o;

        if (since_id != that.since_id) return false;
        if (entitySearch != null ? !entitySearch.equals(that.entitySearch) : that.entitySearch != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entitySearch != null ? entitySearch.hashCode() : 0;
        result = 31 * result + (int) (since_id ^ (since_id >>> 32));
        return result;
    }
}
