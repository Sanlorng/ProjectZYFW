package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import kotlinx.coroutines.*
import okhttp3.MultipartBody

class UserInfoImpl(var mView:UserInfoContract.View?):UserInfoContract.Presenter {
    var initJob:Job? = null
    var updateJob: Job? = null
    var avatarJob: Job? = null
    override fun doInitUserInfo(initPersonInfoRequest: InitPersonInfoRequest) {
        val view = mView!!
        GlobalScope.launch {
            RemoteService.initPersonInfo(initPersonInfoRequest).execute().body()?.apply {
                launch(Dispatchers.Main) {
                    when (get("code").asInt) {
                        200 -> view.onInitUserInfoSuccess(this@apply)
                        else -> view.onInitUserInfoFailed(this@apply)
                    }
                }
            }
        }

    }

    override fun doUpdateUserInfo(updateInfoRequest: UpdateInfoRequest) {
        val view = mView!!
        GlobalScope.launch {
            RemoteService.updatePersonInfo(updateInfoRequest).execute().body()?.apply {
                launch(Dispatchers.Main){
                    when(get("code").asInt){
                        200 -> view.onUpdateUserInfoSuccess(this@apply)
                        else -> view.onUpdateUserInfoFailed(this@apply)
                    }
                }
            }
        }
    }

    override fun doSetupAvatar(file: MultipartBody.Part, body: Map<String,String>) {
        val view = mView!!
        GlobalScope.launch {
            RemoteService.setupUserAvatar(file,body).execute().body()?.apply {
                launch(Dispatchers.Main){
                    when(get("code").asInt){
                    }
                    view.onSetupAvatarSuccess()
                }
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