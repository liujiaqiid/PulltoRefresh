package com.jiang.android.lib;

/**
 * Created by jiang on 16/8/15.
 */

public interface HeadFootListener {

    /**
     * 开始下拉
     */
    void begin();


    /**
     * 回调的精度,单位为px
     *
     * @param progress 当前高度
     * @param all      总高度   为默认高度的2倍
     */
    void progress(float progress, float all);

    void finishing(float progress, float all);

    /**
     * 下拉完毕
     */
    void loading();

    /**
     * 看不见的状态
     */
    void normal();

}
