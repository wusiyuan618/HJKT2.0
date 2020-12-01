package com.wusy.serialportproject.bean

import java.io.Serializable


/**
 * Socket消息实体类
 */
class SocketPackage : Serializable {

    /**
     * 消息类型 1显示 2控制
     */
    var type: String? = null

    /**
     * 消息体
     */
    var content: Any? = null

    /**
     * 消息意图
     */
    var intent: String? = null

    /**
     * 消息描述
     */
    var description: String? = null

}
