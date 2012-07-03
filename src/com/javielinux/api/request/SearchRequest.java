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
}
