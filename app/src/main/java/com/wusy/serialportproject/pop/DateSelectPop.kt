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

class DateSelectPop(context: Context) : BasePopupWindow(context) {
    var wheelYear = findViewById<WheelPicker>(R.id.wheelYear)
    var wheelMonth = findViewById<WheelPicker>(R.id.wheelMonth)
    var wheelDay = findViewById<WheelPicker>(R.id.wheelDay)

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
        return createPopupById(R.layout.dialog_selectdate)
    }

    fun init(listen: View.OnClickListener) {
        wheelYear.data = createWeekList()
        wheelMonth.data = createHourList()
        wheelDay.data = createMinList()

        btnOk.setOnClickListener(listen)
        btnCancel.setOnClickListener {
            dismiss()
        }
        initWheel(wheelYear)
        initWheel(wheelMonth)
        initWheel(wheelDay)
        val calendar = Calendar.getInstance()
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

    private fun createWeekList(): ArrayList<String> {
        var list = ArrayList<String>()
        list.add("周一")
        list.add("周二")
        list.add("周三")
        list.add("周四")
        list.add("周五")
        list.add("周六")
        list.add("周日")
        return list
    }

    private fun createHourList(): ArrayList<String> {
        var list = ArrayList<String>()
        for (i in 1..24) {
            if(i<10){
                list.add("0${i}h")
            }else{
                list.add("${i}h")
            }
        }
        return list
    }

    private fun createMinList(): ArrayList<String> {
        var list = ArrayList<String>()
        for (i in 0..59) {
            if(i<10){
                list.add("0${i}min")
            }else{
                list.add("${i}min")
            }
        }

        return list
    }

    fun getDate(): String {
        var hour=wheelMonth.data[wheelMonth.currentItemPosition].toString().replace("h","")
        var min=if(!isHideDayWheel) ":" + wheelDay.data[wheelDay.currentItemPosition].toString().replace("min","") else ""
        return wheelYear.data[wheelYear.currentItemPosition].toString() + " " + hour +min

    }

    fun hideDaySelect() {
        isHideDayWheel=true
        wheelDay.visibility=View.GONE
    }
}

