package com.xl.updatelib.bean;

import java.io.Serializable;

/**
 * Created by xilinch on 18-2-7.
 */

public class UpgradeModel  implements Serializable {
    public static final String TAG = "UpgradeModel";


    //appName  版本名称
    //appType  客户端类型
    //clientUseragent  渠道号
    //clientVersion  客户端本号
    //downloadUrl   下载地址
    //effectiveTime
    //force   是否强制更新
    //id
    //minVersion
    //state
    //updateLog  版本更新日志
    //updateVersion  版本号

    private String title;

    private String content;

    /**
     * 0无升级  1 可选升级  2强制升级
     */
    private String upgradeFlag;

    private String version;

    /**
     * 下载地址
     */
    private String downloadUrl;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpgradeFlag() {
        return upgradeFlag;
    }

    public void setUpgradeFlag(String upgradeFlag) {
        this.upgradeFlag = upgradeFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return "UpgradeModel{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", upgradeFlag='" + upgradeFlag + '\'' +
                ", version='" + version + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}