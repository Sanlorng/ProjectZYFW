package com.bigcreate.zyfw.callback

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCallback<T> : Callback<T> {
    private var failureBlock: (Throwable.(call: Call<T>) -> Unit)? = null
    private var responseBlock: (Response<T>.(call: Call<T>) -> Unit)? = null

    fun throwable(block: (Throwable.(call: Call<T>) -> Unit)) {
        failureBlock = block
    }

    fun response(block: (Response<T>.(call: Call<T>) -> Unit)) {
        responseBlock = block
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        failureBlock?.invoke(t, call)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        responseBlock?.invoke(response, call)
    }
}

inline fun <T> Call<T>.enqueue(block: RetrofitCallback<T>.() -> Unit) {
    val call = RetrofitCallback<T>()
    block(call)
    enqueue(call)
}