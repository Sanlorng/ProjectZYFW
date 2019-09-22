package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.AppConfig
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.datasource.ProjectListByStudentUserIdDataSource
import com.bigcreate.zyfw.datasource.ProjectListByUserIdDataSource
import com.bigcreate.zyfw.datasource.ProjectListDataSource
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
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
    private lateinit var networkStateViewModel: NetworkStateViewModel
    override fun setContentView() {
        setContentView(R.layout.activity_my_details)
        userId = intent.getIntExtra("userId",Attributes.userId)
        networkStateViewModel = ViewModelProvider(this)[NetworkStateViewModel::class.java]
        listProjectUserDetails.itemAnimator = DefaultItemAnimator()
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
            if (it.userIdentify == "老师") {
                textProjectInfoUserDetails.text = "发布的项目"
                listProjectUserDetails.adapter = ProjectListAdapter { _, item ->
                    startActivity<ProjectDetailsActivity> {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        setDataAndType(Uri.parse(String.format(Attributes.authorityProject, item.projectId)), "project/${item.projectTopic}")
                        putExtra("projectId", item.projectId)

                    }
                }.apply {
                    submitList(PagedList.Builder<Int, SearchModel>(
                            ProjectListByUserIdDataSource(SimplePageRequest(Attributes.token,userId,1),
                                    networkStateViewModel.state),
                            PagedList.Config.Builder()
                                    .setPageSize(10)
                                    .setPrefetchDistance(20)
                                    .build()
                    ).setNotifyExecutor {its ->
                        Handler(Looper.getMainLooper()).post(its)
                    }.setFetchExecutor {its ->
                        Attributes.backgroundExecutors.execute(its)
                    }
                            .build())
                }
            }else if (it.userIdentify == "学生") {
                textProjectInfoUserDetails.text = "参与的项目"
                listProjectUserDetails.adapter = ProjectListAdapter { _, item ->
                    startActivity<ProjectDetailsActivity> {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        setDataAndType(Uri.parse(String.format(Attributes.authorityProject, item.projectId)), "project/${item.projectTopic}")
                        putExtra("projectId", item.projectId)

                    }
                }.apply {
                    submitList(PagedList.Builder<Int, SearchModel>(
                            ProjectListByStudentUserIdDataSource(SimplePageRequest(Attributes.token,userId,1),
                                    networkStateViewModel.state),
                            PagedList.Config.Builder()
                                    .setPageSize(10)
                                    .setPrefetchDistance(20)
                                    .build()
                    ).setNotifyExecutor {its ->
                        Handler(Looper.getMainLooper()).post(its)
                    }.setFetchExecutor {its ->
                        Attributes.backgroundExecutors.execute(its)
                    }
                            .build())
                }
            }
        })
        if (loginViewModel.userInfo.value == null) {
            getUserInfoImpl.doRequest(SimpleRequest(Attributes.token,userId))
        }
        refreshUserDetails.setOnRefreshListener {
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

    override fun onRequestFinished() {
        super.onRequestFinished()
        refreshUserDetails.isRefreshing = false
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
