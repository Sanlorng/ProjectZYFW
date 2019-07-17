package com.bigcreate.zyfw.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.CommentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.Status
import com.bigcreate.zyfw.callback.CommentCallBack
import com.bigcreate.zyfw.callback.FillTextCallBack
import com.bigcreate.zyfw.datasource.CommentListDataSource
import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.CommentListRequest
import com.bigcreate.zyfw.models.ProjectCommentResponse
import com.bigcreate.zyfw.mvp.project.CommentListImpl
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_comment_details.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PROJECT_ID = "projectId"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the [CommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommentsFragment : Fragment(), CommentListImpl.View, FillTextCallBack, CommentCallBack {

    private val background = Executors.newFixedThreadPool(5)
    private lateinit var networkStateViewModel: NetworkStateViewModel
    private var projectId: String? = null
    private var param2: String? = null
    private var layoutPara: ConstraintLayout.LayoutParams? = null
    private val commentImpl = CommentListImpl(this)
    private lateinit var job: Deferred<CommentDialogFragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getString(PROJECT_ID)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_details, container, false)
    }


//    fun marginHeight(height: Int) {
//        cardCommentDetails?.apply {
//            if (layoutPara == null)
//                layoutPara = layoutParams as ConstraintLayout.LayoutParams
//            layoutPara?.bottomMargin = height
//            layoutParams = layoutPara
//            Log.e("marginHeight", height.toString())
//        }
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        networkStateViewModel = ViewModelProviders.of(this).get(NetworkStateViewModel::class.java)
        networkStateViewModel.state.observe(this, Observer {
            when(it.status) {
                Status.SUCCESS -> showProgress(false)
                Status.FAILED -> {
                    showProgress(false)
                    toast(it.msg)
                }
                Status.RUNNING -> showProgress(true)
            }
        })
        progressLoading.isVisible = false
        layoutLoading.isVisible = false
        swipeLayoutCommentDetails.isRefreshing = false
        swipeLayoutCommentDetails.setOnRefreshListener {
            refreshList()
//            commentImpl.doRequest(CommentListRequest(token = Attributes.token, projectId = projectId!!, pageNum = 1))
        }

        job = GlobalScope.async(Dispatchers.Main) {
            CommentDialogFragment().apply {
                fillTextCallBack = this@CommentsFragment
                commentCallBack = this@CommentsFragment
            }
        }
//        buttonShowCommentDialog.setOnClickListener {
//            GlobalScope.launch(Dispatchers.Main) {
//                job.await().show(childFragmentManager, "commentDialog")
//            }
//        }
//        swipeLayoutCommentDetails.apply {
//            setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom+cardCommentDetails.height)
//        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun refreshList() {
        projectId?.run {
            listCommentsDetails.adapter = CommentAdapter().apply {
                submitList(PagedList.Builder<Int,Comment>(CommentListDataSource(CommentListRequest(token = Attributes.token, projectId = projectId!!, pageNum = 1),
                        networkStateViewModel.state),PagedList.Config
                        .Builder()
                        .setPageSize(20)
                        .setPrefetchDistance(40)
                        .build())
                        .setFetchExecutor {
                            background.execute(it)
                        }
                        .setNotifyExecutor {
                            Handler(Looper.getMainLooper()).post(it)
                        }
                        .build())
            }
            listCommentsDetails.layoutManager = LinearLayoutManager(context!!)
//            commentImpl.doRequest(CommentListRequest(token = Attributes.token, projectId = this, pageNum = 1))
        }
    }
    override fun getViewContext(): Context {
        return context!!
    }

    override fun onGetCommentListFailed(jsonObject: JsonObject) {
        textLoading.text = "连接出了点问题"
        progressLoading.isVisible = false
        layoutLoading.isVisible = true
    }

    override fun onGetCommentListSuccess(commentResponse: ProjectCommentResponse) {
        if (commentResponse.list.isEmpty()) {
            textLoading.text = "没有评论"
            progressLoading.isVisible = false
            layoutLoading.isVisible = true
        }
        swipeLayoutCommentDetails.isRefreshing = false
        listCommentsDetails.adapter = CommentAdapter()
        listCommentsDetails.layoutManager = LinearLayoutManager(context!!)
    }

    override fun onRequestFinished() {
        showProgress(false)
    }

    override fun onNetworkFailed() {
        textLoading?.text = "网络出了点差错"
        progressLoading?.isVisible = false
        layoutLoading?.isVisible = true
    }

    override fun onRequesting() {
        textLoading.text = "正在加载评论"
        if (swipeLayoutCommentDetails.isRefreshing.not())
        showProgress(true)
    }

    private fun showProgress(boolean: Boolean) {
        layoutLoading.isVisible = boolean
        progressLoading.isVisible = boolean
    }

    override fun getTextContent(): CharSequence {
//        return buttonShowCommentDialog.text
        return ""
    }

    override fun setTextContent(content: CharSequence) {
//        buttonShowCommentDialog.text = content
    }

    override fun getProjectId(): String {
        return projectId!!
    }

    override fun commentSuccess() {
        projectId?.run {
            commentImpl.doRequest(CommentListRequest(token = Attributes.token, projectId = this, pageNum = 1))
        }
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
         * @return A new instance of fragment CommentsFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CommentsFragment().apply {
                    arguments = Bundle().apply {
                        putString(PROJECT_ID, param1)
                        putString(ARG_PARAM2, param2)
                        putString("title", "评论")
                    }
                }
    }
}
