package com.wusy.serialportproject.ui

import android.util.Log
import android.view.View
import android.widget.TextView
import com.haibin.calendarview.Calendar
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.pop.CaledarPop
import com.wusy.serialportproject.util.DataUtils
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepairFragment :BaseFragment() {
    lateinit var tvDLSPhone:TextView
    lateinit var tvWBTime:TextView
    lateinit var tvWBNextTime:TextView
    lateinit var caledarPop:CaledarPop
    override fun init() {
        tvDLSPhone.text="代理商联系电话:"+ DataUtils.formatPhoneNumber(
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_DLS_PHONE, Constants.DEFAULT_DLS_PHONENUMBER).toString()," - ")
        caledarPop = CaledarPop(context!!, object : CaledarPop.OnUpdateListener {
            override fun updateView(calendar: Calendar?) {
                val dft = SimpleDateFormat("yyyy-MM-dd")
                val curDate = dft.format(calendar?.timeInMillis)
                Log.i("wsy",curDate)
                tvWBTime.text="本次维保时间：${curDate}"
                val nestWBTime=getOldDate(Date(calendar?.timeInMillis?:System.currentTimeMillis()),6)
                tvWBNextTime.text="下次维保时间：${nestWBTime}"
                SharedPreferencesUtil.getInstance(context).saveData(Constants.WBTIME,curDate)
                SharedPreferencesUtil.getInstance(context).saveData(Constants.NESTWBTIME,nestWBTime)

            }
        })
        tvWBTime.setOnClickListener {
            caledarPop.showPopupWindow()
        }
        tvWBTime.text="本次维保时间：${SharedPreferencesUtil.getInstance(context).getData(Constants.WBTIME,"暂无记录")}"
        tvWBNextTime.text="下次维保时间：${SharedPreferencesUtil.getInstance(context).getData(Constants.NESTWBTIME,"暂无记录")}"
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_repair
    }

    override fun findView(view: View?) {
        tvDLSPhone=view!!.findViewById(R.id.tvDLSPhone)
        tvWBTime=view.findViewById(R.id.tvWBTime)
        tvWBNextTime=view.findViewById(R.id.tvWBNextTime)
    }
    fun getOldDate(beginDate:Date,distanceDay: Int): String {
        val dft = SimpleDateFormat("yyyy-MM-dd")
        val date = java.util.Calendar.getInstance()
        date.time = beginDate
        date.set(java.util.Calendar.MONTH, date.get(java.util.Calendar.MONTH) + distanceDay)
        var endDate: Date? = null
        try {
            endDate = dft.parse(dft.format(date.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dft.format(endDate)
    }
}