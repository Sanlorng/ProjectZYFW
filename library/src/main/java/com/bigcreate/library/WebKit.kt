package com.bigcreate.library

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient

object WebKit {
    val okClient:OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { OkHttpClient() }
    val gson:Gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Gson() }
    val mediaJson:MediaType by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MediaType.parse("application/json")!!}
}