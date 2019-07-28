package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.user.LoginImpl
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
}