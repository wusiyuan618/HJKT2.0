package com.wusy.serialportproject.util;

public class CommonConfig {
    //ACTION （监听串口服务中获取到的数据）
    public static final String SERIALPORTPROJECT_ACTION_SP_UI="SERIALPORTPROJECT_ACTION_SP_UI";
    //ACTION（监听对SerialPort模块控制消息）
    public static final String SERIALPORTPROJECT_ACTION_SP_SERVICE="SERIALPORTPROJECT_ACTION_SP_SERVICE";
    //ACTION (监听已经被解析过得环境探测器数据)
    public static final String ACTION_ENVIRONMENTALDETECOTOR_DATA="ACTION_ENVIRONMENTALDETECOTOR_DATA";
    //ACTION (监听环境设置页面对主页面的湿度控制命令)
    public static final String ACTION_ENVAIR_SD_CONTORL="ACTION_ENVAIR_SD_CONTORL";
    //ACTION (监听环境设置页面对主页面的新风控制命令)
    public static final String ACTION_ENVAIR_XF_CONTORL="ACTION_ENVAIR_XF_CONTORL";
    //ACTION (监听环境设置页面对主页面的风阀控制命令)
    public static final String ACTION_ENVAIR_FJ_CONTORL="ACTION_ENVAIR_FJ_CONTORL";
    //ACTION (SystemTextActivity获取日志)
    public static final String ACTION_SYSTEMTEST_LOG="ACTION_SYSTEMTEST_LOG";
    //ACTION (SystemTextActivity更新寄电器状态)
    public static final String ACTION_SYSTEMTEST_JDQ="ACTION_SYSTEMTEST_JDQ";
    //ACTION (EnvAirActivity发送寄电器查询命令)
    public static final String ACTION_ENVAIRACTIVITY_SEND_JDQSEARCH="ACTION_ENVAIRACTIVITY_SEND_JDQSEARCH";
    //ACTION (EnvAirActivity发送环境数据查询命令)
    public static final String ACTION_ENVAIRACTIVITY_SEND_ENVSEARCH="ACTION_ENVAIRACTIVITY_SEND_ENVSEARCH";

}
