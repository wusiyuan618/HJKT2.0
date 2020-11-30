package com.wusy.serialportproject.app

import com.wusy.serialportproject.bean.EnvironmentalDetector
import java.util.*

class Constants {
    companion object {
        /**
         * --------------------共享参数--------------------
         */
        /*存储屏保开启间隔时间 */
        const val SCREEN_SETTING_TIME: String = "screen_setting_time"
        /*存储屏保开启间隔时间在WheelView中的posistion */
        const val SCREEN_SETTING_POSITION: String = "screen_setting_position"
        /*存储屏保的模式type：0=空气质量 1=模拟闹钟 2=数字闹钟 3=息屏*/
        const val SCREEN_SETTING_MODEL_TYPE: String = "screen_setting_model_type"
        /*存储代理商电话 */
        const val DEFAULT_DLS_PHONE = "default_dls_phone"
        /*节能温度 */
        const val DEFAULT_TEMP_JN_ZL = "default_temp_jn_zl"
        const val DEFAULT_TEMP_JN_ZR = "default_temp_jn_zr"

        /*离家温度 */
        const val DEFAULT_TEMP_OUTHOME_ZL = "default_temp_outhome_zl"
        const val DEFAULT_TEMP_OUTHOME_ZR = "default_temp_outhome_zr"

        /* 通风时间 */
        const val DEFAULT_SETTING_TFTIME = "default_setting_tftime"

        /* 加湿湿度 */
        const val SETTING_JS_COUNT = "setting_js_count"
        /* 除湿湿度 */
        const val SETTING_CS_COUNT = "setting_cs_count"

        /* 记录上次日常的温度 */
        const val LAST_TEMP="last_temp"
        /* 存储用户设置的适宜温度 */
        const val ENJOYTEMP="enjoy_temp"
        /* 存储通风是否开启定时 */
        const val ISOPEN_XFTIME="is_open_xftime"

        /*存储空调首页按钮的最后状态*/
        const val BTN_STATE_LN="btn_state_ln"
        //0=开 1=关
        const val BTN_STATE_COLD="btn_state_cold"
        const val BTN_STATE_HEAT="btn_state_heat"

        const val BTN_STATE_SD="btn_state_sd"
        /**
         * 开关 0=开 1=关 默认1
         */
        const val BTN_STATE_SWITCH="btn_state_switch"
        /**
         * 模式 0=日常 1=离家 2=节能 默认0
         */
        const val BTN_STATE_MODE="btn_state_mode"
        /* 设置页按钮选中状态 */
        //true为自动
        const val BTN_SETTING_SD_ISAUTO="btn_setting_sd_isauto"
        const val BTN_SETTING_FJ_ISAUTO="btn_setting_fj_isauto"
        //true为外循环
        const val BTN_SETTING_XF_ISOUT="btn_setting_xf_isout"
        //外循环大小设置0=低 1=中 2=高 3=自动
        const val BTN_SETTING_XF_OUT_TYPE="btn_setting_xf_out_type"

        //增值服务 过滤器和灯管的更新时间
        const val VALUEADDED_UPDATE_DATE_FILTER="valueadded_update_date_filter"
        const val VALUEADDED_UPDATE_DATE_LIGHT="valueadded_update_date_light"
        //记录温度编程的数据
        const val TEMP_CODE="temp_code"
        //记录维保时间
        const val WBTIME="WBTIME"
        const val NESTWBTIME="NESTWBTIME"
        //记录背景图片
        const val IMGID="IMGID"
        /**
         * -------------------------------------------------
         */


        /**
         *  --------------------默认值--------------------
         */
        /* 系统设置权限密码 */
        const val DEFAULT_SYSTEM_SETTINGPWD = "667788"
        /* 代理商默认电话 */
        const val DEFAULT_DLS_PHONENUMBER = "18756568778"
        /*屏保打开默认时间。修改默认时间记得去吧屏保设置的默认position改了*/
        const val DEFAULT_SCREEN_DISTANCE_TIME = "30分钟"
        /* 温度的默认值 */
        const val DEFAULT_TEMPDATA_JNZL = "26℃"
        const val DEFAULT_TEMPDATA_JNZR = "18℃"
        const val DEFAULT_TEMPDATA_LJZL = "28℃"
        const val DEFAULT_TEMPDATA_LJZR = "16℃"
        /* 默认通风时间 */
        const val DEFAULT_TF_TIME = "15分钟"
        /* 默认除湿加湿湿度 */
        const val DEFAULT_JS_COUNT="30%RH"
        const val DEFAULT_CS_COUNT="80%RH"
        /* 默认外循环大小 3=自动 */
        const val DEFAULT_XF_OUTTYPE = 3
        /**
         * -----------------------------------------------
         */

        /**
         * --------------------屏保相关--------------------
         */
        /*屏保是否打开*/
        var isOpenScreen = false
        /*上次触摸屏幕的时间*/
        var lastUpdateTime: Date = Date(System.currentTimeMillis())
        /*当前的空气质量信息*/
        var curED:EnvironmentalDetector?=null
        /**
         * ------------------------------------------------
         */

        /**
         * --------------------全局变量--------------------
         */
        /**
         * ------------------------------------------------
         */
    }
}