package com.xl.update.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xl.updatelib.UpgradeActivity;
import com.xl.updatelib.UpgradeSilenceActivity;
import com.xl.updatelib.bean.UpgradeModel;
import com.xl.updatelib.util.NetworkUtils;
import com.xl.updatelib.util.UtilDateString;
import com.xl.updatelib.util.UtilSharePreference;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xilinch on 2016/6/6.
 * 检查升级
 */
public class UtilCheckUpgrade {
    private static final String TAG = "UtilCheckUpgrade";

    /**
     * 普通的检查升级
     *
     * @param activity
     * @param url      升级地址
     */
    public static void checkUpgradeV2(final Activity activity, String url) {
        if (activity == null || !NetworkUtils.isNetworkAvailable(activity)) {
//            ToastUtils.toastShow(activity, "当前网络不可用,请检查网络!");
            return;
        }
        Log.d(TAG, "请求地址 url:" + url);
        try {
            JSONObject jsonObject = createUpgradeHttpJSON(activity);
            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder().url(url).post(requestBody).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                    Log.e("my", "onFailure:");

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        if (response != null) {
                            String result = response.body().string();
                            Log.e("my", "response:" + result);
                            JSONObject resultJsonObject = new JSONObject(result);
                            if (resultJsonObject != null) {
                                String code = resultJsonObject.optString("code");
                                String message = resultJsonObject.optString("message");
                                JSONObject data = resultJsonObject.optJSONObject("data");
                                if (data != null && activity != null) {
                                    String clientVersion = data.optString("clientVersion");
                                    boolean force = data.optBoolean("force");
                                    String updateLog = data.optString("updateLog");
                                    String downloadUrl = data.optString("downloadUrl");
                                    downloadUrl = "http://wxz.myapp.com/16891/07360129022C5F0B05E381A36ECC66F4.apk?fsname=com.nfdaily.nfplus_4.0.0_4000.apk";

                                    UpgradeModel upgradeModel = new UpgradeModel();
                                    upgradeModel.setContent(updateLog);
                                    if (force) {
                                        upgradeModel.setUpgradeFlag("2");
                                    } else {
                                        upgradeModel.setUpgradeFlag("1");
                                    }
                                    upgradeModel.setVersion(clientVersion);
                                    upgradeModel.setDownloadUrl(downloadUrl);

                                    Intent intent = new Intent(activity, UpgradeActivity.class);
                                    intent.putExtra(UpgradeModel.TAG, upgradeModel);
                                    //可选升级，需要判断是否已忽略版本
                                    if ("1".equals(upgradeModel.getUpgradeFlag())) {
                                        //如果不是强制升级，判断是否是已忽略版本
                                        String version = upgradeModel.getVersion();
                                        String ignoreVersion = UtilSharePreference.getString(activity, version, "");
                                        String dateString = UtilDateString.format(new Date(), UtilDateString.FORMAT_SHORT);
                                        String ignoreDateString = UtilSharePreference.getString(activity, dateString, "");
//                                    String ignoreDateString = "";
                                        Log.i(TAG, "版本升级：ignoreVersion:" + ignoreVersion + "  ignoreDateString:" + ignoreDateString);
                                        if (NetworkUtils.isWifi(activity)) {
                                            Log.e(TAG, "is wifi:" + upgradeModel.toString());
                                            //wifi下静默下载
                                            Intent startIntent = new Intent();
                                            startIntent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_START);
                                            startIntent.putExtra(UpgradeModel.TAG, upgradeModel);
                                            if (activity != null) {
                                                activity.sendBroadcast(startIntent);
                                            }

                                        } else {
                                            if (TextUtils.isEmpty(ignoreVersion) && TextUtils.isEmpty(ignoreDateString)) {
                                                activity.startActivity(intent);
                                            } else {
                                                //说明已经忽略
                                                Log.i(TAG, "忽略版本升级：" + version);
                                            }
                                        }

                                    } else if ("2".equals(upgradeModel.getUpgradeFlag())) {
                                        Log.i(TAG, "强制升级：" + upgradeModel.getVersion());
                                        activity.startActivity(intent);

                                    } else {
                                        //没有升级

                                    }

                                } else {
                                    //do nothing
                                    //没有升级

                                }
                            } else {
                                //DO NOTHING
                            }
                        }

                    } catch (Exception excetion) {
                        excetion.printStackTrace();
                    } finally {

                    }
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }


    /**
     * 构建请求的内容
     *
     * @param activity
     * @return
     */
    private static JSONObject createUpgradeHttpJSON(Context activity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("insideVersion", "4000");
            jsonObject.put("currentVersion", "4.0.0");
//            String channel = "unkonwn";
//            jsonObject.put("clientUseragent", channel);
            jsonObject.put("appType", "0");
            jsonObject.put("netstate", NetworkUtils.getNetState(activity));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return jsonObject;
        }
    }


}
