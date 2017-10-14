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
    private DatagramSocket clientReceiveSocket;
    private DatagramSocket clientSocket;
    private Thread receiverThread;

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

        try {


            if (null == clientReceiveSocket) {
                clientReceiveSocket = new DatagramSocket(EngineUtils.UDP_UNI_CAST_PORT);
            }

            if (null == clientSocket) {
                clientSocket = new DatagramSocket();
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

       /* if (null == receiverThread) {
            receiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ReceiveMessage();
                }
            }, "MsgListeningThread");

            receiverThread.start();
        }*/
    }


    public void SendMessage(byte[] dataToSend) {
        try {
            InetAddress IPAddress = InetAddress.getByName(EngineUtils.UDP_UNI_CAST_IP);
            Log.i(getClass().getSimpleName(), "Sending Data to IP:PORT  " + IPAddress.toString() + ":"
                    + EngineUtils.UDP_UNI_CAST_PORT + " Data is :" + new String(dataToSend, StandardCharsets.UTF_8));
            if (null == clientSocket) {
                clientSocket = new DatagramSocket();
            }

            DatagramPacket send_packet = new DatagramPacket(dataToSend, dataToSend.length,
                    IPAddress, EngineUtils.UDP_UNI_CAST_PORT);


            clientSocket.send(send_packet);
            ReceiveMessage();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
        }
    }


    public void receiveMessage(){
            receiverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // ReceiveMessage();
                }
            }, "MsgListeningThread");

            receiverThread.start();

    }

    private void ReceiveMessage() {
        try {

//            while (true) {
            byte[] receiveData = new byte[EngineUtils.MAX_MSG_LENGTH];
            DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
            Log.i(getClass().getSimpleName(), "Listening Port Number " + EngineUtils.UDP_UNI_CAST_PORT);

            clientSocket.receive(receive_packet);
            final byte[] responseBytes = Arrays.copyOf(receive_packet.getData(), receive_packet.getLength());
            String recdData = new String(responseBytes, StandardCharsets.UTF_8);
            Log.i(getClass().getSimpleName(), "Receiving data from  " + clientReceiveSocket.getInetAddress() + " :"
                    + " Payload is " + recdData);

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {


                   // removeItemFromQueueAfterResponse(recdData);
                }
            });

               /* if (isTimeOut) {
                    Log.i(getClass().getSimpleName(),"Timed Out");

                } else {
                    retry_count = 0;
                    mUiHandler.removeCallbacks(timerRun);

                    mUDP_Response = EngineUtils.byteArrayToHex(responseBytes);
                    Log.i(getClass().getSimpleName(), " UniCast response : " + mUDP_Response);

                    int error_code = ParseResponseHeader(responseBytes);

                    if (error_code == 0) {
                        NotifyClients(EngineUtils.RequestStatus.SUCCESS, EngineUtils.ERROR_CODES.RFS_SUCCESS);
                    } else {
                        NotifyClients(EngineUtils.RequestStatus.ERROR, err_code);
                    }
                }*/

//            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), " Exception " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param msg
     */
    public void SendMessageToGateway(Message msg) {
        String finalDataToSend = gson.toJson(msg);
        mRequestDataList.add(msg);

        new SendDataToGateway().execute(finalDataToSend.getBytes());
    }

    /**
     *
     * @param msg
     */
    public void SendMessageToGateway(String msg) {
        final byte[] finalData = msg.getBytes();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendMessage(finalData);
            }
        }).start();

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

    /**
     * Implementing executor service with 3 Sec time out
     */

    public void sendMessageToDevice(String message) {
        Future<String> future = executor.submit(new SendReceive(message));
        try {
            System.out.println("Started..");
            System.out.println(future.get(3, TimeUnit.SECONDS));
            System.out.println(" Command given  " + future.get());

            System.out.println("Finished!");
        } catch (InterruptedException e) {
            future.cancel(true);
            System.out.println("InterruptedException!");
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("TimeoutException !");
        } catch (ExecutionException e) {
            future.cancel(true);
            System.out.println("ExecutionException!");
        }
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
