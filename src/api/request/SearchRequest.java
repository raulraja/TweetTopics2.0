package api.request;

import database.EntitySearch;

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
