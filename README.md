# update
### 主要功能
- 通用升级lib；
- 支持可选升级、强制升级、静默升级；
- 支持断点续传；
### 基本用法
## 1.引入lib作为model,或者在项目的依赖中加入 
dependencies {
   compile 'com.xl.updatelib:updateLib:1.0.0'
}
## 2.在manifest声明权限：

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
## 3.在manifest中声明组件
        <!--升级组件注册-->
        <activity
            android:name="com.xl.updatelib.UpgradeActivity"
            android:launchMode="singleTask"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden|adjustResize"/>
        <activity
            android:name="com.xl.updatelib.UpgradeSilenceActivity"
            android:launchMode="singleTask"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden|adjustResize"/>
        <service android:name="com.xl.updatelib.service.UpgradeIntentService"/>
        <service android:name="com.xl.updatelib.service.UpgradeSilenceIntentService"/>
        <!--升级组件注册 end -->
## 4.拷贝类UtilCheckUpgrade到主项目中
## 5.在需要调用升级的activity中 生命周期方法执行
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //执行升级请求
        UtilCheckUpgrade.checkUpgradeV2(this, url);
        //注册广播
        initUpgradeSilenceReciver();

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissUpgradeChoiceSilenceDialog();
        unregisterReceiver(myBroadcastReciver);
    }
    
    
    并且拷贝下面代码：


    private UpgradeChoiceSilenceDialog upgradeChoiceSilenceDialog;
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
    -----end

## 6.接下来请开始调试接口数据。

# 特性说明
## 1.修改升级包路径 Constant.FILE_DIRECTORY，默认存放路径为SD卡，/upgrade/apk/，
    可以使用方法调整  UpdateManager.setAPKSaveDirectry("/test/apk/");此方法要在1.0.1上使用
## 2.勾选不再提示或者点关闭按钮，该版本该天不再提示升级



# 效果演示

![](https://github.com/xilinch/update/blob/master/record/ezgif-1-75ff377f0a.gif)
(https://github.com/xilinch/update/blob/master/record/ezgif-1-75ff377f0a.gif "效果1")


![](https://github.com/xilinch/update/blob/master/record/ezgif-1-3749d6972e.gif)
(https://github.com/xilinch/update/blob/master/record/ezgif-1-3749d6972e.gif "效果2")






