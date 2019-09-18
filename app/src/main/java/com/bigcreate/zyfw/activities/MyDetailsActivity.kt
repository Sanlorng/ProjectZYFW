package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.AppConfig
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_my_details.*
import kotlinx.android.synthetic.main.layout_navigation_header.*
import org.w3c.dom.Attr

class MyDetailsActivity : AuthLoginActivity(), GetUserInfoImpl.View {
    private val getUserInfoImpl = GetUserInfoImpl(this)
    private lateinit var loginViewModel: LoginViewModel
    private var userId = Attributes.userId
    override fun setContentView() {
        setContentView(R.layout.activity_my_details)
        userId = intent.getIntExtra("userId",Attributes.userId)
    }

    override fun afterCheckLoginSuccess() {
//        setSupportActionBar(toolbarMyDetails)
//        toolbarMyDetails.setNavigationOnClickListener {
//            finish()
//        }
        Glide.with(this)
                .load(Attributes.userImg)
                .circleCrop()
                .into(avatarNavigationHeader)
//        supportActionBar?.apply {
//            title = Attributes.userInfo?.userNick
//            subtitle = Attributes.username.replace(Attributes.username.substring(4,8),"****")
//            setDisplayHomeAsUpEnabled(true)
//
//        }
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.userInfo.observe(this, Observer {
            Glide.with(this)
                    .load(it.userHeadPictureLink)
                    .circleCrop()
                    .into(avatarNavigationHeader)
            nickNavigationHeader.text = it.userNick
            phoneNavigationHeader.text = it.userPhone
            locationNavigationHeader.text = it.userAddress
            sexNavigationHeader.text = it.userSex
            identifyNavigationHeader.text = it.userIdentify
            if (userId != Attributes.userId) {
                buttonEditInfoAccount.setImageResource(R.drawable.ic_outline_comment_24px)
                buttonEditInfoAccount.setOnClickListener {
                    startActivity<ChatActivity>()
                }
            }else {
                buttonEditInfoAccount.setOnClickListener {
                    startActivity(Intent(this, RegisterActivity::class.java).apply {
                        type = "updateInfo"
                    })
                }
            }
        })
        if (loginViewModel.userInfo.value == null) {
            getUserInfoImpl.doRequest(SimpleRequest(Attributes.token,userId))
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onGetUserInfoFailed() {

    }

    override fun onGetUserInfoSuccess(userInfo: UserInfo) {
        loginViewModel.userInfo.postValue(userInfo)
    }

    override fun onUserInfoIsEmpty() {
        startActivityForResult(Intent(this@MyDetailsActivity, RegisterActivity::class.java).apply {
            type = "setupInfo"
        }, RequestCode.SETUP_USER_INFO)

    }

    override fun onResume() {
        super.onResume()
        window.translucentSystemUI()
    }
    override fun onDestroy() {
        super.onDestroy()
        getUserInfoImpl.detachView()
    }
}
