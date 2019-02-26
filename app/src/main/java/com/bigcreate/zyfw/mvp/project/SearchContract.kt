package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface SearchContract {
    interface Presenter : BasePresenter {
        fun searchProject(request: SearchRequest)
    }

    interface NetworkView : BaseNetworkView {
        fun onSearchFinished(searchResult: List<SearchModel>)
        fun onSearchFailed(response: JsonObject)
    }
}