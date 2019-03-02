package com.bigcreate.zyfw.base

import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import okhttp3.ResponseBody
import java.io.InputStream

class AuthImageFetch(val responseBody: ResponseBody) : DataFetcher<InputStream> {
    var mStream: InputStream? = null
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        mStream = responseBody.byteStream()
        callback.onDataReady(mStream)
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun cancel() {
//        mStream?.close()
//        responseBody.close()
    }

    override fun cleanup() {
        mStream?.close()
        responseBody.close()
    }
}

fun RequestBuilder<Drawable>.applyCenterCrop(): RequestBuilder<Drawable> {
    return this.apply(RequestOptions.bitmapTransform(CenterCrop()))
}

fun RequestBuilder<Drawable>.applyCircleCrop(): RequestBuilder<Drawable> {
    return this.apply(RequestOptions.bitmapTransform(CircleCrop()))
}