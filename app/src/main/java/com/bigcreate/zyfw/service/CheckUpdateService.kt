package com.bigcreate.zyfw.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.UpdateManagerActivity
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.app.UpdateImpl

class CheckUpdateService : Service(), UpdateImpl.View {
    private val updateImpl = UpdateImpl(this)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("startCheckUpdate", "true")
        updateImpl.doRequest(packageName)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onUpdateCheckFailed(response: RestResult<UpdateInfo>) {

    }

    override fun onUpdateCheckSuccess(updateInfo: UpdateInfo) {
        updateInfo.apply {
            var currentCode = 0L
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                currentCode = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS).versionCode.toLong()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                currentCode = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS).longVersionCode
            if (updateInfo.versionCode.toLong() > currentCode) {
                val builder = NotificationCompat.Builder(this@CheckUpdateService, "1")
                        .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                        .setContentTitle("检查到新版本")
                        .setContentText("版本号：$versionName")
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("版本号：$versionName\n$changelog")
                                .setSummaryText("应用更新"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(PendingIntent.getActivity(this@CheckUpdateService, 0, Intent(this@CheckUpdateService, UpdateManagerActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }, PendingIntent.FLAG_UPDATE_CURRENT)).addAction(
                                NotificationCompat.Action.Builder(R.drawable.ic_file_download_black_24dp, "立即下载",
                                        PendingIntent.getActivity(this@CheckUpdateService, 0, Intent(this@CheckUpdateService, UpdateManagerActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)).build()
                        )
                this@CheckUpdateService.createNotificationChannel()
                with(NotificationManagerCompat.from(this@CheckUpdateService)) {
                    notify(1, builder.build())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        updateImpl.detachView()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "应用更新"
            val descriptionText = "应用更新"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
    }
}
