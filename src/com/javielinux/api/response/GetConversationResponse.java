package com.javielinux.api.response;


import com.javielinux.infos.InfoTweet;

public class GetConversationResponse implements BaseResponse {
    private InfoTweet conversationTweet;

    public InfoTweet getConversationTweet() {
        return conversationTweet;
    }

    public void setConversationTweet(InfoTweet conversationTweet) {
        this.conversationTweet = conversationTweet;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
