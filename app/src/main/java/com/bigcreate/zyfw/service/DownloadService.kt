package com.bigcreate.zyfw.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.bigcreate.library.BuildConfig
import com.bigcreate.library.startInstallApp
import com.bigcreate.library.toast
import com.bigcreate.zyfw.base.DownloadImpl
import com.bigcreate.zyfw.callback.DownloadCallback
import kotlinx.coroutines.Job
import java.io.File

class DownloadService : Service() {
    private var type = "file"
    private var downloadJob : Job? = null
    private val downloadCallBack = object :DownloadCallback {
        override fun onDownloadFailed(msg: String) {

        }

        override fun onDownloadSuccess(path: String) {
            toast("下载完成")
            if (BuildConfig.DEBUG)
                Log.e("type",type)
            if (type == "update")
                startInstallApp(File(path))
        }

        override fun onDownloading(totalLen: Long, currentLen: Long) {

        }

        override fun onPreDownload() {
            toast("准备下载")
        }
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            val savePath = getStringExtra("savePath")
            val downloadUrl = getStringExtra("downloadUrl")
            this@DownloadService.type = getStringExtra("downloadType")
            downloadJob = DownloadImpl.startDownload(savePath,downloadUrl,downloadCallBack)
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        downloadJob?.cancel()
        super.onDestroy()
    }
}
