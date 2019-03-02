package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.startActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import kotlinx.android.synthetic.main.activity_my_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyDetailsActivity : AuthLoginActivity(),GetUserInfoImpl.View {
    private val getUserInfoImpl = GetUserInfoImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)
        setSupportActionBar(toolbarMyDetails)
        toolbarMyDetails.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.apply {
            title = "个人信息"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onGetUserInfoFailed() {

    }

    override fun onGetUserInfoSuccess(userInfo: UserInfo) {

    }

    override fun onUserInfoIsEmpty() {
        startActivityForResult(Intent(this@MyDetailsActivity, SignUpActivity::class.java).apply {
                type = "setupInfo"
        }, RequestCode.SETUP_USER_INFO)

    }
    override fun onDestroy() {
        super.onDestroy()
        getUserInfoImpl.detachView()
    }
}
