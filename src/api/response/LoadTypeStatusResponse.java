package api.response;

import adapters.RowResponseList;
import infos.InfoTweet;

import java.util.ArrayList;

public class LoadTypeStatusResponse implements BaseResponse {

    private ArrayList<InfoTweet> infoTweetArrayList;

    public ArrayList<InfoTweet> getInfoTweetList() {
        return infoTweetArrayList;
    }
    public void setInfoTweetList(ArrayList<InfoTweet> list) {
        this.infoTweetArrayList = list;
    }
}
