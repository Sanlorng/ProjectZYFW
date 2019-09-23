package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ExploreDetailsCommentListAdapter
import com.bigcreate.zyfw.adapter.ExploreListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.base.paddingStatusBar
import com.bigcreate.zyfw.datasource.DynamicCommentListDataSource
import com.bigcreate.zyfw.fragments.CommentDialogFragment
import com.bigcreate.zyfw.models.*
import com.bigcreate.zyfw.mvp.explore.*
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_explore_details.*
import kotlinx.android.synthetic.main.item_user_share_content.*

class ExploreDetailsActivity : AuthLoginActivity(),
        GetExploreDetailsImpl.View ,
        DeleteExploreImpl.View,
        DeleteExploreCommentImpl.View,
        ExploreLikeImpl.View,
        ExploreFavoriteImpl.View,
        ExploreCommentImpl.View,
        CommentDialogFragment.CommentCallback{
    private var dynamicId: Int = -1
    private val getExploreDetailsImpl = GetExploreDetailsImpl(this)
    private val delExploreDetailsImpl = DeleteExploreImpl(this)
    private val delExploreCommentItemImpl = DeleteExploreCommentImpl(this)
    private val favoriteImpl = ExploreFavoriteImpl(this)
    private val likeImpl = ExploreLikeImpl(this)
    private val commentImpl = ExploreCommentImpl(this)
    private val commentDialog = CommentDialogFragment()
    private val commentAdapter = ExploreDetailsCommentListAdapter { item, position, itemView ->
        if (Attributes.userId == item.dyCommentUserId || Attributes.userId == this.item?.dyReleaseUserId) {
                dialog("删除评论","你确定要删除这条评论吗？","确定", DialogInterface.OnClickListener { dialog, which ->
                    delExploreCommentItemImpl.doRequest(ExploreCommentDeleteRequest(
                            Attributes.token,
                            item.dyCommentId,
                            item.dyCommentUserId,
                            item.dyCommentTime
                    ))
                },"取消", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
        }
        refreshDetails()
    }
    private var item : ExploreItem? = null
    private lateinit var popupMenu: PopupMenu
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

                R.id.bottomExploreEdit -> {
                    PopupMenu(this, bottomExploreBar.findViewById(R.id.bottomExploreEdit)).apply {
                        if (menu is MenuBuilder) {
                            (menu as MenuBuilder).setOptionalIconsVisible(true)
                        }
                        inflate(R.menu.explore_edit)
                        menu.findItem(R.id.deleteExplore).apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                iconTintList = ColorStateList.valueOf(Color.RED)
                                val string = SpannableString(getString(R.string.delete))
                                string.setSpan(ForegroundColorSpan(Color.RED),0,string.length,SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
                                title = string
                            }
                        }

                        setOnMenuItemClickListener { its->
                            when(its.itemId) {
                                R.id.editExplore -> {
                                    startActivity<PublishExploreActivity> {
                                        putExtra("dynamicId",dynamicId)
                                        putExtra("editType",true)
                                        putExtra("dynamicContent",exploreItemContent.text)
                                    }
                                }
                                R.id.deleteExplore -> {
                                    delExploreDetailsImpl.doRequest(ExploreDeleteRequest(
                                            dynamicId,
                                            Attributes.userId,
                                            Attributes.token
                                    ))
                                }
                            }
                            true
                        }
                        show()
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
        if (bottomExploreBar.menu is MenuBuilder) {
            (bottomExploreBar.menu as MenuBuilder).setOptionalIconsVisible(true)
        }

        Log.e("isNull",(bottomExploreBar.findViewById<View>(R.id.bottomExploreEdit) == null).toString())
//        bottomExploreBar.inflateMenu(R.menu.explore_edit)
        bottomExploreBar.menu.findItem(R.id.bottomExploreEdit).isVisible = false
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
        this.item = item
        item.apply {
            exploreItemContent.text = dyContent
            exploreItemCreateTime.text = item.dyReleaseTime
            exploreItemUserNick.text = item.userInfoByPart.userNick
            Glide.with(this@ExploreDetailsActivity)
                    .load(item.userInfoByPart.userHeadPictureLink)
                    .circleCrop()
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
            bottomExploreBar.menu.findItem(R.id.bottomExploreEdit).isVisible = Attributes.userId == dyReleaseUserId
//            bottomExploreBar.menu.findItem(R.id.editExplore).isVisible = Attributes.userId == dyReleaseUserId
//            bottomExploreBar.menu.findItem(R.id.deleteExplore).isVisible = Attributes.userId == dyReleaseUserId
        }
    }

    override fun onGetExploreDetailsFailed(jsonObject: JsonObject) {
        swipeLayoutCommentExploreDetails.isRefreshing = false
    }

    override fun onDeleteExploreSuccess() {
        setResult(RequestCode.DELETE_OK)
        finish()
    }

    override fun onDeleteExploreFailed(jsonObject: JsonObject) {
        toast("删除失败")
    }

    override fun onDeleteExploreCommentFailed(jsonObject: JsonObject) {
        toast("删除失败")
    }

    override fun onDeleteExploreCommentSuccess() {
        toast("删除成功")
        refreshDetails()
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
        setResult(RequestCode.UPDATE_OK,Intent().apply {
            putExtra("position",intent.getIntExtra("position",0))
            putExtra("item",item.toJson())
        })
    }

    override fun onLikeSuccess() {

        bottomExploreBar.menu.findItem(R.id.bottomExploreLike).apply {
            isChecked = true
            setIcon(R.drawable.ic_favorite_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = ColorStateList.valueOf(getColor(R.color.like))
            }
        }
        setResult(RequestCode.UPDATE_OK, Intent().apply {
            putExtra("position",intent.getIntExtra("position",0))
            putExtra("item",item.toJson())
        })
    }

    override fun onUnFavoriteSuccess() {
        bottomExploreBar.menu.findItem(R.id.bottomExploreFavorite).apply {
            isChecked = false
            setIcon(R.drawable.ic_star_border_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = null
            }
        }
        setResult(RequestCode.UPDATE_OK,Intent().apply {
            putExtra("position",intent.getIntExtra("position",0))
            putExtra("item",item.toJson())
        })
    }

    override fun onUnlikeSuccess() {
        bottomExploreBar.menu.findItem(R.id.bottomExploreLike).apply {
            isChecked = false
            setIcon(R.drawable.ic_favorite_border_black_24dp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                iconTintList = null
            }
        }
        setResult(RequestCode.UPDATE_OK,Intent().apply {
            putExtra("position",intent.getIntExtra("position",0))
            putExtra("item",item.toJson())
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            RequestCode.UPDATE_OK -> {
                refreshDetails()
                setResult(RequestCode.UPDATE_OK,Intent().apply {
                    putExtra("position",intent.getIntExtra("position",0))
                    putExtra("item",item.toJson())
            })
            }
        }
    }
}
