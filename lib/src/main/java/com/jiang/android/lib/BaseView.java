package com.jiang.android.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiang on 16/8/16.
 */

public abstract class BaseView extends View implements HeadFootListener {
    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

     static final String TAG = "BaseView";
}
