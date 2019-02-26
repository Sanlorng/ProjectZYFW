package com.bigcreate.zyfw.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.project.DetailsContract
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.layout_loading.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PROJECT_ID = "projectId"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DetailsFragment : Fragment(), DetailsContract.NetworkView {
    // TODO: Rename and change types of parameters
    private var projectId: String? = null
    private var param2: String? = null
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
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onGetDetailsFailed(jsonObject: JsonObject) {
//        context?.toast("获取失败")
        testShowInfo.text = "获取失败\n${jsonObject.toJson()}"
    }

    override fun onGetDetailsSuccess(project: Project) {
        layoutDetailsFragment.isVisible = true
        project.apply {
            locationDetails.text = projectAddress
            typeDetails.text = context!!.resources.getStringArray(R.array.project_type_id)[projectTypeId]
            textNameDetails.text = projectPrincipalName
            textDescriptionDetails.text = projectContent
            textNumberDetails.text = projectPeopleNumbers
            textPhoneDetails.text = "+86$projectPrincipalPhone"
            textTimeDetails.text = projectIssueTime
        }
    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onNetworkFailed() {
//        context?.toast("网络连接出错")
    }

    override fun onRequesting() {
        layoutLoading.isVisible = true
    }

    override fun onRequestFinished() {
        layoutLoading.isVisible = false
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
        // TODO: Rename and change types and number of parameters
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
