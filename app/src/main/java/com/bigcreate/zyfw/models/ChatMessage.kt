package com.bigcreate.zyfw.models

data class ChatMessage(val msg: String,val type: Int, val to: String,val sendTime: String, val username:String)
object MessageType{
    const val GLOBAL = 0
    const val PROJECT = 1
    const val USER = 2
}
object SendType {
    const val GLOBAL = "all"
    const val PROJECT = "proAll"
}