package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bigcreate.library.*
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.bigcreate.zyfw.viewmodel.LoginStatus
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


abstract class AuthLoginActivity : AppCompatActivity() {
    private var isOnResume = false
    var checkOnResume = false
    private val loginImpl = LoginImpl(object : LoginImpl.View {
        override fun getViewContext(): Context {
            return this@AuthLoginActivity
        }

        override fun onLoginFailed(response: JsonObject) {
            GlobalScope.launch(Dispatchers.Main) {
                startActivityForResult(Intent(this@AuthLoginActivity, LoginActivity::class.java), RequestCode.LOGIN)
            }
        }

        override fun onLoginSuccess(loginInfo: LoginModel) {
            Attributes.loginUserInfo = loginInfo
            afterCheckLoginSuccess()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
//        val viewModel = ViewModelProvider(application as MyApplication)[LoginViewModel::class.java]
//        viewModel.loginStatus.observe(this, Observer {
//            when(it) {
//                LoginStatus.SUCCESS -> afterCheckLoginSuccess()
//                LoginStatus.ERROR_NETWORK -> {
//                    toast("网络问题，登陆失败")
//                }
//                LoginStatus.ERROR_UNKNOWN -> {
//                    toast("未知问题，登录失败")
//                }
//                else -> {
//                    startActivity(LoginActivity::class.java)
//                }
//            }
//        })
//        viewModel.tryLogin(defaultSharedPreferences.getString("username", "")?:"",
//                    defaultSharedPreferences.getString("password", "")?:"")
        checkLogin()
    }

    abstract fun setContentView()
    abstract fun afterCheckLoginSuccess()
    private fun checkLogin() {
        if (Attributes.loginUserInfo == null) {
            if (hasLoginInfo)
                loginImpl.doRequest(LoginRequest(
                        username = defaultSharedPreferences.getString("username", "")!!,
                        password = defaultSharedPreferences.getString("password", "")!!
                ))
            else
                GlobalScope.launch(Dispatchers.Main) {
                    startActivityForResult(Intent(this@AuthLoginActivity, LoginActivity::class.java), RequestCode.LOGIN)
                }
        } else
            afterCheckLoginSuccess()

    }

    override fun onResume() {
        window.translucentSystemUI(true)
        if (Attributes.loginUserInfo == null && checkOnResume) {
            if (hasLoginInfo)
                loginImpl.doRequest(LoginRequest(
                        username = defaultSharedPreferences.getString("username", "")!!,
                        password = defaultSharedPreferences.getString("password", "")!!
                ))
            else
                GlobalScope.launch(Dispatchers.Main) {
                    isOnResume = true
                    startActivityForResult(Intent(this@AuthLoginActivity, LoginActivity::class.java), RequestCode.LOGIN)
                }
        }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.LOGIN) {
            if (resultCode == ResultCode.OK)
                afterCheckLoginSuccess()
            else
                finish()
            isOnResume = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("AppLoginInfo",Attributes.loginUserInfo.toJson())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getString("AppLoginInfo")?.fromJson<LoginModel>()?.apply {
            Attributes.loginUserInfo = this
        }
    }
}