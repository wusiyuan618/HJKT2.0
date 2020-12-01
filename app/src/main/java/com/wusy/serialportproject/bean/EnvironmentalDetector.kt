package com.wusy.serialportproject.bean

import com.orhanobut.logger.Logger
import com.wusy.serialportproject.devices.Ate24V
import com.wusy.serialportproject.devices.BaseDevices
import com.wusy.serialportproject.devices.EnvQ3
import com.wusy.serialportproject.util.InterAddressUtil

import java.util.ArrayList
import java.util.HashMap

class EnvironmentalDetector(data: String, devices: BaseDevices) {
    var pM25: Int = 0//PM2.5
    var temp: Float = 0f//温度
    var humidity: Float = 0.toFloat()//湿度
    var cO2: Int = 0//CO2
    var pM25OutDoor: Int = 0//室外PM2.5
    var formaldehyde: Double = 0.toDouble()//甲醛
    var tvoc: Double = 0.toDouble()//TVOC
    var hexs: ArrayList<String>? = null
    var pM25Level:Int=0//PM2.5等级
    var aQI:Int=0
    var createTime=0L
    var macAddress=""
    init {
        when (devices.name) {
            "EnvQ3" -> {
                val map=(devices as EnvQ3).parseData(data)
                this.pM25 = (map["PM2.5"]?:0) as Int
                this.temp = (map["temp"]?:0f) as Float
                this.humidity = (map["humidity"]?:0f) as Float
                this.cO2 = (map["CO2"]?:0 )as Int
                this.tvoc = (map["TVOC"]?:0.0 )as Double
                this.pM25OutDoor =(map["PM2.5OutDoor"]?:0) as Int
                this.formaldehyde = (map["formaldehyde"]?:0.0) as Double
                this.hexs = (map["hexs"]?:ArrayList<String>()) as ArrayList<String>
                this.aQI=(map["AQI"]?:0) as Int
            }
            "Ate24V"->{
                val map=(devices as Ate24V).parseData(data)
                this.pM25 = (map["PM2.5"]?:0) as Int
                this.temp = (map["temp"]?:0f) as Float
                this.humidity = (map["humidity"]?:0f) as Float
                this.cO2 = (map["CO2"]?:0 )as Int
                this.tvoc = (map["TVOC"]?:0.0 )as Double
                this.pM25Level = (map["PM2.5污染等级"] ?:0)as Int
                this.hexs = (map["hexs"]?:ArrayList<String>()) as ArrayList<String>
                this.aQI=(map["AQI"]?:0) as Int
            }
            "TestEnv"->{
                this.pM25 =12
                this.temp = 24.2f
                this.humidity =59.0f
                this.cO2 =810
                this.tvoc =0.15
                this.pM25Level = 1
                this.formaldehyde = 0.043
            }
        }
        macAddress= InterAddressUtil.getMacAddress()
        createTime=System.currentTimeMillis()
    }
}
