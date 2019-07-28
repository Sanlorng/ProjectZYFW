package com.bigcreate.zyfw.models

/**
 * Create by Sanlorng on 2018/4/16
 */
data class MessageHeader(
        val id: Int,
        var message: String = "",
        var time: Long,
        var userImg: String = ""
)