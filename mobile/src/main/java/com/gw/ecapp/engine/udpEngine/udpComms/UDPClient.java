package com.gw.ecapp.engine.udpEngine.udpComms;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.gw.ecapp.engine.CommEngine;
import com.gw.ecapp.engine.udpEngine.EngineUtils;
import com.gw.ecapp.engine.udpEngine.packetCreator.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by iningosu on 8/25/2017.
 */

public class UDPClient extends CommEngine {


    private static UDPClient mUDP_instance;
    private Context mContext;

    private Gson gson ;

    private ArrayList<Message> mRequestDataList;

    Handler mUiHandler;

    ExecutorService executor;

    public static UDPClient getInstance(Context context) {
        if (null == mUDP_instance) {
            synchronized (UDPClient.class) {
                mUDP_instance = new UDPClient();
                mUDP_instance.mContext = context;
                mUDP_instance.gson = new Gson();
                mUDP_instance.mRequestDataList = new ArrayList<>();
                mUDP_instance.executor = Executors.newFixedThreadPool(3);
            }
        }
        return mUDP_instance;
    }


    private UDPClient() {
        // create handler in main thread
        if (mUiHandler == null) {
            mUiHandler = new Handler();
        }
    }


    public void SendMessage(byte[] dataToSend) {
        try {
            Log.i(getClass().getSimpleName(),"Thread Name : " + Thread.currentThread().getName());
            InetAddress IPAddress = InetAddress.getByName(EngineUtils.UDP_UNI_CAST_IP);
            Log.i(getClass().getSimpleName(), "Sending Data to IP:PORT  " + IPAddress.toString() + ":"
                    + EngineUtils.UDP_UNI_CAST_PORT + " Data is :" + new String(dataToSend, StandardCharsets.UTF_8));
            DatagramPacket send_packet = new DatagramPacket(dataToSend, dataToSend.length,
                    IPAddress, EngineUtils.UDP_UNI_CAST_PORT);

            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(3000);
            clientSocket.send(send_packet);
            ReceiveMessage(clientSocket);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
        }
    }



    private void ReceiveMessage(DatagramSocket socket) {
        try {
            byte[] receiveData = new byte[EngineUtils.MAX_MSG_LENGTH];
            DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
            Log.i(getClass().getSimpleName(), "Listening Port Number " +socket.getLocalPort());

            socket.receive(receive_packet);
            final byte[] responseBytes = Arrays.copyOf(receive_packet.getData(), receive_packet.getLength());
            String recdData = new String(responseBytes, StandardCharsets.UTF_8);
            Log.i(getClass().getSimpleName(), "Receiving data from  " + socket.getInetAddress() + " :"
                    + " Payload is " + recdData);

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // removeItemFromQueueAfterResponse(recdData);
                }
            });

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
            e.printStackTrace();
        }finally {
            Log.i(getClass().getSimpleName(),"Receiving Thread Name : " + Thread.currentThread().getName());
            socket.close();
        }
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

    /**
     * Implementing executor service with 3 Sec time out
     * this is Async Call
     */

    public void sendMessageToDevice(final String message) {

       Runnable r = new Runnable() {
           @Override
           public void run() {
               Future<String> future = executor.submit(new SendReceive(message));
           }
       };

       mUiHandler.post(r);
    }

    /**
     * class responsible for sending and receiving data
     */
    private class SendReceive implements Callable<String> {

        private String mCommand;

        public SendReceive(String command){
            mCommand = command ;
        }

        @Override
        public String call() throws Exception {

            SendMessage(mCommand.getBytes());

            return  mCommand;

        }
    }


    public void close(){
        executor.shutdown();
    }


}
