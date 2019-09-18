package com.bigcreate.zyfw.service

import android.util.Log
import java.lang.Exception
import java.net.URI
//import javax.websocket.*
//
//@ClientEndpoint
//class WebSocketClient(uri: URI) {
//    private var userSession : Session? = null
//    private var onOpenBlock: (() -> Unit)? = null
//    private var onMessageBlock: (String.() -> Unit)? = null
//    private var onErrorBlock:(Throwable.() -> Unit)? = null
//    private var onCloseBlock:(CloseReason.() -> Unit)? = null
//    init {
//        try {
//            ContainerProvider.getWebSocketContainer().connectToServer(this, uri)
//        }catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    @OnOpen
//    fun onOpen(session: Session) {
//        Log.e("onOpen","")
//        this.userSession = session
//        onOpenBlock?.invoke()
//    }
//
//    @OnClose
//    fun onClose(session: Session,reason: CloseReason) {
//        Log.e("onClose",reason.reasonPhrase)
//        userSession = null
//        onCloseBlock?.invoke(reason)
//    }
//
//    @OnError
//    fun onError(throwable: Throwable) {
//        Log.e("onError","")
//        throwable.printStackTrace()
//        onErrorBlock?.invoke(throwable)
//    }
//
//    @OnMessage
//    fun onMessage(message: String) {
//        onMessageBlock?.invoke(message)
//    }
//
//    fun onOpen(onOpenBlock: (() -> Unit)) {
//        this.onOpenBlock = onOpenBlock
//    }
//
//    fun onMessage(onMessageBlock: (String.() -> Unit)) {
//        this.onMessageBlock = onMessageBlock
//    }
//
//    fun onError(onErrorBlock:(Throwable.() -> Unit)) {
//        this.onErrorBlock = onErrorBlock
//    }
//
//    fun onClose(onCloseBlock:(CloseReason.() -> Unit)) {
//        this.onCloseBlock = onCloseBlock
//    }
//}
//fun Any.webSocket(uri: String,socket:(WebSocketClient.() -> Unit)? = null):WebSocketClient {
//    return webSocket(URI.create(uri))
//}
//
//fun Any.webSocket(uri: URI,socket:(WebSocketClient.() -> Unit)? = null):WebSocketClient {
//    val web = WebSocketClient(uri)
//    socket?.invoke(web)
//    return web
//}