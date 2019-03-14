package com.bigcreate.zyfw.service

import android.app.Notification
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
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.LoginModel
import com.bigcreate.zyfw.models.LoginRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.mvp.project.RecommendImpl
import com.bigcreate.zyfw.mvp.user.LoginImpl
import com.google.gson.JsonObject

class RecommendService : JobService(),RecommendImpl.View {
    private val recommendImpl =  RecommendImpl(this)
    private val loginImpl = LoginImpl(object : LoginImpl.View {
        override fun getViewContext(): Context {
            return this@RecommendService
        }

        override fun onLoginFailed(response: JsonObject) {

        }

        override fun onLoginSuccess(loginInfo: LoginModel) {
            recommendImpl.mView = this@RecommendService
            recommendImpl.doRequest(SimpleRequest(Attributes.token, Attributes.username))
        }
    })
    override fun onStartJob(params: JobParameters?): Boolean {
        val lastLaunchTime = defaultSharedPreferences.getLong("last_launch", -1)
        Log.d("onJob", "On")
        if (lastLaunchTime > 0) {
            val relativeTime = System.currentTimeMillis() - lastLaunchTime
            if (relativeTime > 100) {
                if (Attributes.loginUserInfo == null)
                    if (defaultSharedPreferences.getBoolean("saved_account",false))
                    loginImpl.doRequest(LoginRequest(
                            username = defaultSharedPreferences.getString("username","")!!,
                            password = defaultSharedPreferences.getString("password","")!!
                    ))
                else {
                    recommendImpl.mView = this
                    recommendImpl.doRequest(SimpleRequest(Attributes.token, Attributes.username))
                }

            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        recommendImpl.detachView()
        return true
    }

    override fun onGetRecommendFailed(jsonObject: JsonObject) {
    }

    override fun onGetRecommendSuccess(project: Project) {
        Attributes.loginUserInfo?.run {
            project.apply {
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
                                    putExtra("projectTopic", projectTopic)
                                }, PendingIntent.FLAG_UPDATE_CURRENT))
                createNotificationChannel()
                with(NotificationManagerCompat.from(this@RecommendService)) {
                    notify(0, builder.build().apply {
                        flags = flags or Notification.FLAG_AUTO_CANCEL
                    })
                }
            }
        }
    }

    override fun getViewContext(): Context {
        return this
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
