package com.bigcreate.zyfw.callback

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class PinnedBottomBehavior(context: Context, attributeSet: AttributeSet) : AppBarLayout.ScrollingViewBehavior(context, attributeSet) {
    private var appBarLayout: AppBarLayout? = null
    private var onAnimationRunnablePosted = false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        appBarLayout?.apply {
            startAnimationRunnable(child, this)
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        appBarLayout?.apply {
            val bottomPadding = calculateBottomPadding(this)
            if (bottomPadding != child.paddingBottom) {
                child.setPadding(
                        child.paddingLeft,
                        child.paddingTop,
                        child.paddingRight,
                        bottomPadding)
            }
            startAnimationRunnable(child, this)
        }
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val result = super.onDependentViewChanged(parent, child, dependency)
        if (appBarLayout == null)
            appBarLayout = dependency as AppBarLayout
        val bottomPadding = calculateBottomPadding(appBarLayout)
        val paddingChanged = bottomPadding != child.paddingBottom
        if (paddingChanged) {
            child.setPadding(
                    child.paddingLeft,
                    child.paddingTop,
                    child.paddingRight,
                    bottomPadding)
            child.requestLayout()
        }
//        if (paddingChanged)
        startAnimationRunnable(child, dependency)
        return paddingChanged || result
    }

    private fun calculateBottomPadding(dependency: AppBarLayout?): Int {
//        Log.e("appbar",(dependency != null).toString())
        return dependency!!.totalScrollRange + dependency.top
    }

    private fun startAnimationRunnable(child: View, dependency: View) {
        if (onAnimationRunnablePosted)
            return

        val onPostChildTop = child.top
        val onPostDependencyTop = dependency.top
        onAnimationRunnablePosted = true
        // Start looking for changes at the beginning of each animation frame. If there are any changes, we have to
        // ensure that layout is run again so that we can update the padding to take the changes into account.
        child.postOnAnimation(object : Runnable {
            private val MAX_COUNT_OF_FRAMES_WITH_NO_CHANGES = 5
            private var previousChildTop = onPostChildTop
            private var previousDependencyTop = onPostDependencyTop
            private var countOfFramesWithNoChanges: Int = 0

            override fun run() {
                // Make sure we request a layout at the beginning of each animation frame, until we notice a few
                // frames where nothing changed.
                val currentChildTop = child.top
                val currentDependencyTop = dependency.top
                var hasChanged = false

                if (currentChildTop != previousChildTop) {
                    previousChildTop = currentChildTop
                    hasChanged = true
                    countOfFramesWithNoChanges = 0
                }
                if (currentDependencyTop != previousDependencyTop) {
                    previousDependencyTop = currentDependencyTop
                    hasChanged = true
                    countOfFramesWithNoChanges = 0
                }
                if (!hasChanged) {
                    countOfFramesWithNoChanges++
                }
                if (countOfFramesWithNoChanges <= MAX_COUNT_OF_FRAMES_WITH_NO_CHANGES) {
                    // We can still look for changes on subsequent frames.
                    child.requestLayout()
                    child.postOnAnimation(this)
                } else {
                    child.requestLayout()
                    onAnimationRunnablePosted = false
                }
            }
        })
    }
}