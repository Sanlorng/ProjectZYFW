package com.bigcreate.zyfw.base

import android.content.Context
import android.view.View
import androidx.core.view.updatePadding
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.UserInfo
import com.google.gson.JsonObject
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Attributes {
    object Action {
        const val OPEN_PROJECT = "${BuildConfig.APPLICATION_ID}.OPEN_PROJECT"
    }

    const val authority = "content://${BuildConfig.APPLICATION_ID}"
    const val authorityProject = "$authority/project/%s"
    val backgroundExecutors: ExecutorService = Executors.newFixedThreadPool(5)
    private val listeners = HashMap<String, ((newCity: String) -> Unit)>()
    var AppCity = "桂林市"
        set(value) {
            if (field != value && value.isNotEmpty()) {
                field = value
                listeners.forEach {
                    it.value.invoke(value)
                }
            }
        }
    var loginUserInfo: LoginModel? = null
    var token
        get() = loginUserInfo!!.token
        set(value) {
            loginUserInfo!!.token = value
        }
    var username
        get() = loginUserInfo!!.username
        set(value) {
            loginUserInfo!!.username = value
        }
    var password
        get() = loginUserInfo!!.password
        set(value) {
            loginUserInfo!!.password = value
        }
    var userId
        get() = loginUserInfo!!.userId
        set(value) {
            loginUserInfo!!.userId = value
        }
    var userInfo: UserInfo? = null
    var userImg
        get() = userInfo!!.userHeadPictureLink
        set(value) {
            userInfo!!.userHeadPictureLink = value
        }

    fun addCityListener(tag: String, value: ((newCity: String) -> Unit)) {
        listeners[tag] = value
        value.invoke(AppCity)
    }

    fun removeCityListener(tag: String) {
        listeners.remove(tag)
    }
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
    const val PUBLISH_EXPLORE = 10
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

val JsonObject.newToken: String
    get() = get("newToken").asString
val JsonObject.newTokenFromData: String
    get() = get("data").asJsonObject.get("newToken").asString
val JsonObject.jsonData: JsonObject
    get() = get("data").asJsonObject
val JsonObject.code: Int
    get() = get("code").asInt
val JsonObject.message: String
    get() = get("message").asString
val JsonObject.contentFromData: String
    get() = get("data").asJsonObject.get("content").asString
val JsonObject.content: String
    get() = get("content").asString
val JsonObject.jsonContentFromData: JsonObject
    get() = get("data").asJsonObject.get("content").asJsonObject
val JsonObject.jsonContent: JsonObject
    get() = get("content").asJsonObject

fun JsonObject.getAsString(key: String): String {
    return get(key).asString
}

fun JsonObject.getAsInt(key: String): Int {
    return get(key).asInt
}

fun View.stickHeight() {

    measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    layoutParams = layoutParams.apply {
        height = measuredHeight
    }
}

fun View.paddingStatusBar() {
    updatePadding(top = let {
        it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
    })
}

inline fun <reified T> JsonObject.toObject(): T {
    return toJson().fromJson<T>()
}

inline fun <reified T> JsonObject.getAsObject(key: String): T {
    return get(key).toJson().fromJson<T>()
}
