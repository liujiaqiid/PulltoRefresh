package com.jiang.android.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

/**
 * Created by jiang on 16/8/15.
 */

public class PullToRefreshLayout extends LinearLayout {

    private static final long ANIM_TIME = 250;
    int hIGHER_HEAD_HEIGHT = 100;
    private int HEIGHT;
    private int HEIGHT_2;

    private BaseHeaderView mHeadView;
    private boolean isRefresh;
    private float mTouchY;
    private float mCurrentY;
    private View mChildView;

    private RefreshListener refreshListener;

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        init();
    }

    private void init() {
        setHeadView(new HeaderView(getContext()));
        HEIGHT = Utils.Dp2Px(getContext(), hIGHER_HEAD_HEIGHT);
        HEIGHT_2 = Utils.Dp2Px(getContext(), hIGHER_HEAD_HEIGHT * 2);
        int count = getChildCount();
        if (count != 1) {
            new IllegalArgumentException("child only can be one");
        }
    }

    public void setHeadView(BaseHeaderView view) {
        mHeadView = view;
        invalidate();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        mChildView = getChildAt(0);

        addHeadView();

    }

    private void addHeadView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mHeadView.setLayoutParams(layoutParams);
        addView(mHeadView, 0);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefresh) return false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mCurrentY;
                boolean canChildScrollUp = canChildScrollUp();
                if (dy > 0 && !canChildScrollUp) {
                    mHeadView.begin();
                    return true;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefresh) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dura = mCurrentY - mTouchY;
                dura = Math.min(HEIGHT_2, dura);
                dura = Math.max(0, dura);
                mHeadView.getLayoutParams().height = (int) dura;
                mHeadView.requestLayout();
                mHeadView.progress(dura, HEIGHT_2);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currentY = event.getY();
                final int dy1 = (int) (currentY - mTouchY);

                if (dy1 > HEIGHT) {

                    createAnimatorTranslationY(mHeadView, dy1 > HEIGHT_2 ? HEIGHT_2 : dy1, HEIGHT, new CallBack() {
                        @Override
                        public void onSuccess() {
                            mHeadView.getLayoutParams().height = HEIGHT;
                            mHeadView.requestLayout();
                            isRefresh = true;
                            if (refreshListener != null) {
                                refreshListener.onRefresh();
                            }
                            mHeadView.loading();
                        }
                    });


                } else {
                    setFinish(dy1);
                    mHeadView.normal();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 一次事件结束,还原所有的值
     */
    private void reset() {

        mCurrentY = 0;
        mTouchY = 0;
    }

    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    public void createAnimatorTranslationY(final View v, final int start, final int purpose, final CallBack calllBack) {
        final ValueAnimator anim = ValueAnimator.ofInt(start, purpose);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (value == purpose) {
                    if (calllBack != null)
                        calllBack.onSuccess();
                } else {
                    v.getLayoutParams().height = value;
                    v.requestLayout();
                }
            }

        });
        anim.start();
    }

    public void createAutoAnimatorTranslationY(final View v, final int start, final int purpose, final CallBack calllBack) {
        final ValueAnimator anim = ValueAnimator.ofInt(start, purpose);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (value == 0) {
                    mHeadView.begin();
                } else if (value == purpose) {
                    mHeadView.loading();
                    if (calllBack != null)
                        calllBack.onSuccess();
                } else {
                    mHeadView.progress(start, purpose);
                    v.getLayoutParams().height = value;
                    v.requestLayout();
                }
            }

        });
        anim.start();
    }


    public interface CallBack {
        void onSuccess();
    }

    public void autoRefresh() {
        createAutoAnimatorTranslationY(mHeadView, 0, HEIGHT, new CallBack() {
            @Override
            public void onSuccess() {
                mHeadView.getLayoutParams().height = HEIGHT;
                isRefresh = true;
                mHeadView.requestLayout();
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            }
        });
    }

    public void setFinish(int height) {

        createAnimatorTranslationY(mHeadView, height, 0, new CallBack() {
            @Override
            public void onSuccess() {
                mHeadView.getLayoutParams().height = 0;
                isRefresh = false;
                mHeadView.requestLayout();
                if (refreshListener != null) {
                    refreshListener.finish();
                }
            }
        });


    }

    public void setFinish() {
        setFinish(HEIGHT);
    }
}
