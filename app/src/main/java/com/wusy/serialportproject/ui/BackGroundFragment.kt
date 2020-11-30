package com.wusy.serialportproject.ui

import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.base.BaseRecyclerAdapter
import com.wusy.wusylibrary.util.SharedPreferencesUtil


class BackGroundFragment : BaseFragment() {
    lateinit var imgRecyclerView: RecyclerView
    lateinit var adapter: BackGroudAdapter
    override fun init() {
        adapter = BackGroudAdapter(context!!)
        adapter.list = initList()
        adapter.setOnRecyclerItemClickLitener(object:BaseRecyclerAdapter.onRecyclerItemClickLitener{
            override fun onRecyclerItemLongClick(view: RecyclerView.ViewHolder?, position: Int) {

            }

            override fun onRecyclerItemClick(view: RecyclerView.ViewHolder?, position: Int) {
                for (item in adapter.list){
                    item.isSelect=false
                }
                adapter.list[position].isSelect=true
                adapter.notifyDataSetChanged()
                SharedPreferencesUtil.getInstance(context).saveData(Constants.IMGID,adapter.list[position].imgId)
                val intent=Intent()
                intent.action = CommonConfig.ACTION_BGCKGROUND_IMG_CHANGE
                activity?.sendBroadcast(intent)
            }

        })
        imgRecyclerView.layoutManager = GridLayoutManager(activity,3)
        imgRecyclerView.adapter = adapter
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_backgroud
    }

    override fun findView(view: View?) {
        imgRecyclerView = view!!.findViewById(R.id.imgRecyclerView)

    }

    private fun initList(): ArrayList<BackGroundBean> {
        val imgList = ArrayList<BackGroundBean>()
        val curImageId=SharedPreferencesUtil.getInstance(context).getData(Constants.IMGID,R.mipmap.bg2)
        imgList.add(BackGroundBean().apply {
            imgId = R.mipmap.bg2
            showImageId = R.mipmap.bgshow2
            isSelect=(curImageId==imgId)
        })
        imgList.add(BackGroundBean().apply {
            imgId = R.mipmap.bg3
            showImageId = R.mipmap.bgshow3
            isSelect=(curImageId==imgId)
        })
        imgList.add(BackGroundBean().apply {
            imgId = R.mipmap.bg4
            showImageId = R.mipmap.bgshow4
            isSelect=(curImageId==imgId)
        })
        imgList.add(BackGroundBean().apply {
            imgId = R.mipmap.bg5
            showImageId = R.mipmap.bgshow5
            isSelect=(curImageId==imgId)
        })

        return imgList
    }

    class BackGroudAdapter(context: Context) : BaseRecyclerAdapter<BackGroundBean>(context) {
        override fun onMyCreateViewHolder(
            parent: ViewGroup?,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return BackGroundViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.item_background, parent, false)
            )
        }

        override fun onMyBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder is BackGroundViewHolder) {
                val thisHolder = holder as BackGroundViewHolder
                list[position]?.run {
                    thisHolder.img.setImageResource(showImageId ?: R.mipmap.bg)
                    if(isSelect){
                        thisHolder.itemView.setBackgroundResource(R.drawable.border_select)
                    }else{
                        thisHolder.itemView.setBackgroundResource(0)
                    }
                }
            }
        }
    }

    class BackGroundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.iv)
    }

    class BackGroundBean {
        var imgId: Int? = null
        var showImageId:Int?=null
        var isSelect = false
    }
}