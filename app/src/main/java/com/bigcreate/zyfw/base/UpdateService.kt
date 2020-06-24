package com.bigcreate.zyfw.base

import android.util.Log
import com.bigcreate.zyfw.models.CrashLog
import com.bigcreate.zyfw.models.UpdateInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface UpdateService {
    @GET("query")
    fun getAppUpdateVersion(@Query("packageName") packageName: String): Call<UpdateInfo>

    @GET("query/list")
    fun getAppUpdateHistory(@Query("packageName") packageName: String): Call<List<UpdateInfo>>

    @Streaming
    @GET
    fun getAppUpdatePackage(@Url url: String): Call<ResponseBody>

    @POST("crashLog")
    suspend fun uploadCrashLog(@Body crashLog: CrashLog): String

    companion object {
        private const val SERVER_URL = "" //Update Url
        val instance: UpdateService by lazy(mode = LazyThreadSafetyMode.NONE) {
            val client = OkHttpClient.Builder()
                    .addInterceptor {
                        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger {
                            override fun log(message: String) {
                                Log.e("Retrofit",message)
                            }
                        })
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        loggingInterceptor.intercept(it)
                    }
            Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                            .setLenient()
                            .create()))
                    .client(client.build())
                    .build().create(UpdateService::class.java)
        }

        fun getAppUpdateVersion(packageName: String) = instance.getAppUpdateVersion(packageName)
        fun getAppUpdateHistory(packageName: String) = instance.getAppUpdateHistory(packageName)
        fun getAppUpdatePackage(url: String) = instance.getAppUpdatePackage(url)
        suspend fun uploadCrashLog(crashLog: CrashLog) = instance.uploadCrashLog(crashLog)
    }
}