package api;

import api.response.BaseResponse;
import api.response.ErrorResponse;

public interface APIDelegate<T extends BaseResponse> {

    void onResults(T result);

    void onError(ErrorResponse error);

}