package com.wusy.serialportproject.app

object URLForOkHttp{
//    var baseUrl = "http://192.168.1.98:9208/"
    var baseUrl = "http://39.98.255.74:9208/"
    var comParam: String = "&platform=AndroidPad"
    /**
     *  获取注册列表
     */
    fun getAccountList(macAddress: String): String {
        return "${baseUrl}hvProgramApi/appAccount/getAppAccountList?mac=$macAddress$comParam"
    }
    fun requestAccountEdit(): String {
        return "${baseUrl}hvProgramApi/appAccount/addOrUpdate?$comParam"
    }
    fun requestAccountDelete(id:String): String {
        return "${baseUrl}hvProgramApi/appAccount/delete?accountId=$id$comParam"
    }
    fun requestVersionUpdate(): String {
        return "${baseUrl}hvProgramApi/version/getList?productType=3$comParam"
    }
}
