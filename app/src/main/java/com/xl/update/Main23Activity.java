package com.xl.update;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.xl.update.util.UtilCheckUpgrade;
import com.xl.updatelib.UpgradeSilenceActivity;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.dialog.UpgradeChoiceSilenceDialog;
import com.xl.updatelib.util.UtilRequestPermissions;

import java.util.ArrayList;

/**
 * Created by xilinch on 18-2-8.
 *
 * 演示demo 编译版本在23以上的需要申请权限
 */

public class Main23Activity extends Activity {

    private static final int request_code = 0x11144;
    private UpgradeChoiceSilenceDialog upgradeChoiceSilenceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean read_storage_permission = UtilRequestPermissions.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean read_phone_state = UtilRequestPermissions.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        ArrayList<String> list = new ArrayList<>();
        if(!read_storage_permission){
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(!read_phone_state){
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(list != null && list.size() > 0){
            requestPermissions(list);
        }
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpgrade();
            }
        });
        //注册广播
        initUpgradeSilenceReciver();

    }

    private void checkUpgrade(){
        //检查升级
        String url = "http://testapi.nfapp.southcn.com/nfplus-app-api/check/version/update";
        UtilCheckUpgrade.checkUpgradeV2(this, url);

    }


    /**
     * 必须申请到手机sd读写权限才能 执行下载
     * @param list
     */
    private void requestPermissions(ArrayList<String> list){
        if(list != null && list.size() > 0){
            String[] permissions = list.toArray(new String[list.size()]);
            requestPermission(list.toArray(permissions), request_code);
        }
    }

    /**
     * 权限申请
     * @param permission
     * @param requestCode
     */
    private void requestPermission(String[] permission, int requestCode) {
        if (permission == null || permission.length == 0) {
            return;
        }
        UtilRequestPermissions.requestPermission(this, permission, requestCode);
    }




    public void upgradeBySilence(UpgradeModel upgradeModel) {
        upgradeChoiceSilenceDialog = new UpgradeChoiceSilenceDialog(this, upgradeModel);
        upgradeChoiceSilenceDialog.clickUpgrade();
    }

    public void dismissUpgradeChoiceSilenceDialog() {
        if (upgradeChoiceSilenceDialog != null && upgradeChoiceSilenceDialog.isShowing()) {
            upgradeChoiceSilenceDialog.dismiss();
        }
    }

    public void showUpgradeChoiceSilenceDialog() {
        if (upgradeChoiceSilenceDialog != null) {
            upgradeChoiceSilenceDialog.show();
        }
    }

    /**
     * 监听器
     */
    private void initUpgradeSilenceReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_ERROR);
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED);
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS);
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_START);
        registerReceiver(myBroadcastReciver, filter);
    }

    /**
     * 广播
     */
    private BroadcastReceiver myBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS.equals(action)) {
                //更新进度
                int percent = (int) intent.getLongExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS, 0L);
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeProcess(percent);
                }

            } else if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED.equals(action)) {
                //完成
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeFinish(100);
                    upgradeChoiceSilenceDialog.getDialog_upgrade_tv_upgrade_cp().getTextView()
                            .setText("无需下载，点击安装");
                    showUpgradeChoiceSilenceDialog();
                }

            } else if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_ERROR.equals(action)) {
                //错误
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeError(0);
                }
            } else if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_START.equals(action)) {
                Object object = intent.getSerializableExtra(UpgradeModel.TAG);
                Log.e("my","object:" + object);
                if (object != null) {
                    UpgradeModel upgradeModel = (UpgradeModel) object;
                    upgradeBySilence(upgradeModel);
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissUpgradeChoiceSilenceDialog();
        unregisterReceiver(myBroadcastReciver);
    }
}
