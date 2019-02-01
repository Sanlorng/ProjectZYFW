package com.bigcreate.zyfw.mvp.user

import android.util.Log
import androidx.core.content.edit
import com.bigcreate.library.WebKit
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.base.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginImpl(var mView: LoginContract.View?): LoginContract.Presenter{
    private var job:Job? = null
    override fun doLoginByPass(loginRequest: LoginRequest) {
        if (mView== null)
            throw Exception("Please bind view")
        val view = mView!!
        if (!view.getViewContext().isNetworkActive){
            view.onNetworkFailed()
            return
        }
        view.onRequesting()
        job = GlobalScope.launch {
            RemoteService.loginByPass(loginRequest).execute().body()?.apply {
                if (mView!!.getViewContext().isDebugMode) {
                    Log.e("loginRequest",WebKit.gson.toJson(loginRequest))
                    Log.e("loginResponse", this.toString())
                }
                val code = this.get("code").asInt
                launch(Dispatchers.Main) {
                    when (code) {
                        200 -> {
                            launch(Dispatchers.IO) {
                                view.getViewContext().defaultSharedPreferences.edit {
                                    putBoolean("saved_account",true)
                                    putString("username",loginRequest.username)
                                    putString("password",loginRequest.password)
                                }
                            }
                            mView?.onLoginSuccess(LoginModel(loginRequest.username,loginRequest.password,get("data").asJsonObject.get("newToken").asString))
                        }

                        else -> {
                            mView?.onLoginFailed(this@apply)
                        }
                    }

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