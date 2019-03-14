package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.toast
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class JoinProjectImpl(view:View) : BasePresenterImpl<GetProjectRequest,JsonObject,JoinProjectImpl.View>(view) {

    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                when(code) {
                    200 -> {
                        val content = jsonData
                        Attributes.token = content.newToken
                        onJoinRequestSuccess(content.get("Join").asBoolean)
                    }
                    403 -> {
                        getViewContext().toast("权限不足")
                    }
                    else -> onJoinRequestFailed(this)
                }
            }
        }
    }

    override fun backgroundRequest(request: GetProjectRequest): JsonObject? {
        return RemoteService.joinProject(request).execute().body()
    }

    interface View:BaseNetworkView {
        fun onJoinRequestSuccess(join: Boolean)
        fun onJoinRequestFailed(json: JsonObject)
    }
}