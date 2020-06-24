package com.bigcreate.zyfw.base

import android.util.Log
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.callback.DownloadCallback
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File

class DownloadImpl {
    private var downloadJobs = ArrayList<Job>()
    private var lastSecond = 0L
    fun startDownload(savePath: String, url: String, callback: DownloadCallback?): Job {
        return GlobalScope.launch {
            GlobalScope.launch(Dispatchers.Main) {
                callback?.onPreDownload()
            }
            DownloadService.downloadFile(url).execute().body()?.apply {
                if (BuildConfig.DEBUG)
                    Log.e("path", savePath)
                val file = File(savePath)
                file.deleteOnExit()
                file.createNewFile()
                file.length()
                val buffer = ByteArray(1024)
                val out = file.outputStream()
                val bs = byteStream()
                var len = bs.read(buffer)
                var current = len.toLong()
                val total = contentLength()
//                val downloadingJob = GlobalScope.launch {
//                        while (true) {
//                            withContext(Dispatchers.Main) {
//                                callback?.onDownloading(total, current)
//                            }
//                            delay(1000)
//                        }
//                }
                var time = 0L
                while (len > 0) {
                    out.write(buffer, 0, len)
                    current += len
                    len = bs.read(buffer)
                    if (System.currentTimeMillis() - time >= 100 || time == 0L) {
                        time = System.currentTimeMillis()
                        launch(Dispatchers.Main) {
                            callback?.onDownloading(total, current)
                        }
                    }
                    Log.e("downloading","len: $len")
                }
                bs.close()
                GlobalScope.launch(Dispatchers.Main) {
//                    downloadingJob.cancel()
                    callback?.onDownloadSuccess(file.absolutePath)
                }
            }
        }.apply {
            downloadJobs.add(this)
        }
    }

    fun stopDownloads() {
        downloadJobs.forEach {
            it.cancel()
        }
    }

    fun stopDownload(job: Job) {
        downloadJobs.remove(job)
        job.cancel()
    }

    companion object {
        private val instance by lazy(LazyThreadSafetyMode.NONE) {
            DownloadImpl()
        }

        fun startDownload(saveName: String, url: String, callback: DownloadCallback?) = instance.startDownload(saveName, url, callback)
        fun stopDownloads() = instance.stopDownloads()
        fun stopDownload(job: Job) = instance.stopDownload(job)
    }

    interface DownloadService {
        @Streaming
        @GET
        fun downloadFile(@Url url: String): Call<ResponseBody>

        companion object {
            private const val SERVER_URL = "" //Update Url
            val instance: DownloadService by lazy(mode = LazyThreadSafetyMode.NONE) {
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
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client.build())
                        .build().create(DownloadService::class.java)
            }

            fun downloadFile(url: String) = instance.downloadFile(url)
        }
    }
}