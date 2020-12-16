package com.wusy.serialportproject.bean

import android.content.Context
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.wusylibrary.util.SharedPreferencesUtil

class ControlStatusBean(context: Context){
    /**
     * 开关 0=开 1=关 默认1
     */
    var switchStatus=""
    /**
     * 模式 0=日常 1=离家 2=节能 默认0
     */
    var modelStatus=""
    /**
     * 制冷 0=开 1=关
     */
    var cryogenStatus=""
    /**
     * 制热 0=开 1=关
     */
    var heatingStatus=""
    var temp=""
    var createTime=0L
    var macAddress=""
    init {
        val sp=SharedPreferencesUtil.getInstance(context)
        //开关状态
        switchStatus=sp.getData(Constants.BTN_STATE_SWITCH,1).toString()
        //模式状态
        modelStatus=sp.getData(Constants.BTN_STATE_MODE,0).toString()
        //冷热状态
        cryogenStatus=sp.getData(Constants.BTN_STATE_COLD,1).toString()
        heatingStatus=sp.getData(Constants.BTN_STATE_HEAT,1).toString()

        //温度状态
        temp=sp.getData(Constants.ENJOYTEMP,20).toString()
        createTime=System.currentTimeMillis()
        macAddress= InterAddressUtil.getMacAddress()
    }
}
