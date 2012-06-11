package api.response;

import twitter4j.ResponseList;
import twitter4j.UserList;

public class UserListsResponse implements BaseResponse {

    public ResponseList<UserList> responseList = null;
    public int action = 0;
    public String addUser = "";

    public ResponseList<UserList> getUserList() {
        return responseList;
    }
    public void setUserList(ResponseList<UserList> responseList) {
        this.responseList = responseList;
    }

    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }

    public String getAddUser() {
        return addUser;
    }
    public void setAddUser(String addUser) {
        this.addUser = addUser;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
