package com.bigcreate.zyfw.models

/**
 * Create by Sanlorng on 2018/4/16
 */
data class MessageHeader(
        val id: Int,
        var message: String = "",
        var time: Long,
        var userImg: String = "",
        var userNick: String = "",
        var unreadCount: Int = 0
){
    companion object {
        const val GROUP_ID = 0
    }
}