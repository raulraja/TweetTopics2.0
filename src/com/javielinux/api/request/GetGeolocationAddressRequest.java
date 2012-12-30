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

package com.javielinux.api.request;

import android.content.Context;

public class GetGeolocationAddressRequest implements BaseRequest {

    private Context context = null;
    private String text = "";
    private boolean single_result;

    public GetGeolocationAddressRequest(Context context, String text, boolean single_result) {
        this.context = context;
        this.text = text;
        this.single_result = single_result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getSingleResult() {
        return single_result;
    }

    public void setSingleResult(boolean single_result) {
        this.single_result = single_result;
    }
}
