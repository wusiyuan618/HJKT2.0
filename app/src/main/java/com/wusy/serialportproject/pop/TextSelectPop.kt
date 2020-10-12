package com.wusy.serialportproject.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.wusy.serialportproject.R
import razerdp.basepopup.BasePopupWindow
import java.util.*

class TextSelectPop (context: Context) : BasePopupWindow(context) {
    var wheelTemp = findViewById<WheelPicker>(R.id.wheelTemp)
    var btnOk = findViewById<TextView>(R.id.tvOk)
    var btnCancel = findViewById<TextView>(R.id.tvCancel)
    var isHideDayWheel=false
    init {
        setBlurBackgroundEnable(true)
        popupGravity = Gravity.BOTTOM
    }

    override fun onCreateShowAnimation(): Animation {
        return getTranslateVerticalAnimation(1f, 0f, 250)
    }

    override fun onCreateDismissAnimation(): Animation {
        return getTranslateVerticalAnimation(0f, 1f, 250)
    }

    override fun onCreateContentView(): View {
        return createPopupById(R.layout.pop_temp)
    }

    fun init(listen: View.OnClickListener,type:String) {
        if(type=="temp") wheelTemp.data = createTemp()
        else wheelTemp.data = createMode()
        btnOk.setOnClickListener(listen)
        btnCancel.setOnClickListener {
            dismiss()
        }
        initWheel(wheelTemp)
    }

    private fun initWheel(wheel: WheelPicker) {
        wheel.setIndicator(true)
        wheel.itemTextColor = context.resources.getColor(R.color.wheelNormalItem)
        wheel.selectedItemTextColor = context.resources.getColor(R.color.colorText)
        wheel.visibleItemCount = 5
        wheel.setCurtain(true)
        wheel.setAtmospheric(true)
        wheel.isCurved = true
    }

    private fun createTemp(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 5..30){
            list.add("${i}℃")
        }
        return list
    }
    private fun createMode(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("制冷")
        list.add("制热")
        list.add("通风")
        return list
    }

    fun getDate(): String {
        return wheelTemp.data[wheelTemp.currentItemPosition].toString()

    }

}

