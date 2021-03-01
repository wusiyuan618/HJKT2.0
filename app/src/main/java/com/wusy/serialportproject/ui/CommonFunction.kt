package com.wusy.serialportproject.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.app.URLForOkHttp
import com.wusy.serialportproject.util.DownAPK.VersionBean
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.util.OkHttpUtil
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import com.wusy.serialportproject.util.DownAPK.DownLoadApkUtil
import com.wusy.wusylibrary.base.BaseFragment

class CommonFunction {
    fun requestVersion(activity: BaseActivity) {
        OkHttpUtil.getInstance()
            .asynGet(
                URLForOkHttp.requestVersionUpdate(),
                object : OkHttpUtil.ResultCallBack {
                    override fun successListener(call: Call?, response: Response?) {
                        try {
                            var jsonStr = response?.body()?.string() ?: ""
                            var bean =
                                Gson().fromJson<VersionBean>(jsonStr, VersionBean::class.java)
                            if (bean.status == "0" && bean.data != null) {
                                bean.data?.get(0)?.run {
                                    activity.runOnUiThread {
                                        val version = getVersionCode(activity)
                                        if (versionNumber > version) {
                                            DownLoadApkUtil(activity).start(
                                                updateUrl ?: "",
                                                description
                                            )
                                        } else {
                                            activity.showToast("暂无更新")
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Logger.e(e, "APP更新发生了错误：")
                        }
                    }

                    override fun failListener(call: Call?, e: IOException?, message: String?) {

                    }
                })
    }

    /**
     * 上传日志
     *  最新日志下载地址
     *  https://www.hjlapp.com/ows-worker/logs/LogsByWusyLib_0.log
     *  如果拉到过往文件，请先清除浏览器缓存
     */
    fun updateLog(activity: BaseActivity,fragment:BaseFragment?) {
        Logger.i("-----------------设备信息------------------")
        Logger.i("设备制造商：" + Build.MANUFACTURER)
        Logger.i("设备品牌：" + Build.BRAND)
        Logger.i("设备型号：" + Build.MODEL)
        Logger.i("系统版本：" + getVersionCode(activity))
        Logger.i("mac地址：" + InterAddressUtil.getMacAddress())
        Logger.i("-------------------------------------------")
        val url = "https://www.hjlapp.com/cgProgramApi/fileUpload/uploadFile?"
        val file =
            File(Environment.getExternalStorageDirectory().toString() + "/logger/LogsByWusyLib_0.log")
        val maps = HashMap<String, String>()
        maps["type"] = "1"
        Thread.sleep(1000)
        OkHttpUtil.getInstance()
            .upLoadFile(url, "file", file, maps, object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    activity.runOnUiThread {
                        activity.showToast("上传失败,网络错误")
                        Log.e("wsy", e!!.message, e)
                        activity.hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    activity.runOnUiThread {
                        val json = JSONObject(response!!.body()!!.string())
                        if (json.getString("status") == "0")
                            activity.showToast(
                                (json.getString("msg") ?: "上传成功") + "。上传文件大小：${file.length()}"
                            )
                        else
                            activity.showToast("上传失败")
                        activity.hideLoadImage()
                        fragment?.hideLoadImage()
                    }
                }
            })
    }

    private fun getVersionCode(context: Context): Int {        // 包管理者
        val mg = context.packageManager
        try {
            //getPackageInfo(packageName 包名, flags 标志位（表示要获取什么数据）);
            // 0表示获取基本数据
            val info = mg.getPackageInfo(context.packageName, 0)
            return info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }
    fun cleanLog(){
        delAllFile(Environment.getExternalStorageDirectory().toString() + "/logger/")
    }
    /**
     * 删除文件夹里面的所有文件
     *
     * @param path
     * 文件夹路径 如 c:/fqf
     */
    private fun delAllFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            return
        }
        if (!file.isDirectory) {
            return
        }
        val tempList = file.list()
        var temp: File? = null
        for (i in tempList.indices) {
            if (path.endsWith(File.separator)) {
                temp = File(path + tempList[i])
            } else {
                temp = File(path + File.separator + tempList[i])
            }
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                delAllFile(path + "/" + tempList[i])// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i])// 再删除空文件夹
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath
     * 文件夹路径及名称 如c:/fqf
     */
    private fun delFolder(folderPath: String) {
        try {
            delAllFile(folderPath) // 删除完里面所有内容
            var filePath = folderPath
            filePath = filePath
            val myFilePath = java.io.File(filePath)
            myFilePath.delete() // 删除空文件夹
        } catch (e: Exception) {
            Logger.e("删除文件夹出错")
            e.printStackTrace()
        }

    }
}
