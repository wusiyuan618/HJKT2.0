package com.wusy.serialportproject.util.DownAPK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wusy.serialportproject.R;
import com.wusy.wusylibrary.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by XIAO RONG on 2018/7/19.
 */

public class DownLoadApkUtil {
    private BaseActivity activity;
    private ProgressDialog m_pDialog;

    public DownLoadApkUtil(BaseActivity activity){
        this.activity=activity;
    }
    /**
     * 开始下载并更新
     */
    public void start(String path, String content){
        showDialog(new DownLoadBroadCastReceiver(),content);
        Constant.Custom_DOWNLOADAPK_URL=path;
    }
    /***
     * 检查是否更新版本
     */
    private void showDialog(final BroadcastReceiver receiver, String content) {
        final Dialog alert = new UpdateDialog(activity, R.style.MyDialogStyle);
        alert.setContentView(R.layout.upgrade_dialog);
        TextView tvView = (TextView) alert.findViewById(R.id.upgradeText);
//        if (!CommonUtil.getInstance().isNull(sharedPreferencesUtil.getData(Constant.VERSION_CONTENT, ""))) {
//            tvView.setText(Html.fromHtml((String) sharedPreferencesUtil.getData(Constant.VERSION_CONTENT, "")));
//        }
        tvView.setText(content);

        tvView.setWidth(activity.getWindowManager().getDefaultDisplay().getWidth() * 3 / 4);
        Button sureBtn = (Button) alert.findViewById(R.id.btn_sure);
        Button cancleBtn = (Button) alert.findViewById(R.id.btn_cancle);

        sureBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> actionList = new ArrayList<>();
                actionList.add(Constant.DOWNLOAD_ACTION);
                activity.addBroadcastAction(actionList, receiver);
                Intent updateIntent = new Intent(activity,
                        UpdateService.class);
                updateIntent.putExtra("app_name", "hjkt");

                activity.startService(updateIntent);
                alert.dismiss();
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }
    class DownLoadBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.DOWNLOAD_ACTION:
                    int progress = intent.getIntExtra("download", 0);
                    showProgress(progress);
                    break;

                default:
                    break;
            }

        }
    }
    private void showProgress(Integer progress) {
        if (m_pDialog == null) {
            createProgressDialog("正在更新请稍后...");
        }
        m_pDialog.setProgress(progress);
        if (progress == 100) {
            m_pDialog.dismiss();
        }
    }
    private void createProgressDialog(String title) {

        // 创建ProgressDialog对象
        m_pDialog = new ProgressDialog(activity);

        // 设置进度条风格，风格为长形
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // 设置ProgressDialog 标题
        m_pDialog.setTitle(title);
        // 设置ProgressDialog 进度条进度
        m_pDialog.setProgress(100);

        // 设置ProgressDialog 的进度条是否不明确
        m_pDialog.setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        m_pDialog.setCancelable(false);
        // 设置点击进度对话框外的区域对话框不消失
        m_pDialog.setCanceledOnTouchOutside(true);
        // 让ProgressDialog显示
        m_pDialog.show();
    }
}
