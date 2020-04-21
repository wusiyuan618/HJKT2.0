package com.wusy.serialportproject.ui

import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.wusy.serialportproject.R
import com.wusy.serialportproject.app.Constants
import com.wusy.serialportproject.pop.SingleSelectPop
import com.wusy.serialportproject.util.CommonConfig
import com.wusy.wusylibrary.base.BaseFragment
import com.wusy.wusylibrary.util.SharedPreferencesUtil
import org.w3c.dom.Text

class FreshSettingFragment : BaseFragment() {
    lateinit var ivJNWD: ImageView
    lateinit var rlJNWD: RelativeLayout
    lateinit var llJNWDContent: LinearLayout
    lateinit var rlZLJNWD: RelativeLayout
    lateinit var tvZLJNWD: TextView
    lateinit var rlZRJNWD: RelativeLayout
    lateinit var tvZRJNWD: TextView
    var isJNWDOpen = false
    lateinit var temSingleSelectPopForJNZL: SingleSelectPop
    lateinit var temSingleSelectPopForJNZR: SingleSelectPop

    lateinit var ivLJWD: ImageView
    lateinit var rlLJWD: RelativeLayout
    lateinit var llLJWDContent: LinearLayout
    lateinit var rlZLLJWD: RelativeLayout
    lateinit var tvZLLJWD: TextView
    lateinit var rlZRLJWD: RelativeLayout
    lateinit var tvZRLJWD: TextView
    var isLJWDOpen = false
    lateinit var temSingleSelectPopForLJZL: SingleSelectPop
    lateinit var temSingleSelectPopForLJZR: SingleSelectPop

    lateinit var ivAdd: ImageView
    lateinit var temSingleSelectPopForTFSJ: SingleSelectPop

    lateinit var llSDNoAuto: LinearLayout
    lateinit var ivSDNoAuto: ImageView
    lateinit var tvSDNoAuto: TextView
    lateinit var llSDAuto: LinearLayout
    lateinit var ivSDAuto: ImageView
    lateinit var tvSDAuto: TextView
    lateinit var llSDItemAuto: LinearLayout
    lateinit var rlJS: RelativeLayout
    lateinit var tvJS: TextView
    lateinit var rlCS: RelativeLayout
    lateinit var tvCS: TextView
    lateinit var rlSDItemNoAuto: RelativeLayout
    lateinit var llSDNoAutoOff: LinearLayout
    lateinit var ivSDNoAutoOff: ImageView
    lateinit var tvSDNoAutoOff: TextView
    lateinit var llSDNoAutoCS: LinearLayout
    lateinit var ivSDNoAutoCS: ImageView
    lateinit var tvSDNoAutoCS: TextView
    lateinit var llSDAutoJS: LinearLayout
    lateinit var ivSDAutoJS: ImageView
    lateinit var tvSDAutoJS: TextView
    lateinit var SDJSPop: SingleSelectPop
    lateinit var SDCSPop: SingleSelectPop

    lateinit var tvTFSJ: TextView
    lateinit var rlTFSJ: RelativeLayout
    lateinit var tvFJAuto: TextView
    lateinit var ivFJAuto: ImageView
    lateinit var ivFJOpen: ImageView
    lateinit var tvFJOpen: TextView
    lateinit var llFJAuto: LinearLayout
    lateinit var llFJOpen: LinearLayout

    lateinit var tvXFOut: TextView
    lateinit var ivXFOut: ImageView
    lateinit var llXFOut: LinearLayout
    lateinit var llXFIn: LinearLayout
    lateinit var ivXFIn: ImageView
    lateinit var tvXFIn: TextView

    lateinit var rlXFOutSetting: RelativeLayout
    lateinit var tvXFOutAuto: TextView
    lateinit var ivXFOutAuto: ImageView
    lateinit var llXFOutAuto: LinearLayout
    lateinit var llXFOutHigh: LinearLayout
    lateinit var ivXFOutHigh: ImageView
    lateinit var tvXFOutHigh: TextView
    lateinit var tvXFOutMid: TextView
    lateinit var ivXFOutMid: ImageView
    lateinit var llXFOutMid: LinearLayout
    lateinit var llXFOutMin: LinearLayout
    lateinit var ivXFOutMin: ImageView
    lateinit var tvXFOutMin: TextView
    override fun init() {
        /**
         * 节能温度(制冷)
         */
        temSingleSelectPopForJNZL = SingleSelectPop(activity!!)
        temSingleSelectPopForJNZL.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_TEMP_JN_ZL,
                Constants.DEFAULT_TEMPDATA_JNZL
            ) as String
            , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                override fun onSelect(data: Any?, postion: Int) {
                    tvZLJNWD.text = data.toString()
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_TEMP_JN_ZL, data)
                }

            })
        tvZLJNWD.text = SharedPreferencesUtil.getInstance(context).getData(
            Constants.DEFAULT_TEMP_JN_ZL,
            Constants.DEFAULT_TEMPDATA_JNZL
        ) as String
        rlZLJNWD.setOnClickListener {
            temSingleSelectPopForJNZL.showPopupWindow(tvZLJNWD)
        }
        /**
         * 节能温度(制热)
         */
        temSingleSelectPopForJNZR = SingleSelectPop(activity!!)
        temSingleSelectPopForJNZR.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_TEMP_JN_ZR,
                Constants.DEFAULT_TEMPDATA_JNZR
            ) as String
            , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                override fun onSelect(data: Any?, postion: Int) {
                    tvZRJNWD.text = data.toString()
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_TEMP_JN_ZR, data)
//                    temSingleSelectPopForJNZR.dismiss()
                }

            })
        tvZRJNWD.text = SharedPreferencesUtil.getInstance(context).getData(
            Constants.DEFAULT_TEMP_JN_ZR,
            Constants.DEFAULT_TEMPDATA_JNZR
        ) as String
        rlZRJNWD.setOnClickListener {
            temSingleSelectPopForJNZR.showPopupWindow(tvZLJNWD)
        }
        rlJNWD.setOnClickListener {
            if (isJNWDOpen) {
                llJNWDContent.visibility = View.GONE
                ivJNWD.setImageResource(R.mipmap.icon_xiaojiantou)
            } else {
                llJNWDContent.visibility = View.VISIBLE
                ivJNWD.setImageResource(R.mipmap.icon_xiaojiantoulv)
            }
            isJNWDOpen = !isJNWDOpen
        }
        /**
         * 离家温度(制冷)
         */
        temSingleSelectPopForLJZL = SingleSelectPop(activity!!)
        temSingleSelectPopForLJZL.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_TEMP_OUTHOME_ZL,
                Constants.DEFAULT_TEMPDATA_LJZL
            ) as String
            , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                override fun onSelect(data: Any?, postion: Int) {
                    tvZLLJWD.text = data.toString()
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_TEMP_OUTHOME_ZL, data)
//                    temSingleSelectPopForLJZL.dismiss()
                }

            })
        tvZLLJWD.text = SharedPreferencesUtil.getInstance(context).getData(
            Constants.DEFAULT_TEMP_OUTHOME_ZL,
            Constants.DEFAULT_TEMPDATA_LJZL
        ) as String
        rlZLLJWD.setOnClickListener {
            temSingleSelectPopForLJZL.showPopupWindow(tvZLLJWD)
        }
        /**
         * 离家温度(制热)
         */
        temSingleSelectPopForLJZR = SingleSelectPop(activity!!)
        temSingleSelectPopForLJZR.init(initTemp(),
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_TEMP_OUTHOME_ZR,
                Constants.DEFAULT_TEMPDATA_LJZR
            ) as String
            , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                override fun onSelect(data: Any?, postion: Int) {
                    tvZRLJWD.text = data.toString()
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_TEMP_OUTHOME_ZR, data)
//                    temSingleSelectPopForLJZR.dismiss()
                }

            })
        tvZRLJWD.text = SharedPreferencesUtil.getInstance(context).getData(
            Constants.DEFAULT_TEMP_OUTHOME_ZR,
            Constants.DEFAULT_TEMPDATA_LJZR
        ) as String
        rlZRLJWD.setOnClickListener {
            temSingleSelectPopForLJZR.showPopupWindow(tvZRLJWD)
        }
        rlLJWD.setOnClickListener {
            if (isLJWDOpen) {
                llLJWDContent.visibility = View.GONE
                ivLJWD.setImageResource(R.mipmap.icon_xiaojiantou)
            } else {
                llLJWDContent.visibility = View.VISIBLE
                ivLJWD.setImageResource(R.mipmap.icon_xiaojiantoulv)
            }
            isLJWDOpen = !isLJWDOpen
        }
        /**
         * 风机模式
         */
        if(SharedPreferencesUtil.getInstance(context).getData(Constants.BTN_SETTING_FJ_ISAUTO, false)==true) {
            radioFJTotal(ivFJAuto,tvFJAuto)
            rlTFSJ.visibility=View.VISIBLE
        }else{
            radioFJTotal(ivFJOpen,tvFJOpen)
            rlTFSJ.visibility=View.GONE
        }
        llFJAuto.setOnClickListener {
            radioFJTotal(ivFJAuto,tvFJAuto)
            rlTFSJ.visibility=View.VISIBLE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_FJ_ISAUTO, true)
        }
        llFJOpen.setOnClickListener {
            radioFJTotal(ivFJOpen,tvFJOpen)
            rlTFSJ.visibility=View.GONE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_FJ_ISAUTO, false)
        }
        temSingleSelectPopForTFSJ = SingleSelectPop(activity!!)
        temSingleSelectPopForTFSJ.init(initTime(),
            SharedPreferencesUtil.getInstance(context).getData(
                Constants.DEFAULT_SETTING_TFTIME,
                Constants.DEFAULT_TF_TIME
            ) as String
            , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                override fun onSelect(data: Any?, postion: Int) {
                    tvTFSJ.text = data.toString()
                    SharedPreferencesUtil.getInstance(context)
                        .saveData(Constants.DEFAULT_SETTING_TFTIME, data)
//                    temSingleSelectPopForTFSJ.dismiss()
                }

            })
        tvTFSJ.text = SharedPreferencesUtil.getInstance(context).getData(
            Constants.DEFAULT_SETTING_TFTIME,
            Constants.DEFAULT_TF_TIME
        ) as String
        rlTFSJ.setOnClickListener {
            temSingleSelectPopForTFSJ.showPopupWindow(tvXFIn)
        }
        ivAdd.setOnClickListener {
        }

        /**
         * 湿度设置
         */
        if(  SharedPreferencesUtil.getInstance(context)
                .getData(Constants.BTN_SETTING_SD_ISAUTO, true)==true){
            radioSDAutoContorl(ivSDAuto,tvSDAuto)
            llSDItemAuto.visibility=View.VISIBLE
            rlSDItemNoAuto.visibility=View.GONE
        }else{
            radioSDAutoContorl(ivSDNoAuto,tvSDNoAuto)
            llSDItemAuto.visibility=View.GONE
            rlSDItemNoAuto.visibility=View.VISIBLE
        }
        when(SharedPreferencesUtil.getInstance(context).getData(Constants.BTN_STATE_SD, 0)) {
            1 -> {//除湿正在开启
                radioSDNoAuto(ivSDNoAutoCS,tvSDNoAutoCS)
            }
            2 -> {//加湿正在开启
                radioSDNoAuto(ivSDAutoJS,tvSDAutoJS)
            }
            else -> {//全关
                radioSDNoAuto(ivSDNoAutoOff,tvSDNoAutoOff)
            }
        }
        llSDAuto.setOnClickListener {
            radioSDAutoContorl(ivSDAuto,tvSDAuto)
            llSDItemAuto.visibility=View.VISIBLE
            rlSDItemNoAuto.visibility=View.GONE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_SD_ISAUTO, true)
        }
        llSDNoAuto.setOnClickListener {
            radioSDAutoContorl(ivSDNoAuto,tvSDNoAuto)
            llSDItemAuto.visibility=View.GONE
            rlSDItemNoAuto.visibility=View.VISIBLE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_SD_ISAUTO, false)
        }
        llSDAutoJS.setOnClickListener {
            radioSDNoAuto(ivSDAutoJS,tvSDAutoJS)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.BTN_STATE_SD, 2)
            context?.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_SD_CONTORL
                putExtra("type",2)
            })
        }
        llSDNoAutoOff.setOnClickListener {
            radioSDNoAuto(ivSDNoAutoOff,tvSDNoAutoOff)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.BTN_STATE_SD, 0)
            context?.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_SD_CONTORL
                putExtra("type",0)
            })
        }
        llSDNoAutoCS.setOnClickListener {
            radioSDNoAuto(ivSDNoAutoCS,tvSDNoAutoCS)
            SharedPreferencesUtil.getInstance(context).saveData(Constants.BTN_STATE_SD, 1)
            context?.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_SD_CONTORL
                putExtra("type",1)
            })
        }
        tvJS.text =  SharedPreferencesUtil.getInstance(context).getData(
            Constants.SETTING_JS_COUNT,
            Constants.DEFAULT_JS_COUNT
        ) as String
        SDJSPop = SingleSelectPop(activity!!)
        rlJS.setOnClickListener {
            SDJSPop.init(initJSSD(SharedPreferencesUtil.getInstance(context).getData(
                Constants.SETTING_CS_COUNT,
                Constants.DEFAULT_CS_COUNT
            ) as String),
                SharedPreferencesUtil.getInstance(context).getData(
                    Constants.SETTING_JS_COUNT,
                    Constants.DEFAULT_JS_COUNT
                ) as String
                , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                    override fun onSelect(data: Any?, postion: Int) {
                        tvJS.text = data.toString()
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.SETTING_JS_COUNT, data)
                    }

                })
            SDJSPop.showPopupWindow(tvJS)
        }
        tvCS.text =  SharedPreferencesUtil.getInstance(context).getData(
            Constants.SETTING_CS_COUNT,
            Constants.DEFAULT_CS_COUNT
        ) as String
        SDCSPop = SingleSelectPop(activity!!)

        rlCS.setOnClickListener {
            SDCSPop.init(initCS(SharedPreferencesUtil.getInstance(context).getData(
                Constants.SETTING_JS_COUNT,
                Constants.DEFAULT_JS_COUNT
            ) as String),
                SharedPreferencesUtil.getInstance(context).getData(
                    Constants.SETTING_CS_COUNT,
                    Constants.DEFAULT_CS_COUNT
                ) as String
                , object : SingleSelectPop.OnSingleSelectPopSelectListener {
                    override fun onSelect(data: Any?, postion: Int) {
                        tvCS.text = data.toString()
                        SharedPreferencesUtil.getInstance(context)
                            .saveData(Constants.SETTING_CS_COUNT, data)
                    }

                })
            SDCSPop.showPopupWindow(tvJS)
        }
        /**
         * 新风设置
         */
        if(SharedPreferencesUtil.getInstance(context).getData(Constants.BTN_SETTING_XF_ISOUT, true)==true) {
            radioXFTotal(ivXFOut,tvXFOut)
            rlXFOutSetting.visibility=View.VISIBLE
        }else{
            radioXFTotal(ivXFIn,tvXFIn)
            rlXFOutSetting.visibility=View.GONE
        }
        llXFOut.setOnClickListener {
            radioXFTotal(ivXFOut,tvXFOut)
            rlXFOutSetting.visibility=View.VISIBLE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_ISOUT, true)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_XF_CONTORL
                putExtra("isOut",true)
            })
        }
        llXFIn.setOnClickListener {
            radioXFTotal(ivXFIn,tvXFIn)
            rlXFOutSetting.visibility=View.GONE
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_ISOUT, false)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_XF_CONTORL
                putExtra("isOut",false)
            })
        }
        when(SharedPreferencesUtil.getInstance(context).getData(Constants.BTN_SETTING_XF_OUT_TYPE, Constants.DEFAULT_XF_OUTTYPE)) {
            0 -> {//低
                radioXFOut(ivXFOutMin,tvXFOutMin)
            }
            1 -> {//中
                radioXFOut(ivXFOutMid,tvXFOutMid)
            }
            2 -> {//高
                radioXFOut(ivXFOutHigh,tvXFOutHigh)
            }
            else -> {//自动
                radioXFOut(ivXFOutAuto,tvXFOutAuto)
            }
        }
        llXFOutAuto.setOnClickListener {
            radioXFOut(ivXFOutAuto,tvXFOutAuto)
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 3)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_FJ_CONTORL
                putExtra("FJType",3)
            })
        }
        llXFOutHigh.setOnClickListener {
            radioXFOut(ivXFOutHigh,tvXFOutHigh)
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 2)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_FJ_CONTORL
                putExtra("FJType",2)
            })
        }
        llXFOutMid.setOnClickListener {
            radioXFOut(ivXFOutMid,tvXFOutMid)
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 1)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_FJ_CONTORL
                putExtra("FJType",1)
            })
        }
        llXFOutMin.setOnClickListener {
            radioXFOut(ivXFOutMin,tvXFOutMin)
            SharedPreferencesUtil.getInstance(context)
                .saveData(Constants.BTN_SETTING_XF_OUT_TYPE, 0)
            context!!.sendBroadcast(Intent().apply {
                action = CommonConfig.ACTION_ENVAIR_FJ_CONTORL
                putExtra("FJType",0)
            })
        }
    }

    private fun radioSDAutoContorl(iv: ImageView, tv: TextView) {
        ivSDAuto.setImageResource(R.mipmap.btn_normal)
        ivSDNoAuto.setImageResource(R.mipmap.btn_normal)
        tvSDNoAuto.setTextColor(Color.parseColor("#ff666666"))
        tvSDAuto.setTextColor(Color.parseColor("#ff666666"))
        iv.setImageResource(R.mipmap.btn_choose)
        tv.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun radioSDNoAuto(iv: ImageView, tv: TextView) {
        ivSDNoAutoOff.setImageResource(R.mipmap.btn_normal)
        ivSDAutoJS.setImageResource(R.mipmap.btn_normal)
        ivSDNoAutoCS.setImageResource(R.mipmap.btn_normal)

        tvSDNoAutoOff.setTextColor(Color.parseColor("#ff666666"))
        tvSDAutoJS.setTextColor(Color.parseColor("#ff666666"))
        tvSDNoAutoCS.setTextColor(Color.parseColor("#ff666666"))

        iv.setImageResource(R.mipmap.btn_choose)
        tv.setTextColor(Color.parseColor("#ffffff"))
    }


    private fun radioXFTotal(iv: ImageView, tv: TextView) {
        ivXFOut.setImageResource(R.mipmap.btn_normal)
        ivXFIn.setImageResource(R.mipmap.btn_normal)

        tvXFIn.setTextColor(Color.parseColor("#ff666666"))
        tvXFOut.setTextColor(Color.parseColor("#ff666666"))

        iv.setImageResource(R.mipmap.btn_choose)
        tv.setTextColor(Color.parseColor("#ffffff"))
    }
    private fun radioXFOut(iv: ImageView, tv: TextView) {
        ivXFOutAuto.setImageResource(R.mipmap.btn_normal)
        ivXFOutHigh.setImageResource(R.mipmap.btn_normal)
        ivXFOutMid.setImageResource(R.mipmap.btn_normal)
        ivXFOutMin.setImageResource(R.mipmap.btn_normal)

        tvXFOutAuto.setTextColor(Color.parseColor("#ff666666"))
        tvXFOutHigh.setTextColor(Color.parseColor("#ff666666"))
        tvXFOutMid.setTextColor(Color.parseColor("#ff666666"))
        tvXFOutMin.setTextColor(Color.parseColor("#ff666666"))

        iv.setImageResource(R.mipmap.btn_choose)
        tv.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun radioFJTotal(iv: ImageView, tv: TextView) {
        ivFJAuto.setImageResource(R.mipmap.btn_normal)
        ivFJOpen.setImageResource(R.mipmap.btn_normal)

        tvFJAuto.setTextColor(Color.parseColor("#ff666666"))
        tvFJOpen.setTextColor(Color.parseColor("#ff666666"))

        iv.setImageResource(R.mipmap.btn_choose)
        tv.setTextColor(Color.parseColor("#ffffff"))
    }
    override fun getContentViewId(): Int {
        return R.layout.fragment_setting_enr
    }

    override fun findView(view: View?) {
        ivJNWD = view?.findViewById(R.id.ivJNWD)!!
        rlJNWD = view.findViewById(R.id.rlJNWD)
        llJNWDContent = view.findViewById(R.id.llJNWDContent)
        rlZLJNWD = view.findViewById(R.id.rlZLJNWD)
        tvZLJNWD = view.findViewById(R.id.tvZLJNWD)
        rlZRJNWD = view.findViewById(R.id.rlZRJNWD)
        tvZRJNWD = view.findViewById(R.id.tvZRJNWD)

        ivLJWD = view.findViewById(R.id.ivLJWD)
        rlLJWD = view.findViewById(R.id.rlLJWD)
        llLJWDContent = view.findViewById(R.id.llLJWDContent)
        rlZLLJWD = view.findViewById(R.id.rlZLLJWD)
        tvZLLJWD = view.findViewById(R.id.tvZLLJWD)
        rlZRLJWD = view.findViewById(R.id.rlZRLJWD)
        tvZRLJWD = view.findViewById(R.id.tvZRLJWD)

        tvTFSJ = view.findViewById(R.id.tvTFSJ)
        rlTFSJ = view.findViewById(R.id.rlTFSJ)
        ivAdd = view.findViewById(R.id.ivAdd)

        llSDNoAuto = view.findViewById(R.id.llSDNoAuto)
        ivSDNoAuto = view.findViewById(R.id.ivSDNoAuto)
        tvSDNoAuto = view.findViewById(R.id.tvSDNoAuto)
        llSDAuto = view.findViewById(R.id.llSDAuto)
        ivSDAuto = view.findViewById(R.id.ivSDAuto)
        tvSDAuto = view.findViewById(R.id.tvSDAuto)
        llSDItemAuto = view.findViewById(R.id.llSDItemAuto)
        rlJS = view.findViewById(R.id.rlJS)
        tvJS = view.findViewById(R.id.tvJS)
        rlCS = view.findViewById(R.id.rlCS)
        tvCS = view.findViewById(R.id.tvCS)
        rlSDItemNoAuto = view.findViewById(R.id.rlSDItemNoAuto)
        llSDNoAutoOff = view.findViewById(R.id.llSDNoAutoOff)
        ivSDNoAutoOff = view.findViewById(R.id.ivSDNoAutoOff)
        tvSDNoAutoOff = view.findViewById(R.id.tvSDNoAutoOff)
        llSDNoAutoCS = view.findViewById(R.id.llSDNoAutoCS)
        ivSDNoAutoCS = view.findViewById(R.id.ivSDNoAutoCS)
        tvSDNoAutoCS = view.findViewById(R.id.tvSDNoAutoCS)
        llSDAutoJS = view.findViewById(R.id.llSDAutoJS)
        ivSDAutoJS = view.findViewById(R.id.ivSDAutoJS)
        tvSDAutoJS = view.findViewById(R.id.tvSDAutoJS)

        tvFJAuto = view.findViewById(R.id.tvFJAuto)
        ivFJAuto = view.findViewById(R.id.ivFJAuto)
        ivFJOpen = view.findViewById(R.id.ivFJOpen)
        tvFJOpen = view.findViewById(R.id.tvFJOpen)
        llFJAuto = view.findViewById(R.id.llFJAuto)
        llFJOpen = view.findViewById(R.id.llFJOpen)

        tvXFOut = view.findViewById(R.id.tvXFOut)
        ivXFOut = view.findViewById(R.id.ivXFOut)
        llXFOut = view.findViewById(R.id.llXFOut)
        llXFIn = view.findViewById(R.id.llXFIn)
        ivXFIn = view.findViewById(R.id.ivXFIn)
        tvXFIn = view.findViewById(R.id.tvXFIn)

        tvXFOutAuto = view.findViewById(R.id.tvXFOutAuto)
        ivXFOutAuto = view.findViewById(R.id.ivXFOutAuto)
        llXFOutAuto = view.findViewById(R.id.llXFOutAuto)
        llXFOutHigh = view.findViewById(R.id.llXFOutHigh)
        ivXFOutHigh = view.findViewById(R.id.ivXFOutHigh)
        tvXFOutHigh = view.findViewById(R.id.tvXFOutHigh)
        tvXFOutMid = view.findViewById(R.id.tvXFOutMid)
        ivXFOutMid = view.findViewById(R.id.ivXFOutMid)
        llXFOutMid = view.findViewById(R.id.llXFOutMid)
        llXFOutMin = view.findViewById(R.id.llXFOutMin)
        ivXFOutMin = view.findViewById(R.id.ivXFOutMin)
        tvXFOutMin = view.findViewById(R.id.tvXFOutMin)
        rlXFOutSetting = view.findViewById(R.id.rlXFOutSetting)
    }

    fun initTemp(): ArrayList<String> {
        var list = ArrayList<String>()
        for (i in 5..35) {
            list.add("${i}℃")
        }
        return list
    }

    fun initTime(): ArrayList<String> {
        var list = ArrayList<String>()
        for (i in 1..59) {
            list.add("${i}分钟")
        }
        return list
    }

    fun initJSSD(CSCount: String): ArrayList<String> {
        var cs=CSCount.replace("%RH","").toInt()
        var list = ArrayList<String>()
        for (i in 30 until (cs - 5)) {
            list.add("${i}%RH")
        }
        return list
    }

    fun initCS(JSCount: String): ArrayList<String> {
        var js=JSCount.replace("%RH","").toInt()
        var list = ArrayList<String>()
        for (i in (js + 5)..80) {
            list.add("${i}%RH")
        }
        return list
    }
}
