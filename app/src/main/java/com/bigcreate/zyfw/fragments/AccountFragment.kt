package com.bigcreate.zyfw.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.*
import com.bigcreate.zyfw.adapter.MenuListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_navigation_header.*
import kotlinx.android.synthetic.main.layout_navigation_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment(),MainActivity.ChildFragment {
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
            startActivityForResult(Intent(context!!, RegisterActivity::class.java).apply {
                type = "setupInfo"
            }, RequestCode.SETUP_USER_INFO)
        }

        override fun getViewContext(): Context {
            return context!!
        }
    })

    private val menuList: ArrayList<MenuListAdapter.MenuItem> = arrayListOf(
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
            it.add( MenuListAdapter.MenuItem(R.id.testInterface,
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
        navigationMain.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.setting_menu -> startActivity(SettingsActivity::class.java)
//                R.id.startReleaseProjectNavigation -> startActivity(ReleaseProjectActivity::class.java)
                R.id.testInterface -> startActivity(TestInterfaceActivity::class.java)
            }
            true
        }
        navigationMain.setOnClickListener {
            startActivity(MyDetailsActivity::class.java)
        }
        Attributes.loginUserInfo?.apply {
            onLoginSuccess()
        }
        navigationMain.getHeaderView(0).apply {
            buttonEditInfoAccount.setOnClickListener {
                startActivity(Intent(context!!,RegisterActivity::class.java).apply {
                    type = "updateInfo"
                })
            }
        }
    }

    private fun UserInfo.onUserInfoLoad() {
        navigationMain.getHeaderView(0).apply {
            nickNavigationHeader.text = userNick
            phoneNavigationHeader.text = userPhone
            Glide.with(this)
                    .load(Attributes.userImg)
                    .circleCrop()
                    .into(avatarNavigationHeader)
            listAccountMenu.layoutManager = GridLayoutManager(context, 4)
            listAccountMenu.adapter = MenuListAdapter(menuList) {
                when (it.id) {
                    R.id.setting_menu -> startActivity(SettingsActivity::class.java)
                    R.id.testInterface -> startActivity(TestInterfaceActivity::class.java)
                    R.id.userFavoriteNavigation -> startActivity(Intent(context!!,FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin",2)
                    })
                    R.id.userJoinedNavigation -> startActivity(Intent(context!!,FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin",1)
                    })
                    R.id.userReleasedNavigation -> startActivity(Intent(context!!,FavAndJoinActivity::class.java).apply {
                        putExtra("favOrJoin",0)
                    })
                }
            }
        }
    }

    override fun onLoginSuccess() {
        if (Attributes.userInfo!= null)
            Attributes.userInfo?.apply {
                onUserInfoLoad()
            }
        else
            Attributes.loginUserInfo?.apply {
                getUserInfoImpl.doRequest(SimpleRequest(token,userId))
            }
    }
    override fun onResume() {
        super.onResume()
        navigationMain.requestApplyInsets()
    }
}
