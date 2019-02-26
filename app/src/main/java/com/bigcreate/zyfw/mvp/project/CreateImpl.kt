package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.UpdateProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CreateImpl(var mView: CreateContract.NetworkView?) : CreateContract.Presenter {
    private var createJob: Job? = null
    private var updateJob: Job? = null
    override fun doCreateProject(projectRequest: CreateProjectRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive)
                onNetworkFailed()
            createJob = GlobalScope.launch {
                try {
                    RemoteService.createProject(projectRequest).execute().body()?.apply {
                        launch(Dispatchers.Main) {
                            when (get("code").asInt) {
                                200 -> {
                                    Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                                    onCreateProjectSuccess(this@apply)
                                }
                                else -> onCreateProjectFailed(this@apply)
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

    override fun doUpdateProject(updateProjectRequest: UpdateProjectRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive)
                view.onNetworkFailed()
            updateJob = GlobalScope.launch {
                try {
                    RemoteService.updateProject(updateProjectRequest).execute().body()?.apply {
                        launch(Dispatchers.Main) {
                            when (get("code").asInt) {
                                200 -> {
                                    Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                                    view.onUpdateProjectSuccess(this@apply)
                                }
                                else -> view.onUpdateProjectFailed(this@apply)
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

    override fun cancelJob() {
        createJob?.cancel()
        updateJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}