package com.bigcreate.zyfw.fragments

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.set
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MainActivity
import com.bigcreate.zyfw.adapter.MessageListAdapter
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.ChatMessage
import com.bigcreate.zyfw.models.MessageHeader
import com.bigcreate.zyfw.service.MessageService
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.layout_search_bar.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MessageFragment : Fragment(), MainActivity.ChildFragment {
    private var param1: String? = null
    private var param2: String? = null
    private val success = 1
    private var messageMap = SparseArray<MessageHeader>()
    private val messageList = ArrayList<MessageHeader>()
    private var binder: MessageService.MessageBinder? = null
    private var messageTag = "MessageFragment"
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MessageService.MessageBinder
            binder?.addOnMessageReceiveListener(messageTag) {
                onNewMessage(it)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.bindService(Intent(context!!,MessageService::class.java),connection,Service.BIND_AUTO_CREATE)
        val layoutParam = cardViewAppBarMain.layoutParams as ViewGroup.MarginLayoutParams
        layoutParam.topMargin += context?.let {
            it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
        } ?: 0
        cardViewAppBarMain.layoutParams = layoutParam
        swipeMessage.apply {
            cardViewAppBarMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            setProgressViewEndTarget(true, cardViewAppBarMain.measuredHeight + progressViewEndOffset)
        }
        listMessage.apply {
            setPadding(paddingLeft, paddingTop + cardViewAppBarMain.measuredHeight +
                    resources.getDimensionPixelOffset(resources.getIdentifier("status_bar_height", "dimen", "android")),
                    paddingRight, paddingBottom)
        }

//        initHashSet()
        messageList.add(MessageHeader(MessageHeader.GROUP_ID,"全国群聊",System.currentTimeMillis()))
        textMessage.visibility = View.GONE
        hintSearchBar.isVisible = true
        inputSearchBar.isVisible = false
        listMessage.itemAnimator = DefaultItemAnimator()
        listMessage.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listMessage.adapter = MessageListAdapter(messageList)
        listMessage.layoutManager = LinearLayoutManager(context)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hintSearchBar.isVisible = true
        inputSearchBar.isVisible = false
    }


    override fun onStart() {
        super.onStart()
    }

    fun onNewMessage(message: ChatMessage) {
        var item = messageMap[message.chatId]
        Log.e("xxxxx", message.toJson())
        if (item != null) {
            item.message = message.msg
//			item.time = SimpleDateFormat("yyyy.MM.dd hh:mm:ss", Locale.getDefault()).parse(message.time).time
            val index = messageList.indexOf(item)
            if (index == 0) {
                listMessage?.adapter?.notifyItemChanged(0)
            } else {
                messageList.removeAt(index)
                listMessage?.adapter?.notifyItemRemoved(index)
                messageList.add(1, item)
                listMessage?.adapter?.notifyItemInserted(1)
            }
        } else {
            item = MessageHeader(message.chatId, message.msg, 0)
            if (item.id > 0) {
                RemoteService.getHeadLinkAndNick(item.id).enqueue {
                    response {
                        val info = body()
                        if (info != null) {
                            item.userNick = info.userNick ?: ""
                            item.userImg = info.userHeadPictureLink ?: ""
                            val index = messageList.indexOf(item)
                            if (index < 0) {
                                messageList.add(1, item)
                                listMessage?.adapter?.notifyItemInserted(1)
                            } else {
                                listMessage?.adapter?.notifyItemChanged(index)
                            }
                        }
                    }
                }
            }
            messageMap[message.chatId] = item
            messageList.add(1, item)
            listMessage?.adapter?.notifyItemInserted(1)
        }

    }

    override fun onLoginSuccess() {
        context?.bindService(Intent(context!!, MessageService::class.java), connection, Service.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        context?.unbindService(connection)
        super.onDestroy()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessageFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MessageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}
