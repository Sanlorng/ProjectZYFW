package com.bigcreate.zyfw.view

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import kotlinx.android.synthetic.main.layout_item_loop_view_pager_image_adapter.view.*
import java.lang.Exception

class LoopViewPagerImageAdapter private constructor():  LoopViewPagerAdapter(){
    private var mList: List<ImageWrapper>? = null
    private var mUrlLst: List<String>? = null
    private var mTitleList: List<String>? = null

    constructor(list: List<ImageWrapper>):this() {
        mList = list
    }
    constructor(urlLst: List<String>, titleList: List<String>):this() {
        if (urlLst.size != titleList.size)
            throw Exception("title size and url size diff")
        mUrlLst = urlLst
        mTitleList = titleList
    }
    override fun getDataCount(): Int {
        return when {
            mList != null -> mList!!.size
            mTitleList != null -> mTitleList!!.size
            else -> 0
        }
    }

    override fun getTitle(position: Int): String {
        return when {
            mList != null -> mList!![position].title
            mTitleList != null -> mTitleList!![position]
            else -> ""
        }
    }

    private fun getUrl(position: Int): String {
        return when {
            mList != null -> mList!![position].url
            mUrlLst != null -> mUrlLst!![position]
            else -> ""
        }
    }
    override fun createView(container: ViewGroup, position: Int): View {
        return ImageView(container.context).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_XY
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                Glide.with(this)
                    .load(getUrl(position))
                    .into(this)
        }
    }
    data class ImageWrapper(val url: String, val title: String)
}