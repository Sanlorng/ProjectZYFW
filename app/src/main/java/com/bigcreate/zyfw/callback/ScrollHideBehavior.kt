package com.bigcreate.zyfw.callback

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

class ScrollHideBehavior(context: Context, attrs: AttributeSet):CoordinatorLayout.Behavior<View>(context,attrs) {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return  axes == ViewCompat.SCROLL_AXIS_VERTICAL ||super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)

        if (dyConsumed > 0 ) {
                animateOut(child)
        }
        else if (dyConsumed < 0 ) {
                animateIn(child)
        }
    }

    private fun animateOut(fab:View) {
        if (fab is AppBarLayout)
            fab.animate().translationY(-(fab.height+ (fab.layoutParams as CoordinatorLayout.LayoutParams).topMargin + 24).toFloat()).setInterpolator(LinearInterpolator()).start()
        else
            fab.animate().translationY((fab.height+ (fab.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin).toFloat()).setInterpolator(LinearInterpolator()).start()
    }

    private fun animateIn(fab:View) {
        fab.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
    }
}