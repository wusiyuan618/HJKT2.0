package com.wusy.serialportproject.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.wusy.serialportproject.R
import com.wusy.serialportproject.adapter.SettingAdapter
import com.wusy.serialportproject.app.BaseTouchActivity
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.bean.SettingBean
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.layout_total

class SettingActivity : BaseTouchActivity() {
    lateinit var adapter: SettingAdapter
    override fun findView() {

    }

    override fun init() {
        ivBack.setOnClickListener {
            finish()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SettingAdapter(this)
        adapter.fm = supportFragmentManager
        recyclerView.adapter = adapter
        val imgId =
            SharedPreferencesUtil.getInstance(this).getData(Constants.IMGID, R.mipmap.bg2) as Int
        layout_total.setBackgroundResource(imgId)
        initBroadCast()
        Thread(Runnable {
            adapter.list = createList()
            adapter.notifyDataSetChanged()
        }).start()

    }

    override fun getContentViewId(): Int {
        return R.layout.activity_setting
    }

    private fun createList(): ArrayList<SettingBean> {
        val list = ArrayList<SettingBean>()
        val ft = supportFragmentManager.beginTransaction()
        list.add(SettingBean().apply {
            this.title = "屏保时间"
            this.isSelect = true
            this.fragment = ScreenSettingFragment()
            ft.add(
                R.id.fragmentBox,
                this.fragment as ScreenSettingFragment,
                "ScreenSettingFragment"
            )
        })
        list.add(SettingBean().apply {
            this.title = "环境设置"
            this.isSelect = false
            this.fragment = FreshSettingFragment()
            ft.add(R.id.fragmentBox, this.fragment as FreshSettingFragment, "FreshSettingFragment")
        })
        list.add(SettingBean().apply {
            this.title = "温度编程"
            this.isSelect = false
            this.fragment = TempCodeFragment()
            ft.add(R.id.fragmentBox, this.fragment as TempCodeFragment, "TempCodeFragment")
        })
        list.add(SettingBean().apply {
            this.title = "背景设置"
            this.isSelect = false
            this.fragment = BackGroundFragment()
            ft.add(R.id.fragmentBox, this.fragment as BackGroundFragment, "BackGroundFragment")
        })
        list.add(SettingBean().apply {
            this.title = "维保"
            this.isSelect = false
            this.fragment = RepairFragment()
            ft.add(R.id.fragmentBox, this.fragment as RepairFragment, "RepairFragment")
        })
        list.add(SettingBean().apply {
            this.title = "系统设置"
            this.isSelect = false
            this.isShow = false
            this.fragment = SystemSettingFragment()
            ft.add(
                R.id.fragmentBox,
                this.fragment as SystemSettingFragment,
                "SystemSettingFragment"
            )
        })
        ft.commit()
        return list
    }

    private fun initBroadCast() {
        val boradCast = SettingBoradCast()
        val actionList = ArrayList<String>()
        actionList.add(CommonConfig.ACTION_BGCKGROUND_IMG_CHANGE)
        addBroadcastAction(actionList, boradCast)
    }

    internal inner class SettingBoradCast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CommonConfig.ACTION_BGCKGROUND_IMG_CHANGE) {//发送寄电器查询命令
                //设置背景图片
                val imgId = SharedPreferencesUtil.getInstance(this@SettingActivity).getData(
                    Constants.IMGID,
                    R.mipmap.bg2
                ) as Int
                layout_total.setBackgroundResource(imgId)
            }
        }
    }
}
