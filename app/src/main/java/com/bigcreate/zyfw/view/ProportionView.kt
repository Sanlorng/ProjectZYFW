package com.bigcreate.zyfw.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bigcreate.zyfw.R


open class ProportionView : ViewGroup {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : this(context, attrs, defAttrStyle, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttrs, defStyleRes) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ProportionView)
        widthWeight = array.getInt(R.styleable.ProportionView_width_weight, widthWeight)
        heightWeight = array.getInt(R.styleable.ProportionView_height_weight, heightWeight)
        array.recycle()
    }

    var widthWeight = 1
        set(value) {
            if (field != value) {
                field = value
                proportion = heightWeight.toFloat() / field
            }
        }
    var heightWeight = 1
        set(value) {
            if (field != value) {
                field = value
                proportion = field.toFloat() / widthWeight
            }
        }
    private var proportion = 1f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 1) {
            throw IllegalArgumentException("child count must less than 2")
        }
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val paddingHorizontal = paddingLeft + paddingRight
        val paddingVertical = paddingTop + paddingBottom
        //宽确定，则最终宽确定
        when (widthMode) {
            View.MeasureSpec.EXACTLY -> {
                val width = View.MeasureSpec.getSize(widthMeasureSpec)
                val height = (width * proportion).toInt()
                val child = getChildAt(0)
                if (child != null) {
                    val params = child.layoutParams
                    var marginHorizontal = 0
                    var marginVertical = 0
                    if (params is ViewGroup.MarginLayoutParams) {
                        marginHorizontal = params.leftMargin + params.rightMargin
                        marginVertical = params.topMargin + params.bottomMargin
                    }
                    val childWidthMS =
                        getChildExactlyMeasureSpec(
                            Math.max(0, width - marginHorizontal - paddingHorizontal),
                            params.width
                        )
                    val childHeightMS =
                        getChildExactlyMeasureSpec(
                            Math.max(0, height - marginVertical - paddingVertical),
                            params.height
                        )
                    child.measure(childWidthMS, childHeightMS)
                }
                setMeasuredDimension(width, height)

            }
            View.MeasureSpec.AT_MOST -> {//宽不确定，先measure child，自身宽度==child的测量宽度，此时自身宽度确定，再重新measure child高度

                if (MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)) {
                    val height = View.MeasureSpec.getSize(heightMeasureSpec)
                    val width = (height / proportion).toInt()
                    val child = getChildAt(0)
                    if (child != null) {
                        val params = child.layoutParams
                        var marginHorizontal = 0
                        var marginVertical = 0
                        if (params is ViewGroup.MarginLayoutParams) {
                            marginHorizontal = params.leftMargin + params.rightMargin
                            marginVertical = params.topMargin + params.bottomMargin
                        }
                        val childWidthMS =
                            getChildExactlyMeasureSpec(
                                Math.max(0, width - marginHorizontal - paddingHorizontal),
                                params.width
                            )
                        val childHeightMS =
                            getChildExactlyMeasureSpec(
                                Math.max(0, height - marginVertical - paddingVertical),
                                params.height
                            )
                        child.measure(childWidthMS, childHeightMS)
                    }
                    setMeasuredDimension(width, height)
                    return
                }
                var width = View.MeasureSpec.getSize(widthMeasureSpec)

                val child = getChildAt(0)
                if (child == null) {
                    width = Math.max(0, paddingLeft + paddingRight)
                    val height = (width * proportion).toInt()
                    setMeasuredDimension(width, height)
                    return
                }
                val params = child.layoutParams
                var marginHorizontal = 0
                var marginVertical = 0
                if (params is ViewGroup.MarginLayoutParams) {
                    marginHorizontal = params.leftMargin + params.rightMargin
                    marginVertical = params.topMargin + params.bottomMargin
                }
                var childWidthMS: Int
                childWidthMS =
                    if (params.width == ViewGroup.LayoutParams.MATCH_PARENT || params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        View.MeasureSpec.makeMeasureSpec(
                            Math.max(0, width - marginHorizontal - paddingHorizontal),
                            View.MeasureSpec.AT_MOST
                        )
                    } else {
                        View.MeasureSpec.makeMeasureSpec(params.width, View.MeasureSpec.EXACTLY)
                    }

                var childHeightMS = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

                child.measure(childWidthMS, childHeightMS)

                width = Math.min(Math.max(0, child.measuredWidth + marginHorizontal + paddingHorizontal), width)

                val height = (width * proportion).toInt()

                setMeasuredDimension(width, height)

                childWidthMS =
                    getChildExactlyMeasureSpec(Math.max(0, width - marginHorizontal - paddingHorizontal), params.width)
                childHeightMS =
                    getChildExactlyMeasureSpec(Math.max(0, height - marginVertical - paddingVertical), params.height)

                child.measure(childWidthMS, childHeightMS)

            }
            else -> super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }


    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }


    private fun getChildExactlyMeasureSpec(size: Int, params: Int): Int {
        return when (params) {
            LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
            LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST)
            else -> MeasureSpec.makeMeasureSpec(params, MeasureSpec.EXACTLY)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0) ?: return
        val pl = paddingLeft
        val pt = paddingTop
        val params = child.layoutParams
        var ml = 0
        var mt = 0
        if (params is ViewGroup.MarginLayoutParams) {
            ml = params.leftMargin
            mt = params.topMargin
        }
        child.layout(pl + ml, pt + mt, child.measuredWidth + pl + ml, child.measuredHeight + pt + mt)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        if (childCount > 1)
//            throw IllegalArgumentException("child count must less than 2")
//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val paddingHorizontal = paddingLeft + paddingRight
//        val paddingVertical = paddingTop + paddingBottom
//        if (widthMode == MeasureSpec.EXACTLY) {
//            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//            val heightSize = (width * proportion).toInt()
//            getChildAt(0)?.apply {
//                var marginHorizontal = 0
//                var marginVertical = 0
//                if (layoutParams is MarginLayoutParams) {
//                    val params = layoutParams as MarginLayoutParams
//                    marginHorizontal = params.leftMargin + params.rightMargin
//                    marginVertical = params.topMargin + params.bottomMargin
//                }
//
//                val childWidth = getChildExactlyMeasureSpec(Math.max(0,widthSize - marginHorizontal - paddingHorizontal), layoutParams.width)
//                val childHeight = getChildExactlyMeasureSpec(Math.max(0,heightSize - marginVertical - paddingVertical), layoutParams.height)
//                measure(childWidth,childHeight)
//            }
//            setMeasuredDimension(widthSize,heightSize)
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            var widthSize = MeasureSpec.getSize(widthMeasureSpec)
//            val child = getChildAt(0)
//            if (child == null) {
//                widthSize = Math.max(0, paddingLeft + paddingRight)
//                val heightSize = (widthSize + proportion).toInt()
//                setMeasuredDimension(widthSize,heightSize)
//                return
//            } else {
//                child.apply {
//                    var marginHorizontal = 0
//                    var marginVertical = 0
//                    if (layoutParams is MarginLayoutParams) {
//                        val params = layoutParams as MarginLayoutParams
//                        marginHorizontal = params.leftMargin + params.rightMargin
//                        marginVertical = params.topMargin + params.bottomMargin
//                    }
//
//                    var childWidth = if (layoutParams.width == LayoutParams.MATCH_PARENT || layoutParams.width == LayoutParams.WRAP_CONTENT)
//                        MeasureSpec.makeMeasureSpec(Math.max(0, widthSize - marginHorizontal - paddingHorizontal),MeasureSpec.AT_MOST)
//                    else
//                        MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY)
////                    val childWidth = getChildExactlyMeasureSpec(Math.max(0,widthSize - marginHorizontal - marginVertical), layoutParams.width)
//                    var childHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
////                    measure(childWidth,childHeight)
//                    measure(childWidth,childHeight)
//
//                    widthSize = Math.min(Math.max(0,measuredWidth + marginHorizontal + paddingHorizontal), width)
//                    val heightSize = (widthSize * proportion).toInt()
//                    setMeasuredDimension(widthSize,heightSize)
//                    childWidth = getChildExactlyMeasureSpec(Math.max(0,widthSize - marginHorizontal - paddingHorizontal), layoutParams.width)
//                    childHeight = getChildExactlyMeasureSpec(Math.max(0,heightSize - marginVertical - paddingVertical), layoutParams.height)
//
//                    measure(childWidth,childHeight)
//
//                }
//            }
//        } else
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }
//
//    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
//        return MarginLayoutParams(context,attrs)
//    }
//
//    private fun getChildExactlyMeasureSpec(size: Int, params: Int) = when(params) {
//        LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
//        LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST)
//        else -> MeasureSpec.makeMeasureSpec(params, MeasureSpec.EXACTLY)
//    }
//
//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        getChildAt(0)?.apply {
//            var marginLeft = 0
//            var marginTop = 0
//
//            if(layoutParams is MarginLayoutParams) {
//                val params = layoutParams as MarginLayoutParams
//                marginLeft = params.leftMargin
//                marginTop = params.topMargin
//            }
//
//            layout(paddingLeft + marginLeft,
//                paddingTop + marginTop,
//                measuredWidth + paddingLeft + marginLeft,
//                measuredHeight + paddingTop + marginTop)
//        }
//    }
}