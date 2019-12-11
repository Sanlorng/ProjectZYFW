package com.bigcreate.zyfw.base

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.core.view.updatePadding
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.models.UserInfoByPart
import com.google.gson.JsonObject
import java.lang.Exception
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

object Attributes {
    object Action {
        const val OPEN_PROJECT = "${BuildConfig.APPLICATION_ID}.OPEN_PROJECT"
    }
    val userTemp = SparseArray<UserInfoByPart>()
    const val authority = "content://${BuildConfig.APPLICATION_ID}"
    const val authorityProject = "$authority/project/%s"
    val backgroundExecutors: ExecutorService = Executors.newFixedThreadPool(10)
    private val listeners = HashMap<String, ((newCity: String) -> Unit)>()
    private val userinfoListeners = HashMap<String,((userInfo: UserInfo?) -> Unit)>()
    private val provinceListeners = HashMap<String, ((newProvince: String) -> Unit)>()
    var AppCity = "桂林"
        set(value) {
            if (field != value && value.isNotEmpty()) {
                if (value[value.length-1] == '市') {
                    field = value.subSequence(0,value.length-1).toString()
                }else {
                    field = value
                }
                listeners.forEach {
                    it.value.invoke(field)
                }
            }
        }
    var AppProvince = "广西"
        set(value) {
            if (field != value && value.isNotEmpty()) {
                field = value
                provinceListeners.forEach {
                    it.value.invoke(value)
                }
            }
        }
    var loginUserInfo: LoginModel? = null

    var token
        get() =  try {
                loginUserInfo!!.token
            }catch (e: Throwable) {
                throw e
            }
        set(value) {
            try {
                loginUserInfo!!.token = value
            }catch (e : Exception) {
                throw e
            }
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
    set(value) {
        field = value
        userinfoListeners.forEach {
            it.value.invoke(field)
        }
    }
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
    fun addUserInfoListener(tag: String, value: ((userInfo: UserInfo?) -> Unit)) {
        userinfoListeners[tag] = value
        value.invoke(userInfo)
    }

    fun removeUserInfoListener(tag: String) {
        userinfoListeners.remove(tag)
    }
    fun addProvinceListener(tag: String, value: ((newProvince: String) -> Unit)) {
        provinceListeners[tag] = value
        value.invoke(AppProvince)
    }

    fun removeProvinceListener(tag: String) {
        provinceListeners.remove(tag)
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
    const val DELETE_OK = 11
    const val UPDATE_OK = 12
    const val FAVORITE_OK = 13
    const val UNFAVORITE_OK = 14
    const val LIKE_OK = 15
    const val UNLIKE_OK = 16
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
