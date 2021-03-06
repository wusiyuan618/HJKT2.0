package com.wusy.serialportproject.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.SerialPortUtil

class SerialPortServiceS3: Service(){
    private lateinit var serialPortBradCast:SerialPortBradCast
    lateinit var intentFilter: IntentFilter
    lateinit var serialPortUtilS2: SerialPortUtil
    val SerialPort="/dev/ttyS3"
    val Baudrate=9600

    override fun onCreate() {
        Logger.d("SerialPortService star")
        super.onCreate()
        initBroadCast()
        initSerialPort()
    }
    private fun initBroadCast(){
        serialPortBradCast= SerialPortBradCast()
        intentFilter= IntentFilter()
        intentFilter.addAction(CommonConfig.SERIALPORTPROJECT_ACTION_SP_SERVICE)
        registerReceiver(serialPortBradCast,intentFilter)
    }
    private fun initSerialPort(){
        serialPortUtilS2= SerialPortUtil(handler)
        serialPortUtilS2.openSerialPort(SerialPort,Baudrate)
        Logger.d("系统启动串口:$SerialPort\t匹配波特率：$Baudrate")

    }

    override fun onDestroy() {
        super.onDestroy()
        if (serialPortBradCast!=null) unregisterReceiver(serialPortBradCast)
        Logger.d("SerialPortService destory")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY

    }
    private var handler= @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                0->{
                    if(msg.obj.toString().substring(0,2).toLowerCase() == "7f"
                        ||msg.obj.toString().substring(0,2).toLowerCase() == "ff") return//无效数据
                    Logger.d("ttyS3获取到了从传感器发送到Android主板的串口数据:\n"
                            +msg.obj.toString())
                    var intent= Intent()
                    intent.action= CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI
                    intent.putExtra("msg",msg.obj.toString())
                    sendBroadcast(intent)
                    val intentLog=Intent()
                    intentLog.action=CommonConfig.ACTION_SYSTEMTEST_LOG
                    intentLog.putExtra("log","ttyS3获取到了从传感器发送到Android主板的串口数据:\n"
                            +msg.obj.toString())
                    sendBroadcast(intentLog)
                }
            }
        }
    }
    inner class SerialPortBradCast : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.action== CommonConfig.SERIALPORTPROJECT_ACTION_SP_SERVICE){
                when(intent.getStringExtra("data")){
                    "send"->{
//                        Thread.sleep(50)
//                        SerialPortUtil.switchUartde(1,SerialPortUtil.TTYS3_STAUS_FILEPATH)
//                        Log.i("wsy","改变了收发状态，当前为----发送状态")
                        var msg=intent.getStringExtra("msg")
                        serialPortUtilS2.sendSerialPort(msg)
//                        Thread.sleep(10)
//                        SerialPortUtil.switchUartde(0,SerialPortUtil.TTYS3_STAUS_FILEPATH)
//                        Log.i("wsy","改变了收发状态，当前为----接收状态")
                    }
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
