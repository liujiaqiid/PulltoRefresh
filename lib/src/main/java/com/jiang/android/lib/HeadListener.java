package com.jiang.android.lib;

/**
 * Created by jiang on 16/8/15.
 */

public interface HeadListener {

    /**
     * 开始下拉
     */
    public void begin();


    /**
     * 下拉精度
     *
     * @param progress
     */
    public void progress(float progress, float all);

    /**
     * 下拉完毕
     */
    public void loading();

    /**
     * 看不见的状态
     */
    public void normal();

}
