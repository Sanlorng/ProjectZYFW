package com.bigcreate.zyfw.viewmodel

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Attr

class LoginViewModel(application: Application): AndroidViewModel(application) {
    var userInfo = MutableLiveData<UserInfo>()
    var token = MutableLiveData<String>()
    var userId:Int = -1
    var loginStatus = MutableLiveData<LoginStatus>()
    var getUserInfoStatus = MutableLiveData<UserInfoStatus>()
    fun refreshUserInfo(uid:Int = userInfo.value?.userId?.toInt()?:userId) {
        getUserInfoStatus.postValue(UserInfoStatus.STATUS_GETTING)
        RemoteService.instance.getUserInfoBySelf(SimpleRequest(token.value?:"",uid))
                .enqueue {
                    error {
                        getUserInfoStatus.postValue(UserInfoStatus.ERROR_NETWORK)
                    }
                    response {
                        body()?.apply {
                            if (isJsonNull) {
                                getUserInfoStatus.postValue(UserInfoStatus.STATUS_EMPTY_USER)
                            }
                            else
                                when (code) {
                                    200 -> jsonContentFromData.toJson().fromJson<UserInfo>().apply {
                                        token.postValue(jsonContentFromData.getAsString("token"))
                                        Attributes.token = token.value?:""
                                        userInfo.postValue(this)
                                        getUserInfoStatus.postValue(UserInfoStatus.STATUS_SUCCESS)
                                    }
                                    404 -> getUserInfoStatus.postValue(UserInfoStatus.STATUS_EMPTY_USER)
                                    else -> getUserInfoStatus.postValue(UserInfoStatus.ERROR_PERMISSION)
                                }
                        }
                    }
                }
    }

    fun tryLogin(userName: String,password: String) {
        if (token.value == null) {
            reLogin(userName, password)
        }else {
            loginStatus.postValue(LoginStatus.STATUS_LOGIN)
            loginStatus.postValue(LoginStatus.SUCCESS)
        }
    }

    fun tryGetUserInfo() {
        if (userInfo.value == null) {
            refreshUserInfo()
        }else {
            getUserInfoStatus.postValue(UserInfoStatus.STATUS_GETTING)
            getUserInfoStatus.postValue(UserInfoStatus.STATUS_SUCCESS)
        }
    }
    fun reLogin(userName:String,password:String) {
        loginStatus.postValue(LoginStatus.STATUS_LOGIN)
        RemoteService.loginByPass(LoginRequest(userName,password)).enqueue {
            error {
                loginStatus.postValue(LoginStatus.ERROR_NETWORK)
            }

            response {
                body()?.apply {
                    when(code) {
                        200 -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                getApplication<MyApplication>().defaultSharedPreferences.edit {
                                    putBoolean("saved_account", true)
                                    putString("username", userName)
                                    putString("password", password)
                                }
                            }
                            Attributes.loginUserInfo = LoginModel(
                                    userName,
                                    password,
                                    newTokenFromData,
                                    jsonData.getAsInt("userId")
                            )
                            token.postValue(newTokenFromData)
                            loginStatus.postValue(LoginStatus.SUCCESS)
                            userId = jsonData.getAsInt("userId")
                            refreshUserInfo(userId)
                        }
                        400 -> {
                            loginStatus.postValue(LoginStatus.ERROR_PASS)
                        }
                        else -> {
                            loginStatus.postValue(LoginStatus.ERROR_UNKNOWN)
                        }
                    }
                }
            }
        }
    }

}
enum class LoginStatus {
    STATUS_LOGIN,
    STATUS_EXIT,
    SUCCESS,
    ERROR_UNKNOWN,
    ERROR_PASS,
    ERROR_USER,
    ERROR_USER_OR_PASS,
    ERROR_NETWORK
}

enum class UserInfoStatus {
    STATUS_GETTING,
    STATUS_EMPTY_USER,
    ERROR_NETWORK,
    ERROR_PERMISSION,
    STATUS_SUCCESS
}