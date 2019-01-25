package com.bigcreate.zyfw.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.MessageListAdapter
import com.bigcreate.zyfw.models.MessageHeader
import kotlinx.android.synthetic.main.fragment_message.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MessageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MessageFragment : Fragment(), Runnable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val SUCESS = 1
    private var messageList = HashMap<String,MessageHeader>()
    private var listener: OnFragmentInteractionListener? = null
    private var handler = @SuppressLint
    object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what){
                SUCESS -> {
                    no_message.visibility = View.GONE
                    message_recyclerview.adapter = MessageListAdapter(messageList)
                    message_recyclerview.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Thread(this).start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onStart() {
        super.onStart()
        val activity = activity as AppCompatActivity
        toolbar_message.title = getString(R.string.title_message)
        toolbar_message.inflateMenu(R.menu.toolbar_message)
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MessageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun run() {
        Looper.prepare()
        initHashSet()
        val msg = Message()
        msg.what = SUCESS
        handler.sendMessage(msg)
        Looper.loop()
    }
    fun initHashSet(){
        for (i in 1..10)
        messageList?.put(i.toString(),MessageHeader())
    }
}
