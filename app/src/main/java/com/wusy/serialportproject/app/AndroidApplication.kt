package com.wusy.serialportproject.app

import android.content.Intent
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.service.ScreenService
import com.wusy.serialportproject.service.SerialPortService
import com.wusy.serialportproject.service.SerialPortServiceS3
import com.wusy.serialportproject.ui.EnvAirActivity
import com.wusy.wusylibrary.base.BaseApplication
import com.wusy.wusylibrary.util.loggerExpand.MyDiskLogAdapter

class AndroidApplication : BaseApplication(){
    private val restartHandler = Thread.UncaughtExceptionHandler { _, ex ->
        Logger.e(ex, Thread.currentThread().toString() + "--APP异常捕获uncaughtException")
//        restartApp()
    }
    override fun onCreate() {
        super.onCreate()
        //将日志放入本地
        Logger.addLogAdapter(MyDiskLogAdapter())

        startSerialPortService()
        startSerialPort3Service()
        startScreenService()
        Thread.setDefaultUncaughtExceptionHandler(restartHandler) // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
    }

    /**
     * 启动串口服务
     */
    private fun startSerialPortService(){
        var intent=Intent(this,SerialPortService::class.java)
        startService(intent)
    }
    private fun startSerialPort3Service(){
        var intent=Intent(this, SerialPortServiceS3::class.java)
        startService(intent)
    }
    /**
     * 启动屏保服务
     */
    private fun startScreenService(){
        var intent=Intent(this,ScreenService::class.java)
        startService(intent)
    }
    private fun restartApp() {
        Logger.e("应用开始重启")
        val intent = Intent(this, EnvAirActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }
}
