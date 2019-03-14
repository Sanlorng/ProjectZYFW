package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class DetailsImpl(mView: View?) :
        BasePresenterImpl<GetProjectRequest, JsonObject, DetailsImpl.View>(mView) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                when (code) {
                    200 -> {
                        val content = jsonData
                        Attributes.token = content.newToken
                        onGetDetailsSuccess(content.get("content").toJson().fromJson<Project>()) }
                    else -> onGetDetailsFailed(this@apply)
                }
            }
        }
    }

    override fun backgroundRequest(request: GetProjectRequest): JsonObject? {
        return RemoteService.getProjectInfo(request).execute().body()
    }
    interface View : BaseNetworkView {
        fun onGetDetailsSuccess(project: Project)
        fun onGetDetailsFailed(jsonObject: JsonObject)
    }
}