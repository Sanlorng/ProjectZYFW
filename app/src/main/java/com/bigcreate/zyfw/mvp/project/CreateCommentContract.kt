package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.CreateCommentRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface CreateCommentContract {
    interface Presenter : BasePresenter {
        fun doCreateComment(createCommentRequest: CreateCommentRequest)
    }

    interface View : BaseNetworkView {
        fun onCreateCommentSuccess(jsonObject: JsonObject)
        fun onCreateCommentFailed(jsonObject: JsonObject)
    }
}