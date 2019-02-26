package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface RegisterContract {
    interface Presenter : BasePresenter {
        fun doRegister(registerRequest: RegisterRequest)
        fun doSendValidCode(phoneNumber: String)
    }

    interface NetworkView : BaseNetworkView {
        fun onRegisterSuccess(loginModel: LoginModel)
        fun onRegisterFaild(response: JsonObject)
        fun onValidCodeSend()
        fun onValidCodeFail(response: JsonObject)
    }
}