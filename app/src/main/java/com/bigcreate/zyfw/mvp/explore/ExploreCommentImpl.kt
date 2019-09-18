package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.ExploreCommentRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject
import org.w3c.dom.Attr

class ExploreCommentImpl(view: View?):BasePresenterImpl<ExploreCommentRequest,JsonObject,ExploreCommentImpl.View>(view) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                if (code == 200) {
                    Attributes.token = jsonData.newToken
                    onCommentSuccess()
                    return
                }
            }
            onCommentFailed()
        }
    }

    override fun backgroundRequest(request: ExploreCommentRequest): JsonObject? {
        return RemoteService.commentExploreItem(request).execute().body()
    }
    interface View: BaseNetworkView {
        fun onCommentSuccess()
        fun onCommentFailed()
    }
}