package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.isNetworkActive
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.models.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class SearchImpl(var mView: SearchContract.NetworkView?) : SearchContract.Presenter {
    var job: Job? = null
    override fun detachView() {
        cancelJob()
        mView = null
    }

    override fun searchProject(request: SearchRequest) {
        if (mView == null)
            throw Throwable("Please bind view")
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            job = GlobalScope.launch {
                RemoteService.searchProjectByBlur(request).execute().body()?.apply {
                    try {
                        launch(Dispatchers.Main) {
                            when (get("code").asInt) {
                                200 -> {
                                    Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                                    onSearchFinished(get("data").asJsonObject.get("content").asJsonArray.toJson().fromJson<List<SearchModel>>())
                                }
                                else -> {
                                    onSearchFailed(this@apply)
                                }
                            }
                        }
                    } catch (e: SocketTimeoutException) {
                        onRequestFinished()
                        onNetworkFailed()
                    }
                }
            }
        }
    }

    override fun cancelJob() {
        job?.cancel()
    }
}