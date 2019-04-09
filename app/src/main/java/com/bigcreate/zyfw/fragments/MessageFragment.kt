package com.bigcreate.zyfw.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MainActivity
import com.bigcreate.zyfw.adapter.MessageListAdapter
import com.bigcreate.zyfw.models.MessageHeader
import com.google.android.material.appbar.AppBarLayout
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
class MessageFragment : Fragment(),MainActivity.ChildFragment{
    private var param1: String? = null
    private var param2: String? = null
    private val success = 1
    private var messageList = HashMap<String, MessageHeader>()


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
        val layoutParam = cardViewAppBarMain.layoutParams as AppBarLayout.LayoutParams
        layoutParam.topMargin += context?.let {
            it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height", "dimen", "android"))
        }?:0
        cardViewAppBarMain.layoutParams = layoutParam
        swipeMessage.apply {
            cardViewAppBarMain.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
            setProgressViewEndTarget(true,cardViewAppBarMain.measuredHeight + progressViewEndOffset)
        }
        listMessage.apply {
            setPadding(paddingLeft,paddingTop + cardViewAppBarMain.measuredHeight +
                    resources.getDimensionPixelOffset(resources.
                            getIdentifier("status_bar_height", "dimen", "android")),
                    paddingRight,paddingBottom)
        }
        initHashSet()
        textMessage.visibility = View.GONE
        listMessage.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listMessage.adapter = MessageListAdapter(messageList)
        listMessage.layoutManager = LinearLayoutManager(context)
        if (BuildConfig.DEBUG)
            Log.e("onActivityCreate","Message")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (BuildConfig.DEBUG)
            Log.e("onHiddenChanged","Message")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hintSearchBar.isVisible = true
        inputSearchBar.isVisible = false
        if (BuildConfig.DEBUG)
            Log.e("onViewCreate","Message")
    }

    override fun getUserVisibleHint(): Boolean {
        if (BuildConfig.DEBUG)
            Log.e("onUserVisibleHintCreate","Message")
        return super.getUserVisibleHint()
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG)
            Log.e("onStart","Message")
    }

    override fun onLoginSuccess() {

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


    private fun initHashSet() {
        for (i in 1..20)
            messageList[i.toString()] = MessageHeader()
        textMessage.visibility = View.GONE
        listMessage.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listMessage.adapter = MessageListAdapter(messageList)
        listMessage.layoutManager = LinearLayoutManager(context)
    }
}
