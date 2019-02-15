package com.bigcreate.zyfw.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.WebKit
import com.bigcreate.library.getRequest
import com.bigcreate.library.isVisible
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.CommentAdapter
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.myApplication
import com.bigcreate.zyfw.callback.CommentCallBack
import com.bigcreate.zyfw.fragments.CommentDialogFragment
import com.bigcreate.zyfw.fragments.FillTextCallBack
import com.bigcreate.zyfw.models.CommentResponse
import com.bigcreate.zyfw.models.ProjectResponse
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory
import com.tencent.mapsdk.raster.model.LatLng
import com.tencent.mapsdk.raster.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_project_details.*

class ProjectDetailsActivity : AppCompatActivity(),FillTextCallBack,CommentCallBack {
    private var searchResponse : ProjectResponse ?= null
    private var commentResponse : CommentResponse ?= null
    private var projectId : String ?= null
    private var projectName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_project_details)
            setSupportActionBar(toolbar_project_details)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar_project_details.setNavigationOnClickListener {
                finish()
            }
            projectId = intent.getStringExtra("projectId")
            projectName = intent.getStringExtra("projectTopic")
            textView_project_title.text = projectName

//            attemptSearch()
        val tencentLocation = TencentLocationManager.getInstance(this)
        val request = TencentLocationRequest.create()
        request?.run {
            requestLevel = TencentLocationRequest.REQUEST_LEVEL_NAME
            isAllowCache = true
            interval = 1500
            isAllowGPS =true
            isAllowDirection = true
        }
        val commentFragment = CommentDialogFragment()
        commentFragment.fillTextCallBack = this
        commentFragment.commentCallBack = this
        cardView_comment.setOnClickListener {
            commentFragment.show(supportFragmentManager,"commentFragment")
        }
        tencentLocation.requestLocationUpdates(request,GetLocationListenner())
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
    fun updateInfo(){
        progressBar4.isVisible = false
        app_bar_map.isVisible = true
        searchResponse?.content?.run {
            app_bar_map.map.setCenter(LatLng(latitude,longitude))
            app_bar_map.map.setZoom(20)
            val marker = app_bar_map.map.addMarker(
                    MarkerOptions()
                            .position(LatLng(latitude,longitude))
                            .title(projectAddress.split(projectRegion).last())
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.defaultMarker()))
            marker.showInfoWindow()
            textView_Address.text = projectAddress
            textView_Content.text = projectContent
            textView_Region.text = projectRegion
            textView_name.text = projectPrincipalName
            textView_numbers.text = projectPeopleNumbers
            textView_phone.text = projectPrincipalPhone
            textView_topic.text = projectTopic
            commentResponse?.content?.run {
                recycler_comments.adapter = CommentAdapter(this)
                recycler_comments.layoutManager = LinearLayoutManager(this@ProjectDetailsActivity)
            }
            if (commentResponse== null){
                //textView_Comments.text = "网络出错，无法获得评论"
            }else{
                if (commentResponse!!.content == null){
                    textView_Comments.text = " "
                }else
                    textView_Comments.text = getString(R.string.comment)

            }
        }
    }
    inner class GetLocationListenner: TencentLocationListener {
        override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {

        }

        override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
            p0?.run {

            }
        }
    }

    override fun onResume() {
        window.transucentSystemUI(true)
        super.onResume()
    }

    override fun getTextContent(): CharSequence {
        return editText.text
    }

    override fun setTextContent(content: CharSequence) {
        editText.text = content
    }

    override fun commentSuccess() {
        Thread{
            val responseComment = WebKit.okClient.getRequest(WebInterface.COMMENT_URL + projectId)?.string()
            commentResponse = WebKit.gson.fromJson(responseComment,CommentResponse::class.java)
            runOnUiThread {
            commentResponse?.content?.run {
                recycler_comments.adapter = CommentAdapter(this)
                recycler_comments.layoutManager = LinearLayoutManager(this@ProjectDetailsActivity)
            }
            if (commentResponse== null){
                textView_Comments.text = "网络出错，无法获得评论"
            }else{
                if (commentResponse!!.content == null)
                    textView_Comments.text = "此项目没有评论"
            }
            }
        }.start()
    }
}
