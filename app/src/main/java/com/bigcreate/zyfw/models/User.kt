package com.bigcreate.zyfw.models


import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * Create by Sanlorng on 2018/4/9
 */
data class User(val name: String, val  password: String, val token: String, val userId: String): LitePalSupport(){
    var isLogin : Boolean ?= null
    var registTime: Date ?= null
}

data class UserInfo(var username: String,var userId:String, var userNick: String, var userSex: String, var userIdentify: String, var userAddress: String, var userPhone: String, var imgBase64: String)