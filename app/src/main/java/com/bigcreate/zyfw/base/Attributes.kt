package com.bigcreate.zyfw.base

import android.content.Context
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.UserInfo

object Attributes {
    var loginUserInfo: LoginModel? = null
    var token: String? = null
    var isLogin = false
    var userInfo: UserInfo? = null
    var userImg: String? = null
}

val Context.hasLoginInfo: Boolean
    get() = defaultSharedPreferences.getBoolean("saved_account", false)
val Context.isDebugMode: Boolean
    get() = defaultSharedPreferences.getBoolean("is_debug_mode", BuildConfig.DEBUG)

object RequestCode {
    const val LOGIN = 1
    const val REGISTER = 2
    const val EDIT_PROJECT = 3
    const val AVATAR = 4
}

object ResultCode {
    const val OK = 1
    const val ERROR = -1
}

object NotiChannel {
    const val RECOMMEND = 0
    const val APP_UPDATE = 1
    const val MESSAGE = 2
}