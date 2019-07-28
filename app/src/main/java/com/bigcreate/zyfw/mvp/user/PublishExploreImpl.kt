package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.PublishExploreRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class PublishExploreImpl(view: View) : BasePresenterImpl<PublishExploreRequest, JsonObject, PublishExploreImpl.View>(view) {

    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                if (code == 200) {
                    Attributes.token = newTokenFromData
                    explorePublishSuccess()
                } else
                    explorePublishFailed()
            }
        }
    }

    override fun backgroundRequest(request: PublishExploreRequest): JsonObject? {
        return request.run {
            RemoteService.explorePublish(parts, token, dyContent).execute().body()
        }
    }

    interface View : BaseNetworkView {
        fun explorePublishSuccess()
        fun explorePublishFailed()
    }
}