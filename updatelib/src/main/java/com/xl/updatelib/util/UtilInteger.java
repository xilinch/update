package com.xl.updatelib.util;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by xilinch on 2017/4/11.
 * 封装对integer的操作
 */

public class UtilInteger {

    /**
     * string到int的转化
     * 缺省值为-1
     * @param parseStr
     * @return
     */
    public static int parseInt(String parseStr){
        int defaultValue = -1;
        return parseInt(parseStr,defaultValue);
    }

    /**
     * string到int的转化
     * @param parseStr
     * @return
     */
    public static int parseInt(String parseStr, int defaultValue){
        int result = defaultValue;
        if(!TextUtils.isEmpty(parseStr)){
            try{
                result = Integer.parseInt(parseStr);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return result;
    }

    /**
     * string到long的转化
     * 缺省值为-1
     * @param parseStr
     * @return
     */
    public static long parseLong(String parseStr){
        long defaultValue = -1;
        return parseLong(parseStr,defaultValue);
    }

    /**
     * string到long的转化
     * @param parseStr
     * @return
     */
    public static long parseLong(String parseStr, long defaultValue){
        long result = defaultValue;
        if(!TextUtils.isEmpty(parseStr)){
            try{
                result = Long.parseLong(parseStr);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return result;
    }


}
