package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.WebKit
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.isNetworkActive
import com.bigcreate.zyfw.models.ProjectResponse
import com.bigcreate.zyfw.models.SearchRequest
import com.bigcreate.zyfw.mvp.base.SearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchImpl(var mView:SearchContract.View?): SearchContract.Presenter{
    var job : Job? = null
    override fun detachView() {
        cancelJob()
        mView = null
    }

    override fun searchProject(request: SearchRequest) {
        if (mView == null)
            throw Throwable("Please bind view")
        val view = mView!!
        if (!view.getViewContext().isNetworkActive) {
            view.onNetworkFailed()
            return
        }
        mView?.onRequesting()
        job = GlobalScope.launch {
            RemoteService.searchProjectByBlur(request).execute().body()?.apply {
                launch(Dispatchers.Main) {
                    when (get("code").asInt){
                        200 -> {
                            Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                            mView?.onSearchFinished(get("data").asJsonObject.get("content").asJsonArray.toJson().fromJson<List<SearchModel>>())
                        }
                        else -> {
                            mView?.onSearchFailed(this@apply)
                        }
                    }
                }
            }
        }
    }

    override fun cancelJob() {
        job?.cancel()
    }
}