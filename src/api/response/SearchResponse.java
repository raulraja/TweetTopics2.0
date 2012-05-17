package api.response;

import adapters.ResponseListAdapter;
import infos.InfoSaveTweets;

public class SearchResponse implements BaseResponse {

    private ResponseListAdapter responseListAdapter = null;
    private InfoSaveTweets infoSaveTweets = null;

    public ResponseListAdapter getListAdapter() {
        return responseListAdapter;
    }
    public void  setListAdapter(ResponseListAdapter responseListAdapter) {
        this.responseListAdapter = responseListAdapter;
    }

    public InfoSaveTweets getInfoSaveTweets() {
        return infoSaveTweets;
    }
    public void  setInfoSaveTweets(InfoSaveTweets infoSaveTweets) {
        this.infoSaveTweets = infoSaveTweets;
    }
}
