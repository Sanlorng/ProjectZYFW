package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.isNetworkActive
import com.bigcreate.zyfw.models.GetProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailsImpl(var mView: DetailsContract.View?):DetailsContract.Presenter {
    private var getDetailsJob: Job? = null
    override fun doGetDetails(getProjectRequest: GetProjectRequest) {
        val view = mView!!
        if (!view.getViewContext().isNetworkActive)
            view.onNetworkFailed()
        getDetailsJob = GlobalScope.launch {
            RemoteService.getProjectInfo(getProjectRequest).execute().body()?.apply {
                launch(Dispatchers.Main) {
                    when(get("code").asInt) {
                        200 -> view.onGetDetailsSuccess(this@apply)
                        else -> view.onGetDetailsFailed(this@apply)
                    }
                }
            }
        }
    }

    override fun cancelJob() {
        getDetailsJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}