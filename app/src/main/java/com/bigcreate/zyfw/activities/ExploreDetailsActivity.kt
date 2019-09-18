package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.fromJson
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ExploreDetailsCommentListAdapter
import com.bigcreate.zyfw.adapter.ExploreListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.datasource.DynamicCommentListDataSource
import com.bigcreate.zyfw.fragments.CommentDialogFragment
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.explore.ExploreCommentImpl
import com.bigcreate.zyfw.mvp.explore.ExploreFavoriteImpl
import com.bigcreate.zyfw.mvp.explore.ExploreLikeImpl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_explore_details.*
import kotlinx.android.synthetic.main.item_user_share_content.*
import kotlinx.android.synthetic.main.item_user_share_content.view.*

class ExploreDetailsActivity : AppCompatActivity(),
        CommentDialogFragment.CommentCallback,
        ExploreCommentImpl.View,
        ExploreLikeImpl.View,
        ExploreFavoriteImpl.View{
    private val commentDialogFragment = CommentDialogFragment()
    private val commentImpl = ExploreCommentImpl(this)
    private val favoriteImpl = ExploreFavoriteImpl(this)
    private val likeImpl = ExploreLikeImpl(this)
    private var dynamicId = -1
    private lateinit var item: ExploreItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_details)
        window.translucentSystemUI(true)
//        setSupportActionBar(toolbarExploreDetails)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbarExploreDetails.setNavigationOnClickListener {
//            finishAfterTransition()
//        }
        bottomExploreBar.setOnClickListener {
            commentDialogFragment.show(supportFragmentManager,"exploreDetails")
        }
        bottomExploreBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.bottomExploreLike -> {
                    if (item.praise) {
                        likeImpl.doUnlikeRequest(request = ExploreItemLikeRequest(
                                Attributes.token,dynamicId
                        ))
                    }else {
                        likeImpl.doLikeRequest(request = ExploreItemLikeRequest(
                                Attributes.token,dynamicId
                        ))
                    }
                }

                R.id.bottomExploreFavorite -> {
                    if (item.favorite) {
                        favoriteImpl.doUnfavoriteRequest(request = ExploreItemFavoriteRequest(
                                Attributes.token,dynamicId
                        ))
                    }else {
                        favoriteImpl.doFavoriteRequest(request = ExploreItemFavoriteRequest(
                                Attributes.token,dynamicId
                        ))
                    }
                }
            }
            true
        }
        swipeLayoutCommentExploreDetails.setOnRefreshListener {
            refreshList()
            swipeLayoutCommentExploreDetails.isRefreshing = false
        }

        commentDialogFragment.commentCallBack = this
        user_share_action_layout.isVisible = false
        val data = intent.getStringExtra("shareItem") ?: ""
        val item = data.fromJson<ExploreItem>()
        this.item = item.apply {
            if (praise) {
                val like = bottomExploreBar.menu.findItem(R.id.bottomExploreLike)
                like.isChecked = true
            }
            exploreItemUserAvatar.setOnClickListener {
                it.context.startActivity<MyDetailsActivity> {
                    putExtra("userId",userInfoByPart.userId)
                }
            }
            dynamicId = dyId
            exploreItemUserNick.text = userInfoByPart.userNick
            exploreItemContent.text = dyContent
            exploreItemCreateTime.text = dyReleaseTime.split(" ").first()
            Glide.with(this@ExploreDetailsActivity)
                    .load(userInfoByPart.userHeadPictureLink)
                    .circleCrop()
                    .into(exploreItemUserAvatar)
            if (listImageExploreItem.adapter == null) {
                listImageExploreItem.layoutManager = GridLayoutManager(this@ExploreDetailsActivity, when {
                    dynamicPicture.size % 5 == 0 -> 5
                    dynamicPicture.size % 4 == 0 -> 4
                    else -> 3
                })
                listImageExploreItem.adapter = ExploreListAdapter.ExploreItemImageAdapter({ view, item, intent ->
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@ExploreDetailsActivity, view, item).toBundle())
                }, dynamicPicture)
                refreshList()
            }

        }

    }

    private fun refreshList() {
        exploreItemCommentList.adapter = ExploreDetailsCommentListAdapter().apply {
            submitList(PagedList.Builder<Int,ExploreCommentItem> (
                    DynamicCommentListDataSource(MutableLiveData(),dynamicId),
                    PagedList.Config.Builder()
                            .setPageSize(20)
                            .setPrefetchDistance(20)
                            .build()
            ).setNotifyExecutor {
                Handler(Looper.getMainLooper()).post(it)
            }.setFetchExecutor {
                Attributes.backgroundExecutors.execute(it)
            }
                    .build())
        }
    }

    override fun getCommentContent(): CharSequence {
        return bottomCommentTextExplore.text
    }

    override fun onCommentDone(content: String) {
        commentImpl.doRequest(ExploreCommentRequest(
                Attributes.token,dynamicId,content
        ))
    }

    override fun setCommentContent(content: String) {
        bottomCommentTextExplore.text = content
    }

    override fun onFavoriteSuccess() {
        item.favorite = true
    }

    override fun onUnFavoriteSuccess() {
        item.favorite = false
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onCommentFailed() {

    }

    override fun onCommentSuccess() {
        commentDialogFragment.dismiss()
        refreshList()
        bottomCommentTextExplore.text = ""
        toast("评论成功")
    }

    override fun onLikeSuccess() {
        item.praise = true
    }

    override fun onUnlikeSuccess() {
        item.praise = false
    }
}
