package com.bigcreate.zyfw.activities

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.ChatMessage
import com.bigcreate.zyfw.models.MessageType
import com.bigcreate.zyfw.service.MessageService
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.WebSocket
import java.util.*

class ChatActivity : AuthLoginActivity() {
    private val chatMessages = ArrayList<ChatMessage>()
    private var unreadMessage = 0
    private var sendType = MessageType.SINGLE
    private var chatId = 0
    private lateinit var socketClient: WebSocket
    private var binder: MessageService.MessageBinder? = null
    private val messageTag = "messageDetails"
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MessageService.MessageBinder
            binder?.addOnMessageReceiveListener(messageTag) {
                onNewMessage(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binder?.removeAllListener(messageTag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarChat)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "聊天"
        val layoutParam = toolbarChat.layoutParams as ViewGroup.MarginLayoutParams
        layoutParam.height += let {
            it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
        }
        toolbarChat.layoutParams = layoutParam
        toolbarChat.title = "聊天"
        toolbarChat.setNavigationOnClickListener {
            finish()
        }
        chatId = intent.getIntExtra("chatId", 0)

        buttonSendChat.isEnabled = false
        bindService(Intent(this, MessageService::class.java), connection, Service.BIND_AUTO_CREATE)

        listChatHistory.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        listChatHistory.adapter = ChatMessageAdapter()
        textUnreadChat.isVisible = false
        listChatHistory.itemAnimator = DefaultItemAnimator()
    }

    //WebSocket接受到新消息时的处理
    private fun onNewMessage(message: ChatMessage) {
        if (message.receiveUserId == chatId || message.sendUserId == chatId) {
            chatMessages.add(message)
            unreadMessage++
            if (unreadMessage > 0)
                textUnreadChat.apply {
                    text = getString(R.string.countMessageVar, unreadMessage)
                    isVisible = true
                }

            listChatHistory.adapter?.notifyItemInserted(chatMessages.lastIndex)
            val manager = listChatHistory.layoutManager as LinearLayoutManager
            if (manager.findLastVisibleItemPosition() == chatMessages.lastIndex - 1 || message.sendUserId == Attributes.userId)
                listChatHistory.scrollToPosition(chatMessages.lastIndex)
        }
    }

    override fun afterCheckLoginSuccess() {
        initListener()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_chat)
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    private fun initListener() {
        //发送点击监听
        buttonSendChat.setOnClickListener {
            val str = ChatMessage(inputMessageChat.text.toString(), chatId, Attributes.userId, "", true, chatId)
//            socketClient.send(str)
            binder?.sendMessage(str)
//            onNewMessage(str)
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
            listChatHistory.smoothScrollToPosition(chatMessages.lastIndex - unreadMessage + 1)
        }
        //消息列表监听
        listChatHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.apply {
                    if (layoutManager is LinearLayoutManager) {
                        val count = chatMessages.size - 1 - (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        if (unreadMessage > 0 && count < unreadMessage) {
                            unreadMessage = count
                            textUnreadChat?.text = getString(R.string.countMessageVar, unreadMessage)
                            if (count == 0)
                                textUnreadChat?.isVisible = false
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    inner class ChatMessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return chatMessages.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.apply {
                findViewById<TextView>(R.id.message_item_chat).apply {
                    text = chatMessages[position].msg
                    maxWidth = resources.displayMetrics.widthPixels * 5 / 8

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false)) {}
        }

        override fun getItemViewType(position: Int): Int {
            return if (chatMessages[position].sendUserId != Attributes.userId) R.layout.item_message_chat_left else R.layout.item_message_chat_right
        }

    }
}
