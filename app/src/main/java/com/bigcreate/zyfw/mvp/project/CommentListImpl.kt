package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
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
                val content = get("data").asJsonObject
                when (get("code").asInt) {
                    200 -> {
                        Attributes.loginUserInfo!!.token = content.get("newToken").asString
                        onGetCommentListSuccess(content.get("content").asJsonObject.get("list").asJsonArray.toJson().fromJson<List<Comment>>())
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