package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.CreateCommentRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class CreateCommentImpl(mView: View?) :
        BasePresenterImpl<CreateCommentRequest, JsonObject, CreateCommentImpl.View>(mView) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                when (code) {
                    200 -> {
                        Attributes.token = newTokenFromData
                        onCreateCommentSuccess(this@apply)
                    }
                    else -> onCreateCommentFailed(this@apply)
                }
            }
        }
    }

    override fun backgroundRequest(request: CreateCommentRequest): JsonObject? {
        return RemoteService.createProjectComment(request).execute().body()
    }

    interface View : BaseNetworkView {
        fun onCreateCommentSuccess(jsonObject: JsonObject)
        fun onCreateCommentFailed(jsonObject: JsonObject)
    }
}