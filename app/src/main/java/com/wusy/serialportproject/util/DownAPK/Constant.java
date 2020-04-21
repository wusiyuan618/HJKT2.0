package com.wusy.serialportproject.util.DownAPK;

import android.os.Environment;

/**
 * Created by XIAO RONG on 2019/3/27.
 * 这是一个我想要偷懒，用来存储变量的类
 */

public class Constant {
    public static final String DOWNLOAD_ACTION = "download_action";
    public final static String FILEDIR= Environment.getExternalStorageDirectory() + "/SerialPort/";
    public static String Custom_DOWNLOADAPK_URL = "";//APP更新时的下载地址
}
