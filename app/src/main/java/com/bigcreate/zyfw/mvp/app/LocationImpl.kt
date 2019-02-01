package com.bigcreate.zyfw.mvp.app

import android.util.Log
import com.bigcreate.library.WebKit
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.base.isNetworkActive
import com.tencent.map.geolocation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocationImpl(var mView: LocationContract.View?):LocationContract.Presenter {
    var alwaysCall = false
    var job: Job? =null
    var location: TencentLocation? = null
    val request = TencentLocationRequest.create().apply {
        requestLevel = TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA
        isAllowCache = true
        interval = 1500
        isAllowGPS = true
        isAllowDirection = true
    }
    val listener = object : TencentLocationListener{
        override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
            location = p0
            if (alwaysCall)
                doLocationRequest()
        }

        override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {

        }
    }
    override fun start() {
        GlobalScope.launch(Dispatchers.Main) {
            if (mView == null)
                throw Throwable("Please bind view")
            val view = mView!!
            if (!view.getViewContext().isNetworkActive) {
                view.onNetworkFailed()
                return@launch
            }
            TencentLocationManager.getInstance(view.getViewContext()).requestLocationUpdates(request, listener)
        }
    }

    override fun doLocationRequest() {
//        Log.e("location",location.toJson())
        job = GlobalScope.launch(Dispatchers.Main) {
            if (location == null) {
                mView?.onLocationRequestFailed()
            } else {
                mView?.onLocationRequestSuccess(location!!)
            }
        }
    }

    override fun detachView() {
        cancelJob()
        mView?.apply {
            TencentLocationManager.getInstance(getViewContext()).removeUpdates(listener)
        }
        mView = null
    }

    override fun cancelJob() {
        job?.cancel()
    }

}