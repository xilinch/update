package com.xl.updatelib.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;

    // toast重复显示不消失问题
    public static void toastShow(Context context, int arg) {
        if (toast == null) {
            toast = Toast.makeText(context, arg, Toast.LENGTH_SHORT);
        } else {
            // toast.cancel(); //取消后只会显示一次
            toast.setText(arg);
        }
        if (toast != null && context != null) {
            toast.show();
        }
    }

    // toast重复显示不消失问题
    public static void toastShowLong(Context context, String arg) {
        if (toast == null) {
            toast = Toast.makeText(context, arg, Toast.LENGTH_LONG);
        } else {
            // toast.cancel(); //取消后只会显示一次
            toast.setText(arg);
        }
        if (toast != null && context != null) {
            toast.show();
        }
    }

    // toast重复显示不消失问题
    public static void toastShowLong(Context context, int arg) {
        if (toast == null) {
            toast = Toast.makeText(context, arg, Toast.LENGTH_LONG);
        } else {
            // toast.cancel(); //取消后只会显示一次
            toast.setText(arg);
        }
        if (toast != null && context != null) {
            toast.show();
        }
    }

    // toast重复显示不消失问题
    public static void toastShow(Context context, String arg) {
        if (toast == null) {
            toast = Toast.makeText(context, arg, Toast.LENGTH_SHORT);
        } else {
            // toast.cancel(); //取消后只会显示一次
            toast.setText(arg);
        }
        if (toast != null && context != null) {
            toast.show();
        }
    }


}
