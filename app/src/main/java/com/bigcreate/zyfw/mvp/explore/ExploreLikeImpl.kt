package com.bigcreate.zyfw.mvp.explore

import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.ExploreItemLikeRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class ExploreLikeImpl(view: View?) : BaseMultiPresenterImpl<ExploreLikeImpl.View>(view) {
    private val likeInter = object : PresenterInter<ExploreItemLikeRequest,JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    if (code == 200) {
                        Attributes.token = jsonData.newToken
                        onLikeSuccess()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ExploreItemLikeRequest): JsonObject? {
            return RemoteService.exploreItemLike(request).execute().body()
        }
    }

    private val unlikeInter = object : PresenterInter<ExploreItemLikeRequest,JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    if (code == 200) {
                        Attributes.token = jsonData.newToken
                        onUnlikeSuccess()
                    }
                }
            }
        }

        override fun backgroundRequest(request: ExploreItemLikeRequest): JsonObject? {
            return RemoteService.exploreItemUnlike(request).execute().body()
        }
    }

    fun doLikeRequest(view:View? = mView,request: ExploreItemLikeRequest) {
        mView = view
        cancelJob()
        addJob(likeInter.doRequest(view,request))
    }

    fun doUnlikeRequest(view:View? = mView,request: ExploreItemLikeRequest) {
        mView = view
        cancelJob()
        addJob(unlikeInter.doRequest(view,request))
    }

    interface View:BaseNetworkView {
        fun onLikeSuccess()
        fun onUnlikeSuccess()
    }
}