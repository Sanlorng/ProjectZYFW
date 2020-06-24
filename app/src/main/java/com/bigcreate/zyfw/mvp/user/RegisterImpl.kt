package com.bigcreate.zyfw.mvp.user

import androidx.core.content.edit
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.RegisterRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterImpl(mView: View?) : BaseMultiPresenterImpl<RegisterImpl.View>(mView) {
    private lateinit var registerRequest: RegisterRequest
    private lateinit var phoneNumber: String
    private val registerInter = object : PresenterInter<RegisterRequest, JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
//                            onRegisterSuccess()
                            val loginModel = LoginModel(registerRequest.username, registerRequest.password,
                                    newTokenFromData, jsonData.getAsInt("userId"))
                            onRegisterSuccess(loginModel)
                            GlobalScope.launch(Dispatchers.IO) {
                                getViewContext().defaultSharedPreferences.edit {
                                    putBoolean("saved_account", true)
                                    putString("username", registerRequest.username)
                                    putString("password", registerRequest.password)
                                }
                                Attributes.loginUserInfo = loginModel
                            }
                        }
                        else -> {
                            onRegisterFailed(this@apply)
                        }
                    }
                }
            }
        }

        override fun backgroundRequest(request: RegisterRequest): JsonObject? {
            return RemoteService.register(request).execute().body()
        }
    }
    private val sendCodeInter = object : PresenterInter<String, JsonObject?> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
                            onValidCodeSend()
                        }
                        else -> {
                            onValidCodeFail(this@apply)
                        }
                    }
                }
                if (checkPhoneNumber(phoneNumber))
                    onValidCodeSend()
            }
        }

        override fun backgroundRequest(request: String): JsonObject? {
            phoneNumber = request
            return if (!checkPhoneNumber(phoneNumber)) RemoteService.getPhoneCode(request).execute().body()
            else null
        }
    }
    private val resetPassInter = object : PresenterInter<RegisterRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
                            val loginModel = LoginModel(registerRequest.username, registerRequest.password,
                                    newTokenFromData, jsonData.getAsInt("userId"))
                            onRegisterSuccess(loginModel)
                            GlobalScope.launch(Dispatchers.IO) {
                                getViewContext().defaultSharedPreferences.edit {
                                    putBoolean("saved_account", true)
                                    putString("username", registerRequest.username)
                                    putString("password", registerRequest.password)
                                }
                                Attributes.loginUserInfo = loginModel
                            }
                        }
                        else -> {
                            onRegisterFailed(this@apply)
                        }
                    }
                }
            }
        }

        override fun backgroundRequest(request: RegisterRequest): JsonObject? {
            return RemoteService.resetPassword(request).execute().body()
        }
    }

    fun doRegister(registerRequest: RegisterRequest) {
        this.registerRequest = registerRequest
        addJob(registerInter.doRequest(mView, registerRequest))
    }

    fun doResetPassword(registerRequest: RegisterRequest) {
        this.registerRequest = registerRequest
        addJob(resetPassInter.doRequest(mView, registerRequest))
    }

    fun doSendValidCode(phoneNumber: String) {
        addJob(sendCodeInter.doRequest(mView, phoneNumber))
    }

    private fun checkPhoneNumber(phoneNumber: String): Boolean {
        return false
    }

    interface View : BaseNetworkView {
        fun onRegisterSuccess(loginModel: LoginModel)
        fun onRegisterFailed(response: JsonObject)
        //        fun onForgetPassSuccess(loginModel: LoginModel)
//        fun onForgetPassFailed(response: JsonObject)
        fun onValidCodeSend()

        fun onValidCodeFail(response: JsonObject)
    }
}