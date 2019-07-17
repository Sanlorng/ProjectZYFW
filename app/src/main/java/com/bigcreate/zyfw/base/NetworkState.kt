package com.bigcreate.zyfw.base

enum class Status {
    FAILED,
    RUNNING,
    SUCCESS
}
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String = ""){
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String = "") = NetworkState(Status.FAILED,msg)
    }
}