package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.bigcreate.zyfw.mvp.base.LoginModel
import com.google.gson.JsonObject

interface RegisterContract {
    interface Presenter: BasePresenter{
        fun doRegister(registerRequest: RegisterRequest)
        fun doSendValidCode(phoneNumber: String)
    }

    interface View: BaseView{
        fun onRegisterSuccess(loginModel: LoginModel)
        fun onRegisterFaild(response: JsonObject)
        fun onValidCodeSend()
        fun onValidCodeFail(response: JsonObject)
    }
}