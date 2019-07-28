package com.bigcreate.zyfw.view

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class LoopViewPagerAdapter : PagerAdapter() {
    abstract fun getDataCount(): Int
    abstract fun getTitle(position: Int): String
    override fun getCount(): Int {
        return getDataCount() + 2
    }

    fun transPosition(position: Int): Int {
        return when (position) {
            0 -> getDataCount() - 1
            getDataCount() + 1 -> 0
            else -> position - 1
        }
    }

    abstract fun createView(container: ViewGroup, position: Int): View
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return createView(container, transPosition(position)).apply {
            container.addView(this)
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as View)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(container.getChildAt(0))
    }
}