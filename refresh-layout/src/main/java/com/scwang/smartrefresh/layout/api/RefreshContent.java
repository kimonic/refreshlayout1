package com.scwang.smartrefresh.layout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 刷新内容组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshContent {
    /**
     * 微调控制项
     */
    void moveSpinner(int spinner);

    /**
     * 可以刷新
     */
    boolean canRefresh();

    /**
     * 可以加载
     */
    boolean canLoadmore();

    /**
     * 得到测量宽度
     */
    int getMeasuredWidth();

    int getMeasuredHeight();

    void measure(int widthSpec, int heightSpec);

    void layout(int left, int top, int right, int bottom);

    View getView();

    View getScrollableView();

    ViewGroup.LayoutParams getLayoutParams();

    void onActionDown(MotionEvent e);

    void onActionUpOrCancel();

    void fling(int velocity);

    /**设置组件*/
    void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
    /**初始化header和footer*/
    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    /**设置滚动边界策略*/
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);
    /**当内容未满时允许加载更多*/
    void setEnableLoadmoreWhenContentNotFull(boolean enable);
    /**完成后滚动内容*/
    AnimatorUpdateListener scrollContentWhenFinished(int spinner);
}
