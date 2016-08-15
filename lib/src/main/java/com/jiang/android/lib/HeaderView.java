package com.jiang.android.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiang on 16/8/15.
 */

public class HeaderView extends View implements HeadListener {

    private Bitmap bitmap;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
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
