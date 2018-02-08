package com.xl.updatelib.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xl.updatelib.R;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.service.UpgradeSilenceIntentService;
import com.xl.updatelib.util.UtilDateString;
import com.xl.updatelib.util.UtilSharePreference;
import com.xl.updatelib.view.CustomProgressWithPercentView;

import java.util.Date;

/**
 * Created by xilinch on 2016/6/3.
 * 升级对话框
 */
public class UpgradeChoiceSilenceDialog extends BaseUpgradeDailog {


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
     * 忽略该版本
     */
    private CheckBox dialog_upgrade_cb_ignore;

    /**
     * 升级
     */
    private CustomProgressWithPercentView dialog_upgrade_tv_upgrade_cp;

    /**
     * 关闭
     */
    private ImageView dialog_upgrade_iv_close;

    /**
     * 升级模型
     */
    private UpgradeModel upgradeModel;

    public UpgradeChoiceSilenceDialog(Activity context, UpgradeModel upgradeModel) {
        super(context, THEME);
        this.activity = context;
        this.upgradeModel = upgradeModel;
        initView();
    }

    public UpgradeChoiceSilenceDialog(Activity context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.activity = context;
        initView();
    }

    public UpgradeChoiceSilenceDialog(Activity context, int theme) {
        super(context, THEME);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_ugrade_choise);
        dialog_upgrade_tv_title = (TextView) findViewById(R.id.dialog_upgrade_tv_title);
        dialog_upgrade_tv_content = (TextView) findViewById(R.id.dialog_upgrade_tv_content);
        dialog_upgrade_tv_upgrade_cp = (CustomProgressWithPercentView) findViewById(R.id.dialog_upgrade_tv_upgrade_cp);
        dialog_upgrade_iv_close = (ImageView) findViewById(R.id.dialog_upgrade_iv_close);
        dialog_upgrade_cb_ignore = (CheckBox) findViewById(R.id.dialog_upgrade_cb_ignore);

        dialog_upgrade_tv_title.setText("发现新版本V" + upgradeModel.getVersion());
        dialog_upgrade_tv_content.setText(upgradeModel.getContent());

        //可选升级
        dialog_upgrade_iv_close.setVisibility(View.VISIBLE);
        dialog_upgrade_cb_ignore.setVisibility(View.VISIBLE);
        dialog_upgrade_iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_upgrade_cb_ignore.isChecked()){
                    UtilSharePreference.putString(getContext(),upgradeModel.getVersion(),upgradeModel.getVersion());
                } else {

                }
                String dateString = UtilDateString.format(new Date(), UtilDateString.FORMAT_SHORT);
                UtilSharePreference.putString(getContext(),dateString,dateString);
                dismiss();
            }
        });
        dialog_upgrade_tv_upgrade_cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                clickUpgrade();
                install(upgradeModel);
                dismiss();
            }
        });
        setParams();
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


    public void clickUpgrade(){
        //点击开始下载
        if (checkBySilence()) {
            dialog_upgrade_tv_upgrade_cp.setEnabled(false);
            statisticsDownload();
            Intent intent = new Intent(activity, UpgradeSilenceIntentService.class);
            intent.putExtra(UpgradeModel.TAG, upgradeModel);
            activity.startService(intent);
        }
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

    private void setParams() {
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 0.2f;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

}
