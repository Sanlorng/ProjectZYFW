package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.mvp.base.LoginModel
import com.bigcreate.zyfw.mvp.user.LoginContract
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.coroutines.*

class LaunchActivity : AppCompatActivity(),LoginContract.View {
    private val presenter = LoginImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        window.defaultStatusBarMask(false)
        window.transucentSystemUI(true)
        window.setStatusBarMask(false)
            when {
                hasLoginInfo -> {
                    GlobalScope.launch {
                        val request = LoginRequest(defaultSharedPreferences.getString("username", "")!!, defaultSharedPreferences.getString("password", "")!!)
                        presenter.doLoginByPass(request)
                    }
                }
                else -> {
                    showProgress(false)
                    button_login_launch.setOnClickListener {
                        startActivityForResult(Intent(this@LaunchActivity, LoginActivity::class.java), RequestCode.LOGIN)
                    }
                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RequestCode.LOGIN -> {
                if (resultCode == ResultCode.OK){
                    startActivity(MainActivity::class.java)
                    finish()
                }
            }
        }
    }

    override fun getViewContext(): Context {
        return this
    }



    override fun onLoginFailed(response: JsonObject) {
        toast("登陆失败")
        showProgress(false)
    }

    override fun onLoginSuccess(loginInfo: LoginModel) {
        Attributes.loginUserInfo = loginInfo
        startActivity(MainActivity::class.java)
        finish()
    }

    override fun onNetworkFailed() {
        toast("网络连接失败")
    }

    override fun onRequesting() {
        showProgress(true)
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    private fun showProgress(value: Boolean){
        button_login_launch.isVisible = !value
        progressBar_launch.isVisible = value
    }
}
