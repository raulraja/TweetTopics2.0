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

package com.javielinux.infos;

import com.javielinux.utils.Utils;
import twitter4j.RateLimitStatus;

import java.util.ArrayList;
import java.util.List;


public class InfoSaveTweets {

	private long newerId = 0;
	private long olderId = 0;
	private int error = Utils.NOERROR;
	private RateLimitStatus rate = null;
	private int newMessages = 0;

    private List<Long> ids = new ArrayList<Long>();
	
	public InfoSaveTweets() {
		
	}

	public void setNewerId(long newerId) {
		this.newerId = newerId;
	}

	public long getNewerId() {
		return newerId;
	}

	public void setOlderId(long olderId) {
		this.olderId = olderId;
	}

	public long getOlderId() {
		return olderId;
	}

	public void setRate(RateLimitStatus rate) {
		this.rate = rate;
	}

	public RateLimitStatus getRate() {
		return rate;
	}

	public void setNewMessages(int newMessages) {
		this.newMessages = newMessages;
	}

	public int getNewMessages() {
		return newMessages;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int getError() {
		return error;
	}


    public List<Long> getIds() {
        return ids;
    }

    public void addId(long id) {
        ids.add(id);
    }
}
