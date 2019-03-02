package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.fragments.CommentsFragment
import com.bigcreate.zyfw.fragments.DetailsFragment
import com.bigcreate.zyfw.fragments.ProjectActionItemListDialogFragment
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.ProjectFavoriteRequest
import com.bigcreate.zyfw.mvp.project.DetailsImpl
import com.bigcreate.zyfw.mvp.project.FavoriteProjectImpl
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.JsonObject
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory
import com.tencent.mapsdk.raster.model.LatLng
import com.tencent.mapsdk.raster.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.coroutines.*

class ProjectDetailsActivity : AuthLoginActivity(), DetailsImpl.View, ProjectActionItemListDialogFragment.Listener, FavoriteProjectImpl.View {
    private var project: Project? = null
    private var projectId = -1
    private lateinit var fragmentJob: Deferred<DetailsImpl.View>
    private var commentsFragment: CommentsFragment? = null
    private var projectName: String? = null
    private val detailsImpl = DetailsImpl(this)
    private var appbarHeight = 0
    private var favoriteProjectImpl = FavoriteProjectImpl(this)
    private var isFavoriteRequest = false
    private var projectType = -1
    private lateinit var favoriteIcon: Deferred<MenuItem>
    private lateinit var actionIcon: Deferred<MenuItem>
    private lateinit var bottomJob: Deferred<ProjectActionItemListDialogFragment>
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
        fragmentJob = GlobalScope.async(Dispatchers.Main) {
            FragmentAdapter(supportFragmentManager, listOf(DetailsFragment.newInstance(projectId.toString(), ""), CommentsFragment.newInstance(projectId.toString(), ""))).let {
                commentsFragment = it.list[1] as CommentsFragment
                commentsFragment?.marginHeight(appbarHeight)
                viewPagerDetails.adapter = it
                tabProjectDetails.setupWithViewPager(viewPagerDetails)
                it.list[0] as DetailsImpl.View
            }
        }
        bottomJob = GlobalScope.async(Dispatchers.Main) {
            ProjectActionItemListDialogFragment.newInstance(0)
        }
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbarLayout, verticalOffset ->
            if (appbarHeight == 0)
                appbarHeight = appbar.height -
                        toolbar_project_details.height -
                        resources.getDimensionPixelOffset(resources
                                .getIdentifier("status_bar_height", "dimen", "android"))
        })
        detailsImpl.doRequest(GetProjectRequest(token = Attributes.loginUserInfo!!.token, projectId = projectId.toString()))
    }
    override fun onDestroy() {
        super.onDestroy()
        detailsImpl.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.EDIT_PROJECT,RequestCode.SELECT_IMAGE,RequestCode.SELECT_VIDEO -> if (resultCode == ResultCode.OK) detailsImpl.doRequest(GetProjectRequest(Attributes.loginUserInfo!!.token, projectId.toString()))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onGetDetailsFailed(jsonObject: JsonObject) {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onGetDetailsFailed(jsonObject) }
        toast(jsonObject.toJson())
    }

    override fun onGetDetailsSuccess(project: Project) {
        this.project = project
        project.run {
            projectType = projectTypeId
            GlobalScope.launch(Dispatchers.Main) {
                fragmentJob.await().onGetDetailsSuccess(project)
                if (Attributes.loginUserInfo!!.username == username)
                    actionIcon.await().apply {
                        isVisible = true
                        icon = getDrawable(R.drawable.ic_mode_edit_black_24dp)!!.apply {
                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
                        }
                    }
                favoriteIcon.await().apply {
                    icon = if (favorite)
                        getDrawable(R.drawable.ic_star_black_24dp)!!.apply {
                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
                        }
                    else
                        getDrawable(R.drawable.ic_star_border_black_24dp)!!.apply {
                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
                        }
                    isChecked = favorite
                }
            }
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
            this@ProjectDetailsActivity.project = this
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
        if (isFavoriteRequest.not())
            GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onRequesting() }
    }

    override fun onRequestFinished() {
        isFavoriteRequest = false
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onRequestFinished() }
    }

    override fun onFavoriteProjectFailed() {
        toast("收藏失败")
    }

    override fun onFavoriteProjectSuccess() {
        GlobalScope.launch(Dispatchers.Main) {
            favoriteIcon.await().apply {
                isChecked = true
                icon = getDrawable(R.drawable.ic_star_black_24dp)?.apply {
                    DrawableCompat.setTint(this,getColor(R.color.colorAccent))
                }
            }
        }
    }

    override fun onUnFavoriteProjectFailed() {
        toast("取消收藏失败")
    }

    override fun onUnFavoriteProjectSuccess() {
        GlobalScope.launch(Dispatchers.Main) {
            favoriteIcon.await().apply {
                isChecked = false
                icon = getDrawable(R.drawable.ic_star_border_black_24dp)?.apply {
                    DrawableCompat.setTint(this,getColor(R.color.colorAccent))
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_project_details, menu)
        menu?.apply {
            project?.run {

            }
            favoriteIcon = GlobalScope.async(Dispatchers.Main) {
                findItem(R.id.projectDetailsFavorite)
            }
            actionIcon = GlobalScope.async(Dispatchers.Main) {
                findItem(R.id.projectDetailsAction)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            null -> {
            }
            R.id.projectDetailsAction -> {
//                startActivity(Intent(this,ReleaseProjectActivity::class.java).apply {
////                    putExtra("projectId",)
//                })
                GlobalScope.launch(Dispatchers.Main) {
                    bottomJob.await().show(supportFragmentManager, "bottomSheet")
                }
            }
            R.id.projectDetailsFavorite -> {
                isFavoriteRequest = true
                Attributes.loginUserInfo?.apply {
                    val request = ProjectFavoriteRequest(
                            projectId = projectId,
                            projectClassifyId = projectType.toString(),
                            username = username,
                            token = token
                    )
                    if (item.isChecked.not())
                        favoriteProjectImpl.doFavoriteProject(request)
                    else
                        favoriteProjectImpl.doUnFavoriteProject(request)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProjectActionItemClicked(position: Int) {
        when (position) {
            R.drawable.ic_mode_edit_black_24dp -> startActivityForResult(Intent(this,
                    ReleaseProjectActivity::class.java).apply {
                putExtra("editMode", true)
                putExtra("projectId", projectId)
                putExtra("projectInfo", project.toJson())
            }, RequestCode.EDIT_PROJECT)
            R.drawable.ic_delete_black_24dp -> AlertDialog.Builder(this)
                    .setTitle("删除项目")
                    .setPositiveButton("确定") { dialog, which ->
                        try {
                            GlobalScope.launch {
                                RemoteService.deleteProject(
                                        GetProjectRequest(Attributes.loginUserInfo!!.token,
                                                projectId.toString())).execute().body()?.apply {
                                    launch(Dispatchers.Main) {
                                        if (get("code").asInt == 200) {
                                            setResult(ResultCode.OK)
                                            finish()
                                        } else {
                                            toast("删除失败")
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toast("删除失败")
                        }
                    }
                    .setNegativeButton("取消") { _, _ ->

                    }
                    .create().show()
            R.drawable.ic_add_a_photo_black_24dp -> startActivityForResult(
                    Intent(this,SelectImageAndVideoActivity::class.java).apply {
                        type = "image"
                        putExtra("projectId",projectId.toString())
                    },RequestCode.SELECT_IMAGE)
            R.drawable.ic_video_call_black_24dp -> startActivityForResult(
                    Intent(this,SelectImageAndVideoActivity::class.java).apply {
                        type = "video"
                        putExtra("projectId",projectId.toString())
                    },RequestCode.SELECT_VIDEO)
        }
    }


}
