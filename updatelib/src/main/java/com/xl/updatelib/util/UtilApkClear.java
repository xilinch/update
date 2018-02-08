package com.xl.updatelib.util;

import android.os.Environment;


import com.xl.updatelib.constant.Constant;
import com.xl.updatelib.service.UpgradeIntentService;

import java.io.File;

/**
 * Created by xilinch on 2017/8/23.
 */

public class UtilApkClear {

    /**
     * 清除缓存
     * @return
     */
    public static boolean clearAPK(){
        boolean success = false;
        try{
            File file = Environment.getExternalStorageDirectory();
            if(file != null){
                String filePath = file.getAbsolutePath().concat(Constant.FILE_DIRECTORY );
                File deretory = new File(filePath);
                if(deretory != null && deretory.exists()){
                    for (File delFile:deretory.listFiles()) {
                        delFile.delete();
                    }
                    success = true;
                }
            }

        } catch (Exception exception) {
          exception.printStackTrace();
        }
        return success;
    }
}
