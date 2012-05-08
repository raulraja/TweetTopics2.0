package api;

public interface APIDelegate {

    void onResults(APIResult result);

    void onError(APIResult error);

}