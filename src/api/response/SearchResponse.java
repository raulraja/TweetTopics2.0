package api.response;

import infos.InfoSaveTweets;

public class SearchResponse implements BaseResponse {

    private InfoSaveTweets infoSaveTweets = null;


    public InfoSaveTweets getInfoSaveTweets() {
        return infoSaveTweets;
    }
    public void  setInfoSaveTweets(InfoSaveTweets infoSaveTweets) {
        this.infoSaveTweets = infoSaveTweets;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
