package com.wusy.serialportproject.ui.screen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wusy.serialportproject.R
import com.wusy.wusylibrary.base.BaseRecyclerAdapter

class ScreenAdapter(context: Context) : BaseRecyclerAdapter<ScreenAdapter.ScreenBean>(context){
    override fun onMyCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ScreenViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_screen_adapter, parent, false)
        )
    }

    override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(holder is ScreenViewHolder){
            val thisHolder=holder as ScreenViewHolder
            list[position].run {
                thisHolder.tvCount.text=count
                thisHolder.tvName.text=name
                thisHolder.tvUnit.text=unit
                thisHolder.tvStatus.text=status
                val background =  thisHolder.tvStatus.background as GradientDrawable
                background.setColor(Color.parseColor(color))
            }
        }
    }
    class ScreenViewHolder(itemView: View)  :RecyclerView.ViewHolder(itemView){
        val tvName=itemView.findViewById<TextView>(R.id.tvName)
        val tvCount=itemView.findViewById<TextView>(R.id.tvCount)
        val tvUnit=itemView.findViewById<TextView>(R.id.tvUnit)
        val tvStatus=itemView.findViewById<TextView>(R.id.tvStatus)

    }
    class ScreenBean{
        var name=""
        var count=""
        var unit=""
        var status=""
        var color="#059E04"
    }

}

