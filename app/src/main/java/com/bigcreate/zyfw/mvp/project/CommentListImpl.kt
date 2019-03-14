package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.CommentListRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class CommentListImpl(mView: View?) :
        BasePresenterImpl<CommentListRequest, JsonObject, CommentListImpl.View>(mView) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                val content = jsonData
                when (code) {
                    200 -> {
                        Attributes.token = content.newToken
                        onGetCommentListSuccess(content.getAsObject("list"))
                    }
                    else -> onGetCommentListFailed(this@apply)
                }
            }
        }
    }

    override fun backgroundRequest(request: CommentListRequest): JsonObject? = RemoteService.getProjectComments(request).execute().body()
    interface View : BaseNetworkView {
        fun onGetCommentListSuccess(list: List<Comment>)
        fun onGetCommentListFailed(jsonObject: JsonObject)
    }
}