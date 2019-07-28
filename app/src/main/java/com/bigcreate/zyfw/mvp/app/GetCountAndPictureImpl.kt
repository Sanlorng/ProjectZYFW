package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.CountAndPictureModel
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl

class GetCountAndPictureImpl(view: View) : BasePresenterImpl<String, RestResult<CountAndPictureModel>, GetCountAndPictureImpl.View>(view) {
    override fun afterRequestSuccess(data: RestResult<CountAndPictureModel>?) {
        data?.apply {
            when (code) {
                200 -> mView?.onLoadSuccess(this)
                else -> mView?.onLoadFailed()
            }
        }
    }

    override fun backgroundRequest(request: String): RestResult<CountAndPictureModel>? {
        return RemoteService.getCountAndPicture().execute().body()
    }

    interface View : BaseNetworkView {
        fun onLoadSuccess(result: RestResult<CountAndPictureModel>)
        fun onLoadFailed()
    }
}