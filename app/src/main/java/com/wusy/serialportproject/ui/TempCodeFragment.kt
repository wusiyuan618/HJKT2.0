package com.wusy.serialportproject.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.pop.DateSDSelectPop
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import com.wusy.wusylibrary.view.FullyLinearLayoutManager
import com.google.gson.JsonParser
import com.wusy.serialportproject.R


class TempCodeFragment : BaseFragment() {
    lateinit var btnAdd: TextView
    lateinit var recyclerView: RecyclerView
    private lateinit var dateSDSelectPop: DateSDSelectPop
    lateinit var adapter: TempCodeAdapter
    override fun init() {
        recyclerView.layoutManager = FullyLinearLayoutManager(context)
        adapter = TempCodeAdapter(context!!)
        adapter.setEmptyText("暂无列表内容")
        val str =
            SharedPreferencesUtil.getInstance(context).getData(Constants.TEMP_CODE, "").toString()
        if (str != "") {
            val gson = Gson()//创建Gson对象
            val jsonParser = JsonParser()
            val jsonElements = jsonParser.parse(str).asJsonArray//获取JsonArray对象
            val beans = ArrayList<TempCodeBean>()
            for (bean in jsonElements) {
                beans.add(gson.fromJson(bean, TempCodeBean::class.java))
            }
            adapter.list=beans
        }
        recyclerView.adapter = adapter

        dateSDSelectPop = DateSDSelectPop(context!!)
        dateSDSelectPop.listener = object : DateSDSelectPop.ClickOkListener {
            override fun clickOk(startTime: String, endTime: String, temp: String) {
                adapter.list.add(TempCodeBean().apply {
                    this.startTime = startTime
                    this.endTime = endTime
                    this.temp = temp
                })
                adapter.notifyDataSetChanged()
                SharedPreferencesUtil.getInstance(context)
                    .saveData(Constants.TEMP_CODE, Gson().toJson(adapter.list).toString())
            }
        }
        btnAdd.setOnClickListener {
            dateSDSelectPop.showPopupWindow()
        }

    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_temp_code
    }

    override fun findView(view: View?) {
        btnAdd = view!!.findViewById(R.id.btnAdd)
        recyclerView = view.findViewById(R.id.recyclerView)

    }

    class TempCodeAdapter(context: Context) : BaseRecyclerAdapter<TempCodeBean>(context) {
        override fun onMyCreateViewHolder(
            parent: ViewGroup?,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return TempCodeViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.item_temp_code, parent, false)
            )
        }

        override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder is TempCodeViewHolder) {
                val thisHolder = holder as TempCodeViewHolder
                list[position].run {
                    thisHolder.tvStartTime.text = "开始时间：${startTime}"
                    thisHolder.tvTemp.text = "温度：${temp}"
                    thisHolder.tvDelete.setOnClickListener {
                        list.remove(list[position])
                        notifyDataSetChanged()
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.TEMP_CODE, Gson().toJson(list).toString())
                    }
                }
            }

        }

    }

    class TempCodeBean {
        var startTime = ""
        var endTime = ""
        var temp = ""
    }

    class TempCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStartTime = itemView.findViewById<TextView>(R.id.tvStartTime)
        val tvEndTime = itemView.findViewById<TextView>(R.id.tvEndTime)
        val tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)
        val tvDelete = itemView.findViewById<TextView>(R.id.tvDelete)

    }
}