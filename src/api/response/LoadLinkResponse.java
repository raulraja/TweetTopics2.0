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
}
