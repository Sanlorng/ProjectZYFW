package com.bigcreate.zyfw.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.*
import com.amap.api.navi.model.AMapNaviLocation
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.activities.RegisterActivity
import com.bigcreate.zyfw.adapter.DetailsMediaAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.project.DetailsImpl
import com.bigcreate.zyfw.mvp.project.JoinProjectImpl
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_project_details.*
import kotlinx.android.synthetic.main.item_project_details_header.*
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
class DetailsFragment : Fragment(), DetailsImpl.View, JoinProjectImpl.View {
    private var projectId: Int = 0
    private var param2: String? = null
    private val presenter = DetailsImpl(this)
    private val joinPresenter = JoinProjectImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = (it.getString(PROJECT_ID)?:"0").toInt()
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
            presenter.doRequest(GetProjectRequest(Attributes.token, projectId))
        }
//        buttonJoinProjectDetails.setOnClickListener {
//            joinPresenter.doRequest(GetProjectRequest(Attributes.token,projectId!!))
//        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onJoinRequestFailed(json: JsonObject) {

    }

    override fun onJoinRequestSuccess(join: Boolean) {
        presenter.doRequest(GetProjectRequest(Attributes.token, projectId))
    }

    override fun onGetDetailsFailed(jsonObject: JsonObject) {
        context?.toast("获取失败")
        if (swipeLayoutProjectDetails.isRefreshing && activity is DetailsImpl.View)
            (activity as DetailsImpl.View).onGetDetailsFailed(jsonObject)
        swipeLayoutProjectDetails.isRefreshing = false
//        testShowInfo.text = "获取失败\n${jsonObject.toJson()}"
    }

    override fun onGetDetailsSuccess(project: Project) {
//        layoutDetailsFragment.isVisible = true
        project.apply {
            //locationDetails.text = getString(R.string.localeProjectVar, projectAddress)
            //typeDetails.text = getString(R.string.typeProjectVar, resources.getStringArray(R.array.project_type_id)[projectTypeId - 1])
            //textNameDetails.text = getString(R.string.contactNameVar, projectPrincipalName)
            textDescriptionDetails.text = projectContent
            //textNumberDetails.text = getString(R.string.needPeoleNumVar, projectPeopleNumbers)
           // textPhoneDetails.text = projectPrincipalPhone
            //textTimeDetails.text = projectIssueTime
            testShowInfo.text = projectTopic
            contactProjectDetails.getLogoView().isVisible = true
            Glide.with(this@DetailsFragment)
                    .load(userInfoByPart.userHeadPictureLink)
                    .circleCrop()
                    .into(contactProjectDetails.getLogoView())
            if (Attributes.userId == userInfoByPart.userId) {
                contactProjectDetails.setActionIcon(R.drawable.ic_outline_edit_24px)
                contactProjectDetails.setActionText("编辑")
                contactProjectDetails.setOnActionClick(View.OnClickListener {
                    startActivity<RegisterActivity> {
                        type = "updateInfo"
                    }
                })
            }else {
                contactProjectDetails.setOnActionClick(View.OnClickListener {
                    it.context.startActivity<ChatActivity> {
                        putExtra("chatId",userInfoByPart.userId)
                    }
                })
            }
            contactProjectDetails.setTitleText(projectPrincipalName)
            contactProjectDetails.setSubTitleText(projectPrincipalPhone)

            contactProjectDetails.setOnItemClick(View.OnClickListener {
                it.context.startActivity<MyDetailsActivity> {
                    putExtra("userId",userInfoByPart.userId)
                }
            })



            navigationProjectDetails.setOnActionClick(View.OnClickListener {
                val endPoi = Poi(projectAddress, LatLng(latitude,longitude),"")
                AmapNaviPage.getInstance().showRouteActivity(it.context, AmapNaviParams(null,null,endPoi,AmapNaviType.WALK,AmapPageType.NAVI),null)
            })

            //navigationProjectDetails.setTitleText(getString(R.string.localeProjectVar, projectAddress))
            navigationProjectDetails.setTitleText(projectAddress)
            navigationProjectDetails.setSubTitleText(getString(R.string.typeProjectVar, resources.getStringArray(R.array.project_type_id)[projectTypeId - 1]))

            otherProjectDetails.setTitleText(getString(R.string.needPeoleNumVar, projectPeopleNumbers))
            otherProjectDetails.setSubTitleText(projectIssueTime)

            otherProjectDetails.setOnActionClick(View.OnClickListener {
                joinPresenter.doRequest(GetProjectRequest(Attributes.token,projectId))
            })
            if (join) {
                context?.getDrawable(R.drawable.ic_favorite_black_24dp)?.setTint(context!!.getColor(R.color.colorAccent))
                otherProjectDetails.setActionIcon(R.drawable.ic_favorite_black_24dp)
            }else {
                otherProjectDetails.setActionIcon(R.drawable.ic_favorite_border_black_24dp)
            }
//            ArrayList<DetailsMediaAdapter.Model>().apply {
//                add(DetailsMediaAdapter.Header("").apply {
//                    this.project = project
//                })
//                if (projectPictureLinkTwo.isNotEmpty())
//                    projectPictureLinkTwo.forEach {
//                        add(DetailsMediaAdapter.Image(it))
//                    }
//                if (projectPictureLinkTwo.isNotEmpty())
//                    projectVideoLinkTwo.forEach {
//                        add(DetailsMediaAdapter.Video(it))
//                    }
//                buttonJoinProjectDetails.isVisible = !join

//                listDetailMedia.layoutManager = LinearLayoutManager(context!!)
//                listDetailMedia.adapter = DetailsMediaAdapter(this)
//            }
        }
        if (swipeLayoutProjectDetails.isRefreshing && activity is DetailsImpl.View)
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
        if (swipeLayoutProjectDetails?.isRefreshing?.not() == true)
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
//    inner class ProjectDetailsNaviInfoCallback : INaviInfoCallback {
//        override fun onArriveDestination(p0: Boolean) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onArrivedWayPoint(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onCalculateRouteFailure(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onCalculateRouteSuccess(p0: IntArray?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onExitPage(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onGetNavigationText(p0: String?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onInitNaviFailure() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onLocationChange(p0: AMapNaviLocation?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onMapTypeChanged(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onReCalculateRoute(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onStartNavi(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onStopSpeaking() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onStrategyChanged(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getCustomMiddleView(): View {
//            return
//        }
//
//        override fun getCustomNaviBottomView(): View {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getCustomNaviView(): View {
//            ret
//        }
//    }
}
