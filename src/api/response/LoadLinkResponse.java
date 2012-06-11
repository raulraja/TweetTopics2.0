package api.response;


import infos.InfoLink;

public class LoadLinkResponse implements BaseResponse {
    private InfoLink infoLink;

    public InfoLink getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(InfoLink infoLink) {
        this.infoLink = infoLink;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
