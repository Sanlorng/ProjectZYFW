package com.bigcreate.zyfw.base

import android.content.Context
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.UserInfo
import com.google.gson.JsonObject

object Attributes {
    var loginUserInfo: LoginModel? = null
    var token
    get() = loginUserInfo!!.token
    set(value) {
        loginUserInfo!!.token = value}
    var username
    get() = loginUserInfo!!.username
    set(value) {
        loginUserInfo!!.username = value}
    var password
    get() = loginUserInfo!!.password
    set(value) {
        loginUserInfo!!.password = value
    }
    var userInfo: UserInfo? = null
    var userImg: String? = null
}

val Context.hasLoginInfo: Boolean
    get() = defaultSharedPreferences.getBoolean("saved_account", false)

object RequestCode {
    const val LOGIN = 1
    const val REGISTER = 2
    const val EDIT_PROJECT = 3
    const val AVATAR = 4
    const val OPEN_PROJECT = 5
    const val SETUP_USER_INFO = 6
    const val SELECT_IMAGE = 7
    const val SELECT_VIDEO = 8
    const val INSTALL_PERMISSION = 9
}

object ResultCode {
    const val OK = 1
    const val ERROR = -1
}

object NotificationChannel {
    const val RECOMMEND = 0
    const val APP_UPDATE = 1
    const val MESSAGE = 2
}

val JsonObject.newToken:String
get()  = get("newToken").asString
val JsonObject.newTokenFromData:String
get() = get("data").asJsonObject.get("newToken").asString
val JsonObject.jsonData:JsonObject
get() = get("data").asJsonObject
val JsonObject.code:Int
get() = get("code").asInt
val JsonObject.message:String
get() = get("message").asString
val JsonObject.contentFromData:String
    get() = get("data").asJsonObject.get("content").asString
val JsonObject.content:String
    get() = get("content").asString
val JsonObject.jsonContentFromData:JsonObject
    get() = get("data").asJsonObject.get("content").asJsonObject
val JsonObject.jsonContent:JsonObject
    get() = get("content").asJsonObject
fun JsonObject.getAsString(key: String):String {
    return get(key).asString
}

fun JsonObject.getAsInt(key: String): Int {
    return get(key).asInt
}

inline fun <reified T>JsonObject.toObject():T {
    return toJson().fromJson<T>()
}
 inline fun  <reified T>JsonObject.getAsObject(key: String):T {
     return get(key).toJson().fromJson<T>()
 }
