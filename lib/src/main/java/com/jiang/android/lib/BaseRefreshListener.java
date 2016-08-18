package com.jiang.android.lib;

/**
 * Created by jiang on 16/8/18.
 */

public interface BaseRefreshListener {


    /**
     * 刷新
     */
    void refresh();

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 完成刷新
     */
    void finish();

    /**
     * 完成加载更多
     */
    void finishLoadMore();
}
