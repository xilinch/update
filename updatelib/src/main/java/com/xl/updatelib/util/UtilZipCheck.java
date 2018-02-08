package com.xl.updatelib.util;

import android.util.Log;

import java.io.File;
import java.util.zip.ZipFile;

/**
 * Created by xilinch on 18-2-7.
 */

public class UtilZipCheck {

    public static boolean isErrorZip(String filePath) {
        File file = new File(filePath);
        boolean isRight = true;
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(file);
        } catch (Exception var44) {
            var44.printStackTrace();
            Log.e("my", "installAPK zipFile error");
            isRight = false;
            if(file != null && file.exists()) {
                file.delete();
            }
        } finally {
            if(zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception var42) {

                    var42.printStackTrace();
                } finally {
                    zipFile = null;
                }
            }

        }
        return isRight;
    }
}
