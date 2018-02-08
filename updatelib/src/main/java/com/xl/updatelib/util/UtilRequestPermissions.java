package com.xl.updatelib.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by xilinch on 2017/5/3.
 * 对关键的权限进行申请  calendar /camera/ contacts/location/microphone /phone/senors/sms/storage
 * 增加对版本号的判断，大于等于23  (6.0)以上才进行权限的申请
 */

public class UtilRequestPermissions {


    /**
     * 拨打电话的请求码
     */
    public static final int REQUEST_CODE_CALL_PHONE = 0x9001;
    /**
     * 存储
     */
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x9002;
    /**
     * 发送短信
     */
    public static final int REQUEST_CODE_SEND_SMS = 0x9003;
    /**
     * 传感器
     */
    public static final int REQUEST_CODE_BODY_SENSORS = 0x9004;
    /**
     * 录音
     */
    public static final int REQUEST_CODE_RECORD_AUDIO = 0x9005;
    /**
     * 定位
     */
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x9006;
    /**
     * 相机
     */
    public static final int REQUEST_CODE_CAMERA = 0x9007;
    /**
     * 扫描相机
     */
    public static final int REQUEST_CODE_ACTIVITY_CAMERA = 0x9010;
    /**
     * 二维码存储
     */
    public static final int REQUEST_CODE_ACTIVITY_READ_EXTERNAL_STORAGE= 0x9011;
    /**
     * 读取日历
     */
    public static final int REQUEST_CODE_READ_CALENDAR = 0x9008;
    /**
     * 录音
     */
    public static final int REQUEST_CODE_READ_CONTACTS = 0x9009;
    /**
     * 请求权限
     * @param activity
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(Activity activity, String[] permission, int requestCode){
        ActivityCompat.requestPermissions(activity, permission, requestCode);

    }

    /**
     * 检查权限
     * @param activity
     * @param permission
     * @return
     */
    public static boolean checkSelfPermission(Context activity, String permission){
        boolean isGranted = true;
        if(Build.VERSION.SDK_INT >= 23){
            isGranted = ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return isGranted;
    }

    /**
     * 日历
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionCalendar(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CODE_READ_CALENDAR);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.READ_CALENDAR}, requestCode);
            }
        }

    }

    /**
     * 照相
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionCamera(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
            }
        }
    }

    /**
     * 联系人
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionContacts(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){
            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            } else {

                requestPermission(activity, new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
            }
        }
    }

    /**
     * 定位
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionLocation(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            } else {

                requestPermission(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
            }
        }
    }

    /**
     * 录音
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionMicrophone(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
            }
        }
    }

    /**
     * 传感器
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionSensors(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.BODY_SENSORS}, REQUEST_CODE_BODY_SENSORS);
            } else {

                requestPermission(activity, new String[]{Manifest.permission.BODY_SENSORS}, requestCode);
            }
        }
    }

    /**
     * 短信
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionSms(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SEND_SMS);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.SEND_SMS}, requestCode);

            }
        }
    }

    /**
     * 存储
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionStorage(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){

            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            }
        }
    }

    /**
     * 电话 有关,包括READ_PHONE_STATE  /CALL_PHONE /READ_CALL_LOG/WRTITE_CALL_LOG/ADD_VOICEMAIL/USE_SIP/PROCESS_OUTGOING_CALLS
     * @param activity
     * @param requestCode
     */
    public static void requestPermissionPhone(Activity activity,int requestCode){
        if(Build.VERSION.SDK_INT >= 23){
            if(requestCode == 0){
                requestPermission(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_CALL_PHONE);
            } else {
                requestPermission(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
            }
        }

    }

}