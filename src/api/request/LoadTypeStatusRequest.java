package api.request;

public class LoadTypeStatusRequest implements BaseRequest {

    private int type = 0;
    private String user_search_text = "";
    private String user = "";
    private int list_id = 0;

    public LoadTypeStatusRequest(int type, String user, String user_search_text, int list_id) {
        this.type = type;
        this.user = user;
        this.user_search_text = user_search_text;
        this.list_id = list_id;
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
}
