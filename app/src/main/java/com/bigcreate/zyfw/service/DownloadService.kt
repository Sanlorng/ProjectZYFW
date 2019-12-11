package com.bigcreate.zyfw.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bigcreate.library.BuildConfig
import com.bigcreate.library.startInstallApp
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.DownloadImpl
import com.bigcreate.zyfw.callback.DownloadCallback
import kotlinx.coroutines.Job
import java.io.File

class DownloadService : Service() {
    private var type = "file"
    private var downloadJob: Job? = null
    private val downloadCallBack = object : DownloadCallback {
        override fun onDownloadFailed(msg: String) {
            notificationBuilder.setContentTitle("下载失败")
            manageService.notify(3,notificationBuilder.build())
        }

        override fun onDownloadSuccess(path: String) {
            toast("下载完成")
            notificationBuilder.setProgress(100,100,false)
                    .setContentTitle("下载完成")
            manageService.notify(3,notificationBuilder.build())
            if (BuildConfig.DEBUG)
                Log.e("type", type)
            if (type == "update")
                startInstallApp(File(path))
        }

        override fun onDownloading(totalLen: Long, currentLen: Long) {
            notificationBuilder.setProgress(10000,((currentLen /totalLen.toFloat())*10000).toInt(),false)
                    .setContentTitle("正在下载")
//            toast("正在下载$currentLen, 总计 $totalLen")
//            Log.e("download","正在下载$currentLen, 总计 $totalLen")
            manageService.notify(3,notificationBuilder.build())
        }

        override fun onPreDownload() {
            toast("准备下载")
            manageService.notify(3,notificationBuilder.build())
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var manageService: NotificationManagerCompat
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            val savePath = getStringExtra("savePath")
            val downloadUrl = getStringExtra("downloadUrl")
            val downloadTitle = getStringExtra("downloadTitle")?:downloadUrl.split("/").last()
            this@DownloadService.type = getStringExtra("downloadType")
            if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT)
                createNotificationChannel()
            notificationBuilder = NotificationCompat.Builder(this@DownloadService,"3")
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setSummaryText("下载"))
                    .setContentTitle("准备下载")
                    .setContentText(downloadTitle)
                    .setOngoing(false)
                    .setProgress(0,0,true)
            downloadJob = DownloadImpl.startDownload(savePath, downloadUrl, downloadCallBack)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        manageService = NotificationManagerCompat.from(this)
        manageService.createNotificationChannel(NotificationChannel("3","下载",NotificationManager.IMPORTANCE_DEFAULT))
    }

    override fun onDestroy() {
        downloadJob?.cancel()
        super.onDestroy()
    }
}
