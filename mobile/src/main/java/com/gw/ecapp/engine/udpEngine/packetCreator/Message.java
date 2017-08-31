package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.engine.udpEngine.AppUtils;

/**
 * Created by iningosu on 8/25/2017.
 */

public class Message {


    @SerializedName("id")
    private String mMessageId;

    @SerializedName("retryCount")
    private int mRetryCount  = 0;

    @SerializedName("timestamp")
    private long mTimeStampInMillis;

    public Message(){

    }

    public Message(String messageId , long timestamp) {
        this.mMessageId = messageId;
        mTimeStampInMillis = timestamp;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        this.mMessageId = messageId;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public void setRetryCount(int retryCount) {
        this.mRetryCount = retryCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return mMessageId.equals(message.mMessageId);
    }

    @Override
    public int hashCode() {
        return mMessageId.hashCode();
    }
}
