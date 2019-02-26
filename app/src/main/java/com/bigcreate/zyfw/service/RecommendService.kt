package com.bigcreate.zyfw.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bigcreate.library.WebKit
import com.bigcreate.library.fromJson
import com.bigcreate.library.getRequest
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.SimpleRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecommendService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        val lastLaunchTime = defaultSharedPreferences.getLong("last_launch", -1)
        Log.d("onJob", "On")
        if (lastLaunchTime > 0) {
            val relativeTime = System.currentTimeMillis() - lastLaunchTime
            if (relativeTime > 100) {
//                Thread{
                val loginUserId = defaultSharedPreferences.getString("user_id", null)
                val response = WebKit.okClient.getRequest(WebInterface.FINDDATA_URL + loginUserId)?.string()
                //val model = JsonParser().parse(response).asJsonObject
                //if (model.get("code").asInt == 200)
                GlobalScope.launch {
                    Attributes.loginUserInfo?.apply {
                        RemoteService.getRecommendData(SimpleRequest(token, username)).execute().body()?.apply {
                            val data = get("data").asJsonObject
                            when (get("code").asInt) {
                                200 -> {
                                    token = data.get("newToken").asString
                                    if (!data.get("content").isJsonNull) {
                                        data.get("content").toJson().fromJson<Project>().apply {
                                            val builder = NotificationCompat.Builder(this@RecommendService, "0")
                                                    .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                                                    .setContentText(projectTopic)
                                                    .setContentTitle("推荐项目")
                                                    .setStyle(NotificationCompat.BigTextStyle()
                                                            .bigText("由Job发出的通知"))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                    .setContentIntent(
                                                            PendingIntent.getActivity(this@RecommendService,
                                                                    0, Intent(this@RecommendService, ProjectDetailsActivity::class.java).apply {
                                                                putExtra("projectId", projectId)
                                                                        .putExtra("projectTopic", projectTopic)
                                                            }, PendingIntent.FLAG_UPDATE_CURRENT))
                                            createNotificationChannel()
                                            with(NotificationManagerCompat.from(this@RecommendService)) {
                                                notify(0, builder.build())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
//                    "".run {
//                        Log.d("content",response)
//
//                    }
//                }.start()
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "推荐"
            val descriptionText = "推荐项目"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("0", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}
