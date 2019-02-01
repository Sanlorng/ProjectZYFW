package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.bigcreate.zyfw.mvp.base.SearchModel
import com.google.gson.JsonObject

interface SearchContract {
    interface Presenter:BasePresenter{
        fun searchProject(request: SearchRequest)
    }

    interface View: BaseView{
        fun onSearchFinished(searchResult: List<SearchModel>)
        fun onSearchFailed(response: JsonObject)
    }
}