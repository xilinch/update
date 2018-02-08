package com.xl.updatelib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.dialog.BaseUpgradeDailog;
import com.xl.updatelib.dialog.UpgradeChoiceSilenceDialog;
import com.xl.updatelib.dialog.UpgradeForceDialog;


/**
 * Created by xilinch on 2016/6/3.
 */
public class UpgradeSilenceActivity extends Activity {


    /**
     * 升级对话框
     */
    private BaseUpgradeDailog upgradeDialog;

    /**
     * 升级
     */
    private UpgradeModel upgradeModel;


    public static final String ACTION_UPDATE_SILENCE_PROGRESS = "com.nfdaily.nfplus.ACTION_UPDATE_SILENCE_PROGRESS";
    public static final String ACTION_UPDATE_SILENCE_ERROR = "com.nfdaily.nfplus.ACTION_UPDATE_SILENCE_ERROR";
    public static final String ACTION_UPDATE_SILENCE_FINISHED = "com.nfdaily.nfplus.ACTION_UPDATE_SILENCE_FINISHED";
    public static final String ACTION_UPDATE_SILENCE_START = "com.nfdaily.nfplus.ACTION_UPDATE_SILENCE_START";

    private BroadcastReceiver myBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_UPDATE_SILENCE_PROGRESS.equals(action)) {
                //更新进度
                int percent = (int) intent.getLongExtra(ACTION_UPDATE_SILENCE_PROGRESS, 0);
                if(upgradeDialog != null){
                    upgradeDialog.upgradeProcess(percent);
                }

            } else if (ACTION_UPDATE_SILENCE_FINISHED.equals(action)) {
                //完成
                if(upgradeDialog != null){

                    upgradeDialog.upgradeFinish(100);
                }
                if(upgradeDialog instanceof UpgradeChoiceSilenceDialog){

                    ((UpgradeChoiceSilenceDialog)upgradeDialog).getDialog_upgrade_tv_upgrade_cp().getTextView().setText("无需下载，点击安装");
                }
                upgradeDialog.show();
            } else if (ACTION_UPDATE_SILENCE_ERROR.equals(action)) {
                //错误
                if(upgradeDialog != null){

                    upgradeDialog.upgradeError(0);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_silence);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_SILENCE_ERROR);
        filter.addAction(ACTION_UPDATE_SILENCE_FINISHED);
        filter.addAction(ACTION_UPDATE_SILENCE_PROGRESS);
        registerReceiver(myBroadcastReciver, filter);


        upgradeModel = (UpgradeModel) getIntent().getSerializableExtra(UpgradeModel.TAG);
        //测试代码
//        upgradeModel.setContent("提示：\n1.修复了peter的bug\n2.修复了bob的bug\n3.修复了lucy的bug");
//        upgradeModel.setTitle("升级提示");
//        upgradeModel.setUpgradeFlag("2");
//        upgradeModel.setVersion("2.3.3");
//        upgradeModel.setDownloadUrl("http://gdown.baidu.com/data/wisegame/74cc5f397f25b197/yingyongbao_7042130.apk");
        //测试代码---end

        if ("2".equals(upgradeModel.getUpgradeFlag())) {
            upgradeDialog = new UpgradeForceDialog(this, upgradeModel);
            upgradeDialog.show();
        } else {
            upgradeDialog = new UpgradeChoiceSilenceDialog(this, upgradeModel);
//            upgradeDialog.show();
            ((UpgradeChoiceSilenceDialog)upgradeDialog).clickUpgrade();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReciver);

    }


}
