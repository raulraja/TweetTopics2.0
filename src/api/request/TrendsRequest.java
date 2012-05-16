package api.request;

public class TrendsRequest implements BaseRequest {

    private int location_id = 0;

    public TrendsRequest(int location_id) {
        this.location_id = location_id;
    }

    public int getLocationId() {
        return location_id;
    }
    public void setLocationId(int location_id) {
        this.location_id = location_id;
    }
}
