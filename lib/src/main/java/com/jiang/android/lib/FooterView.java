package com.jiang.android.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by jiang on 16/8/17.
 */

public class FooterView extends BaseView {

    private Bitmap bitmap;

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dd);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);

    }


    @Override
    public void begin() {
    }

    @Override
    public void progress(float progress, float all) {
    }

    @Override
    public void loading() {
    }

    @Override
    public void normal() {

    }
}
