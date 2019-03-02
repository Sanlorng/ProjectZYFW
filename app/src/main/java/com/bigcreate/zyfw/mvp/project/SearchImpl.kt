package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class SearchImpl(mView: View?) : BasePresenterImpl<SearchRequest, JsonObject, SearchImpl.View>(mView) {
    override fun backgroundRequest(request: SearchRequest): JsonObject? = RemoteService.searchProjectByBlur(request).execute().body()

    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                when (get("code").asInt) {
                    200 -> {
                        onSearchFinished(get("data").asJsonObject.get("content").asJsonObject.get("list").toJson().fromJson<ArrayList<SearchModel>>())
                    }
                    else -> {
                        onSearchFailed(this@apply)
                    }
                }
            }
        }
    }

    interface View : BaseNetworkView {
        fun onSearchFinished(searchResult: ArrayList<SearchModel>)
        fun onSearchFailed(response: JsonObject)
    }
}