package com.bigcreate.zyfw.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.DetailsMediaAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.project.DetailsImpl
import com.bigcreate.zyfw.mvp.project.JoinProjectImpl
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_project_details.*
import kotlinx.android.synthetic.main.layout_loading.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PROJECT_ID = "projectId"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DetailsFragment : Fragment(), DetailsImpl.View,JoinProjectImpl.View {
    private var projectId: String? = null
    private var param2: String? = null
    private val presenter = DetailsImpl(this)
    private val joinPresenter = JoinProjectImpl(this)
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
        return inflater.inflate(R.layout.fragment_project_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        swipeLayoutProjectDetails.setOnRefreshListener {
            presenter.doRequest(GetProjectRequest(Attributes.token,projectId!!))
        }
//        buttonJoinProjectDetails.setOnClickListener {
//            joinPresenter.doRequest(GetProjectRequest(Attributes.token,projectId!!))
//        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onJoinRequestFailed(json: JsonObject) {

    }

    override fun onJoinRequestSuccess(join: Boolean) {
        presenter.doRequest(GetProjectRequest(Attributes.token,projectId!!))
    }

    override fun onGetDetailsFailed(jsonObject: JsonObject) {
        context?.toast("获取失败")
        if (swipeLayoutProjectDetails.isRefreshing&&activity is DetailsImpl.View)
            (activity as DetailsImpl.View).onGetDetailsFailed(jsonObject)
        swipeLayoutProjectDetails.isRefreshing = false
//        testShowInfo.text = "获取失败\n${jsonObject.toJson()}"
    }

    override fun onGetDetailsSuccess(project: Project) {
//        layoutDetailsFragment.isVisible = true
        project.apply {
            ArrayList<DetailsMediaAdapter.Model>().apply {
                add(DetailsMediaAdapter.Header("").apply {
                    this.project = project
                })
                if(projectPictureLinkTwo.isNotEmpty())
                projectPictureLinkTwo.forEach {
                    add(DetailsMediaAdapter.Image(it))
                }
                if (projectPictureLinkTwo.isNotEmpty())
                projectVideoLinkTwo.forEach {
                    add(DetailsMediaAdapter.Video(it))
                }
//                buttonJoinProjectDetails.isVisible = !join

                listDetailMedia.layoutManager = LinearLayoutManager(context!!)
                listDetailMedia.adapter = DetailsMediaAdapter(this)
            }
        }
        if (swipeLayoutProjectDetails.isRefreshing&&activity is DetailsImpl.View)
            (activity as DetailsImpl.View).onGetDetailsSuccess(project)
        swipeLayoutProjectDetails.isRefreshing = false
    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onNetworkFailed() {
        context?.toast("网络连接出错")
    }

    override fun onRequesting() {
        if (swipeLayoutProjectDetails.isRefreshing.not())
        layoutLoading?.isVisible = true
    }

    override fun onRequestFinished() {
        layoutLoading?.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        joinPresenter.detachView()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param projectId Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        @JvmStatic
        fun newInstance(projectId: String, param2: String) =
                DetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(PROJECT_ID, projectId)
                        putString(ARG_PARAM2, param2)
                        putString("title", "详情")
                    }
                }
    }
}
