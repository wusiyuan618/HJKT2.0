package com.wusy.serialportproject.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.wusy.serialportproject.R
import razerdp.basepopup.BasePopupWindow

class EditPop(context: Context,userName:String,passWord:String) : BasePopupWindow(context) {
    private var edUserName = findViewById<EditText>(R.id.edUserName)
    private var edPassWord = findViewById<EditText>(R.id.edPassWord)
    private var tvOk = findViewById<TextView>(R.id.tvOk)
    private var tvCancel = findViewById<TextView>(R.id.tvCancel)
    var listen:OnClickOkListener?=null
    init {
        setBlurBackgroundEnable(true)
        popupGravity = Gravity.CENTER
        edUserName.setText(userName)
        edPassWord.setText(passWord)
        tvCancel.setOnClickListener {
            dismiss()
        }
        tvOk.setOnClickListener {
            val userName=edUserName.text.toString()
            val passWord=edPassWord.text.toString()
            if(userName.length>6){
                Toast.makeText(context,"用户名不能超过6位",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(userName.length>8){
                Toast.makeText(context,"密码不能超过8位",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(listen!=null) listen!!.clickOk(userName,passWord)
        }
    }


    override fun onCreateShowAnimation(): Animation {
        return getDefaultAlphaAnimation(true)
    }

    override fun onCreateDismissAnimation(): Animation {
        return getDefaultAlphaAnimation(false)
    }

    override fun onCreateContentView(): View {
        return createPopupById(R.layout.pop_edit_info)
    }
    interface OnClickOkListener{
        fun clickOk(userName:String,passWord:String)
    }

}


