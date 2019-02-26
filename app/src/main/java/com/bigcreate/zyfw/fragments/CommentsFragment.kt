package com.bigcreate.zyfw.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.CommentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.callback.CommentCallBack
import com.bigcreate.zyfw.models.Comment
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.mvp.project.CommentListContract
import com.bigcreate.zyfw.mvp.project.CommentListImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.coroutines.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PROJECT_ID = "projectId"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CommentsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommentsFragment : Fragment(), CommentListContract.View, FillTextCallBack, CommentCallBack {

    private var projectId: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
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
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }


    fun marginHeight(height: Int) {
        commentCard?.apply {
            if (layoutPara == null)
                layoutPara = layoutParams as ConstraintLayout.LayoutParams
            layoutPara?.bottomMargin = height
            layoutParams = layoutPara
            Log.e("marginHeight", height.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        val intent = activity?.intent
//        val projectId= intent?.getStringArrayExtra("project_id")
        projectId?.run {
            commentImpl.doGetCommentList(GetProjectRequest(token = Attributes.loginUserInfo!!.token, projectId = this))
        }
        job = GlobalScope.async(Dispatchers.Main) {
            CommentDialogFragment().apply {
                fillTextCallBack = this@CommentsFragment
                commentCallBack = this@CommentsFragment
            }
        }
        commentButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                job.await().show(childFragmentManager, "commentDialog")
            }
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onGetCommentListFailed(jsonObject: JsonObject) {
        textLoading.text = "连接出了点问题\n${jsonObject.toJson()}"
        progressLoading.isVisible = false
        layoutLoading.isVisible = true
    }

    override fun onGetCommentListSuccess(list: List<Comment>) {
        if (list.isEmpty()) {
            textLoading.text = "没有评论"
            progressLoading.isVisible = false
            layoutLoading.isVisible = true
        }
        recyclerView_Comments.adapter = CommentAdapter(list.reversed())
        recyclerView_Comments.layoutManager = LinearLayoutManager(context!!)
    }

    override fun onRequestFinished() {
        showProgress(false)
    }

    override fun onNetworkFailed() {
        textLoading.text = "网络出了点差错"
        progressLoading.isVisible = false
        layoutLoading.isVisible = true
    }

    override fun onRequesting() {
        textLoading.text = "正在加载评论"
        showProgress(true)
    }

    private fun showProgress(boolean: Boolean) {
        layoutLoading.isVisible = boolean
        progressLoading.isVisible = boolean
    }

    override fun getTextContent(): CharSequence {
        return commentButton.text
    }

    override fun setTextContent(content: CharSequence) {
        commentButton.text = content
    }

    override fun getProjectId(): String {
        return projectId!!
    }

    override fun commentSuccess() {
        projectId?.run {
            commentImpl.doGetCommentList(GetProjectRequest(token = Attributes.loginUserInfo!!.token, projectId = this))
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
    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

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
