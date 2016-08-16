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
     * 回调的精度,单位为px
     *
     * @param progress 当前高度
     * @param all      总高度   为默认高度的2倍
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
