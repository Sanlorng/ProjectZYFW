package com.bigcreate.zyfw.mvp.user

import androidx.core.content.edit
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginImpl(mView: View?) : BasePresenterImpl<LoginRequest, JsonObject, LoginImpl.View>(mView) {
    private lateinit var loginRequest: LoginRequest
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                when (code) {
                    200 -> {
                        GlobalScope.launch(Dispatchers.IO) {
                            getViewContext().defaultSharedPreferences.edit {
                                putBoolean("saved_account", true)
                                putString("username", loginRequest.username)
                                putString("password", loginRequest.password)
                            }
                        }
                        Attributes.loginUserInfo = LoginModel(loginRequest.username, loginRequest.password, newTokenFromData)
                        onLoginSuccess(Attributes.loginUserInfo!!)
                    }
                    else -> {
                        onLoginFailed(this@apply)
                    }
                }
            }
        }
    }

    override fun backgroundRequest(request: LoginRequest): JsonObject? {
        loginRequest = request
        return RemoteService.loginByPass(loginRequest).execute().body()
    }

    interface View : BaseNetworkView {
        fun onLoginSuccess(loginInfo: LoginModel)
        fun onLoginFailed(response: JsonObject)
    }
}