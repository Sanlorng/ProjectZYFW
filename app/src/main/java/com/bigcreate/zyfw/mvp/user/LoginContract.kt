package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface LoginContract {
    interface Presenter : BasePresenter {
        fun doLoginByPass(loginRequest: LoginRequest)
    }

    interface NetworkView : BaseNetworkView {
        fun onLoginSuccess(loginInfo: LoginModel)
        fun onLoginFailed(response: JsonObject)
    }
}