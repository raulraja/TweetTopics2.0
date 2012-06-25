package com.javielinux.api;

import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;

public interface APIDelegate<T extends BaseResponse> {

    void onResults(T result);

    void onError(ErrorResponse error);

}