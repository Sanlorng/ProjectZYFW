package com.bigcreate.zyfw.base

import java.lang.ref.Reference
import java.lang.ref.SoftReference



abstract class BasePresenter<T> {
    protected var mViewRef: Reference<T>? = null

    fun attachView(view:T){
        mViewRef = SoftReference<T>(view)
    }

    protected fun getView():T?{
        return mViewRef?.get()
    }

    fun isViewAttached():Boolean{
        return mViewRef != null && mViewRef?.get() != null
    }

    fun detachView(){
        mViewRef?.clear()
    }
}