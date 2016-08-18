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
    private static int HEAD_HEIGHT = 100;
    private static int FOOT_HEIGHT = 100;
    private static int head_height;
    private static int head_height_2;
    private static int foot_height;
    private static int foot_height_2;

    private BaseView mHeadView;
    private BaseView mFootView;
    private boolean isRefresh;
    private boolean isLoadMore;
    private float mTouchY;
    private float mCurrentY;
    private View mChildView;

    private boolean canLoadMore = true;
    private boolean canRefresh = true;

    private BaseRefreshListener refreshListener;

    public void setRefreshListener(BaseRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }


    private void cal() {
        head_height = Utils.Dp2Px(getContext(), HEAD_HEIGHT);
        foot_height = Utils.Dp2Px(getContext(), FOOT_HEIGHT);
        head_height_2 = Utils.Dp2Px(getContext(), HEAD_HEIGHT * 2);
        foot_height_2 = Utils.Dp2Px(getContext(), FOOT_HEIGHT * 2);
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

    public static void setHeight(int height) {
        HEAD_HEIGHT = height;
    }

    public static void setFoot(int foot) {
        FOOT_HEIGHT = foot;
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
            mFootView = new FooterView(getContext());
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.BOTTOM;
        mFootView.setLayoutParams(layoutParams);
        if (mFootView.getParent() != null)
            ((ViewGroup) mFootView.getParent()).removeAllViews();
        addView(mFootView);
    }

    private void addHeadView() {
        if (mHeadView == null) {
            mHeadView = new HeadView(getContext());
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mHeadView.setLayoutParams(layoutParams);
        if (mHeadView.getParent() != null)
            ((ViewGroup) mHeadView.getParent()).removeAllViews();
        addView(mHeadView, 0);

    }

    public void setLoadMore(boolean enable) {
        canLoadMore = enable;
    }

    public void setRefresh(boolean enable) {
        canRefresh = enable;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canLoadMore && !canRefresh) return super.onInterceptTouchEvent(ev);
        if (isRefresh || isLoadMore) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mCurrentY;
                if (canRefresh) {
                    boolean canChildScrollUp = canChildScrollUp();
                    if (dy > 0 && !canChildScrollUp) {
                        mHeadView.begin();
                        return true;
                    }
                }
                if (canLoadMore) {
                    boolean canChildScrollDown = canChildScrollDown();
                    if (dy < 0 && !canChildScrollDown) {
                        mFootView.begin();
                        return true;
                    }
                }


        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefresh || isLoadMore) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dura = mCurrentY - mTouchY;
                if (dura > 0 && canRefresh) {
                    dura = Math.min(head_height_2, dura);
                    dura = Math.max(0, dura);
                    mHeadView.getLayoutParams().height = (int) dura;
                    ViewCompat.setTranslationY(mChildView, dura);
                    requestLayout();
                    mHeadView.progress(dura, head_height_2);
                } else {
                    if (canLoadMore) {
                        dura = Math.min(foot_height_2, Math.abs(dura));
                        dura = Math.max(0, Math.abs(dura));
                        mFootView.getLayoutParams().height = (int) dura;
                        ViewCompat.setTranslationY(mChildView, -dura);
                        requestLayout();
                        mFootView.progress(dura, foot_height_2);
                    }

                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currentY = event.getY();
                final int dy1 = (int) (currentY - mTouchY);
                if (dy1 > 0 && canRefresh) {
                    if (dy1 >= head_height) {
                        createAnimatorTranslationY(State.REFRESH, dy1 > head_height_2 ? head_height_2 : dy1, head_height, new CallBack() {
                            @Override
                            public void onSuccess() {
                                isRefresh = true;
                                if (refreshListener != null) {
                                    refreshListener.refresh();
                                }
                                mHeadView.loading();
                            }
                        });

                    } else if (dy1 > 0 && dy1 < head_height) {
                        setFinish(dy1, State.REFRESH);
                        mHeadView.normal();

                    }
                } else {
                    if (canLoadMore) {
                        if (Math.abs(dy1) >= foot_height) {
                            createAnimatorTranslationY(State.LOADMORE, Math.abs(dy1) > foot_height_2 ? foot_height_2 : Math.abs(dy1), foot_height, new CallBack() {
                                @Override
                                public void onSuccess() {
                                    isLoadMore = true;
                                    if (refreshListener != null) {
                                        refreshListener.loadMore();
                                    }
                                    mFootView.loading();
                                }
                            });

                        } else {
                            setFinish(Math.abs(dy1), State.LOADMORE);
                            mFootView.normal();
                        }
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

                if (state == State.REFRESH) {
                    mHeadView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mChildView, value);
                    if (purpose == 0) { //代表结束加载
                        mHeadView.finishing(value, head_height_2);
                    } else {
                        mHeadView.progress(value, head_height_2);
                    }
                } else {
                    mFootView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mChildView, -value);
                    if (purpose == 0) { //代表结束加载
                        mFootView.finishing(value, head_height_2);
                    } else {
                        mFootView.progress(value, foot_height_2);
                    }
                }
                if (value == purpose) {
                    if (calllBack != null)
                        calllBack.onSuccess();
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
                    mHeadView.progress(value, head_height_2);
                }

            }

        });
        anim.start();
    }


    /**
     * 自动下拉刷新
     */
    public void autoRefresh() {
        if (canRefresh)
            createAutoAnimatorTranslationY(mHeadView, 0, head_height, new CallBack() {
                @Override
                public void onSuccess() {
                    isRefresh = true;
                    if (refreshListener != null) {
                        refreshListener.refresh();
                    }
                }
            });
    }

    /**
     * 结束下拉刷新
     */
    private void setFinish(int height, @State.REFRESH_STATE final int state) {
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
                        refreshListener.finishLoadMore();
                    }
                }
            }
        });
    }

    public void setFinish(@State.REFRESH_STATE int state) {
        if (state == State.REFRESH) {
            if (mHeadView != null && mHeadView.getLayoutParams().height > 0 && isRefresh) {
                setFinish(head_height, state);
            }
        } else {
            //这里因为canLoadMore不能真正的标志能不能加载更多,比如:在loadMore回调里调用
            /**
             *   refreshLayout.setLoadMore(false);
             *  refreshLayout.setFinish(State.LOADMORE);
             *  会出现问题
             */
            if (mFootView != null && mFootView.getLayoutParams().height > 0 && isLoadMore) {
                setFinish(foot_height, state);
            }
        }
    }

    public interface CallBack {
        void onSuccess();
    }

}
