package com.jiang.android.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by jiang on 16/8/15.
 */

public class HeadFootView extends BaseView implements HeadFootListener {

    private Bitmap bitmap;
    private Paint mPaint;

    public HeadFootView(Context context) {
        this(context, null);
    }

    public HeadFootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private String text = "";
    private String text2 = "";

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dd);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(Utils.sp2px(getContext(), 20));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        float x = getWidth() / 2 - mPaint.measureText(text, 0, text.length()) / 2;
        float y = getHeight() / 2 - (mPaint.ascent() - mPaint.descent()) / 2;
        canvas.drawText(text, x, y, mPaint);

    }

    public void setText2Canvas(String text, String text2) {
        this.text = text;
        this.text2 = text2;
        Log.i(TAG, "setText2Canvas: " + text2);
        invalidate();


    }

    @Override
    public void begin() {
        setText2Canvas("开始下拉刷新...", null);
    }

    NumberFormat formatter = new DecimalFormat("0.00");

    @Override
    public void progress(float progress, float all) {
        Double x = new Double(1.0 * (progress / all));
        String xx = formatter.format(x);
        Float result = Float.parseFloat(xx) * 100;
        if (progress < all / 2) {
            setText2Canvas("继续下拉...", result + "%");
        } else {
            setText2Canvas("松开即可开始刷新...", result + "%");

        }
    }

    @Override
    public void finishing(float progress, float all) {
        Double x = new Double(1.0 * (progress / all));
        String xx = formatter.format(x);
        Float result = Float.parseFloat(xx) * 100;
        setText2Canvas("刷新结束中...", result + "%");
    }

    @Override
    public void loading() {
        setText2Canvas("刷新...", null);
    }

    @Override
    public void normal() {
        setText2Canvas("结束...", null);
    }


}
