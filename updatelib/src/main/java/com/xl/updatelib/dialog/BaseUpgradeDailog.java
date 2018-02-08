package com.xl.updatelib.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.constant.Constant;
import com.xl.updatelib.service.UpgradeIntentService;
import com.xl.updatelib.service.UpgradeSilenceIntentService;
import com.xl.updatelib.util.ToastUtils;
import com.xl.updatelib.util.UtilRequestPermissions;
import com.xl.updatelib.util.UtilZipCheck;

/**
 * Created by Administrator on 2016/6/24.
 */
public abstract class BaseUpgradeDailog extends Dialog{
    protected Activity activity;

    public BaseUpgradeDailog(Activity context) {
        super(context);
    }

    public BaseUpgradeDailog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseUpgradeDailog(Activity context, int theme) {
        super(context, theme);
    }

    /**
     * 下载更新进度
     * @param percent
     */
    public abstract void upgradeProcess(int percent);

    /**
     * 下载错误
     * @param percent
     */
    public abstract void upgradeError(int percent);

    /**
     * 下载完成
     * @param percent
     */
    public abstract void upgradeFinish(int percent);

    /**
     * 统计下载
     */
    public void statisticsDownload(){


    }


    protected boolean checkPermission(){

        boolean read_storage_permission = UtilRequestPermissions.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        return read_storage_permission;
    }

    protected boolean check(){
        boolean isValide = true;
        if(UpgradeIntentService.isDownloading){
            ToastUtils.toastShow(activity, "正在下载...");
            isValide = false;
        }
        if(Build.VERSION.SDK_INT >= 23){
            boolean isGranted = checkPermission();
            if(isGranted ){
                //donothing
            } else {
                ToastUtils.toastShow(activity, "请先授权应用权限，稍后再试!");
                UtilRequestPermissions.requestPermissionStorage(activity, UtilRequestPermissions.REQUEST_CODE_READ_EXTERNAL_STORAGE);
                isValide = false;
            }
        }
        return isValide;
    }

    protected boolean checkAPKExist(UpgradeModel upgradeModel){
        boolean download = false;
        if(upgradeModel != null ){
            String url = upgradeModel.getDownloadUrl();
            String path = getFileNameFromString(url);
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(Constant.FILE_DIRECTORY + path);
            download = UtilZipCheck.isErrorZip(filePath);
        }

        return download;
    }

    public void install(UpgradeModel upgradeModel){
        if(upgradeModel == null){
            return;
        }
        String url = upgradeModel.getDownloadUrl();
        String fileName = getFileNameFromString(url);
        if(TextUtils.isEmpty(fileName)){
//            ToastUtils.toastShow(getContext(), "下载地址出错!");
            return;
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(Constant.FILE_DIRECTORY + fileName);
        installAPK(filePath);
    }

    /**
     * 安装apk
     *
     * @param path
     */
    private void installAPK(String path) {
        try{
            boolean isRight = UtilZipCheck.isErrorZip(path);
            if(isRight){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }


    protected boolean checkBySilence(){
        boolean isValide = true;
        if(UpgradeIntentService.isDownloading){
//            ToastUtils.toastShow(activity, "正在下载...");
            isValide = false;
        }
        if(Build.VERSION.SDK_INT >= 23){
            boolean isGranted = checkPermission();
            if(isGranted ){
                //donothing
            } else {
                //静默升级不提示，不请求权限，自动退出
//                ToastUtils.toastShow(activity, "请先授权应用权限，稍后再试!");
//                UtilRequestPermissions.requestPermissionStorage(activity, UtilRequestPermissions.REQUEST_CODE_READ_EXTERNAL_STORAGE);
                isValide = false;
            }
        }
        return isValide;
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

}
