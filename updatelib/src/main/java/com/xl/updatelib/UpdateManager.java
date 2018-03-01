package com.xl.updatelib;

import com.xl.updatelib.constant.Constant;

/**
 * Created by xilinch on 18-3-1.
 * 基础管理类
 */

public class UpdateManager {


    /**
     * 设置apk下载存放的路径。  保存在sd上
     * @param sdCardDirectory 设置在sd卡上的目录，如 /yourAppName/apk/
     */
    public static void setAPKSaveDirectry(String sdCardDirectory){
        Constant.FILE_DIRECTORY = sdCardDirectory;

    }
}
