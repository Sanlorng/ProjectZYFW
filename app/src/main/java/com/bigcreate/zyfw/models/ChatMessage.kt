package com.bigcreate.zyfw.models

//data class ChatMessage(val msg: String,val type: Int, val to: String,val sendTime: String, val username:String)
data class ChatMessage(
        val msg: String,
        var receiveUserId: Int,
        var sendUserId: Int,
        val time: String,
        val to: Boolean,
		var chatId:Int = 0
)

data class ChatUser(val userId: Int)
data class UserOnlineInfo(val onlineInfo: IntArray,val onlineCount: Int)
enum class MessageType {
	GROUP,SINGLE
}
