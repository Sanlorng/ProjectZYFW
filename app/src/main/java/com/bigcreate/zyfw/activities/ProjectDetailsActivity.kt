package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.fragments.CommentsFragment
import com.bigcreate.zyfw.fragments.DetailsFragment
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.project.DetailsContract
import com.bigcreate.zyfw.mvp.project.DetailsImpl
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.JsonObject
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory
import com.tencent.mapsdk.raster.model.LatLng
import com.tencent.mapsdk.raster.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.coroutines.*

class ProjectDetailsActivity : AppCompatActivity(), DetailsContract.NetworkView {
    private var project: Project? = null
    private var projectId = -1
    private lateinit var fragmentJob: Deferred<DetailsContract.NetworkView>
    private var commentsFragment: CommentsFragment? = null
    private var projectName: String? = null
    private val detailsImpl = DetailsImpl(this)
    private var appbarHeight = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)
        setSupportActionBar(toolbar_project_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_project_details.setNavigationOnClickListener {
            finish()
        }

        projectId = intent.getIntExtra("projectId", -1)
        projectName = intent.getStringExtra("projectTopic")
        textView_project_title.text = projectName

//        val tencentLocation = TencentLocationManager.getInstance(this)
//        val request = TencentLocationRequest.create()
//        request?.run {
//            requestLevel = TencentLocationRequest.REQUEST_LEVEL_NAME
//            isAllowCache = true
//            interval = 1500
//            isAllowGPS =true
//            isAllowDirection = true
//        }
        toolbar_project_details.inflateMenu(R.menu.toolbar_project_details).apply {
            toolbar_project_details.menu.forEach {
                it.isVisible = false
            }
        }
        toolbar_project_details.inflateMenu(R.menu.toolbar_project_details)
        toolbar_project_details.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editProjectDetails -> startActivityForResult(Intent(this, ProjectDetailsActivity::class.java).apply {
                    putExtra("projectId", projectId)
                    putExtra("projectDetails", project.toJson())
                }, RequestCode.EDIT_PROJECT)
            }
            true
        }
//        val commentFragment = CommentDialogFragment()
//        commentFragment.fillTextCallBack = this
//        commentFragment.commentCallBack = this
//        cardView_comment.setOnClickListener {
//            commentFragment.show(supportFragmentManager,"commentFragment")
//        }
        fragmentJob = GlobalScope.async(Dispatchers.Main) {
            //            val detailsFragment =
//            commentsFragment =
            FragmentAdapter(supportFragmentManager, listOf(DetailsFragment.newInstance(projectId.toString(), ""), CommentsFragment.newInstance(projectId.toString(), ""))).let {
                commentsFragment = it.list[1] as CommentsFragment
                commentsFragment?.marginHeight(appbarHeight)
                viewPagerDetails.adapter = it
                tabProjectDetails.setupWithViewPager(viewPagerDetails)
                it.list[0] as DetailsContract.NetworkView
            }
        }
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbarLayout, verticalOffset ->
            if (appbarHeight == 0)
                appbarHeight = appbar.height -
                        toolbar_project_details.height -
                        resources.getDimensionPixelOffset(resources
                                .getIdentifier("status_bar_height", "dimen", "android"))
//            Log.e("total range",appbarLayout.totalScrollRange.toString())
//            Log.e("appbarHeight",appbarHeight.toString())
//            Log.e("offset",(appbarHeight+ verticalOffset).toString())
//            Log.e("appbar",appbar.height.toString())
//            Log.e("toolbar",toolbar_project_details.height.toString())
//            Log.e("vertical",verticalOffset.toString())
//            Log.e("statusBar",resources.getDimensionPixelOffset(resources.getIdentifier("status_bar_height","dimen","android")).toString())
//
//              commentsFragment?.marginHeight(appbarHeight + verticalOffset)
        })
        detailsImpl.doGetDetails(GetProjectRequest(token = Attributes.loginUserInfo!!.token, projectId = projectId.toString()))
//        viewPagerDetails.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//        })
//        tencentLocation.requestLocationUpdates(request,GetLocationListenner())
    }
//        private fun attemptSearch(){
//            if (task != null)
//                return
//            task = SearchAsyncTask(projectId!!)
//            task!!.execute(null as Void?)
//    }
//    @SuppressLint("StaticFieldLeak")
//    inner class SearchAsyncTask internal constructor(val string: String): AsyncTask<Void, Void, Boolean>(){
//        override fun doInBackground(vararg params: Void?): Boolean {
//            return try {
//                myApplication?.run {
//                    val response = WebKit.okClient.getRequest(WebInterface.PROJECT_URL + projectId)
//                    val responseComment = WebKit.okClient.getRequest(WebInterface.COMMENT_URL + projectId)?.string()
//                    val responseString = response?.string()
//                    Log.d("is client","yes")
//                    responseString?.run {
//                        Log.d("response",this)
//                    }
//                    searchResponse = WebKit.gson.fromJson<ProjectResponse>(responseString, ProjectResponse::class.java)
//                    commentResponse = WebKit.gson.fromJson(responseComment,CommentResponse::class.java)
//                }
//                searchResponse != null && searchResponse?.stateCode?.compareTo(200) == 0
//            }catch (e:Exception){
//                Log.d("error","when search request")
//                false
//            }
//        }
//
//        override fun onPostExecute(result: Boolean?) {
//            if (result!!) {
//                updateInfo()
//            }
//            super.onPostExecute(result)
//        }
//    }
//    fun updateInfo(){
//        progressBar4.isVisible = false
//        app_bar_map.isVisible = true
//        project?.run {
//            app_bar_map.map.setCenter(LatLng(latitude,longitude))
//            app_bar_map.map.setZoom(20)
//            val marker = app_bar_map.map.addMarker(
//                    MarkerOptions()
//                            .position(LatLng(latitude,longitude))
//                            .title(projectAddress.split(projectRegion).last())
//                            .anchor(0.5f, 0.5f)
//                            .icon(BitmapDescriptorFactory.defaultMarker()))
//            marker.showInfoWindow()
//            textView_Address.text = projectAddress
//            textView_Content.text = projectContent
//            textView_Region.text = projectRegion
//            textView_name.text = projectPrincipalName
//            textView_numbers.text = projectPeopleNumbers
//            textView_phone.text = projectPrincipalPhone
//            textView_topic.text = projectTopic
//            commentResponse?.content?.run {
//                recycler_comments.adapter = CommentAdapter(this)
//                recycler_comments.layoutManager = LinearLayoutManager(this@ProjectDetailsActivity)
//            }
//            if (commentResponse== null){
//                //textView_Comments.text = "网络出错，无法获得评论"
//            }else{
//                if (commentResponse!!.content == null){
//                    textView_Comments.text = " "
//                }else
//                    textView_Comments.text = getString(R.string.comment)
//
//            }
//        }
//    }

//    inner class GetLocationListenner: TencentLocationListener {
//        override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
//
//        }
//
//        override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
//            p0?.run {
//
//            }
//        }
//    }

    override fun onResume() {
        window.transucentSystemUI(true)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        detailsImpl.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.EDIT_PROJECT -> if (resultCode == ResultCode.OK) detailsImpl.doGetDetails(GetProjectRequest(Attributes.loginUserInfo!!.token, projectId.toString()))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGetDetailsFailed(jsonObject: JsonObject) {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onGetDetailsFailed(jsonObject) }
        toast(jsonObject.toJson())
    }

    override fun onGetDetailsSuccess(project: Project) {
        this.project = project
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onGetDetailsSuccess(project) }
        project.apply {
            if (username == Attributes.loginUserInfo!!.username)
                toolbar_project_details.menu.findItem(R.id.editProjectDetails).isVisible = true
            app_bar_map.map.addMarker(
                    MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .title(projectAddress.split(projectRegion).last().replace("·", ""))
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.defaultMarker()))
                    .apply {
                        showInfoWindow()
                    }
            app_bar_map.map.apply {
                setCenter(LatLng(latitude, longitude)); setZoom(30)
            }
            app_bar_map.isVisible = true
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onNetworkFailed() {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onNetworkFailed() }
        toast("网络连接失败")
    }

    override fun onRequesting() {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onRequesting() }
    }

    override fun onRequestFinished() {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onRequestFinished() }
    }
//    override fun getTextContent(): CharSequence {
//        return ""
//    }
//
//    override fun setTextContent(content: CharSequence) {
////        editText.text = content
//    }
//
//    override fun commentSuccess() {
////        Thread{
////            val responseComment = WebKit.okClient.getRequest(WebInterface.COMMENT_URL + projectId)?.string()
////            commentResponse = WebKit.gson.fromJson(responseComment,CommentResponse::class.java)
////            runOnUiThread {
////            commentResponse?.content?.run {
////                recycler_comments.adapter = CommentAdapter(this)
////                recycler_comments.layoutManager = LinearLayoutManager(this@ProjectDetailsActivity)
////            }
////            if (commentResponse== null){
////                textView_Comments.text = "网络出错，无法获得评论"
////            }else{
////                if (commentResponse!!.content == null)
////                    textView_Comments.text = "此项目没有评论"
////            }
////            }
////        }.start()
//    }
}
