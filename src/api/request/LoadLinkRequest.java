package api.request;

import infos.InfoLink;

public class LoadLinkRequest implements BaseRequest {

    private InfoLink infoLink;

    public LoadLinkRequest(InfoLink infoLink) {
        this.infoLink = infoLink;

    }

    public InfoLink getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(InfoLink infoLink) {
        this.infoLink = infoLink;
    }
}
