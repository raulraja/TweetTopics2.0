package api.request;

public class UserListsRequest implements BaseRequest {

    private long userId = 0;
    private int action = 0;
    private String addUser = "";
    private String user = "";

    public UserListsRequest(long userId, int action, String addUser, String user) {
        this.action = action;
        this.addUser = addUser;
        this.user = user;
        this.userId = userId;
    }

    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getAddUser() {
        return addUser;
    }
    public void setAddUser(String addUser) {
        this.addUser = addUser;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
