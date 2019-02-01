package com.bigcreate.zyfw.mvp.base

import android.content.Context

interface BaseView {
    fun getViewContext():Context
    fun onNetworkFailed()
    fun onRequesting()
}