package api.request;

public class LoadUserRequest implements BaseRequest {

    private String user = "";

    public LoadUserRequest(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

}
