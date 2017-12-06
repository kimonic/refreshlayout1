package com.scwang.smartrefresh.layout.util;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryUtil {

    //<editor-fold desc="滚动判断">
    public static boolean canRefresh(View targetView, MotionEvent event) {
        if (canScrollUp(targetView) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        if (targetView instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTransformedTouchPointInView(viewGroup, child, event.getX(), event.getY(), point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return canRefresh(child, event);
                }
            }
        }
        return true;
    }

    public static boolean canLoadmore(View targetView, MotionEvent event) {
        if (!canScrollDown(targetView) && canScrollUp(targetView) && targetView.getVisibility() == View.VISIBLE) {
            return true;
        }
        if (targetView instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup, child, event.getX(), event.getY(), point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return canLoadmore(child, event);
                }
            }
        }
        return false;
    }

    /**控件是否可下滑*/
    public static boolean canScrollDown(View targetView, MotionEvent event) {
        if (canScrollDown(targetView) && targetView.getVisibility() == View.VISIBLE) {//控件显示并且可下滑
            return true;
        }
        if (targetView instanceof ViewGroup && event != null) {//targetview 是viewgroup并且event!=null
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();//viewgroup中的子view的个数
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {//遍历子view
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup, child, event.getX(), event.getY(), point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return canScrollDown(child, event);
                }
            }
        }
        return false;
    }

    /**
     * 判断目标view是否可以向上滚动
     */
    public static boolean canScrollUp(View targetView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return targetView.getScrollY() > 0;
            }
        } else {
            return targetView.canScrollVertically(-1);
        }
    }

    /**
     * 判断目标view是否可以向下滚动
     */
    public static boolean canScrollDown(View targetView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else {
                return targetView.getScrollY() < 0;
            }
        } else {
            return targetView.canScrollVertically(1);
        }
    }

    //</editor-fold>

    //<editor-fold desc="transform Point">

    /**点击的坐标点是否在child内*/
    public static boolean isTransformedTouchPointInView(ViewGroup group, View child, float x, float y, PointF outLocalPoint) {
        if (child.getVisibility() != View.VISIBLE) {//控件不显示
            return false;
        }
        final float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        transformPointToViewLocal(group, child, point);
        final boolean isInView = pointInView(child, point[0], point[1], 0);
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0] - x, point[1] - y);
        }
        return isInView;
    }

    /**判断点击坐标点是否在view内*/
    public static boolean pointInView(View view, float localX, float localY, float slop) {
        final float left = /*Math.max(view.getPaddingLeft(), 0)*/ -slop;
        final float top = /*Math.max(view.getPaddingTop(), 0)*/ -slop;
        final float width = view.getWidth()/* - Math.max(view.getPaddingLeft(), 0) - Math.max(view.getPaddingRight(), 0)*/;
        final float height = view.getHeight()/* - Math.max(view.getPaddingTop(), 0) - Math.max(view.getPaddingBottom(), 0)*/;
        return localX >= left && localY >= top && localX < ((width) + slop) &&
                localY < ((height) + slop);
    }

    /**将viewgroup中的点击坐标点(viewgroup左上点为(0,0))转化为相对于view的坐标点(view的左上点为(0,0))*/
    public static void transformPointToViewLocal(ViewGroup group, View child, float[] point) {
        point[0] += group.getScrollX() - child.getLeft();//viewgroup相对于屏幕左边的位置减去child相对于viewgroup左边的位置
        point[1] += group.getScrollY() - child.getTop();
    }
    //</editor-fold>
}
