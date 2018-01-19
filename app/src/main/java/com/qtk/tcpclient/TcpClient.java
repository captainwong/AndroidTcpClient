package com.qtk.tcpclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Jack on 2018-01-19.
 */

public class TcpClient {
    public static final String Tag = "TcpClient";
    public static final String SERVER_IP = "192.168.1.222";
    public static final int SERVER_PORT = 56247;

    private String serverMsg;
    private OnMessageReceived listener = null;
    private boolean running = false;
    private DataOutputStream printWriter;
    private BufferedReader bufferedReader;
    private Thread sendThread;


    public TcpClient(OnMessageReceived listener){
        this.listener = listener;

    }

    public void sendMsg(final String msg){
//        try {
//            new Thread(new Runnable() {
//                @Override
//                public void run () {
//                    if (running && printWriter != null /*&& !printWriter.checkError()*/) {
//                        //printWriter.println(msg);
//                        //printWriter.flush();
//                        try {
//                            printWriter.writeUTF(msg);
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        try {
            printWriter.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopClient(){
        sendMsg(Constants.CLOSED_CONNECTION + "jack");
        running = false;
        if(printWriter!=null){
            //printWriter.flush();
            //printWriter.close();
        }

        listener = null;
        bufferedReader = null;
        printWriter = null;
    }

    public void run(){
        running = true;

        try{
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Log.i(Tag, "connecting...");

            Socket socket = new Socket(serverAddr, SERVER_PORT);
            try{
                //printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                printWriter = new DataOutputStream(socket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendMsg(Constants.LOGIN_NAME + "jack");

                while(running){
                    serverMsg = bufferedReader.readLine();
                    if(serverMsg != null && listener != null){
                        listener.messageReceived(serverMsg);
                    }
                }
            }catch (Exception e){
                Log.e(Tag, e.toString());
            }finally {
                socket.close();
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        running = false;
    }


    public interface OnMessageReceived{
        void messageReceived(String msg);
    }
}
