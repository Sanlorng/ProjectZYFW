package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.isNetworkActive
import com.bigcreate.zyfw.models.CreateProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateImpl(var mView: CreateContract.View?):CreateContract.Presenter {
    var createJob: Job? = null
    override fun doCreateProject(projectRequest: CreateProjectRequest) {
        val view = mView!!
        if (!view.getViewContext().isNetworkActive)
            view.onNetworkFailed()
        createJob = GlobalScope.launch {
            RemoteService.createProject(projectRequest).execute().body()?.apply {
                launch(Dispatchers.Main) {
                    when(get("code").asInt){
                        200 -> view.onCreateProjectSuccess(this@apply)
                        else -> view.onCreateProjectFailed(this@apply)
                    }
                }
            }
        }

    }

    override fun cancelJob() {
        createJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}