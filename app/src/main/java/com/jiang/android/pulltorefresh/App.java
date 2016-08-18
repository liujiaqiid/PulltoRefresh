package com.jiang.android.pulltorefresh;

import android.app.Application;

import com.jiang.android.lib.PullToRefreshLayout;

/**
 * Created by jiang on 16/8/18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PullToRefreshLayout.setHeight(100);
        PullToRefreshLayout.setFoot(100);
    }
}
