package com.wusy.serialportproject.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.popup.MakeSurePopup
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import java.text.SimpleDateFormat
import java.util.*

class ValueAddedFragment:BaseFragment(){
    lateinit var ivFilterUpdate:ImageView
    lateinit var ivLightUpdate:ImageView
    lateinit var tvLightDate:TextView
    lateinit var tvFilterDate:TextView
    var simpleDate=SimpleDateFormat("yyyy-MM-dd")

    override fun init() {
        tvFilterDate.text="上次更换时间："+SharedPreferencesUtil.getInstance(context).getData(Constants.VALUEADDED_UPDATE_DATE_FILTER,"-").toString()
        tvLightDate.text="上次更换时间："+SharedPreferencesUtil.getInstance(context).getData(Constants.VALUEADDED_UPDATE_DATE_LIGHT,"-").toString()

        val makeSurePopupFilter=MakeSurePopup(context)
        makeSurePopupFilter.tvTitle.text="更新过滤网更换时间"
        makeSurePopupFilter.tvContent.text="是否确定更新过滤网更换时间？"
        makeSurePopupFilter.ivSure.setOnClickListener {
            val ca=Calendar.getInstance()
            tvFilterDate.text="上次更换时间："+simpleDate.format(ca.time)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.VALUEADDED_UPDATE_DATE_FILTER,tvFilterDate.text.toString().replace("上次更换时间：",""))
            makeSurePopupFilter.dismiss()
        }
        ivFilterUpdate.setOnClickListener {
            makeSurePopupFilter.showPopupWindow()
        }
        val makeSurePopupLight=MakeSurePopup(context)
        makeSurePopupLight.tvTitle.text="更新灯管更换时间"
        makeSurePopupLight.tvContent.text="是否确定更新灯管更换时间？"
        makeSurePopupLight.ivSure.setOnClickListener {
            val ca=Calendar.getInstance()
            tvLightDate.text="上次更换时间："+simpleDate.format(ca.time)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.VALUEADDED_UPDATE_DATE_LIGHT,tvLightDate.text.toString().replace("上次更换时间：",""))
            makeSurePopupLight.dismiss()
        }
        ivLightUpdate.setOnClickListener {
            makeSurePopupLight.showPopupWindow()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_valueadded
    }

    override fun findView(view: View?) {
        ivFilterUpdate=view!!.findViewById(R.id.ivFilterUpdate)
        ivLightUpdate=view.findViewById(R.id.ivLightUpdate)
        tvLightDate=view.findViewById(R.id.tvLightDate)
        tvFilterDate=view.findViewById(R.id.tvFilterDate)
    }

}