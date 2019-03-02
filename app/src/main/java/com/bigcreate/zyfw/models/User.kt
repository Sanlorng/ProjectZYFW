package com.bigcreate.zyfw.models


import org.litepal.crud.LitePalSupport

/**
 * Create by Sanlorng on 2018/4/9
 */
data class User(val name: String, val password: String, val token: String, val userId: String) : LitePalSupport() {
}

data class UserInfo(var username: String?, var userId: String, var userNick: String, var userSex: String, var userIdentify: String, var userAddress: String, var userPhone: String, var imgBase64: String, var userHeadPictureLink: String)