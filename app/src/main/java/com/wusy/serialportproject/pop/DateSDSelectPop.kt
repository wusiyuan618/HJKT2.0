package com.wusy.serialportproject.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.wusy.serialportproject.R
import razerdp.basepopup.BasePopupWindow

class DateSDSelectPop(context: Context) : BasePopupWindow(context) {
    var rlStartTime = contentView.findViewById<RelativeLayout>(R.id.rlStartTime)
    var rlEndTime = contentView.findViewById<RelativeLayout>(R.id.rlEndTime)
    var tvStartTime = contentView.findViewById<TextView>(R.id.tvStartTime)
    var tvEndTime = contentView.findViewById<TextView>(R.id.tvEndTime)
    var rlTemp = contentView.findViewById<RelativeLayout>(R.id.rlTemp)
    var tvTemp = contentView.findViewById<TextView>(R.id.tvTemp)
    var btnOk = contentView.findViewById<TextView>(R.id.btn_ok)
    var listener:ClickOkListener?=null
    var startPop: DateSelectPop = DateSelectPop(context).apply {
        init(View.OnClickListener {
            tvStartTime.text = getDate()
            dismiss()
        })
    }
    var endPop: DateSelectPop = DateSelectPop(context).apply {
        init(View.OnClickListener {
            tvEndTime.text = getDate()
            dismiss()
        })
    }
    var tempPop: TempSelectPop = TempSelectPop(context).apply {
        init(View.OnClickListener {
            tvTemp.text = getDate()
            dismiss()
        })
    }
    init {
        setBlurBackgroundEnable(true)
        popupGravity = Gravity.CENTER

        rlStartTime.setOnClickListener {
            startPop.showPopupWindow()
        }
        rlEndTime.setOnClickListener {
            endPop.showPopupWindow()
        }
        rlTemp.setOnClickListener {
            tempPop.showPopupWindow()
        }
        btnOk.setOnClickListener {
            if(tvStartTime.text.isEmpty()){
                Toast.makeText(context,"请选择开始时间",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            if(tvEndTime.text.isEmpty()){
//                Toast.makeText(context,"请选择结束时间",Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            if(tvTemp.text.isEmpty()){
                Toast.makeText(context,"请选择温度",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            listener?.run {
//                val startWeek=tvStartTime.text.toString().split(" ")[0]
//                val startHour=tvStartTime.text.toString().split(" ")[1].split(":")[0].toInt()
//                val startMin=tvStartTime.text.toString().split(" ")[1].split(":")[1].toInt()
//                val endWeek=tvEndTime.text.toString().split(" ")[0]
//                val endHour=tvEndTime.text.toString().split(" ")[1].split(":")[0].toInt()
//                val endMin=tvEndTime.text.toString().split(" ")[1].split(":")[1].toInt()
//                if(startWeek!=endWeek){
//                    Toast.makeText(context,"暂不支持跨天，请选择同一天",Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
//               if(startHour==endHour){
//                    if(startMin>endMin) {
//                        Toast.makeText(context, "结束时间不能晚于开始时间", Toast.LENGTH_SHORT).show()
//                        return@setOnClickListener
//                    }
//                }else if(startHour>=endHour){
//                    Toast.makeText(context,"结束时间不能晚于开始时间",Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
                clickOk(tvStartTime.text.toString(),tvEndTime.text.toString(),tvTemp.text.toString())
                dismiss()
            }
        }
    }


    override fun onCreateShowAnimation(): Animation {
        return getDefaultAlphaAnimation(true)
    }

    override fun onCreateDismissAnimation(): Animation {
        return getDefaultAlphaAnimation(false)
    }

    override fun onCreateContentView(): View {
        return createPopupById(R.layout.dialog_select_date)
    }
    open interface ClickOkListener{
        fun clickOk(startTime:String,endTime:String,temp:String)
    }
}