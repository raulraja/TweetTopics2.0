package com.javielinux.api.request;

import com.javielinux.database.EntitySearch;

public class SearchRequest implements BaseRequest {

    private EntitySearch entitySearch = null;

    public SearchRequest(EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }


    public EntitySearch getEntitySearch() {
        return entitySearch;
    }
    public void setEntitySearch(EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }
}
