package com.gw.ecapp.engine.udpEngine.udpComms;

/**
 * Created by gobi on 12/29/2017.
 */

public interface UDPRequestStatus {

    /**
     * call back when udp request is successful
     */
    void requestSuccess();

    /**
     * call back when given requst if timeout
     */
    void requestTimeOut();

    /**
     * call back for retry count
     * @param retryCount
     */
    void requestRetryCount(int retryCount);
}
