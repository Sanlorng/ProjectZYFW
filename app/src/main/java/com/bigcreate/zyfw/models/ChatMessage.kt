package com.bigcreate.zyfw.models

import com.google.gson.annotations.Expose

//data class ChatMessage(val msg: String,val type: Int, val to: String,val sendTime: String, val username:String)
data class ChatMessage(
        val msg: String,
        var receiveUserId: Int,
        var sendUserId: Int,
        val time: String,
        val to: Boolean,
        @Expose
        var chatId: Int = 0
)

data class ChatUser(val userId: Int)
data class UserOnlineInfo(val onlineInfo: IntArray, val onlineCount: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserOnlineInfo

        if (!onlineInfo.contentEquals(other.onlineInfo)) return false
        if (onlineCount != other.onlineCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = onlineInfo.contentHashCode()
        result = 31 * result + onlineCount
        return result
    }
}

enum class MessageType {
    GROUP, SINGLE
}
