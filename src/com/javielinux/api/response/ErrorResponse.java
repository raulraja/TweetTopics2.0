/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.api.response;

import com.javielinux.utils.Utils;
import twitter4j.RateLimitStatus;

public class ErrorResponse implements BaseResponse {
    private int typeError = Utils.NOERROR;
    private RateLimitStatus rateError = null;

    private Throwable error;
    private String msgError;

    public Throwable getError() {
        return error;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setError(Throwable error, String msgError) {
        this.error = error;
        this.msgError = msgError;
    }

    public void setError(String msgError) {
        this.msgError = msgError;
    }

    public int getTypeError() {
        return typeError;
    }

    public void setTypeError(int typeError) {
        this.typeError = typeError;
    }

    public RateLimitStatus getRateError() {
        return rateError;
    }

    public void setRateError(RateLimitStatus rateError) {
        this.rateError = rateError;
    }

    @Override
    public boolean isError() {
        return true;
    }
}
