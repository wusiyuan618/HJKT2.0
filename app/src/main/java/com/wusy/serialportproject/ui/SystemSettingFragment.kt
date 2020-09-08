package com.wusy.serialportproject.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.popup.MakeSurePopup
import com.wusy.serialportproject.popup.NumberEditPopup
import com.wusy.serialportproject.util.DataUtils
import com.wusy.serialportproject.util.DownAPK.DownLoadApkUtil
import com.wusy.serialportproject.util.DownAPK.VersionBean
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.OkHttpUtil
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.fragment_setting_system.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception

class SystemSettingFragment : BaseFragment() {
    lateinit var rlReSet: RelativeLayout
    lateinit var rlSetPhone: RelativeLayout
    lateinit var rlUpdateLog: RelativeLayout
    lateinit var rlUpdateSys: RelativeLayout
    lateinit var rlExit: RelativeLayout
    lateinit var rlReboot: RelativeLayout
    lateinit var rlSysTest: RelativeLayout


    lateinit var reSetMakeSurePopup: MakeSurePopup
    lateinit var updateLogMakeSurePopup: MakeSurePopup
    lateinit var setPhoneNumberEidtPopup: NumberEditPopup

    lateinit var downLoadUtil: DownLoadApkUtil

    override fun findView(view: View?) {
        rlReSet = view!!.findViewById(R.id.rlReSet)
        rlSetPhone = view.findViewById(R.id.rlSetPhone)
        rlUpdateLog = view.findViewById(R.id.rlUpdateLog)
        rlUpdateSys = view.findViewById(R.id.rlUpdateSys)
        rlExit = view.findViewById(R.id.rlExit)
        rlReboot = view.findViewById(R.id.rlReboot)
        rlSysTest = view.findViewById(R.id.rlSysTest)
    }


    override fun init() {
        initReSetPop()
        initUpdateLogPop()
        initSetPhonePop()
        downLoadUtil = DownLoadApkUtil(activity as BaseActivity)

        rlReSet.setOnClickListener {
            reSetMakeSurePopup.showPopupWindow()
        }
        rlSetPhone.setOnClickListener {
            setPhoneNumberEidtPopup.tvEditContent.text = ""
            setPhoneNumberEidtPopup.showPopupWindow()
        }
        rlUpdateLog.setOnClickListener {
            updateLogMakeSurePopup.showPopupWindow()
        }
        rlUpdateSys.setOnClickListener {
            requestVersion()
        }
        rlExit.setOnClickListener {
            val intent = Intent()
            intent.component =
                ComponentName("com.android.launcher3", "com.android.launcher3.Launcher")
            startActivity(intent)
        }
        rlReboot.setOnClickListener {
            context?.sendBroadcast(Intent("HJL_ACTION_REBOOT"))
//            val rb = Intent(Intent.ACTION_REBOOT)
//            rb.putExtra("nowait", 1)
//            rb.putExtra("interval", 1)
//            rb.putExtra("window", 0)
//            context?.sendBroadcast(rb)
        }
        rlSysTest.setOnClickListener {
            navigateTo(SystemTextActivity::class.java)
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_system
    }

    private fun initReSetPop() {
        reSetMakeSurePopup = MakeSurePopup(context)
        reSetMakeSurePopup.tvTitle.text = "恢复出厂设置"
        reSetMakeSurePopup.tvContent.text = "您是否需要恢复出厂设置呢？"
        reSetMakeSurePopup.ivSure.setOnClickListener {
            showLoadImage()
            Thread(Runnable {
                Thread.sleep(2000)
                activity!!.runOnUiThread {
                    hideLoadImage()
                    showToast("成功恢复出厂设置")
                    reSetMakeSurePopup.dismiss()
                }
            }).start()
        }
    }

    private fun initUpdateLogPop() {
        updateLogMakeSurePopup = MakeSurePopup(context)
        updateLogMakeSurePopup = MakeSurePopup(context)
        updateLogMakeSurePopup.tvTitle.text = "发送运行分析数据"
        updateLogMakeSurePopup.tvContent.text = "您是否需要发送运行分析数据呢？"
        updateLogMakeSurePopup.ivSure.setOnClickListener {
            updateLogMakeSurePopup.dismiss()
            showLoadImage()
            updateLog()
        }
    }

    /**
     * 上传日志
     *  最新日志下载地址
     *  https://www.hjlapp.com/ows-worker/logs/LogsByWusyLib_0.log
     */
    private fun updateLog() {
        Logger.i("-----------------设备信息------------------")
        Logger.i("设备制造商：" + Build.MANUFACTURER)
        Logger.i("设备品牌：" + Build.BRAND)
        Logger.i("设备型号：" + Build.MODEL)
        Logger.i("系统版本：" + Build.VERSION.RELEASE)
        Logger.i("mac地址：" + InterAddressUtil.getMacAddress())
        Logger.i("-------------------------------------------")
        var url = "https://www.hjlapp.com/cgProgramApi/fileUpload/uploadFile?"
        var file =
            File(Environment.getExternalStorageDirectory().toString() + "/logger/LogsByWusyLib_0.log")
        var maps = HashMap<String, String>()
        maps["type"] = "1"
        Thread.sleep(1000)
        OkHttpUtil.getInstance()
            .upLoadFile(url, "file", file, maps, object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    activity!!.runOnUiThread {
                        showToast("上传失败,网络错误")
                        Log.e("wsy", e!!.message, e)
                        hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    activity!!.runOnUiThread {
                        var json = JSONObject(response!!.body()!!.string())
                        if (json.getString("status") == "0")
                            showToast(json.getString("msg") ?: "上传成功")
                        else
                            showToast("上传失败")
                        hideLoadImage()
                    }
                }
            })
    }

    private fun initSetPhonePop() {
        setPhoneNumberEidtPopup = NumberEditPopup(context)
        setPhoneNumberEidtPopup.tvTitle.text = "修改代理商电话"
        setPhoneNumberEidtPopup.tvContent.text = "当前代理商电话：" + DataUtils.formatPhoneNumber(
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_DLS_PHONE,
                Constants.DEFAULT_DLS_PHONENUMBER
            ).toString(), " - "
        )
        setPhoneNumberEidtPopup.numberKeyBoxView.setNumberKeyBoxViewClick {
            when (it) {
                "ok" -> {
                    if (setPhoneNumberEidtPopup.tvEditContent.text.toString().length != 11) {
                        Toast.makeText(context, "请正确输入11位的手机号码", Toast.LENGTH_SHORT).show()
                        return@setNumberKeyBoxViewClick
                    }
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(
                            Constants.DEFAULT_DLS_PHONE,
                            setPhoneNumberEidtPopup.tvEditContent.text.toString()
                        )
                    setPhoneNumberEidtPopup.tvEditContent.text = ""
                    setPhoneNumberEidtPopup.tvContent.text =
                        "当前代理商电话：" + DataUtils.formatPhoneNumber(
                            SharedPreferencesUtil.getInstance(context).getData(
                                Constants.DEFAULT_DLS_PHONE,
                                Constants.DEFAULT_DLS_PHONENUMBER
                            ).toString(), " - "
                        )
                    setPhoneNumberEidtPopup.dismiss()
                    showToast("修改成功")
                }
                "delete" -> {
                    if (setPhoneNumberEidtPopup.tvEditContent.text.toString().isNotEmpty()) {
                        setPhoneNumberEidtPopup.tvEditContent.text =
                            setPhoneNumberEidtPopup.tvEditContent.text.toString()
                                .substring(
                                    0,
                                    setPhoneNumberEidtPopup.tvEditContent.text.toString().length - 1
                                )
                    }
                }
                else -> {
                    setPhoneNumberEidtPopup.tvEditContent.text =
                        setPhoneNumberEidtPopup.tvEditContent.text.toString() + it
                }
            }
        }
    }

    private fun requestVersion() {
        OkHttpUtil.getInstance()
            .asynGet("https://www.hjlapp.com/cgProgramApi/version/getList?productType=12",
                object : OkHttpUtil.ResultCallBack {
                    override fun successListener(call: Call?, response: Response?) {
                        try {
                            var jsonStr = response?.body()?.string() ?: ""
                            var bean =
                                Gson().fromJson<VersionBean>(jsonStr, VersionBean::class.java)
                            if (bean.status == "0" && bean.data != null) {
                                bean.data?.get(0)?.run {
                                    activity!!.runOnUiThread {
                                        var version = getVersionCode(context!!)
                                        if (versionNumber > version) {
                                            downLoadUtil.start(updateUrl ?: "", description)
                                        } else {
                                            showToast("暂无更新")
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

    fun getVersionCode(context: Context): Int {        // 包管理者
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
}