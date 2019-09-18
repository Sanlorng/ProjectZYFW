package com.bigcreate.zyfw.mvp.explore

import android.util.Log
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.ExploreItemFavoriteRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class ExploreFavoriteImpl(view: View?): BaseMultiPresenterImpl<ExploreFavoriteImpl.View>(view) {
    private val favoriteInter = object : PresenterInter<ExploreItemFavoriteRequest,JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    if (code == 200) {
                        Attributes.token = jsonData.newToken
                        onFavoriteSuccess()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ExploreItemFavoriteRequest): JsonObject? {
            return RemoteService.exploreItemFavorite(request).execute().body()
        }
    }

    private val unfavoriteInter = object : PresenterInter<ExploreItemFavoriteRequest,JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    if (code == 200) {
                        Attributes.token = jsonData.newToken
                        onUnFavoriteSuccess()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ExploreItemFavoriteRequest): JsonObject? {
            return RemoteService.exploreItemUnfavorite(request).execute().body()
        }
    }

    fun doFavoriteRequest(view:View? = mView,request: ExploreItemFavoriteRequest) {
        mView = view
        cancelJob()
        addJob(favoriteInter.doRequest(view,request))
    }

    fun doUnfavoriteRequest(view:View? = mView,request: ExploreItemFavoriteRequest) {
        mView = view
        cancelJob()
        addJob(unfavoriteInter.doRequest(view,request))
    }

    interface View:BaseNetworkView {
        fun onFavoriteSuccess()
        fun onUnFavoriteSuccess()
    }
}