package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.library.isNetworkActive
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.GetProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CommentListImpl(var mView: CommentListContract.View?) : CommentListContract.Presenter {
    private var getCommentJob: Job? = null
    override fun doGetCommentList(getProjectRequest: GetProjectRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            try {
                getCommentJob = GlobalScope.launch {
                    RemoteService.getProjectComments(getProjectRequest).execute().body()?.apply {
                        val data = get("data").asJsonObject
                        Attributes.loginUserInfo!!.token = data.get("newToken").asString
                        GlobalScope.launch(Dispatchers.Main) {
                            onRequestFinished()
                            when (get("code").asInt) {
                                200 -> onGetCommentListSuccess(data.get("content").asJsonArray.toJson().fromJson<List<Comment>>())
                                else -> onGetCommentListFailed(this@apply)
                            }
                        }
                    }
                }
            }catch (e: SocketTimeoutException) {
                onRequestFinished()
                onNetworkFailed()
            }
        }
    }

    override fun cancelJob() {
        getCommentJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}