package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.tencent.map.geolocation.TencentLocation

interface LocationContract {
    interface Presenter : BasePresenter {
        fun start()
        fun doLocationRequest()
    }

    interface View : BaseView {
        fun onLocationRequestSuccess(location: TencentLocation)
        fun onLocationRequestFailed()
        fun onLocationPermissionDenied()
    }
}