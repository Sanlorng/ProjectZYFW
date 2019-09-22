package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.ExploreEditRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject
import retrofit2.Retrofit

class UpdateExploreImpl(view: View):BasePresenterImpl<ExploreEditRequest,JsonObject,UpdateExploreImpl.View>(view) {

    override fun afterRequestSuccess(data: JsonObject?) {
        data?.apply {
            mView?.run {
                if (code == 200) {
                    Attributes.token = newTokenFromData
                    onUpdateSuccess()
                    return
                }
                onUpdateFailed(this@apply)
            }
        }
    }

    override fun backgroundRequest(request: ExploreEditRequest): JsonObject? {
        return RemoteService.updateExploreItem(request).execute().body()
    }

    interface View: BaseNetworkView {
        fun onUpdateSuccess()
        fun onUpdateFailed(jsonObject: JsonObject)
    }
}