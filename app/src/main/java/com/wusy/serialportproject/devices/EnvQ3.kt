package com.wusy.serialportproject.devices

import com.orhanobut.logger.Logger
import com.wusy.serialportproject.util.DataUtils
import java.util.ArrayList

class EnvQ3 : BaseDevices() {
    init {
        this.name = "EnvQ3"
        this.type = "Env"
        this.tips = "环境探测器"
        /**
         * 默认查询16个继电器的状态01030000000AC5CD
         */
        this.SearchStatusCode = "02030000000A" + DataUtils.getCRC("02030000000A")
    }

    fun parseData(allData:String):HashMap<String,Any> {
        var data = allData.substring(6)
        var hexs = ArrayList<String>()
        for (i in 0 until data.length / 4) {
            hexs.add(data.substring(i * 4, i * 4 + 4))
        }
        var map=HashMap<String,Any>()
        map["hexs"]=hexs
        try {
            map["PM2.5"]= Integer.parseInt(hexs.get(0), 16)
            map["temp"] = Integer.parseInt(hexs.get(1), 16).toFloat() / 10
            map["humidity"]= Integer.parseInt(hexs.get(2), 16).toFloat() / 10
            map["CO2"]= Integer.parseInt(hexs.get(3), 16)
            map["TVOC"]= Integer.parseInt(hexs.get(4), 16).toDouble() / 100
            map["PM2.5OutDoor"] = Integer.parseInt(hexs.get(6), 16)
            map["formaldehyde"]= Integer.parseInt(hexs.get(7), 16).toDouble() / 1000
            map["AQI"]=calcAQIByPM25(map["PM2.5"] as Int)
            this.log="---------经EnvQ3分析--------\n" +
                    "PM2.5=" + map["PM2.5"] + "\n" +
                    "温度=" + map["temp"] + "\n" +
                    "湿度=" +  map["humidity"] + "\n" +
                    "CO2=" +  map["CO2"] + "\n" +
                    "TVOC=" +  map["TVOC"] + "\n" +
                    "室外PM2.5=" + map["PM2.5OutDoor"] + "\n" +
                    "甲醛=" + map["formaldehyde"] + "\n" +
                    "API="+map["AQI"]+ "\n"+
                    "-----------------------"
            Logger.i(this.log)
        } catch (e: Exception) {
            Logger.e("解析环境数据发生错误", e)
        }
        return map
    }
    fun calcAQIByPM25(pm25:Int):Int{
        var aqi=0
        when(pm25){
            in 0..35->{
                aqi=50/35*(pm25-0)+0
            }
            in 35..75->{
                aqi=50/40*(pm25-35)+50
            }
            in 75..115->{
                aqi=50/40*(pm25-75)+100
            }
            in 115..150->{
                aqi=50/35*(pm25-115)+150
            }
            in 150..250->{
                aqi=100/100*(pm25-150)+200
            }
            in 250..350->{
                aqi=100/100*(pm25-250)+300
            }
            in 350..500->{
                aqi=100/150*(pm25-350)+400
            }
        }
        return aqi
    }
}
