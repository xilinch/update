package com.xl.updatelib.listener;

/**
 * Created by xilinch on 18-2-8.
 */

public interface OnDownloadListener {

    void onDownloadSuccess();

    void onDownloading(int var1);

    void onDownloadFailed();
}
