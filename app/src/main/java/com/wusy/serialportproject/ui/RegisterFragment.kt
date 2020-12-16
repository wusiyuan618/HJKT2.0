package com.wusy.serialportproject.ui

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.OkHttpUtil
import com.wusy.serialportproject.app.URLForOkHttp
import com.wusy.serialportproject.bean.RegisteBean
import com.wusy.serialportproject.pop.EditPop
import com.wusy.serialportproject.util.InterAddressUtil
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RegisterFragment : BaseFragment() {
    lateinit var tvTitle: TextView
    lateinit var tvAdd: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RegisterAdapter
    private var macAddress = ""
    override fun init() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = RegisterAdapter(context!!)
        macAddress = InterAddressUtil.getMacAddress()
        recyclerView.adapter = adapter
        requestList()
        tvAdd.setOnClickListener {
            if(tvTitle.text=="当前登录码："){
                showToast("未能查询到登录码，请绑定设备")
                return@setOnClickListener
            }
            val editPop = EditPop(context!!, "", "")
            editPop.listen = object : EditPop.OnClickOkListener {
                override fun clickOk(userName: String, passWord: String) {
                    requestAdd("", userName, passWord, editPop, 0)
                }
            }
            editPop.showPopupWindow()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_register
    }

    private fun requestList() {
        showLoadImage()
        OkHttpUtil.getInstance()
            .asynGet(URLForOkHttp.getAccountList(macAddress), object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    activity!!.runOnUiThread {
                        showToast("失败")
                        hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    val json = response!!.body()!!.string()
                    val bean = Gson().fromJson(
                        json,
                        RegisteBean::class.java
                    )
                    activity!!.runOnUiThread {
                        Log.i("wsy",json)
                        tvTitle.text = "当前登录码：${bean.data?.loginCode ?: ""}"
                        adapter.list = bean.data?.userList ?: ArrayList()
                        adapter.notifyDataSetChanged()
                        hideLoadImage()
                    }


                }

            })
    }

    private fun requestDelete(id: String, position: Int) {
        showLoadImage()
        OkHttpUtil.getInstance()
            .asynGet(URLForOkHttp.requestAccountDelete(id), object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    activity!!.runOnUiThread {
                        showToast("失败")
                        hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    val json = response!!.body()!!.string()
                    val status = JSONObject(json).getString("status")
                    val msg = JSONObject(json).getString("msg")

                    activity!!.runOnUiThread {
                        if (status == "0") {
                            adapter.list.removeAt(position)
                            adapter.notifyDataSetChanged()
                        } else {
                            showToast("删除用户信息发生错误:$msg")
                        }
                        hideLoadImage()
                    }


                }
            })
    }

    private fun requestAdd(
        id: String,
        userName: String,
        pwd: String,
        editPop: EditPop,
        position: Int
    ) {
        val json = JSONObject()
        json.put("id", id)
        json.put("userName", userName)
        json.put("pwd", pwd)
        json.put("mac",macAddress)
        json.put("loginCode",tvTitle.text.toString().replace("当前登录码：",""))
        showLoadImage()
        OkHttpUtil.getInstance().anysPost(
            URLForOkHttp.requestAccountEdit(),
            "",
            json.toString(),
            object : OkHttpUtil.ResultCallBack {
                override fun failListener(call: Call?, e: IOException?, message: String?) {
                    activity!!.runOnUiThread {
                        showToast("网络异常，请检查网络")
                        hideLoadImage()
                    }
                }

                override fun successListener(call: Call?, response: Response?) {
                    val json = response!!.body()!!.string()
                    val status = JSONObject(json).getString("status")
                    val msg = JSONObject(json).getString("msg")

                    Log.i("wsy",json)
                    activity!!.runOnUiThread {
                        if (status == "0") {
                            if (id == "") {//添加
                                val newId = JSONObject(json).getJSONObject("data").getString("id")
                                adapter.list.add(RegisteBean.DataBean.UserListBean().apply {
                                    this.userName = userName
                                    this.pwd = pwd
                                    this.id=newId
                                })
                                adapter.notifyDataSetChanged()
                            } else {//编辑
                                val bean = adapter.list[position]
                                bean.userName = userName
                                bean.pwd = pwd
                                adapter.notifyDataSetChanged()
                            }
                            editPop.dismiss()
                            hideLoadImage()
                        } else {
                            showToast("编辑用户信息发生错误:$msg")
                            hideLoadImage()
                        }
                    }

                }

            })
    }

    override fun findView(view: View?) {
        tvTitle = view!!.findViewById(R.id.tvTitle)
        tvAdd = view.findViewById(R.id.tvAdd)
        recyclerView = view.findViewById(R.id.recyclerView)

    }

    inner class RegisterAdapter(context: Context) :
        BaseRecyclerAdapter<RegisteBean.DataBean.UserListBean>(context) {
        override fun onMyCreateViewHolder(
            parent: ViewGroup?,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return RegisterViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.item_setting_register, parent, false)
            )
        }


        override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder is RegisterViewHolder) {
                val thisHolder = holder as RegisterViewHolder
                list[position]?.run {
                    thisHolder.tvUserName.text = userName
                    thisHolder.tvPassWordNumber.text = pwd
                    thisHolder.ivDelete.setOnClickListener {
                        requestDelete(id ?: "", position)
                    }
                    thisHolder.ivEdit.setOnClickListener {
                        val editPop = EditPop(context, userName ?: "", pwd ?: "")
                        editPop.listen = object : EditPop.OnClickOkListener {
                            override fun clickOk(userName: String, passWord: String) {
                                requestAdd(id ?: "", userName, passWord, editPop, position)
                            }
                        }
                        editPop.showPopupWindow()
                    }
                    thisHolder.ivEye.setOnClickListener {
                        isOpen = !isOpen
                        if (isOpen) {
                            thisHolder.ivEye.setImageResource(R.mipmap.btn_password_close)
                            thisHolder.tvPassWordNumber.inputType = InputType.TYPE_CLASS_TEXT
                        } else {
                            thisHolder.ivEye.setImageResource(R.mipmap.btn_password_search)
                            thisHolder.tvPassWordNumber.inputType =
                                InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                        }
                    }

                }
            }
        }

    }

    inner class RegisterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvPassWordNumber: TextView = itemView.findViewById(R.id.tvPassWordNumber)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivEye: ImageView = itemView.findViewById(R.id.ivEye)

    }
}
