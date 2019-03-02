package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.base.UpdateService
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl

class UpdateImpl(mView: View?) :
        BasePresenterImpl<String, UpdateInfo, UpdateImpl.View>(mView) {
    override fun afterRequestSuccess(data: UpdateInfo?) {
        data?.apply {
            mView?.onUpdateCheckSuccess(this)
        }
    }

    override fun backgroundRequest(request: String): UpdateInfo? {
        return UpdateService.getAppUpdateVersion(request).execute().body()
    }

    interface View : BaseNetworkView {
        fun onUpdateCheckSuccess(updateInfo: UpdateInfo)
        fun onUpdateCheckFailed(response: RestResult<UpdateInfo>)
    }
}
