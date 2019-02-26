package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter

interface UpdateContract {
    interface Presenter : BasePresenter {
        fun doUpdateCheck(packageName: String)
    }

    interface NetworkView : BaseNetworkView {
        fun onUpdateCheckSuccess(updateInfo: UpdateInfo)
        fun onUpdateCheckFailed(response: RestResult<UpdateInfo>)
    }
}