package com.jiang.android.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by jiang on 16/8/15.
 */

public class PullToRefreshLayout extends FrameLayout {

    private static final long ANIM_TIME = 250;
    private static int hIGHER_HEAD_HEIGHT = 100;
    private static int FOOTER_HEIGHT = 100;
    private int HEIGHT;
    private int HEIGHT_2;
    private int FOO_HEIGHT;
    private int FOO_HEIGHT_2;

    private BaseView mHeadView;
    private BaseView mFootView;
    private boolean isRefresh;
    private boolean isLoadMore;
    private float mTouchY;
    private float mCurrentY;
    private View mChildView;

    private RefreshListener refreshListener;

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }


    private void cal() {
        HEIGHT = Utils.Dp2Px(getContext(), hIGHER_HEAD_HEIGHT);
        FOO_HEIGHT = Utils.Dp2Px(getContext(), FOOTER_HEIGHT);
        HEIGHT_2 = Utils.Dp2Px(getContext(), hIGHER_HEAD_HEIGHT * 2);
        FOO_HEIGHT_2 = Utils.Dp2Px(getContext(), FOOTER_HEIGHT * 2);
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cal();
        int count = getChildCount();
        if (count != 1) {
            new IllegalArgumentException("child only can be one");
        }
    }

    public void setHeadView(BaseView view) {
        mHeadView = view;
    }

    public void setFootView(BaseView view) {
        mFootView = view;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mChildView = getChildAt(0);
        addHeadView();
        addFootView();

    }

    private void addFootView() {
        if (mFootView == null) {
            setFootView(new FooterView(getContext()));
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.BOTTOM;
        mFootView.setLayoutParams(layoutParams);
        addView(mFootView);
    }

    private void addHeadView() {
        if (mHeadView == null) {
            setHeadView(new HeadView(getContext()));
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mHeadView.setLayoutParams(layoutParams);
        addView(mHeadView, 0);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //   if (isRefresh || isLoadMore) return false;
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
                boolean canChildScrollDown = canChildScrollDown();
                if (dy < 0 && !canChildScrollDown) {
                    mFootView.begin();
                    return true;
                }


        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if (isRefresh || isLoadMore) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dura = mCurrentY - mTouchY;
                if (dura > 0) {
                    dura = Math.min(HEIGHT_2, dura);
                    dura = Math.max(0, dura);
                    mHeadView.getLayoutParams().height = (int) dura;
                    ViewCompat.setTranslationY(mChildView, dura);
                    requestLayout();
                    mHeadView.progress(dura, HEIGHT_2);
                } else {
                    dura = Math.min(FOO_HEIGHT_2, Math.abs(dura));
                    dura = Math.max(0, Math.abs(dura));
                    mFootView.getLayoutParams().height = (int) dura;
                    ViewCompat.setTranslationY(mChildView, -dura);
                    requestLayout();
                    mFootView.progress(dura, FOO_HEIGHT_2);

                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currentY = event.getY();
                final int dy1 = (int) (currentY - mTouchY);
                if (dy1 >= HEIGHT) {
                    createAnimatorTranslationY(State.REFRESH, dy1 > HEIGHT_2 ? HEIGHT_2 : dy1, HEIGHT, new CallBack() {
                        @Override
                        public void onSuccess() {
                            isRefresh = true;
                            if (refreshListener != null) {
                                refreshListener.onRefresh();
                            }
                            mHeadView.loading();
                        }
                    });


                } else if (dy1 > 0 && dy1 < HEIGHT) {
                    setFinish(dy1, State.REFRESH);
                    mHeadView.normal();
                } else if (dy1 < 0) {
                    if (Math.abs(dy1) >= FOO_HEIGHT) {
                        createAnimatorTranslationY(State.LOADMORE, Math.abs(dy1) > FOO_HEIGHT_2 ? FOO_HEIGHT_2 : Math.abs(dy1), FOO_HEIGHT, new CallBack() {
                            @Override
                            public void onSuccess() {
                                isLoadMore = true;
                                if (refreshListener != null) {
                                    refreshListener.onLoadMore();
                                }
                                mFootView.loading();
                            }
                        });

                    } else {
                        setFinish(Math.abs(dy1), State.LOADMORE);
                        mFootView.normal();
                    }
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

    public boolean canChildScrollDown() {
        if (mChildView == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }

            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    /**
     * 创建动画
     */
    public void createAnimatorTranslationY(@State.REFRESH_STATE final int state, final int start, final int purpose, final CallBack calllBack) {
        final ValueAnimator anim;
        anim = ValueAnimator.ofInt(start, purpose);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (value == purpose) {
                    if (calllBack != null)
                        calllBack.onSuccess();
                } else {
                    if (state == State.REFRESH) {
                        mHeadView.getLayoutParams().height = value;
                        ViewCompat.setTranslationY(mChildView, value);
                        mHeadView.progress(start, HEIGHT_2);
                    } else {
                        mFootView.getLayoutParams().height = value;
                        ViewCompat.setTranslationY(mChildView, -value);
                        mFootView.progress(start, FOO_HEIGHT_2);

                    }
                }
                requestLayout();


            }

        });
        anim.start();
    }

    /**
     * 用于自动刷新的动画
     *
     * @param v
     * @param start
     * @param purpose
     * @param calllBack
     */
    public void createAutoAnimatorTranslationY(final View v, final int start, final int purpose, final CallBack calllBack) {
        final ValueAnimator anim = ValueAnimator.ofInt(start, purpose);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                v.getLayoutParams().height = value;
                ViewCompat.setTranslationY(mChildView, value);
                requestLayout();
                if (value == 0) {
                    mHeadView.begin();
                } else if (value == purpose) {
                    mHeadView.loading();
                    if (calllBack != null)
                        calllBack.onSuccess();
                } else {
                    mHeadView.progress(start, HEIGHT_2);
                }

            }

        });
        anim.start();
    }


    public interface CallBack {
        void onSuccess();
    }

    /**
     * 自动下拉刷新
     */
    public void autoRefresh() {
        createAutoAnimatorTranslationY(mHeadView, 0, HEIGHT, new CallBack() {
            @Override
            public void onSuccess() {
                isRefresh = true;
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            }
        });
    }

    /**
     * 结束下拉刷新
     */
    public void setFinish(int height, @State.REFRESH_STATE final int state) {
        createAnimatorTranslationY(state, height, 0, new CallBack() {
            @Override
            public void onSuccess() {
                if (state == State.REFRESH) {
                    isRefresh = false;
                    if (refreshListener != null) {
                        refreshListener.finish();
                    }
                } else {
                    isLoadMore = false;
                    if (refreshListener != null) {
                        refreshListener.onFinishLoadMore();
                    }
                }
            }
        });
    }

    public void setFinish(@State.REFRESH_STATE int state) {
        int height;
        if (state == State.REFRESH) {
            height = HEIGHT;
        } else {
            height = FOO_HEIGHT;
        }
        setFinish(height, state);
    }


}
