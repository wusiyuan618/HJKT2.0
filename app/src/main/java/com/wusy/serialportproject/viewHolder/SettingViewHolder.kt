package com.wusy.serialportproject.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.wusy.serialportproject.R

class SettingViewHolder(itemView: View)  :RecyclerView.ViewHolder(itemView){
    val tv=itemView.findViewById<TextView>(R.id.tv)
    fun setVisibility(visibility: Int) {
        itemView.visibility = visibility
        var params = itemView.layoutParams as (RecyclerView.LayoutParams)
        if (visibility == View.VISIBLE) {
            params.width = 280
            params.height =150
        } else {
            params.width = 0
            params.height = 0
        }
        itemView.layoutParams = params
    }
}