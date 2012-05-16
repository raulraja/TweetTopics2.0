package api.response;

import infos.InfoSaveTweets;

public class TwitterUserResponse implements BaseResponse {

    private long user_id = -1;
    private boolean loadOtherColumns = false;
    private InfoSaveTweets infoTimeline = null;
    private InfoSaveTweets infoMentions = null;
    private InfoSaveTweets infoDM = null;

    public long getUserId() {
        return user_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }

    public boolean getLoadOtherColumns() {
        return loadOtherColumns;
    }
    public void setLoadOtherColumns(boolean loadOtherColumns) {
        this.loadOtherColumns = loadOtherColumns;
    }

    public InfoSaveTweets getInfoTimeline() {
        return infoTimeline;
    }
    public void setInfoTimeline(InfoSaveTweets infoTimeline) {
        this.infoTimeline = infoTimeline;
    }

    public InfoSaveTweets getInfoMentions() {
        return infoMentions;
    }
    public void setInfoMentions(InfoSaveTweets infoMentions) {
        this.infoMentions = infoMentions;
    }

    public InfoSaveTweets getInfoDM() {
        return infoDM;
    }
    public void setInfoDM(InfoSaveTweets infoDM) {
        this.infoDM = infoDM;
    }
}
