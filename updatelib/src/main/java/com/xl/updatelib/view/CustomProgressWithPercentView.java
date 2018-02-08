package com.xl.updatelib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xl.updatelib.R;
import com.xl.updatelib.util.DensityUtils;
import com.xl.updatelib.util.UtilInteger;


/**
 * Created by xilinch on 2016/6/14.
 */
public class CustomProgressWithPercentView extends FrameLayout {

    /**
     * 动画开始进度
     */
    private int progressAnimationStart = 0;
    /**
     * 显示速度
     */
    private int progressAnimationStartRate = 0;
    /**
     * 进度条
     */
    private ProgressBar progressBar;
    /**
     * 文本进度
     */
    private TextView tv_percent;
    /**
     * 上下文环境
     */
    private Context context;
    /**
     * 当前进度
     */
    private int currentProgress;
    /***
     * 动画进度
     */
    private int anima_process;
    /**
     * 最大进度
     */
    private static int MAX = 100;
    /**
     * 动态计算显示的宽度位置
     */
    private int width;
    /**
     * 执行子线程
     **/
    private Thread myRunnable;
    /**
     * 更新动画执行
     */
    private Runnable updateRunnable;
    /***
     * 是否停止动画
     */
    private boolean stopAnimation = false;

    /**
     * get方法
     *
     * @return 进度条控件
     */
    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    /**
     * 返回进度文本控件
     *
     * @return
     */
    public TextView getTextView() {
        return this.tv_percent;
    }

    /**
     * 当前进度
     *
     * @return
     */
    public int getCurrentProgress() {
        return this.currentProgress;
    }


    public CustomProgressWithPercentView(Context context) {
        this(context, null);
    }

    public CustomProgressWithPercentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressWithPercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        setWidgets();

        init();


    }

    /**
     * 初始化控件
     */
    private void setWidgets() {
        progressBar = new ProgressBar(context, null, R.style.customProgressbar);
        progressBar.setMax(MAX);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.dd_layer_list_progressbar));
//        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.dd_layer_list_f64e45_progress_ddd_bg_progressbar));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtils.dipTopx(context, 48));
        addView(progressBar, layoutParams);

        tv_percent = new TextView(context);
        tv_percent.setGravity(Gravity.CENTER);
        tv_percent.setText("立即升级");

        progressBar.setSecondaryProgress(100);
        progressBar.setProgress(0);

        tv_percent.setTextColor(getResources().getColor(R.color.white));
        LayoutParams layoutParams1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;

        addView(tv_percent, layoutParams1);

    }

    /**
     * 初始化基础状态
     */
    private void init() {
        currentProgress = 0;

        progressAnimationStart = currentProgress * progressAnimationStartRate / 10;
        if (progressBar != null) {
            progressBar.setProgress(0);
            progressBar.setMax(MAX);
        }


    }


    /**
     * 显示进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        currentProgress = progress;
        progressAnimationStart = currentProgress * progressAnimationStartRate / 10;
        setPercentFormat(progress);
        progressBar.setSecondaryProgress(progress);

    }

    /**
     * 动画runnable
     */
    private void initUpdateRunnable() {
        if (updateRunnable == null && !stopAnimation) {
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (progressBar != null && !stopAnimation) {
                        if(anima_process == 100){
                            progressBar.setProgress(0);
                        } else {

                            progressBar.setProgress(anima_process);
                        }

                    }
                }
            };
        }
    }

    /**
     * 是否需要停止动画
     *
     * @param stopAnimation true停止，false 不需要停止
     */
    private void setStopAnimation(boolean stopAnimation) {
        this.stopAnimation = stopAnimation;
    }

    /**
     * 进度动画显示规则:
     * 显示速度 = 1500/进度+1
     * 进度条进度显示使用子线程更新
     */
    private void startRepeatAnimationRunnable() {
        if (myRunnable == null) {
            myRunnable = new Thread() {
                @Override
                public void run() {
                    //动画
                    while (anima_process != 100 && !stopAnimation) {
                        if (anima_process == 0) {
                            anima_process = progressAnimationStart;
                        }

                        anima_process = ((++anima_process) % (currentProgress + 1));
                        initUpdateRunnable();
                        post(updateRunnable);

                        try {
                            sleep(1500 / (currentProgress + 1));
                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }
                    if(anima_process == 100){

                    }
                }
            };

            myRunnable.start();
        }
    }

    /**
     * show progress
     *
     * @param progress
     */
    public void setProgress(String progress) {
        try {
            int int_progress = UtilInteger.parseInt(progress);
            setProgress(int_progress);


        } catch (Exception e) {

        } finally {

        }

    }

    /**
     * set percent textview text format
     * ex: 15%
     */
    public void setPercentFormat(int percent) {

        if (tv_percent != null) {
            if(percent == 100){

                tv_percent.setText("点击安装");

            } else {

//                tv_percent.setText("正在下载" +);
                tv_percent.setText("正在下载(" +percent + "%)");
            }
//            tv_percent.setText("正在下载(" +percent + "%)");
            layoutPercentTextView(percent);
        }
    }

    /**
     * set the text of textview  format
     * ex: 15%
     */
    public void setPercentFormat(String percent) {
        if (tv_percent != null) {
            tv_percent.setText(percent + "%");
            try {
                int int_percent = UtilInteger.parseInt(percent);
                layoutPercentTextView(int_percent);

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * calcute textview layout width
     * 计算进度文本宽度
     *
     * @param percent
     */
    private void layoutPercentTextView(int percent) {
        startRepeatAnimationRunnable();
        width = getWidth();

//        if (width != 0) {
//            if (percent < 50) {
//                int padding = width * percent / 100 + 10;
//                tv_percent.setGravity(Gravity.CENTER_VERTICAL);
//                tv_percent.setPadding(padding, 0, 0, 0);
//            } else {
//
//                int padding = width - width * percent / 100 + 20;
//                tv_percent.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//                tv_percent.setPadding(0, 0, padding, 0);
//            }
//        }

    }


}
