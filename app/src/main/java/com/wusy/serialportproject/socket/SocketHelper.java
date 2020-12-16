package com.wusy.serialportproject.socket;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.wusy.serialportproject.bean.SocketPackage;
import com.wusy.serialportproject.util.CommonConfig;
import com.wusy.serialportproject.util.InterAddressUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class SocketHelper {
    private static SocketHelper socketHelper;
    private Socket mSocket;
    private String SOCKET_HJKT_STR = "hjl-hv";
    public OnReceiveListener onReceiveListener;
    private String macAddress;

    private SocketHelper() {
        macAddress = InterAddressUtil.getMacAddress();
        if (mSocket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.timeout=10*1000;
                mSocket = IO.socket("http://39.98.255.74:9206/",opts);
                addListensEvent();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static SocketHelper getInstance() {
        if (socketHelper == null) {
            socketHelper = new SocketHelper();
        }
        return socketHelper;
    }

    private void connectInit() {
        SocketPackage socketPackage=new SocketPackage();
        socketPackage.setContent(macAddress);
        socketPackage.setDescription("连接成功，发送mac地址");
        socketPackage.setType("1");
        socketPackage.setIntent("register");
        send(new Gson().toJson(socketPackage));
    }

    /**
     * 建立连接
     */
    public void connect() {
        mSocket.connect();
    }

    /**
     * 关闭连接
     */
    public void disConnect() {
        mSocket.disconnect();
    }

    /**
     * 添加监听
     */
    private void addListensEvent() {
        mSocket.on(SOCKET_HJKT_STR, args -> {
            if (onReceiveListener != null) onReceiveListener.receive(args[0].toString());
            else Logger.e("Socket没有接收消息的监听，收到了服务器的消息:"+args);
        });
        mSocket.on("connect", args -> {
                    Logger.i("Socket Connect");
                    connectInit();
                }
        );

        mSocket.on("connecting", args -> {
                    Logger.i("Socket Connecting");
                }
        );

        mSocket.on("disconnect", args -> Logger.i("Socket Disconnect"));
        mSocket.on("connect_failed", args -> Logger.e("Socket Connect Failed"));
        mSocket.on("error", args -> Logger.e("Socket Error " + args.toString()));
        mSocket.on("reconnect_failed", args -> Logger.e("Socket Reconnect Failed"));
        mSocket.on("reconnect", args -> {
            Logger.i("Socket Reconnect");
            connect();
        });
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            Logger.i("Socket Connect TimeOut");
            connect();
        });

    }
    public boolean isConnected(){
        return mSocket.connected();
    }
    public void send(String str, Context context){

        Intent intent =new  Intent(CommonConfig.ACTION_SYSTEMTEST_LOG);
        intent.putExtra("log", "\n----------------\nSocket发送数据:"+ str+"\n----------------\n");
        context.sendBroadcast(intent);
        send(str);
    }
    public void send(String str){
        if(mSocket.connected()){
            Logger.i("Socket发送数据:"+str);
            mSocket.emit(SOCKET_HJKT_STR, str);
        }else{
            Logger.e("Socket未能连接,无法发送");
        }

    }

    public interface OnReceiveListener {
        void receive(String json);
    }

    public OnReceiveListener getOnReceiveListener() {
        return onReceiveListener;
    }

    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }
}
