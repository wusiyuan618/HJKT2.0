package com.wusy.serialportproject.socket;

import com.orhanobut.logger.Logger;
import com.wusy.serialportproject.util.InterAddressUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class SocketHelper {
    private static SocketHelper socketHelper;
    private Socket mSocket;
    public static String SOCKET_HJKT_STR = "hjl-hv";
    public OnReceiveListener onReceiveListener;
    private String macAddress;

    private SocketHelper() {
        macAddress = InterAddressUtil.getMacAddress();
        if (mSocket == null) {
            try {
                mSocket = IO.socket("http://192.168.1.228:9206/");
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
        mSocket.emit(SOCKET_HJKT_STR, macAddress);
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
    public void addListensEvent() {
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
            addListensEvent();
            connect();
        });
    }

    public void sendTest(){
        mSocket.emit(SOCKET_HJKT_STR, "ceshi");
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
