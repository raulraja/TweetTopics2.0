package api.response;

import twitter4j.Trend;

public class TrendsResponse implements BaseResponse {

    private Trend[] trend_list;

    public Trend[] getTrends() {
        return trend_list;
    }
    public void setTrends(Trend[] trend_list) {
        this.trend_list = trend_list;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
