package com.bigcreate.zyfw.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.util.containsKey
import androidx.core.util.set
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.MyApplication
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.project.RecommendImpl
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Error
import java.net.URI
import java.nio.ByteBuffer

class MessageService : Service(), RecommendImpl.View {
    private lateinit var contactWebsocket: WebSocketClient
    private var binder: MessageBinder = MessageBinder()
    private val url = String.format(WebInterface.WS_URL, Attributes.userId)
    private var retryJob: Job? = null
    private var recommendJob: Job? = null
    private val recommendImpl = RecommendImpl(this)
//    private val url = "ws://192.168.199.124:8080/ProjectForDaChuang/chat/${Attributes.userId}"
    override fun onCreate() {
        super.onCreate()
        Log.e("service start", url)
//        contactWebsocket = MessageSocket(URI.create(url))
        contactWebsocket = MessageSocket(URI.create(url))
        recommendJob = GlobalScope.launch {
            while (true) {
                try {
                    recommendImpl.doRequest(SimpleRequest(Attributes.token, Attributes.userId))
                    delay(1000 * 120)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        contactWebsocket.close(2, "")
        retryJob?.cancel()
        recommendJob?.cancel()
        super.onDestroy()
    }

    override fun getViewContext(): Context {
        return  this
    }

    override fun onGetRecommendFailed(jsonObject: JsonObject) {

    }

    override fun onGetRecommendSuccess(project: Project) {
        Attributes.loginUserInfo?.run {
            project.apply {
                val builder = NotificationCompat.Builder(this@MessageService, "0")
                        .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                        .setContentText(projectContent)
                        .setContentTitle(projectTopic)
//                        .setCategory("推荐项目")
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(projectContent)
                                .setSummaryText("推荐项目")
                                .setBigContentTitle(projectTopic))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(
                                PendingIntent.getActivity(this@MessageService,
                                        0, Intent(this@MessageService, ProjectDetailsActivity::class.java).apply {
                                    //                                    putExtra("projectId", projectId)
//                                    putExtra("projectTopic", projectTopic)
                                    addCategory(Intent.CATEGORY_DEFAULT)
                                    setDataAndType(Uri.parse(String.format(Attributes.authorityProject, projectId)), "project/${projectTopic}")
                                    putExtra("projectId", projectId)
                                }, PendingIntent.FLAG_UPDATE_CURRENT))
                createNotificationChannel()
                with(NotificationManagerCompat.from(this@MessageService)) {
                    notify(0, builder.build().apply {
                        flags = flags or Notification.FLAG_AUTO_CANCEL
                    })
                }
            }
        }
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    interface BadgeListener {
        fun addBadge(uid: Int)
        fun cleanBadge(uid: Int)
    }

    inner class MessageSocket(uri: URI) : WebSocketClient(uri) {
        override fun connect() {
            super.connect()
            println("onConnect")
            Log.e("onConnect","")
        }
        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("onClose $reason")
            if (retryJob == null) {
                retryJob = GlobalScope.launch {
                    while (true) {
                        delay(3000)
                        if (contactWebsocket?.isClosed) {
                            try {
                                contactWebsocket = MessageSocket(URI.create(url))
                                contactWebsocket.connect()
                                retryJob?.cancel()
                                retryJob = null
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            if (code != 2) {
//                contactWebsocket = MessageSocket(URI.create("ws://192.168.199.124:8080/ProjectForDaChuang/chat/39"))
                try {
//                    contactWebsocket = MessageSocket(URI(url))
//                    contactWebsocket.connect()
                }catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        override fun onError(ex: Exception?) {
//            reconnect()
            println(ex)
            ex?.printStackTrace()
        }

        override fun onMessage(message: String?) {
            Log.e("message",message?:"")
            println(message)
            Handler(Looper.getMainLooper()).post {
                if (message != null)
                    binder.onMessage(message)
            }
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            println("onOpen")
            binder.getUserList()
        }


    }

    inner class MessageBinder : Binder() {
        private val onlineUser = ChatMessage("", 0, 0, 0, false)
        private val userListener = HashMap<String, ((newList: List<ChatUser>) -> Unit)>()
        private val messageListener = HashMap<String, ((message: ChatMessage) -> Unit)>()
        private val badgeListener = HashMap<String, BadgeListener>()
        private var sendId = 0
        private var onLineList = List(0) {
            ChatUser(0)
        }

        fun getOnlineList() {
//            contactWebsocket.send(onlineUser.toJson())
        }

        fun onMessage(text: String) {
            Log.e("text", text)
            if (text.contains("onlineInfo")) {
                val info = text.fromJson<UserOnlineInfo>()
                onLineList = List(info.onlineInfo.size) {
                    ChatUser(info.onlineInfo[it])
                }
                userListener.forEach {
                    it.value.invoke(onLineList)
                }
            } else {
                val message = text.fromJson<ChatMessage>()
                if (message.sendUserId == 0 && message.receiveUserId == 0) {
                    message.receiveUserId = Attributes.userId
                    message.sendUserId = sendId
                }
                message.chatId = if (message.sendUserId != Attributes.userId) message.sendUserId else message.receiveUserId
                messageListener.forEach {
                    it.value.invoke(message)
                }
                if (message.sendUserId != Attributes.userId ) {

                    if (MyApplication.resumeCount == 0) {
                        if (!Attributes.userTemp.containsKey(message.sendUserId)) {
                            RemoteService.getHeadLinkAndNick(message.chatId).enqueue {
                                response {
                                    body()?.apply {
                                        Attributes.userTemp[userId] = this
                                        notification(this, message.msg)
                                    }
                                }
                            }
                        } else {
                            notification(Attributes.userTemp[message.sendUserId], message.msg)
                        }
                    }
                   badgeListener.forEach {
                       if (message.sendUserId != Attributes.userId) {
                           it.value.addBadge(if (message.to) message.sendUserId else 0)
                       }
                  }
                }
            }
        }

        fun notification(userInfo: UserInfoByPart,message: String) {
            Glide.with(this@MessageService)
                    .asBitmap()
                    .load(userInfo.userHeadPictureLink)
                    .circleCrop()
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val person = Person.Builder()
                                    .setName(userInfo.userNick)
                                    .setKey(userInfo.userId.toString())
                                    .setIcon(IconCompat.createWithBitmap(resource))
                                    .build()
                            with(NotificationManagerCompat.from(this@MessageService)) {
                                notify(userInfo.userId,NotificationCompat.Builder(this@MessageService,"message")
                                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                        .setContentIntent(PendingIntent.getActivity(this@MessageService,0,Intent(this@MessageService,ChatActivity::class.java).apply {
                                            putExtra("chatId",userInfo.userId)
                                        },PendingIntent.FLAG_UPDATE_CURRENT))
                                        .setAutoCancel(true)
                                        .setStyle(NotificationCompat.MessagingStyle(person)
                                                .setConversationTitle(userInfo.userNick)
                                                .addMessage(message,System.currentTimeMillis(),person))
                                        .setColor(getColor(R.color.colorAccent))
                                        .build())
                            }
                        }
                    })
        }

        fun addOnOnlineListUpdateListener(tag: String, listener: ((newList: List<ChatUser>) -> Unit)) {
            userListener[tag] = listener
            listener.invoke(onLineList)
        }

        fun removeOnlineListUpdateListener(tag: String) {
            userListener.remove(tag)
        }

        fun addBadgeListener(tag: String, listener: BadgeListener) {
            badgeListener[tag] = listener
        }

        fun removeBadgeListener(tag: String) {
            badgeListener.remove(tag)
        }

        fun cleanBadge(uid: Int) {
            badgeListener.forEach {
                it.value.cleanBadge(uid)
            }
        }


        fun sendMessage(message: ChatMessage) {
            sendId = message.sendUserId
            Log.e("message", message.toJson())
            val json = JsonObject()
            json.addProperty("sendUserId",message.sendUserId)
            json.addProperty("receiveUserId",message.receiveUserId)
            json.addProperty("to",message.to)
            json.addProperty("msg",message.msg)
            try {
                contactWebsocket.send(json.toString())
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun addOnMessageReceiveListener(tag: String, listener: ((message: ChatMessage) -> Unit)) {
            messageListener[tag] = listener
            Log.e("messageReceive",messageListener.count().toString())
            Log.e("isOpen",contactWebsocket.isOpen.toString())
            if (messageListener.count() == 1 && contactWebsocket.isOpen.not()) {
                Log.e("toConnect","true")
                try {
                    contactWebsocket.connect()
                }catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        fun removeOnMessageReceiveListener(tag: String) {
            messageListener.remove(tag)
            if (messageListener.count() == 0) {
//                contactWebsocket.close(2)
            }
        }

        fun removeAllListener(tag: String) {
            messageListener.clear()
            badgeListener.clear()
        }

        fun getUserList() {
            val json = JsonObject()
            json.addProperty("sendUserId",0)
            json.addProperty("receiveUserId",0)
            json.addProperty("to",true)
            json.addProperty("msg","")
            try {
                contactWebsocket.send(json.toString())
            }catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
