package com.wusy.serialportproject.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.*
import com.wusy.serialportproject.devices.*
import com.wusy.serialportproject.socket.SocketHelper
import com.wusy.serialportproject.ui.screen.ScreenActivity
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.serialportproject.util.JDQType
import com.wusy.serialportproject.view.CirqueProgressControlView
import com.wusy.wusylibrary.util.OkHttpUtil
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import com.wusy.wusylibrary.util.permissions.PermissionsManager
import com.wusy.wusylibrary.util.permissions.PermissionsResultAction
import kotlinx.android.synthetic.main.activity_envair.*
import kotlinx.android.synthetic.main.activity_item_envair_left.*
import kotlinx.android.synthetic.main.activity_item_envair_center.*
import kotlinx.android.synthetic.main.activity_item_envair_right.ivClod
import kotlinx.android.synthetic.main.activity_item_envair_right.ivHeat
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EnvAirActivity : BaseTouchActivity() {
    /**
     *  对应寄电器开关的位置（即第N个开关）
     */
    companion object {
        /**
         * 制冷
         */
        const val MODE_Cryogen = 2
        /**
         * 制热
         */
        const val MODE_Heating = 3
        /**
         * 除湿
         */
        const val MODE_Dehumidification = 4
        /**
         * 加湿
         */
        const val MODE_Humidification = 5
        /**
         * 新风
         */
        const val MODE_Hairdryer = 6
        /**
         * 风阀 关、小、中、大
         */
        const val MODE_Wind_Min = 7
        const val MODE_Wind_Mid = 8
        const val MODE_Wind_Max = 9
        const val MODE_Wind_Off = 10


        /**
         * 当前使用的继电器
         */
        val currentJDQ: BaseDevices = ZZIO1600()
        val currentEnv: BaseDevices = EnvQ3()
//        val currentEnv: BaseDevices = TextEnvDevice()
//        val currentEnv: BaseDevices = Ate24V()
    }


    private var boradCast: EnvAirBoradCast? = null
    private val buffer = StringBuffer()
    /**
     * 最后发送的开关控制实体
     */
    private var sendBean: EnvAirControlBean = EnvAirControlBean()
    private var lastClickTime: Long = 0
    //点击事件间隔时间
    private val times: Long = 50
    private var curTemp = 25
    private val minTemp = 5
    private val maxTemp = 35
    //自动制冷
    private var isCryogen = false
    //自动制热
    private var isHeating = false

    //新风定时按钮是否打开
    private var isXFTime = false
    private var nextTime = 0L
    private var nextXFIsOpen = false
    //上一次高PM2.5的时间
    private var lastHighPMTime = 0L

    /**
     * 按钮实体
     */
    lateinit var btnClodBean: EnvAirControlBean
    lateinit var btnHeatBean: EnvAirControlBean
    lateinit var btnJSBean: EnvAirControlBean
    lateinit var btnCSBean: EnvAirControlBean
    lateinit var btnXFBean: EnvAirControlBean
    lateinit var btnWindBean: EnvAirControlBean

    lateinit var socketHelper: SocketHelper

    override fun getContentViewId(): Int {
        return R.layout.activity_envair
    }


    override fun findView() {
    }

    override fun init() {
        requestPermissions()
        initView()
        initBroadCast()
        initThread()
        initTestDevices()
    }

    private fun requestPermissions() {
        if (!//写入权限
            PermissionsManager.getInstance().hasAllPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        )
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE

                ), object :
                    PermissionsResultAction() {
                    override fun onGranted() {
                        showLogInfo("权限添加成功")
                    }

                    override fun onDenied(permission: String) {
                        showLogError("用户拒绝添加权限---$permission")
                    }
                })
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        initAllBtn()
        tempControlView.setProgressRange(minTemp, maxTemp)//可以在xml中指定，也可以在代码中设置
        curTemp = SharedPreferencesUtil.getInstance(this).getData(Constants.ENJOYTEMP, 25) as Int
        tempControlView.setProgress(curTemp)  //添加默认数据--注:不能超出范围
        tempControlView.setOnTextFinishListener(object :
            CirqueProgressControlView.OnCirqueProgressChangeListener {
            override fun onChange(minProgress: Int, maxProgress: Int, progress: Int) {
                checkRingColor(progress)
            }

            override fun onChangeEnd(minProgress: Int, maxProgress: Int, progress: Int) {
                curTemp = progress
                SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                    .saveData(Constants.ENJOYTEMP, curTemp)
                if (SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                        Constants.BTN_STATE_MODE,
                        0
                    ) == 0
                ) {
                    SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                        .saveData(Constants.LAST_TEMP, curTemp)
                }
                sendAppControlStatus()
            }
        })
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load(R.mipmap.aqi).apply(options).into(ivAirQualityGif)

        rlSetting.setOnClickListener {
            showLoadImage()
            navigateTo(SettingActivity::class.java)
            hideLoadImage()
        }
        rlRepair.setOnClickListener {
            startActivity(Intent().apply {
                setClass(this@EnvAirActivity, ScreenActivity::class.java)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        //状态重置
        restoreSwitchBtnState()
        //设置背景图片
        val imgId =
            SharedPreferencesUtil.getInstance(this).getData(Constants.IMGID, R.mipmap.bg2) as Int
        layout_total.setBackgroundResource(imgId)
        //设置socket
        socketHelper = SocketHelper.getInstance()
        socketHelper.onReceiveListener = SocketHelper.OnReceiveListener {
            Logger.i("Socket Receive:$it")
            sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_SYSTEMTEST_LOG
                putExtra("log", "\n----------------\nSocket Receive:$it\n----------------\n")
            })
            val data = Gson().fromJson<SocketPackage>(it, SocketPackage::class.java)
            var receiveControlBean: ReceiveControlBean? = null
            if (data.content != null) {
                receiveControlBean = Gson().fromJson<ReceiveControlBean>(
                    data.content.toString(),
                    ReceiveControlBean::class.java
                )
            }

            runOnUiThread {
                when (data.intent) {
                    "Cryogen" -> {
                        if (receiveControlBean?.isOpen == true) {//正在启动，将其关闭
                            recordingBtnState(Constants.BTN_STATE_COLD, 0)
                            recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                            ZL(true)
                        } else {//未启动，将其打开
                            recordingBtnState(Constants.BTN_STATE_COLD, 1)
                            ZL(false)
                            if (!isHeating) {
                                tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                            }
                        }
                    }
                    "Heating" -> {
                        if (receiveControlBean?.isOpen == true) {//正在启动，将其关闭
                            recordingBtnState(Constants.BTN_STATE_COLD, 1)
                            recordingBtnState(Constants.BTN_STATE_HEAT, 0)
                            ZR(true)
                        } else {//未启动，将其打开
                            recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                            ZR(false)
                            if (!isCryogen) {
                                tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                            }
                        }
                    }
                    "Switch" -> {
                        if (receiveControlBean?.isOpen ?: false) {
                            clickON()
                        } else {
                            clickOFF()
                        }
                    }
                    "LJ" -> {
                        clickLJ()
                    }
                    "JN" -> {
                        clickJN()
                    }
                    "Normal" -> {
                        clickModeNormal()
                    }
                    "SetTemp" -> {
                        tempControlView.setProgress(receiveControlBean?.temp ?: 5)
                        checkRingColor(receiveControlBean?.temp ?: 5)
                        curTemp = receiveControlBean?.temp ?: 5
                        SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                            .saveData(Constants.ENJOYTEMP, curTemp)
                        if (SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                                Constants.BTN_STATE_MODE,
                                0
                            ) == 0
                        ) {
                            SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                                .saveData(Constants.LAST_TEMP, curTemp)
                        }
                    }
                    "ReBoot"->{
                        sendBroadcast(Intent("HJL_ACTION_REBOOT"))
                    }
                    "SendLog"->{
                        updateLog()
                    }
                }
                sendAppControlStatus(data.msgId?:"")
            }
        }
        socketHelper.connect()
    }

    var weekMap = HashMap<String, Int>().apply {
        put("周日", 1)
        put("周一", 2)
        put("周二", 3)
        put("周三", 4)
        put("周四", 5)
        put("周五", 6)
        put("周六", 7)
    }

    private fun initThread() {
        Thread(Runnable {
            //这是一个每1min执行一次的定时器，用于检测寄电器状态和环境状态
            while (true) {
                val calendar = Calendar.getInstance()
                if (isXFTime) {//新风定时功能启动中
                    if (SimpleDateFormat("hh:mm").format(Date(nextTime)) == SimpleDateFormat("hh:mm").format(
                            Date(calendar.timeInMillis)
                        )
                    ) {
                        controlXFTime(nextXFIsOpen)
                    }
                }
                Thread.sleep(1000)
                buffer.delete(0, buffer.length)//定时更新下数据存储器，防止出现骚问题
                sendSerial(currentEnv.SearchStatusCode)
                Thread.sleep(2000)
                buffer.delete(0, buffer.length)//定时更新下数据存储器，防止出现骚问题
                sendJDQSearch()
                Thread.sleep(58 * 1000)
            }
        }).start()
        Thread(Runnable {
            //温度编程
            while (true) {
                val calendar = Calendar.getInstance()
                //温度编程 需要每分钟执行一次
                val str =
                    SharedPreferencesUtil.getInstance(this).getData(Constants.TEMP_CODE, "")
                        .toString()
                if (str != "") {
                    val gson = Gson()//创建Gson对象
                    val jsonParser = JsonParser()
                    val jsonElements = jsonParser.parse(str).asJsonArray//获取JsonArray对象
                    for (bean in jsonElements) {
                        val tempBean =
                            gson.fromJson(bean, TempCodeFragment.TempCodeBean::class.java)
                        val startWeek = tempBean.startTime.split(" ")[0]
                        val startHour = tempBean.startTime.split(" ")[1].split(":")[0].toInt()
                        val startMin = tempBean.startTime.split(" ")[1].split(":")[1].toInt()
                        val curWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        val curHour = calendar.get(Calendar.HOUR_OF_DAY)
                        val curMin = calendar.get(Calendar.MINUTE)
                        if (curWeek == weekMap[startWeek]) {
                            if (curHour == startHour && curMin == startMin) {//时间对上了，准备调温
                                curTemp = tempBean.temp.replace("℃", "").toInt()
                                runOnUiThread {
                                    tempControlView.setProgress(curTemp)
                                }
                                SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                                    .saveData(Constants.ENJOYTEMP, curTemp)
                                if (SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                                        Constants.BTN_STATE_MODE,
                                        0
                                    ) == 0
                                ) {
                                    SharedPreferencesUtil.getInstance(this@EnvAirActivity)
                                        .saveData(Constants.LAST_TEMP, curTemp)
                                }
                                //调模式

                                when {
                                    tempBean.mode == "制冷" -> if (isCanClickByOpen()) {
                                        recordingBtnState(Constants.BTN_STATE_COLD, 0)
                                        recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                                        runOnUiThread {
                                            ZL(true)
                                        }

                                    }
                                    tempBean.mode == "制热" -> if (isCanClickByOpen()) {
                                        recordingBtnState(Constants.BTN_STATE_COLD, 1)
                                        recordingBtnState(Constants.BTN_STATE_HEAT, 0)
                                        runOnUiThread {
                                            ZR(true)
                                        }
                                    }
                                    tempBean.mode == "通风" -> {
                                        if (isCanClickByOpen()) {
                                            recordingBtnState(Constants.BTN_STATE_COLD, 1)
                                            recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                                            runOnUiThread {
                                                ZL(false)
                                                ZR(false)
                                            }
                                        }
                                    }
                                    else -> Logger.i("未设置模式")
                                }
                            }
                        }
                    }
                }
                Thread.sleep(60 * 1000)
            }
        }).start()
        Thread(Runnable {
            //这是一个每秒钟执行一次的时间线程
            while (true) {
                val calendar = Calendar.getInstance()
                runOnUiThread {
                    tvDate.text = calendar.get(Calendar.YEAR).toString() + "年" +
                            (calendar.get(Calendar.MONTH) + 1).toString() + "月" +
                            calendar.get(Calendar.DAY_OF_MONTH).toString() + "日" + "      周" + calendar.get(
                        Calendar.DAY_OF_WEEK
                    )
                    tvTime.text =
                        SimpleDateFormat("hh:mm").format(calendar.time) + "  " + if (calendar.get(
                                Calendar.AM_PM
                            ) == 1
                        ) "PM" else "AM"
                }
                //定时重启
                if (SimpleDateFormat("hh:mm:ss").format(calendar.time) == "04:00:00" && calendar.get(
                        Calendar.AM_PM
                    ) == 0
                ) {
                    Logger.i("系统定时重启开始！！！")
                    sendBroadcast(Intent("HJL_ACTION_REBOOT"))
                }

                Thread.sleep(1000)
                val updateTime = SharedPreferencesUtil.getInstance(this)
                    .getData(Constants.VALUEADDED_UPDATE_DATE_FILTER, "").toString()
                if (updateTime != "") {
                    var time = SimpleDateFormat("yyyy-MM-dd").parse(updateTime).time
                    if (time - calendar.timeInMillis > (1000L * 60L * 60L * 24L * 180L)) {//提示更换
                        runOnUiThread {
                            ivAirQualityGif.visibility = View.VISIBLE
                        }
                    } else {
                        if ((Constants.curED?.pM25 ?: 0) < 75) {//将时间清空
                            lastHighPMTime = 0L
                            runOnUiThread {
                                ivAirQualityGif.visibility = View.GONE
                            }
                        } else {
                            if (lastHighPMTime == 0L) {
                                lastHighPMTime = calendar.timeInMillis
                            } else {
                                if (calendar.timeInMillis - lastHighPMTime >= 6L * 60L * 1000L * 60L) {//提示更换
                                    runOnUiThread {
                                        ivAirQualityGif.visibility = View.VISIBLE
                                    }
                                } else {//不提示更换
                                    runOnUiThread {
                                        ivAirQualityGif.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).start()
        Thread(Runnable {
            //这是一个每5min执行一次的时间线程
            while (true) {
                if (SharedPreferencesUtil.getInstance(this).getData(
                        Constants.BTN_STATE_SWITCH,
                        1
                    ) == 1
                ) return@Runnable
                if (SharedPreferencesUtil.getInstance(this).getData(
                        Constants.BTN_SETTING_FJ_ISAUTO,
                        false
                    ) == true
                ) {
                    if (SharedPreferencesUtil.getInstance(this).getData(
                            Constants.BTN_STATE_COLD,
                            1
                        ) != 0 &&
                        SharedPreferencesUtil.getInstance(this).getData(
                            Constants.BTN_STATE_HEAT,
                            1
                        ) != 0 &&
                        SharedPreferencesUtil.getInstance(this).getData(
                            Constants.BTN_STATE_SD,
                            0
                        ) != 1 &&
                        SharedPreferencesUtil.getInstance(this).getData(
                            Constants.BTN_STATE_SD,
                            0
                        ) != 2
                    ) {
                        if (isXFTime) return@Runnable
                        isXFTime = true
                        controlXFTime(true)
                    } else {
                        isXFTime = false
                    }
                }
                Thread.sleep(1000 * 60 * 5)
            }
        }).start()
    }

    /**
     * 通过socket发送大屏状态信息至服务器
     */
    private fun sendAppControlStatus() {
        sendAppControlStatus("")
    }
    private fun sendAppControlStatus(msgId: String) {
        val socketPackage = SocketPackage().apply {
            this.content = ControlStatusBean(this@EnvAirActivity).apply {
                temp= curTemp.toString()
            }
            this.description = "大屏实时状态数据，操作一次，发送一次"
            this.type = "1"
            this.intent = "ControlStatus"
            this.msgId=msgId
        }
        socketHelper.send(Gson().toJson(socketPackage),this)
    }

    private fun initBroadCast() {
        boradCast = EnvAirBoradCast()
        var actionList = ArrayList<String>()
        actionList.add(CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI)
        actionList.add(CommonConfig.ACTION_ENVAIR_SD_CONTORL)
        actionList.add(CommonConfig.ACTION_ENVAIR_XF_CONTORL)
        actionList.add(CommonConfig.ACTION_ENVAIR_FJ_CONTORL)
        actionList.add(CommonConfig.ACTION_ENVAIRACTIVITY_SEND_JDQSEARCH)
        actionList.add(CommonConfig.ACTION_ENVAIRACTIVITY_SEND_ENVSEARCH)
        actionList.add(CommonConfig.ACTION_BGCKGROUND_IMG_CHANGE)
        addBroadcastAction(actionList, boradCast)
    }


    /**
     * 发送寄电器控制命令
     */
    private fun sendJDQControl(bean: EnvAirControlBean) {
        Thread.sleep(30)
        bean.isSend = true
        sendBean = bean
        when (currentJDQ.name) {
            JDQType.SCHIDERON -> {
                sendSerial(currentJDQ.SearchStatusCode)
            }
            JDQType.ZZIO1600 -> {
                sendSerial(
                    (currentJDQ as ZZIO1600).getSendControlCode(
                        sendBean.switchIndex,
                        sendBean.isOpen
                    )
                )
                Logger.i("发送了一条寄电器控制命令：$sendBean")
            }
        }
    }

    /**
     * 查询寄电器状态
     */
    private fun sendJDQSearch() {
        sendSerial(currentJDQ.SearchStatusCode)
    }

    /**
     * 通过串口发送命令
     */
    private fun sendSerial(msg: String) {
        val intent = Intent()
        intent.putExtra("data", "send")
        intent.putExtra("msg", msg)
        Logger.d("EmvAorActivity发送串口数据=$msg")
        intent.action = CommonConfig.SERIALPORTPROJECT_ACTION_SP_SERVICE
        sendBroadcast(intent)

        sendBroadcast(Intent().apply {
            action = CommonConfig.ACTION_SYSTEMTEST_LOG
            putExtra("log", "EmvAorActivity发送串口数据=$msg")
        })
    }


    private fun isCanClick(): Boolean {
        if (System.currentTimeMillis() - lastClickTime >= times) {
            lastClickTime = System.currentTimeMillis()
        } else {
            Toast.makeText(this, "您点得太快了", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isCanClickByOpen(): Boolean {
        return if (SharedPreferencesUtil.getInstance(this).getData(
                Constants.BTN_STATE_SWITCH,
                1
            ) == 0
        ) {
            true
        } else {
            Toast.makeText(this, "请启动空调", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * 做一个测试数据
     */
    private fun initTestDevices() {
        if (currentEnv !is TextEnvDevice) return
        val enD = EnvironmentalDetector("", currentEnv)
        Constants.curED = enD
        tvTempCount.text = enD.temp.toString()
        tvHumidityCount.text = enD.humidity.toString()
        tvAirQualityCount.text = enD.pM25.toString()
        sendBroadcast(Intent().apply {
            action = CommonConfig.ACTION_SYSTEMTEST_LOG
            putExtra("log", "EmvAorActivity发送串口数据=${currentEnv.log}")
        })
        /* 开启制冷制热 */
        if (SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                Constants.BTN_STATE_SWITCH,
                1
            ) == 0
        ) {
            Logger.i("自动控制检测开始")
            startAutoCryogen(enD)
            startAutoHeating(enD)
            startControlXF(enD)
            startContorlSD(enD)
        } else {
            Logger.i("已获取环境数据，但空调未开机")
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 3 -> {//环境检测仪获取到的数据
                    Logger.d("获取的环境检测仪的数据" + msg.obj)
                    //将确定是环境探测器的数据通过广播发出去,并且存储全局数据。方便屏保使用
                    val intent = Intent(CommonConfig.ACTION_ENVIRONMENTALDETECOTOR_DATA)
                    intent.putExtra("data", msg.obj.toString())
                    sendBroadcast(intent)
                    val enD = EnvironmentalDetector(msg.obj.toString(), currentEnv)
                    //给服务器发送温度数据
                    val socketPackage = SocketPackage().apply {
                        this.content = enD
                        this.description = "温度传感器数据，一分钟一次"
                        this.type = "1"
                        this.intent = "EnvironmentalDetector"
                    }
                    socketHelper.send(Gson().toJson(socketPackage),this@EnvAirActivity)
                    Constants.curED = enD
                    tvTempCount.text = enD.temp.toString()
                    tvHumidityCount.text = enD.humidity.toString()
                    tvAirQualityCount.text = enD.pM25.toString()
                    sendBroadcast(Intent().apply {
                        action = CommonConfig.ACTION_SYSTEMTEST_LOG
                        putExtra("log", "EmvAorActivity发送串口数据=${currentEnv.log}")
                    })

                    /* 调节圆盘颜色 */
                    checkRingColor(curTemp)
                    /* 开启制冷制热 */
                    if (SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                            Constants.BTN_STATE_SWITCH,
                            1
                        ) == 0
                    ) {
                        Logger.i("自动控制检测开始")
                        startAutoCryogen(enD)
                        startAutoHeating(enD)
                        startControlXF(enD)
                        startContorlSD(enD)
                    } else {
                        Logger.i("已获取环境数据，但空调未开机")
                    }

                }
                1 -> {
                    Logger.d("获取的SCHIDERON寄电器状态的数据" + msg.obj)
                    val data = msg.obj.toString().substring(16, 18)
                    if (sendBean != null && sendBean.isSend) {
                        //如果有控制命令，则讲获取的数据转为二进制，更改为要发送的二进制，在转成16进制发送
                        sendSerial(
                            (currentJDQ as SCHIDERON).getSendControlCode(
                                calcuHexOfSendControl(data)
                            )
                        )
                        //发送完成，关闭发送事件
                        sendBean.isSend = false
                    } else {
                        //如果不需要控制，只是查询状态，则将按钮与状态对应

                    }
                }
                2 -> {
                    Logger.d("获取的ZZ-IO1600寄电器状态的数据" + msg.obj)
                    var praseList = (currentJDQ as ZZIO1600).parseStatusData(msg.obj.toString())
                    sendBroadcast(Intent().apply {
                        action = CommonConfig.ACTION_SYSTEMTEST_LOG
                        putExtra(
                            "log", "解析的ZZ-IO1600寄电器状态的数据=${praseList}\n" +
                                    "从0计数（寄电器从1计数）\n制冷=${MODE_Cryogen}\t制热=${MODE_Heating}\n" +
                                    "除湿=${MODE_Dehumidification}\t加湿=${MODE_Humidification}\t新风=${MODE_Hairdryer}\n" +
                                    "风阀大=${MODE_Wind_Max}\t风阀中=${MODE_Wind_Mid}\t风阀小=${MODE_Wind_Min}\t风阀关=${MODE_Wind_Off}"
                        )
                    })
                    sendBroadcast(Intent().apply {
                        action = CommonConfig.ACTION_SYSTEMTEST_JDQ
                        putIntegerArrayListExtra("jdq", praseList)
                    })
                    /**
                     * 湿度开关状态检查
                     */
                    if (praseList[btnJSBean.switchIndex] == 1) {//加湿中
                        btnJSBean.isOpen = true
                        recordingBtnState(Constants.BTN_STATE_SD, 2)
                    } else if (praseList[btnCSBean.switchIndex] == 1) {//除湿中
                        btnCSBean.isOpen = true
                        recordingBtnState(Constants.BTN_STATE_SD, 1)
                    } else {//都关闭
                        recordingBtnState(Constants.BTN_STATE_SD, 0)
                    }
                }
            }
        }
    }

    private fun checkRingColor(ringTemp: Int) {
        if (isCryogen) {
            if (Constants.curED != null && ringTemp > Constants.curED!!.temp) {
                //不制冷的时候把按钮颜色换了
                tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
            } else {
                tempControlView.setRingColor(Color.parseColor("#2793ff"))
            }
        }
        if (isHeating) {
            if (Constants.curED != null && ringTemp < Constants.curED!!.temp) {
                //不制热的时候把按钮颜色换了
                tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
            } else {
                tempControlView.setRingColor(Color.parseColor("#eb4f39"))
            }
        }
    }

    /**
     * 自动制冷
     */
    private fun startAutoCryogen(enD: EnvironmentalDetector) {
        if (isCryogen) {
            Logger.i("自动制冷开始检测")
            if (enD.temp > curTemp + 1) {//温度高了，发送打开制冷
                if (btnClodBean.isOpen) return
                Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送打开制冷命令")
                btnClodBean.isOpen = true
                sendJDQControl(btnClodBean)
                btnXFBean.isOpen = true
                sendJDQControl(btnXFBean)
            } else if (enD.temp < curTemp - 1) {
                if (btnClodBean.isOpen) {//温度低了，发送关闭制冷
                    Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送关闭制冷命令")
                    btnClodBean.isOpen = false
                    sendJDQControl(btnClodBean)
                    tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                }
            } else {
                Logger.i("适宜温度，不需要操作制冷命令")
            }
        }
    }

    /**
     * 湿度自动控制
     */
    private fun startContorlSD(enD: EnvironmentalDetector) {
        var isAutoContorlSD = SharedPreferencesUtil.getInstance(this).getData(
            Constants.BTN_SETTING_SD_ISAUTO,
            true
        ) as Boolean
        var JSmin = (SharedPreferencesUtil.getInstance(this).getData(
            Constants.SETTING_JS_COUNT,
            Constants.DEFAULT_JS_COUNT
        ) as String).replace("%RH", "").toInt()
        var CSmax = (SharedPreferencesUtil.getInstance(this).getData(
            Constants.SETTING_CS_COUNT,
            Constants.DEFAULT_CS_COUNT
        ) as String).replace("%RH", "").toInt()
        if (isAutoContorlSD) {
            Logger.i("自动湿度控制开始检测,当前湿度=" + enD.humidity)
            if (enD.humidity < JSmin - 5) {
                Logger.i("湿度过低，打开加湿")
                btnCSBean.isOpen = false
                sendJDQControl(btnCSBean)
                btnJSBean.isOpen = true
                sendJDQControl(btnJSBean)
                btnXFBean.isOpen = true
                sendJDQControl(btnXFBean)
            } else if (enD.humidity > JSmin + 5 && enD.humidity < CSmax - 5) {
                Logger.i("湿度适中，全关")
                btnCSBean.isOpen = false
                sendJDQControl(btnCSBean)
                btnJSBean.isOpen = false
                sendJDQControl(btnJSBean)
            } else if (enD.humidity > CSmax + 5) {
                Logger.i("湿度过高，打开除湿")
                btnJSBean.isOpen = false
                sendJDQControl(btnJSBean)
                btnCSBean.isOpen = true
                sendJDQControl(btnCSBean)
                btnXFBean.isOpen = true
                sendJDQControl(btnXFBean)
            } else {//维持现状
                Logger.i("维持现状")
            }
        } else {
            Logger.i("当前是手动控制湿度模式。")
        }
    }

    /**
     * 自动制热
     */
    private fun startAutoHeating(enD: EnvironmentalDetector) {
        if (isHeating) {
            Logger.i("自动制热开始检测")
            if (enD.temp < curTemp - 1) {//温度低了，发送打开制热
                Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送打开制热命令")
                if (btnHeatBean.isOpen) return
                btnHeatBean.isOpen = true
                sendJDQControl(btnHeatBean)
                btnXFBean.isOpen = true
                sendJDQControl(btnXFBean)
            } else if (enD.temp > curTemp + 1) {
                if (btnHeatBean.isOpen) {//温度高，发送关闭制热
                    Logger.i("当前温度：${enD.temp}  目标温度：${curTemp} 开始发送关闭制热命令")
                    btnHeatBean.isOpen = false
                    sendJDQControl(btnHeatBean)
                    tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                }
            } else {
                Logger.i("适宜温度，不需要操作制热命令")
            }
        }
    }

    /**
     * 开启风阀控制
     */
    private fun startControlXF(enD: EnvironmentalDetector) {
        Logger.i("风阀开始检测当前Co2的值为${enD.cO2}")
        if (SharedPreferencesUtil.getInstance(this).getData(
                Constants.BTN_SETTING_XF_OUT_TYPE,
                Constants.DEFAULT_XF_OUTTYPE
            ) != 3
        ) return
        if (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_MODE, 0) == 2) {
            Logger.i("当前为节能模式，风阀自动关闭")
            return
        }
        when (enD.cO2) {
            in 0..500 -> {
                if (btnWindBean.switchIndex == MODE_Wind_Off && btnWindBean.isOpen) return
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Max
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Min
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Mid
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = true
                    this.switchIndex = MODE_Wind_Off

                }
                sendJDQControl(btnWindBean)
            }
            in 501..600 -> {
                if (btnWindBean.switchIndex == MODE_Wind_Min && btnWindBean.isOpen) return
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Max
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Off
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Mid
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = true
                    this.switchIndex = MODE_Wind_Min
                }
                sendJDQControl(btnWindBean)
            }
            in 601..800 -> {
                if (btnWindBean.switchIndex == MODE_Wind_Mid && btnWindBean.isOpen) return
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Max
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Min
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Off
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = true
                    this.switchIndex = MODE_Wind_Mid
                }
                sendJDQControl(btnWindBean)
            }
            else -> {
                if (btnWindBean.switchIndex == MODE_Wind_Max && btnWindBean.isOpen) return
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Off
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Min
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = false
                    this.switchIndex = MODE_Wind_Mid
                }
                sendJDQControl(btnWindBean)
                btnWindBean.apply {
                    this.isOpen = true
                    this.switchIndex = MODE_Wind_Max
                }
                sendJDQControl(btnWindBean)
            }
        }
    }


    /**
     * 1.将获取到的寄电器开关状态转化为二进制
     * 2.根据我们需要的控制更改二进制
     * 3.将二进制转为发送需要的16进制
     */
    private fun calcuHexOfSendControl(data: String): String {
        var list = getStatus(data, 8)
        //设置点击模块的开关状态
        list[sendBean.switchIndex] = if (sendBean.isOpen) 1 else 0
        //设置点击模块关联模块的开关状态
        if (sendBean.isOpen) {
            when (sendBean.switchIndex) {
                //制冷和制热只有一个能处于启动中
                MODE_Cryogen -> {
                    list[MODE_Heating] = 0
                }
                MODE_Heating -> {
                    list[MODE_Cryogen] = 0
                }
                //加湿和除湿只有一个能处于启动中
                MODE_Dehumidification -> {
                    list[MODE_Humidification] = 0
                }
                MODE_Humidification -> {
                    list[MODE_Dehumidification] = 0
                }
            }
        }
        var bin = ""
        for (i in 0 until list.size) {
            //在获取开关状态的时候，从二进制取0和1，最低位先取，所以转成二进制时，要反着拿值
            bin += list[list.size - 1 - i]
        }
        var hex = intToHex(Integer.parseInt(bin, 2))
        return hex
    }

    /**
     * 10进制转16进制
     */
    private fun intToHex(n: Int): String {
        var n = n
        var s = StringBuffer()
        var a: String
        val b = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )
        if (n == 0) return "00"
        while (n != 0) {
            s = s.append(b[n % 16])
            n /= 16
        }
        a = s.reverse().toString()
        if (a.length == 1) a = "0$a"
        return a
    }

    /**
     * 获取寄电器开关的状态数据
     */
    private fun getStatus(data: String, len: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until len) {
            list.add(Integer.parseInt(data, 16) shr i and 1)
        }
        return list
    }

    /**
     * -------------------------------按钮处理----------------------------------------
     */
    /**
     * 初始化所有按钮
     */
    private fun initAllBtn() {

        btnHeatBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Heating
            this.content = "制热"
        }
        btnClodBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Cryogen
            this.content = "制冷"
        }
        ivClod.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            if (isCryogen) {//正在启动，将其关闭
                recordingBtnState(Constants.BTN_STATE_COLD, 1)
                ZL(false)
                if (!isHeating) {
                    tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                }
            } else {//未启动，将其打开
                recordingBtnState(Constants.BTN_STATE_COLD, 0)
                recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                ZL(true)
            }
            sendAppControlStatus()
        }
        ivHeat.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            if (isHeating) {//正在启动，将其关闭
                recordingBtnState(Constants.BTN_STATE_HEAT, 1)
                ZR(false)
                if (!isCryogen) {
                    tempControlView.setRingColor(Color.parseColor("#7E7D7D"))
                }
            } else {//未启动，将其打开
                recordingBtnState(Constants.BTN_STATE_COLD, 1)
                recordingBtnState(Constants.BTN_STATE_HEAT, 0)
                ZR(true)
            }
            sendAppControlStatus()
        }
        /**
         * 加湿
         */
        btnJSBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Humidification
            this.content = "加湿"
        }
        /**
         * 除湿
         */
        btnCSBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Dehumidification
            this.content = "除湿"
        }


        /**
         * 开启按钮
         */
        llON.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (SharedPreferencesUtil.getInstance(this).getData(
                    Constants.BTN_STATE_SWITCH,
                    1
                ) == 0
            ) {
                clickOFF()
            } else {
                clickON()
            }
            sendAppControlStatus()
        }
        /**
         * 离家按钮
         */
        llLJ.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            if (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_MODE, 0) == 1) {
                clickModeNormal()
            } else {
                clickLJ()
            }
            sendAppControlStatus()

        }
        /**
         * 节能按钮
         */
        llJN.setOnClickListener {
            if (!isCanClick()) return@setOnClickListener
            if (!isCanClickByOpen()) return@setOnClickListener
            if (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_MODE, 0) == 2) {
                clickModeNormal()
            } else {
                clickJN()
            }
            sendAppControlStatus()
        }
        /**
         * 新风
         */
        btnXFBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Hairdryer
            this.content = "新风"
        }
        /**
         * 风力
         */
        btnWindBean = EnvAirControlBean().apply {
            this.isOpen = false
            this.switchIndex = MODE_Wind_Off
            this.content = "风力"
        }
    }

    private fun clickON() {
        Logger.i("空调已启动")
        ivON.setImageResource(R.mipmap.btn_on_selected)
        recordingBtnState(Constants.BTN_STATE_SWITCH, 0)
        restoreBtnState()
        XFON(true)
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_MODE, 0)) {
            0 -> {
                clickModeNormal()
            }
            1 -> {
                clickLJ()
            }
            2 -> {
                clickJN()
            }
        }
    }

    private fun clickOFF() {
        Logger.i("空调已关闭")
        ivON.setImageResource(R.mipmap.btn_on_normal)
        ivNormal.setImageResource(R.mipmap.btn_daily_normal)
        ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
        ivJN.setImageResource(R.mipmap.btn_energy_normal)
        XFON(false)
        ZL(false)
        ZR(false)
        SDAllOff()
        recordingBtnState(Constants.BTN_STATE_SWITCH, 1)
    }

    private fun clickLJ() {
        SharedPreferencesUtil.getInstance(this).saveData(Constants.LAST_TEMP, curTemp)
        if (isHeating) {
            curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                Constants.DEFAULT_TEMP_OUTHOME_ZR,
                Constants.DEFAULT_TEMPDATA_LJZR
            ) as
                    String).split("℃")[0].toInt()
            tempControlView.setProgress(curTemp)
        } else if (isCryogen) {
            curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                Constants.DEFAULT_TEMP_OUTHOME_ZL,
                Constants.DEFAULT_TEMPDATA_LJZL
            ) as String).split("℃")[0].toInt()
            tempControlView.setProgress(curTemp)
        } else {
//            showToast("请先选择冷暖模式")
//            return
        }
        Logger.i("离家模式启动")
        ivLJ.setImageResource(R.mipmap.btn_outhome_selected)
        ivJN.setImageResource(R.mipmap.btn_energy_normal)
        recordingBtnState(Constants.BTN_STATE_MODE, 1)
        //环境设置成内循环
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_XF_ISOUT, false)
        FJControlIn()
        //风机设为自动
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_FJ_ISAUTO, true)
        checkRingColor(curTemp)
    }

    private fun clickJN() {
        SharedPreferencesUtil.getInstance(this).saveData(Constants.LAST_TEMP, curTemp)
        if (isHeating) {
            curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                Constants.DEFAULT_TEMP_JN_ZR,
                Constants.DEFAULT_TEMPDATA_JNZR
            ) as String).split("℃")[0].toInt()
            tempControlView.setProgress(curTemp)
        } else if (isCryogen) {
            curTemp = (SharedPreferencesUtil.getInstance(this).getData(
                Constants.DEFAULT_TEMP_JN_ZL,
                Constants.DEFAULT_TEMPDATA_JNZL
            ) as String).split("℃")[0].toInt()
            tempControlView.setProgress(curTemp)
        } else {
//            showToast("请先选择冷暖模式")
//            return
        }
        FJContorl(0)//风阀切换到低
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_FJ_ISAUTO, true)//风机切换到自动
        Logger.i("节能模式启动")
        ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
        ivJN.setImageResource(R.mipmap.btn_energy_selected)
        recordingBtnState(Constants.BTN_STATE_MODE, 2)
        //新风切换到外循环和低
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_XF_ISOUT, true)
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 0)
        FJContorl(0)
        //风机设为自动
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_FJ_ISAUTO, true)
        checkRingColor(curTemp)
    }

    private fun clickModeNormal() {
        Logger.i("日常模式启动")
        curTemp =
            SharedPreferencesUtil.getInstance(this).getData(Constants.LAST_TEMP, 25).toString()
                .toInt()
        tempControlView.setProgress(curTemp)
        ivLJ.setImageResource(R.mipmap.btn_outhome_normal)
        ivJN.setImageResource(R.mipmap.btn_energy_normal)
        recordingBtnState(Constants.BTN_STATE_MODE, 0)
        //切换正常模式时，开启外循环 和自动
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_XF_ISOUT, true)
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 3)
        FJContorl(3)
        //风机设为自动
        SharedPreferencesUtil.getInstance(this)
            .saveData(Constants.BTN_SETTING_FJ_ISAUTO, false)
        checkRingColor(curTemp)
    }

    private fun ZR(isOpen: Boolean) {
        Logger.i("正在操作制热功能")
        if (isOpen) {
            ivClod.setImageResource(R.mipmap.icon_cool_normal)
            ivHeat.setImageResource(R.mipmap.icon_heat_selected)
            isHeating = true
            isCryogen = false
            btnClodBean.isOpen = false
            sendJDQControl(btnClodBean)
            btnHeatBean.isOpen = true
            sendJDQControl(btnHeatBean)
            btnXFBean.isOpen = true
            sendJDQControl(btnXFBean)
            checkRingColor(curTemp)
        } else {
            ivHeat.setImageResource(R.mipmap.icon_heat_normal)
            isHeating = false
            btnHeatBean.isOpen = false
            sendJDQControl(btnHeatBean)
        }
    }

    private fun ZL(isOpen: Boolean) {
        Logger.i("正在操作制冷功能")
        if (isOpen) {
            ivClod.setImageResource(R.mipmap.icon_cool_selected)
            ivHeat.setImageResource(R.mipmap.icon_heat_normal)
            isHeating = false
            isCryogen = true
            btnHeatBean.isOpen = false
            sendJDQControl(btnHeatBean)
            btnClodBean.isOpen = true
            sendJDQControl(btnClodBean)
            btnXFBean.isOpen = true
            sendJDQControl(btnXFBean)
            checkRingColor(curTemp)
        } else {
            ivClod.setImageResource(R.mipmap.icon_cool_normal)
            isCryogen = false
            btnClodBean.isOpen = false
            sendJDQControl(btnClodBean)
        }
    }

    private fun JS() {
        Logger.i("正在打开加湿功能")
        btnCSBean.isOpen = false
        sendJDQControl(btnCSBean)
        btnJSBean.isOpen = true
        sendJDQControl(btnJSBean)
        btnXFBean.isOpen = true
        sendJDQControl(btnXFBean)
    }

    private fun CS() {
        Logger.i("正在打开除湿功能")
        btnJSBean.isOpen = false
        sendJDQControl(btnJSBean)
        btnCSBean.isOpen = true
        sendJDQControl(btnCSBean)
        btnXFBean.isOpen = true
        sendJDQControl(btnXFBean)
    }

    /**
     * 用于新风被选中
     */
    private fun XFON(isOpen: Boolean) {
        Logger.i("正在操作新风功能")
        if (isOpen) {
            btnXFBean.isOpen = true
            sendJDQControl(btnXFBean)
        } else {
            btnXFBean.isOpen = false
            sendJDQControl(btnXFBean)
        }
    }

    /**
     * 湿度全关方法
     */
    private fun SDAllOff() {
        btnJSBean.isOpen = false
        sendJDQControl(btnJSBean)

        btnCSBean.isOpen = false
        sendJDQControl(btnCSBean)
    }

    /**
     * 新风全关方法
     */
    private fun XFAllOff() {
        isXFTime = false
        btnXFBean.isOpen = false
        sendJDQControl(btnXFBean)
        SharedPreferencesUtil.getInstance(this).saveData(Constants.ISOPEN_XFTIME, false)
    }

    private fun controlXFTime(isOpen: Boolean) {
        var calendar = Calendar.getInstance()
        var curTime = calendar.timeInMillis
        var tfTime = (SharedPreferencesUtil.getInstance(this).getData(
            Constants.DEFAULT_SETTING_TFTIME,
            Constants.DEFAULT_TF_TIME
        ) as String)
            .split("分钟")[0].toInt()
        if (isOpen) {//打开新风，计算下次关闭时间
            btnXFBean.isOpen = true
            sendJDQControl(btnXFBean)
            nextTime = curTime + tfTime * 1000 * 60
            nextXFIsOpen = false
            Logger.i(
                "新风定时功能启动中，新风正在运行,运行时间为${tfTime}分钟，" +
                        "已计算出下次新风关闭时间为：${SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(nextTime))}"
            )
        } else { //关闭新风，计算下次打开时间
            btnXFBean.isOpen = false
            sendJDQControl(btnXFBean)
            nextTime = curTime + (60 - tfTime) * 1000 * 60
            nextXFIsOpen = true
            Logger.i(
                "新风定时功能启动中，新风已关闭，" +
                        "已计算出下次新风开启时间时间为：${SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(nextTime))}"
            )
        }
    }

    /**
     * 规则：从又到左0/1/2
     *
     */
    private fun recordingBtnState(type: String, state: Int) {
        SharedPreferencesUtil.getInstance(this).saveData(type, state)
    }

    private fun restoreBtnState() {
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_SD, 0)) {
            0 -> {//湿度全关
                SDAllOff()
            }
            1 -> {//除湿
                CS()
            }
            2 -> {//加湿
                JS()
            }
        }

        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_COLD, 1)) {
            0 -> {//制冷开启
                ZL(true)
            }
            1 -> {//制冷关闭
                ZL(false)
            }
        }
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_HEAT, 1)) {
            0 -> {//制热开启
                ZR(true)
            }
            1 -> {//关闭制热
                ZR(false)
            }
        }
        curTemp = SharedPreferencesUtil.getInstance(this)
            .getData(Constants.ENJOYTEMP, curTemp).toString().toInt()
        tempControlView.setProgress(curTemp)
    }

    private fun restoreSwitchBtnState() {
        when (SharedPreferencesUtil.getInstance(this).getData(Constants.BTN_STATE_SWITCH, 1)) {
            0 -> {//开启
                clickON()
            }
            1 -> {//关闭
                clickOFF()
            }
        }
    }

    internal inner class EnvAirBoradCast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI) {
                val data = intent.getStringExtra("msg")
                buffer.append(data)

                if (buffer.toString().length > 6 && buffer.toString().substring(
                        0,
                        6
                    ) == "020314"
                ) {//环境探测器数据EnvQ3
                    val message = Message.obtain()
                    message.what = 0
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 6 && buffer.toString().substring(
                        0,
                        6
                    ) == "02032E"
                ) {//环境探测器数据Ate24V
                    val message = Message.obtain()
                    message.what = 3
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 4 &&
                    buffer.toString().substring(0, 4).toLowerCase() == "AA55".toLowerCase() &&
                    buffer.toString().substring(
                        buffer.length - 4,
                        buffer.length
                    ).toLowerCase() == "0D0A".toLowerCase()
                ) {//SCHIDERON寄电器状态
                    val message = Message.obtain()
                    message.what = 1
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
                if (buffer.toString().length > 6 &&
                    buffer.toString().substring(0, 6).toLowerCase() == "FE0102".toLowerCase()
                ) {//ZZ-IO1600寄电器状态
                    val message = Message.obtain()
                    message.what = 2
                    message.obj = buffer.toString()
                    handler.sendMessage(message)
                    buffer.delete(0, buffer.length)
                }
            } else if (intent.action == CommonConfig.ACTION_ENVAIR_SD_CONTORL) {//其他页面控制湿度开关
                when (intent.getIntExtra("type", 0)) {
                    0 -> {//全关
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.BTN_STATE_SD, 0)
                        SDAllOff()
                    }
                    1 -> {//除湿
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.BTN_STATE_SD, 1)
                        CS()
                    }
                    2 -> {//加湿
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.BTN_STATE_SD, 2)
                        JS()
                    }
                }
            } else if (intent.action == CommonConfig.ACTION_ENVAIR_XF_CONTORL) {//其他页面控制新风循环
                if (intent.getBooleanExtra("isOut", true)) {//外循环
                    FJContorl(
                        SharedPreferencesUtil.getInstance(context).getData(
                            Constants.BTN_SETTING_XF_OUT_TYPE,
                            Constants.DEFAULT_XF_OUTTYPE
                        ) as Int
                    )
                } else {//内循环
                    FJControlIn()
                }
            } else if (intent.action == CommonConfig.ACTION_ENVAIR_FJ_CONTORL) {//其他页面控制湿度开关
                FJContorl(intent.getIntExtra("FJType", Constants.DEFAULT_XF_OUTTYPE))
            } else if (intent.action == CommonConfig.ACTION_ENVAIRACTIVITY_SEND_ENVSEARCH) {//发送环境查询命令
                buffer.delete(0, buffer.length)//更新下数据存储器，防止出现骚问题
                sendSerial(currentEnv.SearchStatusCode)
            } else if (intent.action == CommonConfig.ACTION_ENVAIRACTIVITY_SEND_JDQSEARCH) {//发送寄电器查询命令
                buffer.delete(0, buffer.length)//更新下数据存储器，防止出现骚问题
                sendJDQSearch()
            } else if (intent.action == CommonConfig.ACTION_BGCKGROUND_IMG_CHANGE) {//发送寄电器查询命令
                //设置背景图片
                val imgId = SharedPreferencesUtil.getInstance(this@EnvAirActivity).getData(
                    Constants.IMGID,
                    R.mipmap.bg2
                ) as Int
                layout_total.setBackgroundResource(imgId)
            }
        }
    }

    private fun FJControlIn() {
        btnWindBean.isOpen = false
        btnWindBean.switchIndex = MODE_Wind_Min
        sendJDQControl(btnWindBean)
        btnWindBean.isOpen = false
        btnWindBean.switchIndex = MODE_Wind_Max
        sendJDQControl(btnWindBean)
        btnWindBean.isOpen = false
        btnWindBean.switchIndex = MODE_Wind_Mid
        sendJDQControl(btnWindBean)
        btnWindBean.isOpen = true
        btnWindBean.switchIndex = MODE_Wind_Off
        sendJDQControl(btnWindBean)
    }

    private fun FJContorl(type: Int) {
        when (type) {
            0 -> {//低
                btnWindBean.isOpen = true
                btnWindBean.switchIndex = MODE_Wind_Min
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Mid
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Max
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Off
                sendJDQControl(btnWindBean)
            }
            1 -> {//中
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Min
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = true
                btnWindBean.switchIndex = MODE_Wind_Mid
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Max
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Off
                sendJDQControl(btnWindBean)
            }
            2 -> {//高
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Min
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Mid
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = true
                btnWindBean.switchIndex = MODE_Wind_Max
                sendJDQControl(btnWindBean)
                btnWindBean.isOpen = false
                btnWindBean.switchIndex = MODE_Wind_Off
                sendJDQControl(btnWindBean)
            }
            else -> {//自动

            }
        }
    }
    /**
     * 上传日志
     *  最新日志下载地址
     *  https://www.hjlapp.com/ows-worker/logs/LogsByWusyLib_0.log
     */
    private fun updateLog() {
        Logger.i("-----------------设备信息------------------")
        Logger.i("设备制造商：" + Build.MANUFACTURER)
        Logger.i("设备品牌：" + Build.BRAND)
        Logger.i("设备型号：" + Build.MODEL)
        Logger.i("系统版本：" + Build.VERSION.RELEASE)
        Logger.i("mac地址：" + InterAddressUtil.getMacAddress())
        Logger.i("-------------------------------------------")
        val url = "https://www.hjlapp.com/cgProgramApi/fileUpload/uploadFile?"
        val file =
            File(Environment.getExternalStorageDirectory().toString() + "/logger/LogsByWusyLib_0.log")
        val maps = HashMap<String, String>()
        maps["type"] = "1"
        Thread.sleep(1000)
        val macAddress= InterAddressUtil.getMacAddress()
        OkHttpUtil.getInstance()
            .upLoadFile(url, "file", file, maps, object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    runOnUiThread {
                        val socketPackage = SocketPackage().apply {
                            this.content = "$macAddress-设备日志上传失败"
                            this.description = "设备日志上传"
                            this.type = "1"
                            this.intent = "UpdateLog"
                        }
                        socketHelper.send(Gson().toJson(socketPackage),this@EnvAirActivity)
                        hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    runOnUiThread {
                        var json = JSONObject(response!!.body()!!.string())
                        if (json.getString("status") == "0"){
                            val socketPackage = SocketPackage().apply {
                                this.content = "$macAddress-设备日志上传成功"
                                this.description = "设备日志上传"
                                this.type = "1"
                                this.intent = "UpdateLog"
                            }
                            socketHelper.send(Gson().toJson(socketPackage),this@EnvAirActivity)
                        }
                        hideLoadImage()
                    }
                }
            })
    }
}