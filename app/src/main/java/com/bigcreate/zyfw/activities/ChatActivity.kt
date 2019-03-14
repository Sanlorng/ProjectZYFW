package com.bigcreate.zyfw.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.WebKit
import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.models.ChatMessage
import com.bigcreate.zyfw.models.SendType
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.nio.charset.Charset
import java.util.*

class ChatActivity : AuthLoginActivity() {
    private val chatMessages = ArrayList<ChatMessage>()
    private var unreadMessage = 0
    private var webSocketLink = "${WebInterface.WS_URL}0/%s/0"
    private var sendType = SendType.GLOBAL
    private lateinit var socketClient:WebSocket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "聊天"

        val layoutParam = toolbarChat.layoutParams as AppBarLayout.LayoutParams
//        layoutParam.topMargin += let {
//            it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
//        }
        layoutParam.height += let {
            it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
        }
        toolbarChat.layoutParams = layoutParam
        toolbarChat.requestApplyInsets()
        toolbarChat.title = "聊天"
        toolbarChat.setNavigationOnClickListener {
            finish()
        }

        initChatType(intent.getStringExtra("type"))
        buttonSendChat.isEnabled = false
        Log.e("webLink",webSocketLink)
        GlobalScope.launch {
            socketClient = WebKit.okClient.newWebSocket(Request.Builder()
                .url(webSocketLink)
                .build(), object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e("message",text)
                runOnUiThread {
                    onNewMessage(text)
                }
                super.onMessage(webSocket, text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.e("message",bytes.string(Charset.defaultCharset()))
                super.onMessage(webSocket, bytes)
            }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.e("onOpen","true")
                    super.onOpen(webSocket, response)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("onFailure","true")
                    t.printStackTrace()
                    super.onFailure(webSocket, t, response)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.e("onClosing","true")
                    super.onClosing(webSocket, code, reason)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.e("onClosed","true")
                    super.onClosed(webSocket, code, reason)
                }
        }) }

        listChatHistory.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        listChatHistory.adapter = ChatMessageAdapter()
        textUnreadChat.isVisible = false
        listChatHistory.itemAnimator = DefaultItemAnimator()
    }

    //WebSocket接受到新消息时的处理
    private fun onNewMessage(json : String){
        val message = json.fromJson<ChatMessage>()
        chatMessages.add(message)
        unreadMessage++
        if (unreadMessage>0)
                textUnreadChat.apply {
                    text = getString(R.string.countMessageVar,unreadMessage)
                    isVisible = true
                }

        listChatHistory.adapter?.notifyItemInserted(chatMessages.lastIndex)
        val manager = listChatHistory.layoutManager as LinearLayoutManager
        if (manager.findLastVisibleItemPosition()==chatMessages.lastIndex-1||message.username == Attributes.username)
            listChatHistory.scrollToPosition(chatMessages.lastIndex)
    }
    override fun afterCheckLoginSuccess() {
        initListener()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_chat)
    }

    override fun onDestroy() {
        socketClient.close(1000,"destroy")
        super.onDestroy()
    }
    private fun initChatType(sendType: String?) {
        when(sendType) {
            SendType.GLOBAL -> webSocketLink = webSocketLink.format(Attributes.username)
            SendType.PROJECT -> {}
            null -> {

            }
            else -> finish()
        }
    }
    private fun initListener() {
        //发送点击监听
        buttonSendChat.setOnClickListener {
            val str = ChatMessage(inputMessageChat.text.toString(),0,sendType, "",Attributes.username).toJson()
            socketClient.send(str)
            inputMessageChat.text.clear()
        }
        //输入监听
        inputMessageChat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.isEmpty()) {
                    buttonSendChat.isEnabled = false
                    buttonSendChat.setColorFilter(ContextCompat.getColor(this@ChatActivity, R.color.color737373))
                } else {
                    buttonSendChat.isEnabled = true
                    buttonSendChat.setColorFilter(ContextCompat.getColor(this@ChatActivity, R.color.colorAccent))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        //未读消息监听
        textUnreadChat.setOnClickListener {
            it.isVisible = false
            listChatHistory.smoothScrollToPosition(chatMessages.lastIndex - unreadMessage+1)
        }
        //消息列表监听
        listChatHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.apply {
                    if (layoutManager is LinearLayoutManager){
                        val count = chatMessages.size - 1 - (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        if (unreadMessage>0 && count < unreadMessage){
                            unreadMessage = count
                            textUnreadChat?.text = getString(R.string.countMessageVar,unreadMessage)
                            if (count == 0)
                                textUnreadChat?.isVisible = false
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
    inner class ChatMessageAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun getItemCount(): Int {
            return chatMessages.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.apply {
                findViewById<TextView>(R.id.message_item_chat).apply {
                    text = chatMessages[position].msg
                    maxWidth = resources.displayMetrics.widthPixels *5/8

                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object :RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(viewType,parent,false)){}
        }

        override fun getItemViewType(position: Int): Int {
            return if (chatMessages[position].username!=Attributes.loginUserInfo?.username) R.layout.item_message_chat_left else R.layout.item_message_chat_right
        }

    }
}
