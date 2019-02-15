package com.bigcreate.zyfw.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.bigcreate.library.WebKit
import com.bigcreate.library.getRequest
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.ProjectResponse
import com.google.gson.JsonParser

class RecommendService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        val lastLaunchTime = defaultSharedPreferences.getLong("last_launch",-1)
        Log.d("onJob","On")
        if (lastLaunchTime>0){
            val relativeTime = System.currentTimeMillis()-lastLaunchTime
            if (relativeTime> 100){
                Thread{
                    val loginUserId = defaultSharedPreferences.getString("user_id",null)
                    val response = WebKit.okClient.getRequest(WebInterface.FINDDATA_URL+loginUserId)?.string()
                    //val model = JsonParser().parse(response).asJsonObject
                    //if (model.get("code").asInt == 200)

                    "".run {
                        Log.d("content",response)
                        val builder = NotificationCompat.Builder(this@RecommendService,"0")
                                .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                                .setContentText("由Job发出的通知")
                                .setContentTitle("推荐项目")
                                .setStyle(NotificationCompat.BigTextStyle()
                                        .bigText("由Job发出的通知"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        createNotificationChannel()
                        with(NotificationManagerCompat.from(this@RecommendService)){
                            notify(0,builder.build())
                        }
                    }
                }.start()
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "推荐"
            val descriptionText = "推荐项目"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("0",name,importance).apply{
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}
