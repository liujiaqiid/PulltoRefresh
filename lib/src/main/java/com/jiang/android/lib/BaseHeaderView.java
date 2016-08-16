package com.jiang.android.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiang on 16/8/16.
 */

public abstract class BaseHeaderView extends View implements HeadListener {
    public BaseHeaderView(Context context) {
        super(context);
    }

    public BaseHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
