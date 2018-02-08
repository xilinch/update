package com.xl.updatelib.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.xl.updatelib.R;
import com.xl.updatelib.UpgradeActivity;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.constant.Constant;
import com.xl.updatelib.listener.OnDownloadListener;
import com.xl.updatelib.util.ToastUtils;
import com.xl.updatelib.util.UtilApkClear;
import com.xl.updatelib.util.UtilZipCheck;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xilinch on 2016/6/3.
 * 升级下载服务，支持断点下载
 */
public class UpgradeIntentService extends IntentService {

    private static final String TAG = "UpgradeIntentService";

    private static final int NOTIFICATION_ID = 0X345444;

    private UpgradeModel upgradeModel;

    public static boolean isDownloading = false;

    private NotificationManager notificationManager;

    private Notification notification;

    private NotificationCompat.Builder builder;

    private String filePath;

    public UpgradeIntentService() {
        super("UpgradeIntentService");
    }

    public UpgradeIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        upgradeModel = (UpgradeModel) intent.getSerializableExtra(UpgradeModel.TAG);
        if (upgradeModel != null) {
            initNotifycation();
            Log.i(TAG, "onHandleIntent-- isDownloading:" + isDownloading);
            if (isDownloading) {
                //正在下载
                Toast.makeText(UpgradeIntentService.this, "正在下载...", Toast.LENGTH_SHORT).show();
            } else {
                //进入下载
                begainDownload();
            }
        }
    }

    /**
     * 开始下载
     */
    private synchronized void begainDownload() {
        isDownloading = true;
        Log.i(TAG, "begainDownload-- isDownloading:" + isDownloading);
        final String url = upgradeModel.getDownloadUrl();
        String fileName = getFileNameFromString(url);
        if (TextUtils.isEmpty(fileName)) {
            ToastUtils.toastShow(this, "下载地址出错!");
            return;
        }

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(Constant.FILE_DIRECTORY + fileName);
        Log.i("my", "filePath..." + filePath);

        download(filePath, url, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.i("my", "onDownloadSuccess...");
                isDownloading = false;
                Log.i(TAG, "begainDownload-- Override:" + isDownloading);
                boolean isRight = UtilZipCheck.isErrorZip(filePath);
                if (isRight) {
                    installAPK(filePath);
                    Intent intent = new Intent();
                    intent.setAction(UpgradeActivity.ACTION_UPDATE_FINISHED);
                    intent.putExtra(UpgradeActivity.ACTION_UPDATE_FINISHED, 100);
                    sendBroadcast(intent);
                    if (builder != null) {
                        builder.setProgress(100, 100, false);
                        builder.setContentText("下载进度100%");
                        notification = builder.build();
                        if (notificationManager == null) {
                            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        }
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(UpgradeActivity.ACTION_UPDATE_BROKEN);
                    sendBroadcast(intent);
                    UtilApkClear.clearAPK();
                }
            }

            @Override
            public void onDownloading(int percent) {
                Log.i("my", "percent..." + percent);
                if (builder != null) {
                    builder.setProgress(100, (int) percent, false);
                    builder.setContentText("下载进度" + percent + "%");
                    notification = builder.build();
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                Intent intent = new Intent();
                intent.setAction(UpgradeActivity.ACTION_UPDATE_PROGRESS);
                intent.putExtra(UpgradeActivity.ACTION_UPDATE_PROGRESS, percent);
                sendBroadcast(intent);
            }

            @Override
            public void onDownloadFailed() {
                Log.i("my", "onDownloadFailed...");
                if (builder != null) {
                    builder.setContentText("下载失败");
                    notification = builder.build();
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                Intent intent = new Intent();
                intent.setAction(UpgradeActivity.ACTION_UPDATE_ERROR);
                sendBroadcast(intent);
                UtilApkClear.clearAPK();
            }
        });
    }


    /**
     * 初始化通知栏
     */
    private void initNotifycation() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("开始下载")
                    .setContentTitle("南方Plus下载")
                    .setContentText("下载进度 0%")
                    .setProgress(100, 0, true);
            notification = builder.build();
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    /**
     * 下载
     *
     * @param fileName
     * @param url
     * @param listener
     */
    private void download(String fileName, String url, final OnDownloadListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        final File file = new File(fileName);
        int index = fileName.lastIndexOf("/");
        Log.e("my", "fileName:" + fileName + "  index:" + index);
        if (index > 0 && index < fileName.length()) {
            String fileDirectory = fileName.substring(0, index);
            Log.e("my", "fileDirectory:" + fileDirectory);
            File saveFileDirectory = new File(fileDirectory);
            if (!saveFileDirectory.exists()) {
                saveFileDirectory.mkdirs();
            }
        }

        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                Log.e("my", "success:" + success);
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        long fileSize = 0L;
        if (file != null && file.exists()) {
            fileSize = file.length();
        }

        Log.e("my", "fileSize:" + fileSize);
        Request request = (new Request.Builder()).url(url).addHeader("RANGE", "bytes=" + fileSize + "-").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Request request, IOException e) {
                listener.onDownloadFailed();
            }

            public void onResponse(Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                FileOutputStream fos = null;

                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    Log.e("my", "response:" + response.toString() + "   total:" + total);
                    if (total != 0L) {
                        if (206 == response.code()) {
                            fos = new FileOutputStream(file, true);
                        } else if (200 == response.code()) {
                            fos = new FileOutputStream(file);
                        } else if (416 == response.code()) {
                            listener.onDownloadSuccess();
                            return;
                        }

                        long sum = 0L;
                        int offset = (int) file.length();
                        Log.e("my", "offset:" + offset);
                        long currentTime = System.currentTimeMillis();

                        int len;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += (long) len;
                            int progress = (int) ((float) sum * 1.0F / (float) total * 100.0F);
                            long updateTime = System.currentTimeMillis();
                            if (updateTime - currentTime >= 300L) {
                                listener.onDownloading(progress);
                                currentTime = updateTime;
                            }
                        }

                        fos.flush();
                    }

                    listener.onDownloadSuccess();
                } catch (Exception var30) {
                    var30.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException var29) {
                        var29.printStackTrace();
                    }

                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException var28) {
                        var28.printStackTrace();
                    }

                }

            }
        });
    }


    /**
     * 安装apk
     *
     * @param path
     */
    private void installAPK(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 获取url的文件名
     *
     * @return
     */
    private String getFileNameFromString(String url) {
        String fileName = "";
        if (url != null) {
            int index = url.lastIndexOf("/");
            if (index > 0) {
                fileName = url.substring(index + 1, url.length());
            }
        }
        return fileName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDownloading = false;
        upgradeModel = null;
        notificationManager = null;
    }

}
