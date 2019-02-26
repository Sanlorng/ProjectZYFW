package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.isNetworkActive
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class DetailsImpl(var mView: DetailsContract.NetworkView?) : DetailsContract.Presenter {
    private var getDetailsJob: Job? = null
    override fun doGetDetails(getProjectRequest: GetProjectRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive)
                onNetworkFailed()
            onRequesting()
            try {
                getDetailsJob = GlobalScope.launch {
                    RemoteService.getProjectInfo(getProjectRequest).execute().body()?.apply {
                        onRequestFinished()
                        if (get("code").asInt == 200)
                            Attributes.loginUserInfo!!.token = get("data").asJsonObject.get("newToken").asString
                        launch(Dispatchers.Main) {
                            when (get("code").asInt) {
                                200 -> onGetDetailsSuccess(get("data").asJsonObject.get("content").toJson().fromJson<Project>())
                                else -> onGetDetailsFailed(this@apply)
                            }
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                onRequestFinished()
                onNetworkFailed()
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