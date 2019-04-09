package com.bigcreate.zyfw.activities

//import com.amap.api.maps.model.LatLng
//import com.amap.api.maps.model.MarkerOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.callback.CommentCallBack
import com.bigcreate.zyfw.callback.FillTextCallBack
import com.bigcreate.zyfw.fragments.CommentDialogFragment
import com.bigcreate.zyfw.fragments.CommentsFragment
import com.bigcreate.zyfw.fragments.DetailsFragment
import com.bigcreate.zyfw.fragments.ProjectActionItemListDialogFragment
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.ProjectFavoriteRequest
import com.bigcreate.zyfw.mvp.project.DetailsImpl
import com.bigcreate.zyfw.mvp.project.FavoriteProjectImpl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.tencent.mapsdk.raster.model.LatLng
import com.tencent.mapsdk.raster.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.coroutines.*

class ProjectDetailsActivity : AuthLoginActivity(), DetailsImpl.View, ProjectActionItemListDialogFragment.Listener, FavoriteProjectImpl.View,CommentCallBack,FillTextCallBack {
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
    private var commentText = ""
//    private lateinit var favoriteIcon: Deferred<MenuItem>
    private lateinit var commentJob: Deferred<CommentDialogFragment>
    private lateinit var bottomJob: Deferred<ProjectActionItemListDialogFragment>
    private lateinit var viewPagerFragments : List<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapProjectDetails.onCreate(savedInstanceState)
    }
    override fun setContentView() {
        setContentView(R.layout.activity_project_details)
        bottomAppBarDetails.inflateMenu(R.menu.toolbar_project_details)
        bottomAppBarDetails.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.projectDetailsAction -> GlobalScope.launch(Dispatchers.Main) {
                    bottomJob.await().show(supportFragmentManager, "bottomSheet")
                }
                R.id.projectDetailsGroupChat -> startActivity(Intent(this,ChatActivity::class.java).apply {

                })

                R.id.projectDetailsFavorite -> {
                    isFavoriteRequest = true
                    Attributes.loginUserInfo?.apply {
                        val request = ProjectFavoriteRequest(
                                projectId = projectId,
                                projectUserId = userId,
                                token = token,
                                projectClassifyId = projectType.toString()
                        )
                        if (bottomAppBarDetails.menu.findItem(R.id.projectDetailsFavorite).isChecked.not())
                            favoriteProjectImpl.doFavoriteProject(request)
                        else
                            favoriteProjectImpl.doUnFavoriteProject(request)
                    }
                }
            }
            true
        }
        buttonShowCommentDialog.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                commentJob.await().show(supportFragmentManager, "commentDialog")
            }
        }
    }
    override fun afterCheckLoginSuccess() {
        setSupportActionBar(toolbarProjectDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarProjectDetails.setNavigationOnClickListener {
            finish()
        }
        projectId = intent.getIntExtra("projectId", -1)
        projectName = intent.getStringExtra("projectTopic")
        textProjectTitle.text = projectName
        fragmentJob = GlobalScope.async(Dispatchers.Main) {
            viewPagerFragments = listOf(DetailsFragment.newInstance(projectId.toString(), ""), CommentsFragment.newInstance(projectId.toString(), ""))
            FragmentAdapter(supportFragmentManager, viewPagerFragments).let {
//                commentsFragment = it.list[1] as CommentsFragment
//                commentsFragment?.marginHeight(appbarHeight)
                viewPagerDetails.adapter = it
                tabProjectDetails.setupWithViewPager(viewPagerDetails)
                it.list[0] as DetailsImpl.View
            }
        }
        bottomJob = GlobalScope.async(Dispatchers.Main) {
            ProjectActionItemListDialogFragment.newInstance(0)
        }

        commentJob = GlobalScope.async(Dispatchers.Main) {
            CommentDialogFragment().apply {
                commentCallBack = this@ProjectDetailsActivity
                fillTextCallBack = this@ProjectDetailsActivity
            }
        }

        Attributes.loginUserInfo?.run {
            detailsImpl.doRequest(GetProjectRequest(token = token, projectId = projectId.toString()))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        detailsImpl.detachView()
        mapProjectDetails.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.EDIT_PROJECT,RequestCode.SELECT_IMAGE,RequestCode.SELECT_VIDEO -> if (resultCode == ResultCode.OK) detailsImpl.doRequest(GetProjectRequest(Attributes.token, projectId.toString()))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun commentSuccess() {
        if (viewPagerDetails.currentItem == 1)
            if (viewPagerFragments[1] is CommentCallBack)
                (viewPagerFragments[1] as CommentCallBack).commentSuccess()
    }

    override fun getProjectId(): String {
        return projectId.toString()
    }

    override fun getTextContent(): CharSequence {
        return buttonShowCommentDialog.text
    }

    override fun setTextContent(content: CharSequence) {
        buttonShowCommentDialog.text = content
    }
    override fun onGetDetailsFailed(jsonObject: JsonObject) {
        GlobalScope.launch(Dispatchers.Main) { fragmentJob.await().onGetDetailsFailed(jsonObject) }
        bottomAppBarDetails.isVisible = false
//        buttonJoinProjectDetails.isVisible = false
        toast(jsonObject.toJson())
    }

    override fun onGetDetailsSuccess(project: Project) {
        this.project = project
        project.run {
            projectType = projectTypeId
            GlobalScope.launch(Dispatchers.Main) {
                fragmentJob.await().onGetDetailsSuccess(project)
//                val menu = bottomAppBarDetails.menu
//                if (Attributes.username == username) {
//                    favoriteIcon.await().isVisible = true
//                    menu.findItem(R.id.projectDetailsAction).isVisible = true
//                    menu.findItem(R.id.projectDetailsFavorite).isVisible = false
//                }else {
//                    favoriteIcon.await().isVisible = false
//                    menu.findItem(R.id.projectDetailsFavorite).isVisible = true
//                }
//                if (join) {
//                    buttonJoinProjectDetails.isVisible = false
//                }

                bottomAppBarDetails.menu.apply {
                    findItem(R.id.projectDetailsAction).isVisible = Attributes.username == username
                    findItem(R.id.projectDetailsJoin).apply {
                        isChecked = join
                        icon = if (join)
                            getDrawable(R.drawable.ic_favorite_black_24dp)?.apply {
                                setTint(getColor(R.color.colorAccent))
                            } else
                            getDrawable(R.drawable.ic_favorite_border_black_24dp)
                    }
                    findItem(R.id.projectDetailsFavorite).apply {
                        isChecked = favorite
                        icon = if (favorite)
                            getDrawable(R.drawable.ic_star_black_24dp)?.apply {
                                setTint(getColor(R.color.colorAccent))
                            } else
                            getDrawable(R.drawable.ic_star_border_black_24dp)
                    }
                }
//                if (Attributes.username == username)
//                    actionIcon.await().apply {
//                        isVisible = true
//                        icon = icon.apply {
//                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
//                        }
//                    }
//                favoriteIcon.await().apply {
//                    icon = if (favorite)
//                        getDrawable(R.drawable.ic_star_black_24dp)!!.apply {
//                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
//                        }
//                    else
//                        getDrawable(R.drawable.ic_star_border_black_24dp)!!.apply {
//                            DrawableCompat.setTint(this, getColor(R.color.colorAccent))
//                        }
//                    isChecked = favorite
//                }
            }
            mapProjectDetails.map.addMarker(
                    MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .title(projectAddress.split(projectRegion).last().replace("·", ""))
                            .anchor(1f, 1f))
                    .apply {
                        showInfoWindow()
                    }
            mapProjectDetails.map.apply {
//                setPointToCenter(latitude.toInt(),longitude.toInt())
                setCenter(LatLng(latitude, longitude)); setZoom(30)
            }
            mapProjectDetails.isVisible = true
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
            bottomAppBarDetails.menu.findItem(R.id.projectDetailsFavorite).apply {
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
            bottomAppBarDetails.menu.findItem(R.id.projectDetailsFavorite).apply {
                isChecked = false
                icon = getDrawable(R.drawable.ic_star_border_black_24dp)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_project_details, menu)
//        menu?.apply {
//            forEach {
//                it.isVisible = it.itemId == R.id.projectDetailsFavorite
//            }
//            favoriteIcon = GlobalScope.async(Dispatchers.Main) {
//                findItem(R.id.projectDetailsFavorite)
//            }
//        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            null -> {
            }
            R.id.projectDetailsAction -> {

            }
            R.id.projectDetailsFavorite -> {
                isFavoriteRequest = true
                Attributes.loginUserInfo?.apply {
                    val request = ProjectFavoriteRequest(
                            projectId = projectId,
                            projectUserId = userId,
                            token = token,
                            projectClassifyId = projectType.toString()
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

    override fun onResume() {
        super.onResume()
        mapProjectDetails.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapProjectDetails.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapProjectDetails.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapProjectDetails.onSaveInstanceState(outState)
    }
    override fun onProjectActionItemClicked(position: Int) {
        when (position) {
            R.drawable.ic_outline_edit_24px -> startActivityForResult(Intent(this,
                    ReleaseProjectActivity::class.java).apply {
                putExtra("editMode", true)
                putExtra("projectId", projectId)
                putExtra("projectInfo", project.toJson())
            }, RequestCode.EDIT_PROJECT)
            R.drawable.ic_outline_delete_outline_24px -> MaterialAlertDialogBuilder(this)
                    .setTitle("删除项目")
                    .setPositiveButton("确定") { _, _ ->
                        try {
                            GlobalScope.launch {
                                RemoteService.deleteProject(
                                        GetProjectRequest(Attributes.token,
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
            R.drawable.ic_outline_add_photo_alternate_24px -> startActivityForResult(
                    Intent(this,SelectImageAndVideoActivity::class.java).apply {
                        type = "image"
                        putExtra("projectId",projectId.toString())
                    },RequestCode.SELECT_IMAGE)
            R.drawable.ic_outline_video_call_24px -> startActivityForResult(
                    Intent(this,SelectImageAndVideoActivity::class.java).apply {
                        type = "video"
                        putExtra("projectId",projectId.toString())
                    },RequestCode.SELECT_VIDEO)
        }
    }

//    inner class FixViewPagerScroll(private val mView: ViewPager): View.OnLayoutChangeListener {
//        private var mLastChildHeight = 0
//        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
//            v?.apply {
//                val childHeight = top - bottom - paddingTop - paddingBottom
//                if (childHeight == 0)
//                    return
//                if (mLastChildHeight == 0) {
//                    mLastChildHeight = childHeight
//                    return
//                }
//
//                if (mLastChildHeight == childHeight)
//                    return
////                recomputeScrollPosition(mView,mView.scrollY,childHeight,mLastChildHeight)
//            }
//        }
//    }
//
//    private fun recomputeScrollPosition(viewPager: ViewPager, scrollY: Int, childHeight: Int, oldChildHeight: Int){
//        val scrollOffset = scrollY.toFloat() / oldChildHeight
//        val newOffsetPixels = (scrollOffset * childHeight).toInt()
//        viewPager.scrollTo(viewPager.scrollX,newOffsetPixels)
//    }
}
