package com.xl.update;

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

public class MainActivity extends Activity {

    private UpgradeChoiceSilenceDialog upgradeChoiceSilenceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpgrade();
            }
        });
        //注册广播
        initUpgradeSilenceReciver();

    }

    //------升级
    private void checkUpgrade(){
        //检查升级
        String url = "http://testapi.nfapp.southcn.com/nfplus-app-api/check/version/update";
        UtilCheckUpgrade.checkUpgradeV2(this, url);

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

    //-----升级end

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissUpgradeChoiceSilenceDialog();
        unregisterReceiver(myBroadcastReciver);
    }
}
