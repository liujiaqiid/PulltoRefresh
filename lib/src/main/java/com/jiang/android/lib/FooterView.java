package com.jiang.android.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by jiang on 16/8/17.
 */

public class FooterView extends BaseView {



    private Bitmap bg;
    private Bitmap line_left;
    private Bitmap line_right;
    private int line_left_x;
    private int line_left_y;
    private int line_right_x;
    private int line_right_y;
    private int bg_x;
    private int bg_y;
    private float line_left_progress;
    private float line_right_progress;
    private int MAX_PROGRESS = 270;
    private int RES_PROGRESS = 0;

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
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.boxes);
        line_left = BitmapFactory.decodeResource(getResources(), R.drawable.lid_l);
        line_right = BitmapFactory.decodeResource(getResources(), R.drawable.lid_r);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bg, bg_x, bg_y, null);
        canvas.save();
        canvas.clipRect(line_left_x, line_left_y - line_left.getWidth(), line_left_x + line_left.getWidth() * 2, line_left_y + line_left.getWidth());
        canvas.rotate(line_left_progress, line_left_x + line_left.getWidth(), line_left_y + line_left.getHeight());
        canvas.drawBitmap(line_left, line_left_x, line_left_y, null);
        canvas.restore();
        canvas.save();
        canvas.clipRect(line_right_x - line_right.getWidth(), line_right_y - line_right.getWidth(), line_right_x + line_right.getWidth(), line_right_y + line_right.getWidth());
        canvas.rotate(line_right_progress, line_right_x, line_right_y + line_right.getHeight());
        canvas.drawBitmap(line_right, line_right_x, line_right_y, null);
        canvas.restore();
    }



    @Override
    public void begin() {
    }


    @Override
    public void progress(float progress, float all) {
        int pro = MAX_PROGRESS - (int) (progress / (all / 2) * MAX_PROGRESS);
        int result = pro > RES_PROGRESS ? pro : RES_PROGRESS;
        result = result > 180 ? 180 : result;
        line_left_progress = result;
        line_right_progress = -result;
        Log.i(TAG, "progress: " + result);
        bg_x = getWidth() / 2 - bg.getWidth() / 2;
        bg_y = getMeasuredHeight() / 2 - bg.getHeight() / 2;
        line_left_x = getWidth() - getWidth() / 2 - bg.getWidth() / 2 - line_left.getWidth();
        line_left_y = getMeasuredHeight() - getMeasuredHeight() / 2 - bg.getHeight() / 2;
        line_right_x = getWidth() / 2 + bg.getWidth() / 2;
        line_right_y = getMeasuredHeight() - getMeasuredHeight() / 2 - bg.getHeight() / 2;
        Log.i(TAG, "progress: " + "left_x:" + line_left_x + "; left_y:" + line_left_y);
        Log.i(TAG, "progress: " + "right_x:" + line_right_x + "; right_y:" + line_right_y);
        invalidate();
    }

    @Override
    public void finishing(float progress, float all) {
        progress(progress, all);
    }

    @Override
    public void loading() {
        int result = 0;
        line_left_progress = result;
        line_right_progress = -result;
        bg_x = getWidth() / 2 - bg.getWidth() / 2;
        bg_y = getMeasuredHeight() / 2 - bg.getHeight() / 2;
        line_left_x = getWidth() - getWidth() / 2 - bg.getWidth() / 2 - line_left.getWidth();
        line_left_y = getMeasuredHeight() - getMeasuredHeight() / 2 - bg.getHeight() / 2;
        line_right_x = getWidth() / 2 + bg.getWidth() / 2;
        line_right_y = getMeasuredHeight() - getMeasuredHeight() / 2 - bg.getHeight() / 2;
        invalidate();
    }

    @Override
    public void normal() {
    }

}
