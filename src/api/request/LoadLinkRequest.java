package api.request;

import infos.InfoLink;

public class LoadLinkRequest implements BaseRequest {

    private InfoLink infoLink;
    private String link;

    public LoadLinkRequest(String link, InfoLink infoLink) {
        this.infoLink = infoLink;
        this.link = link;
    }

    public InfoLink getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(InfoLink infoLink) {
        this.infoLink = infoLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
