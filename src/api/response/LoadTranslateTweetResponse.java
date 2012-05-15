package api.response;

import infos.InfoUsers;

public class LoadTranslateTweetResponse implements BaseResponse {

    private InfoUsers infoUsers;

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }

    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }
}
