package com.jiang.android.pulltorefresh;

import android.app.Application;

import com.jiang.android.lib.PullToRefreshLayout;
import com.jiang.android.pulltorefresh.view.FooterView;
import com.jiang.android.pulltorefresh.view.HeadView;

/**
 * Created by jiang on 16/8/18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PullToRefreshLayout.setHeadView(new HeadView(this));
        PullToRefreshLayout.setFootView(new FooterView(this));
        PullToRefreshLayout.setHeight(100);
        PullToRefreshLayout.setFoot(50);
    }
}
