package com.gw.ecapp.engine.udpEngine.udpComms;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.AppUtils;
import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by iningosu on 8/25/2017.
 */

public class UDPClient extends CommEngine {


    private static UDPClient mUDP_instance;
    private Context mContext;
    private DatagramSocket clientReceiveSocket;
    private DatagramSocket clientSendSocket;
    private Thread receiverThread;

    private Gson gson ;

    private ArrayList<Message> mRequestDataList;

    Handler mUiHandler;

    public static UDPClient getInstance(Context context) {
        if (null == mUDP_instance) {
            synchronized (UDPClient.class) {
                mUDP_instance = new UDPClient();
                mUDP_instance.mContext = context;
                mUDP_instance.gson = new Gson();
                mUDP_instance.mRequestDataList = new ArrayList<>();
            }
        }
        return mUDP_instance;
    }


    private UDPClient() {

        try {
            if (null == clientReceiveSocket) {
                clientReceiveSocket = new DatagramSocket(AppUtils.UDP_UNI_CAST_PORT);
            }
            if (null == clientSendSocket) {
                clientSendSocket = new DatagramSocket();
            }

         /*   if (null == clients) {
                clients = new CopyOnWriteArrayList<>();
            }*/

        } catch (SocketException se) { se.printStackTrace(); }
          /*catch (Exception e) { e.printStackTrace(); }*/

        // create handler in main thread
        if (mUiHandler == null) {
            mUiHandler = new Handler();
        }

        if (null == receiverThread) {
            receiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ReceiveMessage();
                }
            }, "MsgListeningThread");

            receiverThread.start();
        }
    }


    public void SendMessage(byte[] dataToSend) {
        try {
            InetAddress IPAddress = InetAddress.getByName(AppUtils.UDP_UNI_CAST_IP)/*AppUtils.UDP_UNI_CAST_IP*/;
            Log.i(getClass().getSimpleName(), "Sending Data to IP:PORT" + IPAddress.toString() + ":"
                    +AppUtils.UDP_UNI_CAST_PORT + " Data is :" + new String(dataToSend, StandardCharsets.UTF_8));
            DatagramPacket send_packet = new DatagramPacket(dataToSend, dataToSend.length,
                    IPAddress, AppUtils.UDP_UNI_CAST_PORT);
            clientSendSocket.send(send_packet);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
        }
    }


    private void ReceiveMessage() {
        try {

            while (true) {
                byte[] receiveData = new byte[AppUtils.MAX_MSG_LENGTH];
                DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
                Log.i(getClass().getSimpleName(), "Listening Port Number 5665");

                clientReceiveSocket.receive(receive_packet);
                final byte[] responseBytes = Arrays.copyOf(receive_packet.getData(), receive_packet.getLength());

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String recdData = new String(responseBytes, StandardCharsets.UTF_8);
                        removeItemFromQueueAfterResponse(recdData);
                    }
                });

               /* if (isTimeOut) {
                    Log.i(getClass().getSimpleName(),"Timed Out");

                } else {
                    retry_count = 0;
                    mUiHandler.removeCallbacks(timerRun);

                    mUDP_Response = AppUtils.byteArrayToHex(responseBytes);
                    Log.i(getClass().getSimpleName(), " UniCast response : " + mUDP_Response);

                    int error_code = ParseResponseHeader(responseBytes);

                    if (error_code == 0) {
                        NotifyClients(AppUtils.RequestStatus.SUCCESS, AppUtils.ERROR_CODES.RFS_SUCCESS);
                    } else {
                        NotifyClients(AppUtils.RequestStatus.ERROR, err_code);
                    }
                }*/

            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
            e.printStackTrace();
        }
    }

    public void SendMessageToGateway(Message msg) {
        String finalDataToSend = gson.toJson(msg);
        mRequestDataList.add(msg);

        new SendDataToGateway().execute(finalDataToSend.getBytes());
    }


    private class SendDataToGateway extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... params) {
            SendMessage(params[0]);
            return null;
        }

    }


    private void removeItemInQueueAfterTimeOut(){
        // this function needs to called with specfied interval
        // remove item from

    }



    private void removeItemFromQueueAfterResponse(String response) {
        Message msg = gson.fromJson(response, Message.class);
        for (Message message : mRequestDataList) {
            if (message.equals(msg)) {
                mRequestDataList.remove(message);
                break;
            }
        }
    }



}
