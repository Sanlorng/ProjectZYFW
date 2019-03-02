package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.ProjectFavoriteRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class FavoriteProjectImpl(mView: View?) : BaseMultiPresenterImpl<FavoriteProjectImpl.View>(mView) {
    private val favoriteInter = object : PresenterInter<ProjectFavoriteRequest, JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (get("code").asInt) {
                        200 -> {
                            Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                            onFavoriteProjectSuccess()
                        }
                        else -> onFavoriteProjectFailed()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ProjectFavoriteRequest): JsonObject? {
            return RemoteService.instance.favoriteProject(request).execute().body()
        }
    }
    private val unFavoriteInter = object : PresenterInter<ProjectFavoriteRequest, JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (get("code").asInt) {
                        200 -> {
                            Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                            onUnFavoriteProjectSuccess() }
                        else -> onUnFavoriteProjectFailed()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ProjectFavoriteRequest): JsonObject? {
            return RemoteService.instance.unFavoriteProject(request).execute().body()
        }
    }

    fun doFavoriteProject(favoriteRequest: ProjectFavoriteRequest) {
        cancelJob()
        addJob(favoriteInter.doRequest(mView, favoriteRequest))
    }

    fun doUnFavoriteProject(unFavoriteRequest: ProjectFavoriteRequest) {
        cancelJob()
        addJob(unFavoriteInter.doRequest(mView, unFavoriteRequest))
    }
    interface View : BaseNetworkView {
        fun onFavoriteProjectSuccess()
        fun onFavoriteProjectFailed()
        fun onUnFavoriteProjectSuccess()
        fun onUnFavoriteProjectFailed()
    }
}