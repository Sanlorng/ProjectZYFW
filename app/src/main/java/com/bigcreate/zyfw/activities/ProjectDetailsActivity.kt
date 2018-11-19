package com.bigcreate.zyfw.activities

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.myApplication
import com.bigcreate.zyfw.models.ProjectResponse
import com.bigcreate.zyfw.models.SearchRequire
import com.bigcreate.zyfw.models.SearchResponse
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory
import com.tencent.mapsdk.raster.model.LatLng
import com.tencent.mapsdk.raster.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.android.synthetic.main.activity_search.*

class ProjectDetailsActivity : AppCompatActivity() {
    private var task : SearchAsyncTask? = null
    private var searchResponse : ProjectResponse ?= null
    private var project_id : String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_project_details)
            setSupportActionBar(toolbar_project_details)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar_project_details.setNavigationOnClickListener {
                finish()
            }
            project_id = intent.getStringExtra("project_id")
            attemptSearch()
        val tencentLocation = TencentLocationManager.getInstance(this)
        val request = TencentLocationRequest.create()
        request?.run {
            requestLevel = TencentLocationRequest.REQUEST_LEVEL_NAME
            isAllowCache = true
            interval = 1500
            isAllowGPS =true
            isAllowDirection = true
        }
        tencentLocation.requestLocationUpdates(request,GetLocationListenner())
        }
        private fun attemptSearch(){
            if (task != null)
                return
            task = SearchAsyncTask(project_id!!)
            task!!.execute(null as Void?)
    }
    @SuppressLint("StaticFieldLeak")
    inner class SearchAsyncTask internal constructor(val string: String): AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                myApplication?.run {
                    val response = WebKit.okClient.getRequest(WebInterface.PROJECT_URL + project_id)
                    val responseString = response?.string()
                    Log.d("is client","yes")
                    responseString?.run {
                        Log.d("response",this)
                    }
                    searchResponse = WebKit.gson.fromJson<ProjectResponse>(responseString, ProjectResponse::class.java)
                }
                searchResponse != null && searchResponse?.stateCode?.compareTo("200") == 0
            }catch (e:Exception){
                Log.d("error","when search request")
                false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            if (result!!) {
                updateInfo()
            }
            super.onPostExecute(result)
        }
    }
    fun updateInfo(){
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
}
