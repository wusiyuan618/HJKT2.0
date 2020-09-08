package com.wusy.serialportproject.app

import android.util.Log
import com.wusy.wusylibrary.base.BaseActivity
import java.util.*
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.os.Build
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.*
import com.orhanobut.logger.Logger
import com.wusy.wusylibrary.R
import com.wusy.wusylibrary.util.StatusBarUtil


abstract class BaseTouchActivity : BaseActivity(){
    private var downX: Float=0.0f
    private var downY: Float=0.0f
    private var upX:Float=0.0f
    private var upY:Float=0.0f
    private var distance=250
    override fun beforeSetContentView() {
        super.beforeSetContentView()
        isChangeStatusBar=false
//        StatusBarUtil.transparencyBar(this)
//        window.decorView.systemUiVisibility =
//            SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        try {
//            val layout = findViewById<ViewGroup>(R.id.layout_total)
//            layout.clipToPadding = true
//            layout.fitsSystemWindows = true
//            layout.setBackgroundColor(resources.getColor(R.color.titleViewBackgroundColor))
//        } catch (e: Exception) {
//            Logger.e("该Activity没有为首层Layout添加id--layout_total，无法管理状态栏。")
//        }
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
//设置全屏
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideBottomUIMenu()

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN->{
                downX=event.rawX
                downY=event.rawY
            }
            MotionEvent.ACTION_MOVE->{

            }
            MotionEvent.ACTION_UP->{
                upX=event.rawX
                upY=event.rawY
                if(upX>downX&&Math.abs(upX-downX)>distance&&Math.abs(upX-downX)>Math.abs(upY-downY)){//右滑
                    Log.i("wsy","用户右滑了")
                    onFingerRightTouch()
                }
                if(upX<downX&&Math.abs(upX-downX)>distance&&Math.abs(upX-downX)>Math.abs(upY-downY)){//左滑
                    Log.i("wsy","用户左滑了")
                    onFingerLeftTouch()
                }
                if(upY<downY&&Math.abs(upY-downY)>distance&&Math.abs(upY-downY)>Math.abs(upX-downX)){//上滑
                    Log.i("wsy","用户上滑了")
                    onFingerTopTouch()
                }
                if(upY>downY&&Math.abs(upY-downY)>distance&&Math.abs(upY-downY)>Math.abs(upX-downX)){//下滑
                    Log.i("wsy","用户下滑了")
                    onFingerBottomTouch()
                }
            }

        }
        return super.onTouchEvent(event)
    }
    open fun onFingerLeftTouch(){

    }
    open fun onFingerRightTouch(){

    }
    open fun onFingerTopTouch(){

    }
    open fun onFingerBottomTouch(){

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Constants.lastUpdateTime= Date(System.currentTimeMillis())
        return super.dispatchTouchEvent(ev)
    }
    protected fun hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
            decorView.systemUiVisibility = uiOptions
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }
}
