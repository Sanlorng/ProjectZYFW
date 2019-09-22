package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.ExploreCommentDeleteRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class DeleteExploreCommentImpl(view: View):BasePresenterImpl<ExploreCommentDeleteRequest,JsonObject,DeleteExploreCommentImpl.View>(view) {
    override fun afterRequestSuccess(data: JsonObject?) {
        data?.apply {
            mView?.run {
                if (code == 200) {
                    Attributes.token = newTokenFromData
                    onDeleteExploreCommentSuccess()
                    return
                }
                onDeleteExploreCommentFailed(this@apply)
            }
        }
    }

    override fun backgroundRequest(request: ExploreCommentDeleteRequest): JsonObject? {
        return RemoteService.deleteExploreComment(request).execute().body()
    }
    interface View: BaseNetworkView {
        fun onDeleteExploreCommentSuccess()
        fun onDeleteExploreCommentFailed(jsonObject: JsonObject)
    }
}