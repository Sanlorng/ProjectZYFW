package com.bigcreate.library

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.OkHttpClient

object WebKit {
    val okClient:OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { OkHttpClient() }
    val gson:Gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Gson() }
    val mediaJson:MediaType by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MediaType.parse("application/json")!!}
}
val Any?.toJson: () -> String
get() = {
    if (this == null)
        "{\"message:\":\"this object is null\"}"
    else
        WebKit.gson.toJson(this)
}

fun <T> String.fromJson(classOfT: Class<T>): T{
    return WebKit.gson.fromJson<T>(this,classOfT)
}

inline fun <reified T> String.fromJson() = WebKit.gson.fromJson<T>(this ,object :TypeToken<T>(){}.type)