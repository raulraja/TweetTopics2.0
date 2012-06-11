package api.response;

import infos.InfoUsers;

public class LoadUserResponse implements BaseResponse {

    private InfoUsers infoUsers;

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }
    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
