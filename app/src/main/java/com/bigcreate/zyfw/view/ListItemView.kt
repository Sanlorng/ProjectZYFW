package com.bigcreate.zyfw.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.core.view.isVisible
import com.bigcreate.zyfw.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.layout_list_item_view.view.*

class ListItemView:MaterialCardView {
    private var actionClick: OnClickListener? = null
    private var actionLongClick: OnLongClickListener? = null
    private var itemClick: OnClickListener? = null
    private var itemLongClick: OnLongClickListener? = null
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        LayoutInflater.from(context).inflate(R.layout.layout_list_item_view,this)
        val array = context.obtainStyledAttributes(attrs,R.styleable.ListItemView)
        if (array.hasValue(R.styleable.ListItemView_logoIcon)) {
            setLogo(array.getResourceId(R.styleable.ListItemView_logoIcon,R.drawable.ic_account_circle_black_24dp))
        } else {
            logoView.isVisible = false
        }
        if (array.hasValue(R.styleable.ListItemView_showAction)) {
            showAction(array.getBoolean(R.styleable.ListItemView_showAction,false))
        }
        if (array.hasValue(R.styleable.ListItemView_actionIcon)) {
            setActionIcon(array.getResourceId(R.styleable.ListItemView_actionIcon,R.drawable.ic_account_circle_black_24dp))
        }

        if (array.hasValue(R.styleable.ListItemView_title)) {
            setTitleText(array.getString(R.styleable.ListItemView_title)?:"")
        }
        if (array.hasValue(R.styleable.ListItemView_subTitle)) {
            setSubTitleText(array.getString(R.styleable.ListItemView_subTitle)?:"")
        }

        if (array.hasValue(R.styleable.ListItemView_actionText)) {
            setActionText(array.getString(R.styleable.ListItemView_actionText)?:"")
        }
        array.recycle()
    }

    fun setLogo(@DrawableRes resId: Int) {
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4f,resources.displayMetrics).toInt()
        logoView.setImageResource(resId)
        logoView.setPadding(padding,padding,padding,padding)
    }

    fun getLogoView():ImageView {
        return logoView
    }

    fun setActionText(text: CharSequence) {
        actionTextView.text = text
    }

    fun setTitleText(text: CharSequence) {
        headerTextView.text = text
    }

    fun setSubTitleText(text: CharSequence) {
        summaryTextView.text = text
    }

    fun setActionIcon(@DrawableRes resId: Int) {
        actionTextView.setCompoundDrawablesWithIntrinsicBounds(null,context.getDrawable(resId),null,null)
    }

    fun showAction(show:Boolean) {
        actionTextView.isVisible = show
        dividerView.isVisible = show
    }

    fun setOnActionClick(onClickListener: OnClickListener?) {
        actionTextView.setOnClickListener(onClickListener)
    }

    fun setOnActionLongClick(onLongClickListener: OnLongClickListener?) {
        actionTextView.setOnLongClickListener(onLongClickListener)
    }


    fun setOnItemClick(onClickListener: OnClickListener?) {
        layoutHeaderItemView.setOnClickListener(onClickListener)
    }

    fun setOnItemLongClick(onLongClickListener: OnLongClickListener?) {
        layoutHeaderItemView.setOnLongClickListener(onLongClickListener)
    }
}