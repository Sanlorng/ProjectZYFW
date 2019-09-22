package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.models.ExploreDeleteRequest
import com.bigcreate.zyfw.models.ExploreRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class DeleteExploreImpl(view: View): BasePresenterImpl<ExploreDeleteRequest,JsonObject,DeleteExploreImpl.View>(view){
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.apply {
            data?.run {
                if (code == 200) {
                    onDeleteExploreSuccess()
                }else {
                    onDeleteExploreFailed(data)
                }
            }
        }
    }

    override fun backgroundRequest(request: ExploreDeleteRequest): JsonObject? {
        return RemoteService.delExploreItem(request).execute().body()
    }
    interface View : BaseNetworkView {
        fun onDeleteExploreFailed(jsonObject: JsonObject)
        fun onDeleteExploreSuccess()
    }
}