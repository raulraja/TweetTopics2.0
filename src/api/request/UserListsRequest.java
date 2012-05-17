package api.request;

public class UserListsRequest implements BaseRequest {

    private int action = 0;
    private String addUser = "";
    private String user = "";

    public UserListsRequest(int action, String addUser, String user) {
        this.action = action;
        this.addUser = addUser;
        this.user = user;
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
}
