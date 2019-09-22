package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.statusBarHeight
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ExploreDetailsCommentListAdapter
import com.bigcreate.zyfw.adapter.ExploreListAdapter
import com.bigcreate.zyfw.adapter.ProvinceProjectAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.paddingStatusBar
import com.bigcreate.zyfw.datasource.DynamicCommentListDataSource
import com.bigcreate.zyfw.datasource.ExploreDataSource
import com.bigcreate.zyfw.datasource.ProvinceProjectDataSource
import com.bigcreate.zyfw.fragments.CommentDialogFragment
import com.bigcreate.zyfw.fragments.ProvinceProjectDialogFragment
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.explore.*
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_explore_details.*
import kotlinx.android.synthetic.main.activity_province_project.*
import kotlinx.android.synthetic.main.item_user_share_content.*

class ExploreDetailsActivity : AuthLoginActivity(),
        GetExploreDetailsImpl.View ,
        DeleteExploreImpl.View,
        ExploreLikeImpl.View,
        ExploreFavoriteImpl.View,
        ExploreCommentImpl.View,
        CommentDialogFragment.CommentCallback{
    private var dynamicId: Int = -1
    private val getExploreDetailsImpl = GetExploreDetailsImpl(this)
    private val delExploreDetailsImpl = DeleteExploreImpl(this)
    private val favoriteImpl = ExploreFavoriteImpl(this)
    private val likeImpl = ExploreLikeImpl(this)
    private val commentImpl = ExploreCommentImpl(this)
    private val commentDialog = CommentDialogFragment()
    private val commentAdapter = ExploreDetailsCommentListAdapter()
    private lateinit var networkState:NetworkStateViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun afterCheckLoginSuccess() {
        bottomExploreBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.bottomExploreFavorite -> {
                    if (it.isChecked) {
                        favoriteImpl.doUnfavoriteRequest(this,ExploreItemFavoriteRequest(Attributes.token,dynamicId))
                    }else {
                        favoriteImpl.doFavoriteRequest(this,ExploreItemFavoriteRequest(Attributes.token,dynamicId))
                    }
                }

                R.id.bottomExploreLike -> {
                    if (it.isChecked) {
                        likeImpl.doUnlikeRequest(this,ExploreItemLikeRequest(Attributes.token,dynamicId))
                    }else {
                        likeImpl.doLikeRequest(this, ExploreItemLikeRequest(Attributes.token,dynamicId))
                    }
                }
            }
            true
        }
        exploreItemCommentList.adapter = commentAdapter
        refreshDetails()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_explore_details)
        networkState = ViewModelProvider(this)[NetworkStateViewModel::class.java]
        dynamicId = intent.getIntExtra("dynamicId",-1)
        commentDialog.commentCallBack = this
        bottomExploreBar.setOnClickListener {
            commentDialog.show(supportFragmentManager,javaClass.name)
        }
        swipeLayoutCommentExploreDetails.setOnRefreshListener {
            refreshDetails()
        }
        setSupportActionBar(toolbarExploreDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarExploreDetails.setNavigationOnClickListener {
            finish()
        }
        toolbarExploreDetails.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        toolbarExploreDetails.layoutParams.apply {
            height = toolbarExploreDetails.measuredHeight + statusBarHeight
            toolbarExploreDetails.layoutParams = this
        }
        toolbarExploreDetails.paddingStatusBar()
        layoutUserShareContent.updatePadding(top = layoutUserShareContent.paddingTop + statusBarHeight)
    }

    private fun refreshDetails() {
        getExploreDetailsImpl.doRequest(ExploreRequest(Attributes.token,dynamicId))
        commentAdapter.
            submitList(PagedList.Builder<Int,ExploreCommentItem>(
                    DynamicCommentListDataSource(networkState.state,dynamicId),
                    PagedList.Config.Builder()
                            .setPageSize(10)
                            .setPrefetchDistance(20)
                            .build())
                    .setNotifyExecutor {
                Handler(Looper.getMainLooper()).post(it)
            }
                    .setFetchExecutor {
                        Attributes.backgroundExecutors.execute(it)
                    }.build())

    }
    override fun onGetExploreDetailsSuccess(item: ExploreItem) {
        swipeLayoutCommentExploreDetails.isRefreshing = false
        item.apply {
            exploreItemContent.text = dyContent
            exploreItemCreateTime.text = item.dyReleaseTime
            exploreItemUserNick.text = item.userInfoByPart.userNick
            Glide.with(this@ExploreDetailsActivity)
                    .load(item.userInfoByPart.userHeadPictureLink)
                    .into(exploreItemUserAvatar)
            user_share_action_layout.isVisible = false
            listImageExploreItem.layoutManager = GridLayoutManager(this@ExploreDetailsActivity, when {
                dynamicPicture.size % 5 == 0 -> 5
                dynamicPicture.size % 4 == 0 -> 4
                else -> 3
            })
            listImageExploreItem.adapter = ExploreListAdapter.ExploreItemImageAdapter({ view, item, intent ->
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@ExploreDetailsActivity, view, item).toBundle())
            },dynamicPicture)
            if (praise) {
                onLikeSuccess()
            }else {
                onUnlikeSuccess()
            }

            if (favorite) {
                onFavoriteSuccess()
            }else {
                onUnlikeSuccess()
            }
        }
    }

    override fun onGetExploreDetailsFailed(jsonObject: JsonObject) {
        swipeLayoutCommentExploreDetails.isRefreshing = false
    }

    override fun onDeleteExploreSuccess() {
        finish()
    }

    override fun onDeleteExploreFailed(jsonObject: JsonObject) {
        toast("删除失败")
    }

    override fun getCommentContent(): CharSequence {
        return bottomCommentTextExplore.text
    }

    override fun onCommentDone(content: String) {
        commentImpl.doRequest(ExploreCommentRequest(Attributes.token,dynamicId,content))
    }

    override fun onCommentFailed() {

    }

    override fun onCommentSuccess() {
        commentDialog.dismiss()
        bottomCommentTextExplore.text = ""
        refreshDetails()
    }

    override fun onFavoriteSuccess() {
        bottomExploreBar.menu.findItem(R.id.bottomExploreFavorite).apply {
            isChecked = true
            setIcon(R.drawable.ic_star_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = ColorStateList.valueOf(getColor(R.color.favorite))
            }
        }
    }

    override fun onLikeSuccess() {

        bottomExploreBar.menu.findItem(R.id.bottomExploreLike).apply {
            isChecked = true
            setIcon(R.drawable.ic_favorite_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = ColorStateList.valueOf(getColor(R.color.like))
            }
        }
    }

    override fun onUnFavoriteSuccess() {
        bottomExploreBar.menu.findItem(R.id.bottomExploreFavorite).apply {
            isChecked = false
            setIcon(R.drawable.ic_star_border_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = null
            }
        }
    }

    override fun onUnlikeSuccess() {
        bottomExploreBar.menu.findItem(R.id.bottomExploreLike).apply {
            isChecked = false
            setIcon(R.drawable.ic_favorite_border_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = null
            }
        }
    }

    override fun setCommentContent(content: String) {
        bottomCommentTextExplore.text = content
    }
    override fun getViewContext(): Context {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        favoriteImpl.detachView()
        likeImpl.detachView()
        commentImpl.detachView()
        delExploreDetailsImpl.detachView()
        getExploreDetailsImpl.detachView()
        commentDialog.commentCallBack = null
    }
}
