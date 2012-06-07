package api.request;

public class LoadUserRequest implements BaseRequest {

    private long userId = 0;
    private String user = "";

    public LoadUserRequest(long userId, String user) {
        this.user = user;
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
