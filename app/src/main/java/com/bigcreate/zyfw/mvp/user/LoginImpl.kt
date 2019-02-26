package com.bigcreate.zyfw.mvp.user

import androidx.core.content.edit
import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class LoginImpl(var mView: LoginContract.NetworkView?) : LoginContract.Presenter {
    private var job: Job? = null
    override fun doLoginByPass(loginRequest: LoginRequest) {
        if (mView == null)
            throw Exception("Please bind view")
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            job = GlobalScope.launch {
                try {
                    RemoteService.loginByPass(loginRequest).execute().body()?.apply {
                        val code = this.get("code").asInt
                        launch(Dispatchers.Main) {
                            when (code) {
                                200 -> {
                                    launch(Dispatchers.IO) {
                                        getViewContext().defaultSharedPreferences.edit {
                                            putBoolean("saved_account", true)
                                            putString("username", loginRequest.username)
                                            putString("password", loginRequest.password)
                                        }
                                    }
                                    onLoginSuccess(LoginModel(loginRequest.username, loginRequest.password, get("data").asJsonObject.get("newToken").asString))
                                }

                                else -> {
                                    onLoginFailed(this@apply)
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
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }

    override fun cancelJob() {
        job?.cancel()
    }
}