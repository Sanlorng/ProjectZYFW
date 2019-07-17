package com.bigcreate.zyfw.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.*

import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.layout_loop_view_pager.view.*

class LoopViewPager: LinearLayout {
    private var loopController: Job? = null
    var showTitle = true
    set(value) {
        titleLoopViewPager?.isVisible = value
        field = value
    }
    var adapter: LoopViewPagerAdapter? = null
        set(value) {
            field = value
            mLoopViewPager.adapter = field
            mLoopViewPager.setCurrentItem(1, false)
            if (showTitle)
            titleLoopViewPager.text = value?.run {
                getTitle(transPosition(mLoopViewPager.currentItem))
            }
            onResume()
        }
    /*
    * 毫秒
    */
    var loopTime = 5000L

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super (context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super (context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int, defStyleRes: Int)
            : super (context, attrs, defStyleAttrs, defStyleRes)
    init {
//        if (layoutParams == null)
//            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,1080)
//        else
//            layoutParams = layoutParams.apply {
//                width = LayoutParams.MATCH_PARENT
//                height = 1080
//            }
        LayoutInflater.from(context).inflate(R.layout.layout_loop_view_pager,this)
        titleLoopViewPager.background.alpha = 204
        mLoopViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            private var curPosition = -1
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING)
                    onStop()
                else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    onResume()
//                    Log.e("IDLE",curPosition.toString())
                    adapter?.apply {
                        if (curPosition == 0)
                            mLoopViewPager.setCurrentItem( getDataCount() ,false)
                        else if (curPosition == getDataCount() + 1)
                            mLoopViewPager.setCurrentItem(1,false)
                        if (showTitle)
                        titleLoopViewPager.text = getTitle(transPosition(curPosition))
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
//                adapter?.apply {
//                    when(position) {
//                        getDataCount()+2 -> mLoopViewPager.setCurrentItem(1, false)
//                        0 -> mLoopViewPager.setCurrentItem(getDataCount()+1, false)
//                    }
//                }
                curPosition = position
//                Log.e("position", position.toString())

            }
        })
    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
//        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
//        measureChildren(widthMeasureSpec, heightMeasureSpec)
//
//        when {
//            childCount == 0 -> setMeasuredDimension(0, 0)
//            widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST -> {
//                val childWidth = 0
//                val childHeight = 0
//                for (i in 0 until childCount) {
//                    childHeight +=
//                }
//
//
//                setMeasuredDimension(childWidth * childCount,)
//            }
//        }
//    }
//
//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//
//    }

    /*
     * 在前台时调用
     */
    fun onResume() {
//        Log.e("loopController adapter","${loopController == null} ${adapter != null}")
        if (loopController == null&&adapter!= null)
            loopController = GlobalScope.launch {
                while (true) {
                    delay(loopTime)
                    withContext(Dispatchers.Main) {
                        mLoopViewPager.currentItem += 1
                    }
                }
            }
    }

    /*
     *在后台时使用
     */
    private fun onStop() {
        loopController?.cancel()
        loopController = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onResume()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onStop()
    }


}