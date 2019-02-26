package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.BuildConfig
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocationImpl(var mView: LocationContract.NetworkView?) : LocationContract.Presenter {
    var alwaysCall = false
    var isFisrtCall = false
    var job: Job? = null
    var location: TencentLocation? = null
    private var requestLevel = TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA
    val request: TencentLocationRequest = TencentLocationRequest.create().apply {
        isAllowCache = true
        interval = 1500
        isAllowGPS = true
        isAllowDirection = true
    }!!
    private val listener = object : TencentLocationListener {
        override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
            location = p0
            if (alwaysCall || isFisrtCall)
                doLocationRequest()
            if (BuildConfig.DEBUG) {
//                Log.e("is update", "update")
//                Log.e("always call", alwaysCall.toString())
            }
        }

        override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {

        }
    }

    override fun start() {
        GlobalScope.launch(Dispatchers.Main) {
            if (mView == null)
                throw Throwable("Please bind view")
            val view = mView!!
//            if (!view.getViewContext().isNetworkActive) {
//                view.onNetworkFailed()
//                return@launch
//            }
            TencentLocationManager.getInstance(view.getViewContext()).requestLocationUpdates(request.apply { requestLevel = this@LocationImpl.requestLevel }, listener)
        }
    }

    override fun doLocationRequest() {
//        Log.e("location",location.toJson())
        job = GlobalScope.launch(Dispatchers.Main) {
            if (!alwaysCall)
                mView?.onRequesting()
            if (location == null) {
                if (!alwaysCall)
                    isFisrtCall = true
                mView?.onLocationRequestFailed()
            } else {
                isFisrtCall = false
                mView?.onLocationRequestSuccess(location!!)
            }
            if (!alwaysCall)
                mView?.onRequestFinished()
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