package com.bigcreate.zyfw.base

import android.content.Context

class CrashHandle(private val mContext: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {

    }
}