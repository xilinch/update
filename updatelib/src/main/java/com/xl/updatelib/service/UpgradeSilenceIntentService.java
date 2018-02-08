package com.xl.updatelib.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.UpgradeSilenceActivity;
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
public class UpgradeSilenceIntentService extends IntentService {

    private static final String TAG = "UpgradeSilenceIntentService";

    private UpgradeModel upgradeModel;

    public static boolean isDownloading = false;

    private String filePath;

    public UpgradeSilenceIntentService() {
        super(TAG);
    }

    public UpgradeSilenceIntentService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        upgradeModel = (UpgradeModel) intent.getSerializableExtra(UpgradeModel.TAG);
        if(upgradeModel != null){
            Log.i(TAG, "onHandleIntent-- isDownloading:" + isDownloading);
            if (isDownloading) {
                //正在下载
//                Toast.makeText(UpgradeSilenceIntentService.this, "正在下载...", Toast.LENGTH_SHORT).show();
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
        String url = upgradeModel.getDownloadUrl();
        String fileName = getFileNameFromString(url);
        if(TextUtils.isEmpty(fileName)){
            ToastUtils.toastShow(this, "下载地址出错!");
            return;
        }

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(Constant.FILE_DIRECTORY + fileName);
        Log.i("my", "filePath：" + filePath + "  url:" + url);

        download(filePath, url, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.i("my", "onDownloadSuccess...responseInfo=" + filePath);
                isDownloading = false;
                boolean isRight = UtilZipCheck.isErrorZip(filePath);
                Log.i(TAG, "onDownloadSuccess-:" + isRight);
                if(isRight){
                    Intent intent = new Intent();
                    intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED);
                    intent.putExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED, 100);
                    sendBroadcast(intent);
                } else {
                    UtilApkClear.clearAPK();
                }
            }

            @Override
            public void onDownloading(int percent) {
                Log.i("my", "onLoading..." + " percent=" + percent );

                Intent intent = new Intent();
                intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS);
                intent.putExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS, percent);
                sendBroadcast(intent);
            }

            @Override
            public void onDownloadFailed() {
                Log.i("my", "onDownloadFailed" + filePath);
                UtilApkClear.clearAPK();
            }
        });

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
     * 获取url的文件名
     * @return
     */
    private String getFileNameFromString(String url){
        String fileName = "";
        if(url != null){
            int index = url.lastIndexOf("/");
            if(index > 0){
                fileName = url.substring(index+ 1 ,url.length());
            }
        }
        return fileName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDownloading = false;
        upgradeModel = null;
    }
}
