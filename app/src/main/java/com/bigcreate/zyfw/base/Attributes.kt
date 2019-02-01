package com.bigcreate.zyfw.base

import android.content.Context
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.base.LoginModel

object Attributes{
    var loginUserInfo:LoginModel? =null
    val isLogin: Boolean = false
    var token: String? = null
}
val Context.isLogin: Boolean
get() = Attributes.isLogin

val Context.hasLoginInfo: Boolean
get() = defaultSharedPreferences.getBoolean("saved_account",false)
val Context.isDebugMode: Boolean
get() = defaultSharedPreferences.getBoolean("is_debug_mode",BuildConfig.DEBUG)

object RequestCode{
    const val LOGIN = 1
    const val REGISTER = 2
}
object ResultCode {
    const val OK = 1
    const val ERROR = -1
}