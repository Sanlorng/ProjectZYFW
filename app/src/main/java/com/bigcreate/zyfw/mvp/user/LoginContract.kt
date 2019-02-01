package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.LoginByPassResponse
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.bigcreate.zyfw.mvp.base.LoginModel
import com.google.gson.JsonObject

interface LoginContract {
    interface Presenter: BasePresenter{
        fun doLoginByPass(loginRequest: LoginRequest)
    }

    interface View: BaseView{
        fun onLoginSuccess(loginInfo: LoginModel)
        fun onLoginFailed(response: JsonObject)
    }
}