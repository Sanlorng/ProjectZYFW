package com.bigcreate.zyfw.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ExploreDetailsCommentListAdapter
import com.bigcreate.zyfw.adapter.ProvinceProjectAdapter
import com.bigcreate.zyfw.base.Attributes
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

class ExploreDetailsActivity : AppCompatActivity(),
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
        setContentView(R.layout.activity_explore_details)
        networkState = ViewModelProvider(this)[NetworkStateViewModel::class.java]
        dynamicId = intent.getIntExtra("dynamicId",-1)
        commentDialog.commentCallBack = this
//        exploreItemCommentList.adapter = commentAdapter
        bottomExploreBar.setOnClickListener {
            commentDialog.show(supportFragmentManager,javaClass.name)
        }
        swipeLayoutCommentExploreDetails.setOnRefreshListener {
            refreshDetails()
        }
        refreshDetails()
//        Attributes.addProvinceListener(javaClass.name) {
//            exploreItemCommentList.adapter = ProvinceProjectAdapter() {
//                ProvinceProjectDialogFragment().apply {
//                    this.pronviceProject = this@ProvinceProjectAdapter
//                    show(supportFragmentManager,"provinceProject")
//                }
//            }
//                    .apply {
//                        submitList(PagedList.Builder<Int, ProvinceProject>(
//                                ProvinceProjectDataSource(it),
//                                PagedList.Config.Builder()
//                                        .setPageSize(20)
//                                        .setPrefetchDistance(40)
//                                        .build()
//                        ).setNotifyExecutor {
//                            Handler(Looper.getMainLooper()).post(it)
//                        }.setFetchExecutor {
//                            Attributes.backgroundExecutors.execute(it)
//                        }
//                                .build())
//                    }
//        }
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
//        exploreItemCommentList.isVisible = false
//        exploreItemCommentList.isVisible = true

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
    }

    override fun onFavoriteSuccess() {

    }

    override fun onLikeSuccess() {

    }

    override fun onUnFavoriteSuccess() {

    }

    override fun onUnlikeSuccess() {

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
