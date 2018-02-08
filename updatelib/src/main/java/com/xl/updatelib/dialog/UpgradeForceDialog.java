package com.xl.updatelib.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xl.updatelib.R;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.service.UpgradeIntentService;
import com.xl.updatelib.view.CustomProgressWithPercentView;


/**
 * Created by xilinch on 2016/6/3.
 * 升级对话框
 */
public class UpgradeForceDialog extends BaseUpgradeDailog {


    private static final int THEME = R.style.dialog_upgrade;

    /**
     * 升级标题
     */
    private TextView dialog_upgrade_tv_title;

    /**
     * 升级提示内容
     */
    private TextView dialog_upgrade_tv_content;

    /**
     * 升级
     */
    private CustomProgressWithPercentView dialog_upgrade_tv_upgrade_cp;

    /**
     * 升级模型
     */
    private UpgradeModel upgradeModel;

    public UpgradeForceDialog(Activity context, UpgradeModel upgradeModel) {
        super(context, THEME);
        this.activity = context;
        this.upgradeModel = upgradeModel;
        initView();
    }

    public UpgradeForceDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.activity = context;
        initView();
    }

    public UpgradeForceDialog(Activity context, int theme) {
        super(context, THEME);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_ugrade_force);
        dialog_upgrade_tv_title = (TextView) findViewById(R.id.dialog_upgrade_tv_title);
        dialog_upgrade_tv_content = (TextView) findViewById(R.id.dialog_upgrade_tv_content);
        dialog_upgrade_tv_upgrade_cp = (CustomProgressWithPercentView) findViewById(R.id.dialog_upgrade_tv_upgrade_cp);

        dialog_upgrade_tv_title.setText("发现新版本V" + upgradeModel.getVersion());
        dialog_upgrade_tv_content.setText(upgradeModel.getContent());

        dialog_upgrade_tv_upgrade_cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击开始下载
                if(check()){
                    dialog_upgrade_tv_upgrade_cp.setEnabled(false);
                    statisticsDownload();
                    Intent intent = new Intent(activity, UpgradeIntentService.class);
                    intent.putExtra(UpgradeModel.TAG, upgradeModel);
                    activity.startService(intent);
                }
            }
        });

        setParams();

    }

    public CustomProgressWithPercentView getDialog_upgrade_tv_upgrade_cp(){
        return dialog_upgrade_tv_upgrade_cp;
    }

    @Override
    public void upgradeError(int percent) {
        if(dialog_upgrade_tv_upgrade_cp != null){
            dialog_upgrade_tv_upgrade_cp.setEnabled(true);
        }
    }

    @Override
    public void upgradeProcess(int percent) {
        if(dialog_upgrade_tv_upgrade_cp != null){
            dialog_upgrade_tv_upgrade_cp.setProgress(percent);
            dialog_upgrade_tv_upgrade_cp.setEnabled(false);
        }
    }

    @Override
    public void upgradeFinish(int percent) {
        if(dialog_upgrade_tv_upgrade_cp != null){
            dialog_upgrade_tv_upgrade_cp.setProgress(100);
            dialog_upgrade_tv_upgrade_cp.setEnabled(true);
        }
    }

    private void setParams(){
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 0.2f;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }


    @Override
    public void onBackPressed() {

    }
}
