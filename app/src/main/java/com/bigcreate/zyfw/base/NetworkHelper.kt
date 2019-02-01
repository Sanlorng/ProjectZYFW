package com.bigcreate.zyfw.base

import android.content.Context

object NetworkHelper{
    var isNetworkActive = true
}
val Context.isNetworkActive: Boolean
get() = NetworkHelper.isNetworkActive