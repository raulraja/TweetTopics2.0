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

import android.location.Address;

import java.util.ArrayList;

public class GetGeolocationAddressResponse implements BaseResponse {

    private ArrayList<Address> address_list;
    private boolean single_result;

    public ArrayList<Address> getAddressList() {
        return this.address_list;
    }

    public void setAddressList(ArrayList<Address> address_list) {
        this.address_list = address_list;
    }

    public boolean getSingleResult() {
        return this.single_result;
    }

    public void setSingleResult(boolean single_result) {
        this.single_result = single_result;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
