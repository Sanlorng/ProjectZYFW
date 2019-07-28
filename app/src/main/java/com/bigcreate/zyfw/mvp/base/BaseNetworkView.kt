package com.bigcreate.zyfw.mvp.base

import android.content.Context

interface BaseView {
    fun getViewContext(): Context
    fun onRequesting() {}
    fun onRequestFinished() {}
}

interface BaseNetworkView : BaseView {
    fun onNetworkFailed() {}
}

