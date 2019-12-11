package com.bigcreate.zyfw.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.*
import com.bigcreate.zyfw.adapter.MenuListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.MyApplication
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.bigcreate.zyfw.viewmodel.UserInfoStatus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_navigation_header.*
import kotlinx.android.synthetic.main.layout_navigation_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : LoginFragment(), MainActivity.ChildFragment {
    private val getUserInfoImpl = GetUserInfoImpl(object : GetUserInfoImpl.View {
        override fun onGetUserInfoFailed() {
            toast("获取用户信息失败")
        }

        override fun onGetUserInfoSuccess(userInfo: UserInfo) {
            userInfo.apply {
                onUserInfoLoad()

            }
        }

        override fun onUserInfoIsEmpty() {
//            startActivityForResult(Intent(context!!, RegisterActivity::class.java).apply {
//                type = "setupInfo"
//            }, RequestCode.SETUP_USER_INFO)
        }

        override fun getViewContext(): Context {
            return context!!
        }
    })

    private var menuList: ArrayList<MenuListAdapter.MenuItem> = arrayListOf(
            MenuListAdapter.MenuItem(R.id.userReleasedNavigation,
                    R.drawable.ic_outline_send_24px,
                    R.string.releasedProject),
            MenuListAdapter.MenuItem(R.id.userJoinedNavigation,
                    R.drawable.ic_favorite_border_black_24dp,
                    R.string.joinedProject),
            MenuListAdapter.MenuItem(R.id.userFavoriteNavigation,
                    R.drawable.ic_star_border_black_24dp,
                    R.string.favoriteProject),
            MenuListAdapter.MenuItem(R.id.setting_menu,
                    R.drawable.ic_outline_settings_24px,
                    R.string.settings)).also {
        if (BuildConfig.DEBUG)
            it.add(MenuListAdapter.MenuItem(R.id.testInterface,
                    R.drawable.ic_outline_build_24px,
                    R.string.interfaceTest))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        Attributes.addUserInfoListener("accountFragment") {
            it?.onUserInfoLoad()
        }
        Attributes.loginUserInfo?.apply {
            onLoginSuccess()
        }
        buttonEditInfoAccount.setOnClickListener {
                startActivity(Intent(context!!, RegisterActivity::class.java).apply {
                    type = "updateInfo"
                })
            }

        getLoginViewModel().getUserInfoStatus.observe(this, Observer {
            when(it) {
                UserInfoStatus.STATUS_SUCCESS -> {
                    getLoginViewModel().userInfo.value?.onUserInfoLoad()
                }
            }
        })
        getLoginViewModel().tryGetUserInfo()
    }

    private fun UserInfo.onUserInfoLoad() {

            nickNavigationHeader.text = userNick
            phoneNavigationHeader.text = userPhone
            Glide.with(this@AccountFragment)
                    .load(userHeadPictureLink)
                    .circleCrop()
                    .into(avatarNavigationHeader)
            locationNavigationHeader.text = userAddress
            sexNavigationHeader.text = userSex + "性"
            identifyNavigationHeader.text = userIdentify
        emailNavigationHeader.text = userEmail
        userSchoolNavigationHeader.text = schoolName
            listAccountMenu.layoutManager = GridLayoutManager(context, 4)
            listAccountMenu.adapter = MenuListAdapter(menuList) {
                when (it.id) {
                    R.id.setting_menu -> startActivity<SettingsActivity>()
                    R.id.testInterface -> startActivity<TestInterfaceActivity>()
                    R.id.userFavoriteNavigation -> startActivity(Intent(context!!, FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin", 1)
                        putExtra("identify",userIdentify)
                    })
                    R.id.userJoinedNavigation -> startActivity(Intent(context!!, FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin", 0)
                        putExtra("identify",userIdentify)
                    })
                    R.id.userReleasedNavigation -> startActivity(Intent(context!!, FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin", 0)
                        putExtra("identify",userIdentify)
                    })
                }
            }
            reloadMenuList(userIdentify)
            listAccountMenu.adapter?.notifyDataSetChanged()

    }

    private fun reloadMenuList(userIdentify: String) {
        menuList.clear()
        menuList.addAll(when(userIdentify) {
            "老师" -> {
                arrayListOf(
                        MenuListAdapter.MenuItem(R.id.userReleasedNavigation,
                                R.drawable.ic_outline_send_24px,
                                R.string.releasedProject),
                        MenuListAdapter.MenuItem(R.id.userFavoriteNavigation,
                                R.drawable.ic_star_border_black_24dp,
                                R.string.favoriteProject),
                        MenuListAdapter.MenuItem(R.id.setting_menu,
                                R.drawable.ic_outline_settings_24px,
                                R.string.settings))
            }
            "学生" -> {
                arrayListOf(
                        MenuListAdapter.MenuItem(R.id.userJoinedNavigation,
                                R.drawable.ic_favorite_border_black_24dp,
                                R.string.joinedProject),
                        MenuListAdapter.MenuItem(R.id.userFavoriteNavigation,
                                R.drawable.ic_star_border_black_24dp,
                                R.string.favoriteProject),
                        MenuListAdapter.MenuItem(R.id.setting_menu,
                                R.drawable.ic_outline_settings_24px,
                                R.string.settings))
            }
            else -> {
                arrayListOf(
                        MenuListAdapter.MenuItem(R.id.setting_menu,
                                R.drawable.ic_outline_settings_24px,
                                R.string.settings))
            }
        })
        if (BuildConfig.DEBUG)
            menuList.add(MenuListAdapter.MenuItem(R.id.testInterface,
                    R.drawable.ic_outline_build_24px,
                    R.string.interfaceTest))
    }
    override fun onLoginSuccess() {
        if (Attributes.userInfo != null)
            Attributes.userInfo?.apply {
                onUserInfoLoad()
            }
        else
            Attributes.loginUserInfo?.apply {
                getUserInfoImpl.doRequest(SimpleRequest(token, userId))
            }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        Attributes.removeUserInfoListener("accountFragment")
    }
}
