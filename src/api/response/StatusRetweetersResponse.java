package api.response;

import twitter4j.ResponseList;
import twitter4j.User;

public class StatusRetweetersResponse implements BaseResponse {

    private ResponseList<User> responseList;

    public ResponseList<User> getUserList() {
        return responseList;
    }
    public void setUserList(ResponseList<User> responseList) {
        this.responseList = responseList;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
