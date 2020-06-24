package com.bigcreate.zyfw.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.util.containsKey
import androidx.core.util.remove
import androidx.core.util.set
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.alibaba.idst.nls.nlsclientsdk.transport.javawebsocket.JWebSocketClient
import com.amap.api.col.n3.tj
import com.bigcreate.library.startActivity
import com.bigcreate.library.statusBarLight
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.fragments.AccountFragment
import com.bigcreate.zyfw.fragments.ExploreFragment
import com.bigcreate.zyfw.fragments.HomeFragment
import com.bigcreate.zyfw.fragments.MessageFragment
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.user.GetUserInfoImpl
import com.bigcreate.zyfw.service.MessageService
import com.bigcreate.zyfw.service.RecommendService
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer


class MainActivity : AuthLoginActivity() {
    private lateinit var homeFragment: HomeFragment
    private lateinit var messageFragment: MessageFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var webSocket: WebSocketClient
    private val messageTag = "MainActivity"
    private var binder: MessageService.MessageBinder? = null
    private var badgeCount = 0
        set(value) {
            field = value
            if (field != 0) {
                bottomNavigationMain?.getOrCreateBadge(R.id.messageFragment)?.number = value
                bottomNavigationMain?.getOrCreateBadge(R.id.messageFragment)?.isVisible = true
            } else {
                bottomNavigationMain?.getOrCreateBadge(R.id.messageFragment)?.number = value
                bottomNavigationMain?.getOrCreateBadge(R.id.messageFragment)?.isVisible = false
            }
            Log.e("field",field.toString())
        }
    private val badgeListener = object : MessageService.BadgeListener {
        override fun addBadge(uid: Int) {
            Log.e("onBadge",uid.toString())
            var key = uid
            if (uid == 0) {
//                key = -1
            }
            if (uidList.containsKey(key).not())
                uidList[key] = 0
            uidList[key] = uidList[key] + 1
            badgeCount += 1
        }

        override fun cleanBadge(uid: Int) {
            var key = uid
            if (uid == 0) {
//                key = -1
            }
            if (uidList.containsKey(key)) {
                val value = uidList[key]
                uidList.remove(key, value)
                badgeCount -= value
            }
//            Log.e("uid",uid.toString())
//            Log.e("value",value.toString())
        }
    }
    private var uidList = SparseIntArray()
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MessageService.MessageBinder
            binder?.addBadgeListener(messageTag, badgeListener)
            binder?.addOnMessageReceiveListener(messageTag) {
//                messageFragment.onNewMessage(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    private val getUserInfoImpl = GetUserInfoImpl(object : GetUserInfoImpl.View {
        override fun onGetUserInfoFailed() {
            toast("获取用户信息失败")
        }

        override fun onGetUserInfoSuccess(userInfo: UserInfo) {
            userInfo.apply {
                Attributes.userInfo = this
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelGroup = NotificationChannelGroup("user",userNick)
                    val channelMessage = NotificationChannel("message","普通消息",NotificationManager.IMPORTANCE_HIGH)
                    val manager = NotificationManagerCompat.from(this@MainActivity)
                    manager.createNotificationChannelGroup(channelGroup)
                    channelMessage.group = "user"
                    manager.createNotificationChannel(channelMessage)
                }
                val releaseView = bottomNavigationMain.menu.findItem(R.id.releaseProjectActivity)
                if (userIdentify == "2" || userIdentify == "老师") {
                    releaseView.isVisible = true
                    bottomNavigationMain.forEach {
                        if (it is BottomNavigationMenuView) {
                            if (it[2] is BottomNavigationItemView && it[2] is FrameLayout) {
                                val view = FloatingActionButton(it[2].context)
//                            view.layoutParams = ViewGroup
//                            bottomNavigationMain.addView(view)
//                            it[2].setOnClickListener(null)
//                            view.shrink()
                                it[2].background = null
                                view.size = FloatingActionButton.SIZE_MINI
//                            view.setImageResource(R.drawable.ic_add_black_24dp)
//                            view.background = getDrawable(R.drawable.ripple_circle_primary)
                                view.setImageResource(R.drawable.ic_add_black_24dp)
                                val typedValue = TypedValue()
                                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                                view.backgroundTintList = getColorStateList(typedValue.resourceId)
                                view.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                view.compatElevation = 0f
                                view.elevation = 0f
                                view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                    //                                val margin = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_margin)
//                                updateMargins(margin,margin,margin,margin)
                                    gravity = Gravity.CENTER
                                }
//                            view.iconTint = getColor(obtainStyledAttributes())
                                view.setOnClickListener {
                                    startActivity<ReleaseProjectActivity>()
                                }
                                val vg = it[2] as FrameLayout
                                vg.forEach { itss ->
                                    itss.isVisible = false
                                }
                                vg.addView(view)
                            }
                        }
                    }
                } else {
                    releaseView.isVisible = false
                    bottomNavigationMain.forEach {
                        if (it is BottomNavigationMenuView) {
                            val itemView = it[bottomNavigationMain.menu.size() / 2] as FrameLayout
                            itemView.forEach { its ->
                                if (its is FloatingActionButton)
                                    itemView.removeView(its)
                            }
                        }
                    }
                }
            }
        }

        override fun onUserInfoIsEmpty() {
            startActivityForResult(Intent(this@MainActivity, RegisterActivity::class.java).apply {
                type = "setupInfo"
            }, RequestCode.SETUP_USER_INFO)
        }

        override fun getViewContext(): Context {
            return this@MainActivity
        }
    })

    override fun setContentView() {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.navigationBarColor = getColor(R.color.colorPrimary)
        setContentView(R.layout.activity_main)
        homeFragment = HomeFragment()
        messageFragment = MessageFragment()
        exploreFragment = ExploreFragment()
        accountFragment = AccountFragment()
        viewpagerMain.adapter = FragmentAdapter(supportFragmentManager, listOf(
                homeFragment, messageFragment, exploreFragment, accountFragment
        ))
        viewpagerMain.offscreenPageLimit = 4
        bottomNavigationMain.setOnNavigationItemSelectedListener {
            window.statusBarLight(it.itemId != R.id.accountFragment)

            when (it.itemId) {
                R.id.releaseProjectActivity -> {
//                    startActivity(ReleaseProjectActivity::class.java)
                    false
                }
                else -> {
                    viewpagerMain.setCurrentItem(getFragmentPosition(it.itemId), true)
                    true
                }
            }
        }
        viewpagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                bottomNavigationMain.selectedItemId = getFragmentId(position)
            }
        })
        checkOnResume = true
        GlobalScope.launch(Dispatchers.Default) {
            Log.e("onLaunch","")
            // chat url
            webSocket = object : WebSocketClient(URI("")) {
                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.e("onClose",reason?:"")
                }

                override fun onError(ex: java.lang.Exception?) {
                    ex?.printStackTrace()
                }

                override fun onMessage(message: String?) {
                    Log.e("onMessage","")
                }

                override fun onMessage(bytes: ByteBuffer?) {
                    super.onMessage(bytes)
                    Log.e("onMessageByte","")
                }

                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.e("onOpen","")
                }

                override fun onWebsocketPing(conn: WebSocket?, f: Framedata?) {
                    super.onWebsocketPing(conn, f)
                    Log.e("onPing",f.toString())
                }

                override fun onWebsocketPong(conn: WebSocket?, f: Framedata?) {
                    super.onWebsocketPong(conn, f)
                    Log.e("onPong","")
                    send(JsonObject().apply {
                        addProperty("test","test")
                    }.toString())
                }

            }
//            webSocket.connect()
        }
    }

    override fun onResume() {
        super.onResume()
        window.statusBarLight(bottomNavigationMain.selectedItemId != R.id.accountFragment)
    }

    override fun afterCheckLoginSuccess() {
        val messageIntent = Intent(this, MessageService::class.java)
        //startService(messageIntent)
        bindService(messageIntent, connection, Service.BIND_AUTO_CREATE)
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 0)
        } else {
            initView()
        }
        scheduleNotification()

    }


    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.first() == arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).first() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            initView()
        }
    }

    private fun initView() {
        defaultSharedPreferences.edit().putLong("last_launch", System.currentTimeMillis()).apply()
        Attributes.loginUserInfo?.run {
            getUserInfoImpl.doRequest(SimpleRequest(token, userId))

        }
        viewpagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                bottomNavigationMain.selectedItemId = getFragmentId(position)
            }
        })
    }

    private fun getFragmentPosition(id: Int): Int = when (id) {
        R.id.homeFragment -> 0
        R.id.messageFragment -> 1
        R.id.exploreFragment -> 2
        R.id.accountFragment -> 3
        else -> 0
    }

    private fun getFragmentId(position: Int): Int = when (position) {
        0 -> R.id.homeFragment
        1 -> R.id.messageFragment
        2 -> R.id.exploreFragment
        3 -> R.id.accountFragment
        else -> R.id.homeFragment
    }

    private fun scheduleNotification() {
        try {
            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(1, ComponentName(packageName, RecommendService::class.java.name))
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(1000)
                    .build()
            jobScheduler.schedule(jobInfo)
            val intent = Intent(this, RecommendService::class.java)
            startService(intent)
            Log.d("schedule", "is set")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("schedule", "failed")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.SETUP_USER_INFO -> if (resultCode == ResultCode.OK) {
                Attributes.loginUserInfo?.apply {
                    getUserInfoImpl.doRequest(SimpleRequest(token, userId))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    interface ChildFragment {
        fun onLoginSuccess()
    }
}
