package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.ProjectFavoriteRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class FavoriteProjectImpl(view: View?) : BaseMultiPresenterImpl<FavoriteProjectImpl.View>(view) {
    private val favoriteInter = object : PresenterInter<ProjectFavoriteRequest, JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
                            Attributes.token = newTokenFromData
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
                    when (code) {
                        200 -> {
                            Attributes.token = newTokenFromData
                            onUnFavoriteProjectSuccess()
                        }
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