package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface CommentListContract {
    interface View : BaseNetworkView {
        fun onGetCommentListSuccess(list: List<Comment>)
        fun onGetCommentListFailed(jsonObject: JsonObject)
    }

    interface Presenter : BasePresenter {
        fun doGetCommentList(getProjectRequest: GetProjectRequest)
    }
}