package com.wusy.serialportproject.ui

import android.view.View
import android.widget.TextView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.util.DataUtils
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil

class RepairFragment :BaseFragment() {
    lateinit var tvDLSPhone:TextView
    override fun init() {
        tvDLSPhone.text="代理商联系电话:"+ DataUtils.formatPhoneNumber(
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_DLS_PHONE, Constants.DEFAULT_DLS_PHONENUMBER).toString()," - ")
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_repair
    }

    override fun findView(view: View?) {
        tvDLSPhone=view!!.findViewById(R.id.tvDLSPhone)
    }

}