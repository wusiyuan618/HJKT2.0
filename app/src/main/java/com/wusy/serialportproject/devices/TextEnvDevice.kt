package com.wusy.serialportproject.devices

class TextEnvDevice:BaseDevices(){
    init {
        this.name= "TestEnv"
        this.type="Env"
        this.tips="环境探测器测试"
        /**
         * 默认查询16个继电器的状态
         */
        this.SearchStatusCode= ""
    }
}
