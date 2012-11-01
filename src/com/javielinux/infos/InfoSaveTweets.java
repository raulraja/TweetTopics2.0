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
