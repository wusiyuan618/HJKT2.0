package com.wusy.serialportproject.util.DownAPK;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.wusy.wusylibrary.util.OkHttpUtil;

import java.io.File;

/**
 *
 * Filename: UpdateService.java Description: 今天修改了当增量包合成失败的时候，重新下载整个最新的apk
 *
 * @version: 2.8.0 Create at: 2016年5月20日 下午3:25:52
 *
 */
public class UpdateService extends Service {
	private final int TIMEOUT = 10 * 1000;// 超时
	private final int DOWN_OK = 1;
	private final int DOWN_ERROR = 0;
	private String app_name;
	private String file_name;

	private NotificationManager notificationManager;
	private Notification notification;

	private Intent updateIntent;
	private PendingIntent pendingIntent;

	private int notification_id = 0;
	ProgressDialog m_pDialog;
	private File updateDir = new File(Constant.FILEDIR);
	File updateFile;
	String patchUrl = Constant.FILEDIR;
	String newApkUrl = Constant.FILEDIR;
	private String downUrl = "";
	private int flag = 2;
	private Intent broadCast;
	@Override
	public IBinder onBind(Intent arg0) {
		stopSelf();
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (CommonUtil.getInstance().isNull(intent)) {
			stopSelf();
			Log.d("UpService", intent + "--------------------");
		} else {

			app_name = intent.getStringExtra("app_name");
			file_name = app_name + ".apk";


			patchUrl = patchUrl + app_name + ".patch";
			// 创建文件
			CommonUtil.getInstance().createFile(app_name + ".patch");
			CommonUtil.getInstance().createFile(file_name);
			updateFile = new File(updateDir + "/" + app_name + ".patch");
			if(flag==1)flag=2;
			broadCast = new Intent();
			broadCast.setAction(Constant.DOWNLOAD_ACTION);
			broadCast.putExtra("download", 0);
			sendBroadcast(broadCast);
			downFile();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/***
	 * 开线程下载
	 */
	public void createThread(final String downUrl) {
		final Message message = new Message();
		if (SystemUtils.isNetworkAvailable(getApplicationContext())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						OkHttpUtil.getInstance().download(downUrl,0, newApkUrl, file_name, new OkHttpUtil.OnDownloadListener() {
							@Override
							public void onDownloadSuccess(File downfile, File file) {
								Log.i("wsy",file.getName()+"下载完成");
								hand();
								stopSelf();
							}
							@Override
							public void onDownloading(int progress, File file) {
//								LogUtil.i("update progress---"+progress);
								broadCast.putExtra("download", progress);
								sendBroadcast(broadCast);
							}
							@Override
							public void onDownloadFailed(String error) {
								Log.i("wsy","下载错误");
							}
						});
					} catch (Exception e) {
					}
				}
			}).start();
		} else {
			Toast.makeText(getApplicationContext(), "网络无连接，请稍后下载！",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 根据类型下载不同的文件
	 *
	 * @exception
	 * @since 1.0.0
	 */
	protected void downFile() {
		// 下载增量包路径
		if (1 == flag) {
//			downUrl = (String) vsPreference.getData(Constant.VERSION_PATCH_FILE_PATH,"");
		} else if (2 == flag) {
			downUrl =Constant.Custom_DOWNLOADAPK_URL;
		}
		createThread(downUrl);
	}

	/**
	 * 根据不同的包安装
	 *
	 * @exception
	 * @since 1.0.0
	 */
	protected void hand() {
		// 增量合成方法
		if (1 == flag) {
//			ApkUpdate update = new ApkUpdate(getApplicationContext(),
//					packageName, patchUrl, newApkUrl);
//			// 检查低版本的apk 是否存在
//			if (update.isOldApkExist()) {
//				// 判断是否成功合成新的apk
//				if (update.newApkGet()) {
//					update.installApk();
//					delData();
//				} else {
//					// Toast.makeText(getApplicationContext(),
//					// "下载失败！：" + newApkUrl + "增量包地址：" + patchUrl,
//					// Toast.LENGTH_SHORT).show();
//					flag = 2;
//					downFile();
//				}
//			}
		}
		// 整包安装方法
		else if (2 == flag) {
			Log.i("wsy","开始安装");
			File apkFile = new File(newApkUrl, file_name);
			OkHttpUtil.getInstance().openFile(apkFile,this);
		}
	}
}
