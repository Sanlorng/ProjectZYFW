package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.CreateCommentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CreateCommentImpl(var mView: CreateCommentContract.View?) : CreateCommentContract.Presenter {
    private var createCommentJob: Job? = null
    override fun doCreateComment(createCommentRequest: CreateCommentRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            createCommentJob = GlobalScope.launch {
                try {

                    RemoteService.createProjectComment(createCommentRequest).execute().body()?.apply {
                        val data = get("data").asJsonObject
                        if (get("code").asInt == 200)
                            Attributes.loginUserInfo!!.token = data.get("newToken").asString
                        GlobalScope.launch(Dispatchers.Main) {
                            onRequestFinished()
                            when (get("code").asInt) {
                                200 -> onCreateCommentSuccess(this@apply)
                                else -> onCreateCommentFailed(this@apply)
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
        createCommentJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}