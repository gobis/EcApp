package com.gw.ecapp.engine.udpEngine.packetCreator;

import com.google.gson.annotations.SerializedName;
import com.gw.ecapp.AppUtils;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by iningosu on 8/25/2017.
 */

public class Message {


    @SerializedName("origin")
    private String mOrigin;

    @SerializedName("id")
    private String mMessageId;

    @SerializedName("retryCount")
    private transient int mRetryCount  = 0;

    @SerializedName("timestamp")
    private transient  long mTimeStampInMillis;

    @SerializedName("command")
    private String mCommand;

    public Message(String command){
        mMessageId = UUID.randomUUID().toString();
        mTimeStampInMillis = Calendar.getInstance().getTimeInMillis();
        mOrigin = AppUtils.ORIGIN;
        mCommand = command ;
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
