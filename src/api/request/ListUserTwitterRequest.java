package api.request;

public class ListUserTwitterRequest implements BaseRequest {

    private String user;

    public ListUserTwitterRequest(String user) {
        this.user = user;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
