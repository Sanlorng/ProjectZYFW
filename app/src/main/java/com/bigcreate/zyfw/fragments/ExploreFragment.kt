package com.bigcreate.zyfw.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.toJson
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ExploreDetailsActivity
import com.bigcreate.zyfw.activities.MainActivity
import com.bigcreate.zyfw.activities.PublishExploreActivity
import com.bigcreate.zyfw.adapter.ExploreListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.Status
import com.bigcreate.zyfw.base.paddingStatusBar
import com.bigcreate.zyfw.datasource.ExploreDataSource
import com.bigcreate.zyfw.models.ExploreCommentRequest
import com.bigcreate.zyfw.models.ExploreItem
import com.bigcreate.zyfw.models.ExploreItemFavoriteRequest
import com.bigcreate.zyfw.models.ExploreItemLikeRequest
import com.bigcreate.zyfw.mvp.explore.ExploreCommentImpl
import com.bigcreate.zyfw.mvp.explore.ExploreFavoriteImpl
import com.bigcreate.zyfw.mvp.explore.ExploreLikeImpl
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.item_user_share_content.view.*

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment(), MainActivity.ChildFragment {
    private val commentTemp = SparseArray<String>()
    private lateinit var networkStateViewModel: NetworkStateViewModel
    private lateinit var commentDialogFragment: CommentDialogFragment
    private val favoriteImpl = ExploreFavoriteImpl(null)
    private val likeImpl = ExploreLikeImpl(null)
    private val commentImpl = ExploreCommentImpl(CommentItemView())
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onLoginSuccess() {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        commentDialogFragment = CommentDialogFragment()
        networkStateViewModel = ViewModelProvider(this).get(NetworkStateViewModel::class.java)
        networkStateViewModel.state.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> showProgress(false)
                Status.FAILED -> {
                    showProgress(false)
                    toast(it.msg)
                }
                Status.RUNNING -> showProgress(true)
            }
        })
        startPublishExplore.setOnClickListener {
            startActivityForResult(Intent(it.context, PublishExploreActivity::class.java), RequestCode.PUBLISH_EXPLORE)
        }
        refreshExploreList.paddingStatusBar()
        refreshExploreList.setOnRefreshListener {
            refreshList()
            refreshExploreList.isRefreshing = false
        }
        listExplore.itemAnimator = DefaultItemAnimator()
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(context!!.getDrawable(R.drawable.divider)!!)
        listExplore.addItemDecoration(decoration)
        refreshList()
    }



    private fun showProgress(boolean: Boolean) {
        refreshExploreList.isRefreshing = boolean
    }

    private fun refreshList() {
        listExplore.layoutManager = LinearLayoutManager(context)
        listExplore.adapter = ExploreListAdapter({ view, item, position ->
            when (view.id) {
                R.id.exploreItemLike -> {
                    if (item.praise) {
                        likeImpl.doUnlikeRequest(LikeItemView(item, position), ExploreItemLikeRequest(
                                Attributes.token,item.dyId
                        ))
                    } else {
                        likeImpl.doLikeRequest(LikeItemView(item, position), ExploreItemLikeRequest(
                                Attributes.token,item.dyId
                        ))
                    }
                }

                R.id.exploreItemFavorite -> {
                    if (item.favorite) {
                        favoriteImpl.doUnfavoriteRequest(FavoriteItemView(item,position), ExploreItemFavoriteRequest(
                                Attributes.token,item.dyId
                        ))
                    } else {
                        favoriteImpl.doFavoriteRequest(FavoriteItemView(item,position), ExploreItemFavoriteRequest(
                                Attributes.token,item.dyId
                        ))
                    }
                }
                R.id.exploreItemComment -> {
                    commentDialogFragment.commentCallBack = object : CommentDialogFragment.CommentCallback {
                        override fun getCommentContent(): CharSequence {
                            return commentTemp[item.dyId,""]
                        }

                        override fun onCommentDone(content: String) {
                            commentTemp.remove(item.dyId)
                            commentImpl.doRequest(CommentItemView(), ExploreCommentRequest(
                                    Attributes.token,item.dyId,content
                            ))
                            //toast("评论成功")
                        }

                        override fun setCommentContent(content: String) {
                            commentTemp.put(item.dyId,content)
                        }
                    }

                    commentDialogFragment.show(childFragmentManager,"ExploreFragment")
                }

                else -> {
                    val intent = Intent(context!!, ExploreDetailsActivity::class.java)
                    intent.putExtra("shareItem", item.toJson())
                    view.apply {
//                        startActivity(intent)
                        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                                Pair(exploreItemUserAvatar, "shareAvatar"),
                                Pair(exploreItemContent, "shareContent"),
                                Pair(exploreItemUserNick, "shareNick"),
                                Pair(exploreItemCreateTime, "shareTime"),
                                Pair(listImageExploreItem, "shareImages")).toBundle())
                    }

//                    context?.startActivity(ExploreDetailsActivity::class.java)
                }
            }

        }) { view, item, intent ->
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, view, item).toBundle())
        }.apply {
            submitList(PagedList.Builder<Int, ExploreItem>(
                    ExploreDataSource(networkStateViewModel.state),
                    PagedList.Config.Builder()
                            .setPageSize(10)
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
    inner class FavoriteItemView(private val item: ExploreItem,private val position: Int):ExploreFavoriteImpl.View {
        override fun onFavoriteSuccess() {
            Log.e("FavoriteSuccess",item.toString())
            item.favorite = true
            listExplore.adapter?.notifyItemChanged(position)
        }

        override fun onUnFavoriteSuccess() {
            item.favorite = false
            listExplore.adapter?.notifyItemChanged(position)
        }

        override fun getViewContext(): Context {
            return context!!
        }
    }
    inner class LikeItemView(private val item: ExploreItem,private val position: Int):ExploreLikeImpl.View {
        override fun onLikeSuccess() {
            item.praise = true
            listExplore.adapter?.notifyItemChanged(position)
        }

        override fun onUnlikeSuccess() {
            item.praise = false
            listExplore.adapter?.notifyItemChanged(position)
        }

        override fun getViewContext(): Context {
            return context!!
        }
    }

    inner class CommentItemView(): ExploreCommentImpl.View {
        override fun onCommentFailed() {

        }

        override fun onCommentSuccess() {
            commentDialogFragment.dismiss()
            toast("评论成功")
        }
        override fun getViewContext(): Context {
            return context!!
        }
    }
}
