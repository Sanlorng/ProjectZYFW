package com.bigcreate.zyfw.base

import android.app.Activity
import android.app.Application
import android.app.LauncherActivity
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.util.forEach
import androidx.lifecycle.*
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.activities.CrashActivity
import com.bigcreate.zyfw.activities.MainActivity
import com.bigcreate.zyfw.models.CrashLog
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import org.litepal.LitePal
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import java.nio.ByteBuffer

/**
 * Create by Sanlorng on 2018/4/9
 */
class MyApplication : Application(),ViewModelStoreOwner {
    //    private val
    private val viewModelStore = ViewModelStore()
    private lateinit var webSocketClient : WebSocketClient
    companion object {
        val EXCEPTIONS = ArrayList<Throwable>()
        var resumeCount = 0
    }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }

    override fun onCreate() {
        super.onCreate()
//        defaultSharedPreferences.edit {
//            putString("nightModeSwitch","1")
//        }
        AppCompatDelegate.setDefaultNightMode((defaultSharedPreferences.getString("nightModeSwitch",AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString())?:"1").toInt())
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            e.printStackTrace()
            EXCEPTIONS.add(e)
            startActivity<CrashActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        LitePal.initialize(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

            }

            override fun onActivityDestroyed(activity: Activity?) {

            }

            override fun onActivityPaused(activity: Activity?) {
                resumeCount --
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity is MainActivity || activity is ChatActivity) {
                    val manager = NotificationManagerCompat.from(this@MyApplication)
                    Attributes.userTemp.forEach { key, value ->
                        manager.cancel(key)
                    }
                }
                resumeCount ++
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity?) {

            }

            override fun onActivityStopped(activity: Activity?) {

            }
        })
    }
}