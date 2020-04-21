package com.wusy.serialportproject.util.DownAPK

class VersionBean {

    /**
     * msg : 成功
     * data : [{"createTime":1571899899038,"description":"2","id":"d251bd0d50fa4052a364a5d1a4713319","productType":"5","status":"1","updateUrl":"https://www.hjlapp.com//ows-worker/apk/app-debug.apk","useStatus":"1","versionNumber":1}]
     * status : 0
     */

    var msg: String? = null
    var status: String? = null
    var data: List<DataBean>? = null

    class DataBean {
        /**
         * createTime : 1571899899038
         * description : 2
         * id : d251bd0d50fa4052a364a5d1a4713319
         * productType : 5
         * status : 1
         * updateUrl : https://www.hjlapp.com//ows-worker/apk/app-debug.apk
         * useStatus : 1
         * versionNumber : 1
         */

        var createTime: Long = 0
        var description: String? = null
        var id: String? = null
        var productType: String? = null
        var status: String? = null
        var updateUrl: String? = null
        var useStatus: String? = null
        var versionNumber: Int = 0
    }
}
