package com.wusy.serialportproject.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.ui.screen.ScreenActivity
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.base.BaseActivity
import kotlinx.android.synthetic.main.activity_system_test.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SystemTextActivity:BaseActivity() {
    var imageViewList = ArrayList<ImageView>()
    var isReceiveLog=true
    override fun findView() {
    }

    override fun init() {
        imageViewList.add(ivOut1)
        imageViewList.add(ivOut2)
        imageViewList.add(ivOut3)
        imageViewList.add(ivOut4)
        imageViewList.add(ivOut5)
        imageViewList.add(ivOut6)
        imageViewList.add(ivOut7)
        imageViewList.add(ivOut8)
        imageViewList.add(ivOut9)
        imageViewList.add(ivOut10)
        imageViewList.add(ivOut11)
        imageViewList.add(ivOut12)
        imageViewList.add(ivOut13)
        imageViewList.add(ivOut14)
        imageViewList.add(ivOut15)
        imageViewList.add(ivOut16)

        ivEnvBtn.setOnClickListener {
            sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIRACTIVITY_SEND_ENVSEARCH
            })
        }

        ivJDQBtn.setOnClickListener {
            sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIRACTIVITY_SEND_JDQSEARCH
            })
        }
        ivScreenBtn.setOnClickListener {
            startActivity(Intent().apply {
                setClass(this@SystemTextActivity, ScreenActivity::class.java)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        ivIsReceiveBtn.setOnClickListener {
            if(isReceiveLog) ivIsReceiveBtn.setImageResource(R.mipmap.btn_journal_normal)
            else ivIsReceiveBtn.setImageResource(R.mipmap.btn_journal_selected)
            isReceiveLog=!isReceiveLog
//            var intent=Intent()
//            intent.action=CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI
//            intent.putExtra("msg","02")
//            sendBroadcast(intent)
//            var intent2=Intent()
//            intent2.action=CommonConfig.SERIALPORTPROJECT_ACTION_SP_UI
//            intent2.putExtra("msg","032E000A0002000000000000000000000000000100320196000100F90034000A000A0000001E000200010005000F000151B0")
//            sendBroadcast(intent2)
        }

        ivBack.setOnClickListener {
            finish()
        }

        tvLog.movementMethod = ScrollingMovementMethod.getInstance()

        addBroadcastAction(ArrayList<String>().apply {
            this.add(CommonConfig.ACTION_SYSTEMTEST_LOG)
            this.add(CommonConfig.ACTION_SYSTEMTEST_JDQ)
        },SystemTestBroad())
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_system_test
    }

    /**
     * 向TextView中添加内容
     * 当TextView的内容超标时，一直显示最后一行
     */
    fun refreshLogView(msg: String) {
        runOnUiThread {
            tvLog.append(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date())+" : "+msg+"\n")
            var offset = tvLog.lineCount * tvLog.lineHeight
            if (offset > tvLog.height) {
                tvLog.scrollTo(0, offset - tvLog.height + tvLog.lineHeight * 2)
            }
        }
    }
    internal inner class SystemTestBroad : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CommonConfig.ACTION_SYSTEMTEST_LOG) {
                if(isReceiveLog) refreshLogView(intent.getStringExtra("log").toString())
            }
            if (intent.action == CommonConfig.ACTION_SYSTEMTEST_JDQ) {
                var jdqList=intent.getIntegerArrayListExtra("jdq")
                for (i in jdqList.indices){
                    if(jdqList[i]==1){
                        imageViewList[i].setImageResource(R.mipmap.icon_light_open)
                    }else{
                        imageViewList[i].setImageResource(R.mipmap.icon_light_close)
                    }
                }
            }
        }
    }
}
