package com.bigcreate.zyfw.mvp.user

import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.SocketTimeoutException

class UserInfoImpl(var mView: UserInfoContract.NetworkView?) : UserInfoContract.Presenter {
    private var initJob: Job? = null
    private var updateJob: Job? = null
    private var avatarJob: Job? = null
    override fun doInitUserInfo(initPersonInfoRequest: InitPersonInfoRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            try {
                GlobalScope.launch {
                    RemoteService.initPersonInfo(initPersonInfoRequest).execute().body()?.apply {
                        launch(Dispatchers.Main) {
                            onRequestFinished()
                            when (get("code").asInt) {
                                200 -> onInitUserInfoSuccess(this@apply)
                                else -> onInitUserInfoFailed(this@apply)
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

    override fun doUpdateUserInfo(updateInfoRequest: UpdateInfoRequest) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            try {
                GlobalScope.launch {
                    RemoteService.updatePersonInfo(updateInfoRequest).execute().body()?.apply {
                        launch(Dispatchers.Main) {
                            onRequestFinished()
                            when (get("code").asInt) {
                                200 -> onUpdateUserInfoSuccess(this@apply)
                                else -> onUpdateUserInfoFailed(this@apply)
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

    override fun doSetupAvatar(file: File, token: String, username: String) {
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            try {
            GlobalScope.launch {
                val type = MediaType.parse("multipart/form-data")
                val part = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(type, file))
                    RemoteService.setupUserAvatar(part, RequestBody.create(type, token), RequestBody.create(type, username)).execute().body()?.apply {
                        launch(Dispatchers.Main) {
                            onRequestFinished()
                            when (get("code").asInt) {
                                200 -> onSetupAvatarSuccess()
                                else -> onSetupAvatarFailed()
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
        initJob?.cancel()
        updateJob?.cancel()
        avatarJob?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }
}