package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.jsonContentFromData
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class RecommendImpl(view: View) : BasePresenterImpl<SimpleRequest, JsonObject, RecommendImpl.View>(view) {

    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                val content = jsonContentFromData
                when (code) {
                    200 -> {
                        if (!content.isJsonNull)
                            onGetRecommendSuccess(content.toJson().fromJson<Project>())
                        else
                            onGetRecommendFailed(this)
                    }
                    else -> onGetRecommendFailed(this)
                }
            }
        }
    }

    override fun backgroundRequest(request: SimpleRequest): JsonObject? {
        return RemoteService.getRecommendData(request).execute().body()
    }

    interface View : BaseNetworkView {
        fun onGetRecommendSuccess(project: Project)
        fun onGetRecommendFailed(jsonObject: JsonObject)
    }
}