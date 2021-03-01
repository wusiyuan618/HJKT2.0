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
import com.wusy.serialportproject.app.URLForOkHttp
import com.wusy.serialportproject.popup.MakeSurePopup
import com.wusy.serialportproject.popup.NumberEditPopup
import com.wusy.serialportproject.util.DataUtils
import com.wusy.serialportproject.util.DownAPK.DownLoadApkUtil
import com.wusy.serialportproject.util.DownAPK.VersionBean
import com.wusy.wusylibrary.base.BaseActivity
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.OkHttpUtil
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import okhttp3.Call
import okhttp3.Response

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
    lateinit var commonFunction: CommonFunction

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
        commonFunction=CommonFunction()

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
            commonFunction.requestVersion(activity as BaseActivity)
        }
        rlExit.setOnClickListener {
            val intent = Intent()
            intent.component =
                ComponentName("com.android.launcher3", "com.android.launcher3.Launcher")
            startActivity(intent)
        }
        rlReboot.setOnClickListener {
            context?.sendBroadcast(Intent("HJL_ACTION_REBOOT"))
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
        reSetMakeSurePopup.tvTitle.text = "清理日志数据"
        reSetMakeSurePopup.tvContent.text = "您是否需要清理日志数据呢？"
        reSetMakeSurePopup.ivSure.setOnClickListener {
            showLoadImage()
            Thread(Runnable {
                activity!!.runOnUiThread {
                    hideLoadImage()
                    commonFunction.cleanLog()
                    showToast("成功清理日志数据")
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
            commonFunction.updateLog(activity as BaseActivity,this)
        }
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
}