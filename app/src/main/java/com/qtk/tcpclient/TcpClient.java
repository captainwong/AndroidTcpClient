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
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Thread worker;
    Socket socket;


    public TcpClient(OnMessageReceived listener){
        this.listener = listener;

    }

    public void sendMsg(final String msg){
        Log.i(Tag, "Sending msg:" + msg);
        if (printWriter != null && !printWriter.checkError()) {
            printWriter.println(msg);
            printWriter.flush();
        }
    }

    public void stopClient(){
        sendMsg(Constants.CLOSED_CONNECTION + "jack");
        running = false;

        if(worker!=null) {
            try {
                worker.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally {
                worker=null;
            }
        }

        if(printWriter!=null){
            printWriter.flush();
            printWriter.close();
            printWriter = null;
        }

        try{
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            socket=null;
        }

        listener = null;
        bufferedReader = null;
    }

    public void startClient(){
        try{
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Log.i(Tag, "connecting...");

            socket = new Socket(serverAddr, SERVER_PORT);
            try{
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                //printWriter = new DataOutputStream(socket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendMsg(Constants.LOGIN_NAME + "jack");

                worker = new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            while (running) {
                                serverMsg = bufferedReader.readLine();
                                if (serverMsg != null && listener != null) {
                                    listener.messageReceived(serverMsg);
                                }
                            }
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });

                running = true;
                worker.start();

            }catch (Exception e){
                Log.e(Tag, e.toString());
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public interface OnMessageReceived{
        void messageReceived(String msg);
    }
}
