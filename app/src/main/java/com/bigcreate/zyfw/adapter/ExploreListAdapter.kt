package com.bigcreate.zyfw.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.PopupMenuCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ImageViewActivity
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.DynamicPicture
import com.bigcreate.zyfw.models.ExploreItem
import com.bigcreate.zyfw.mvp.explore.ExploreFavoriteImpl
import com.bigcreate.zyfw.mvp.explore.ExploreLikeImpl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user_share_content.view.*
import kotlinx.android.synthetic.main.layout_item_select.view.*

class ExploreListAdapter(private val onItemClick: ((view: View, item: ExploreItem, position: Int) -> Unit)? = null,
                         private val onItemImageOpenTransition: ((view: View, item: String, intent: Intent) -> Unit)? = null) : PagedListAdapter<ExploreItem, ExploreListAdapter.ViewHolder>(diff) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            getItem(position)?.apply {
                Log.e("imageList",dynamicPicture.toString())
                Log.e("item",this.toString())
                exploreItemUserNick.text = userInfoByPart.userNick
                exploreItemContent.text = dyContent
                exploreItemCreateTime.text = dyReleaseTime.split(" ").first()
                Glide.with(context)
                        .load(userInfoByPart.userHeadPictureLink)
                        .circleCrop()
                        .into(exploreItemUserAvatar)
                exploreItemUserAvatar.setOnClickListener {
                    it.context.startActivity<MyDetailsActivity> {
                        putExtra("userId",userInfoByPart.userId)
                    }
                }

                listImageExploreItem.layoutManager = GridLayoutManager(context, when {
                        dynamicPicture.size % 5 == 0 -> 5
                        dynamicPicture.size % 4 == 0 -> 4
                        else -> 3
                })
                listImageExploreItem.adapter = ExploreItemImageAdapter(onItemImageOpenTransition, dynamicPicture)
//                if (Attributes.userId == userInfoByPart.userId) {
//                    exploreItemUserAction.isVisible = true
//                    exploreItemUserAction.setOnClickListener {
//                    }
//                }
//                if (Attributes.userId == dyReleaseUserId) {
//                    exploreItemUserAction.isVisible = true
//                    exploreItemUserAction.setOnClickListener {
//                        PopupMenu(context,exploreItemUserAction).apply {
//                            menuInflater.inflate(R.menu.explore_edit,menu)
//                            if (menu is MenuBuilder) {
//                                (menu as MenuBuilder).setOptionalIconsVisible(true)
//                            }
//                            menu.findItem(R.id.deleteExplore).apply {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
//                                    iconTintList = ColorStateList.valueOf(Color.RED)
//                                    val string = SpannableString(context.getString(R.string.delete))
//                                    string.setSpan(ForegroundColorSpan(Color.RED),0,string.length,SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
//                                    title = string
//                                }
//                            }
//                            setOnMenuItemClickListener {
//
//                                true
//                            }
//                            show()
//                        }
//                    }
//                }else {
//                    exploreItemUserAction.isVisible = false
//                }

                if (favorite) {
                    exploreItemFavorite.setImageResource(R.drawable.ic_star_black_24dp)
                    exploreItemFavorite.imageTintList = ColorStateList.valueOf(context.getColor(R.color.favorite))
                }else {
                    exploreItemFavorite.imageTintList = exploreItemComment.imageTintList
                    exploreItemFavorite.setImageResource(R.drawable.ic_star_border_black_24dp)
                }

                if (praise) {
                    exploreItemLike.setImageResource(R.drawable.ic_favorite_black_24dp)
                    exploreItemLike.imageTintList = ColorStateList.valueOf(context.getColor(R.color.like))
                }else {
                    exploreItemLike.imageTintList = exploreItemComment.imageTintList
                    exploreItemLike.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
                val typedValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.textColorSecondary,typedValue,true)
                exploreItemComment.imageTintList = ColorStateList.valueOf(context.getColor(typedValue.resourceId))
                setOnClickListener {
                    onItemClick?.invoke(it, this, position)
                }
                exploreItemComment.setOnClickListener {
                    onItemClick?.invoke(it,this,position)
                }
                exploreItemFavorite.setOnClickListener {
                    onItemClick?.invoke(it,this,position)
                }
                exploreItemLike.setOnClickListener {
                    onItemClick?.invoke(it,this,position)
                }
                if (isDelete) {
                    isVisible = false
                }
            }
        }
    }

    fun getExploreItem(position: Int) = getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(R.layout.item_user_share_content, parent)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        ExploreFavoriteImpl.View,
        ExploreLikeImpl.View{
        constructor(resId: Int, parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(resId, parent, false))

        override fun getViewContext(): Context {
            return itemView.context
        }

        override fun onFavoriteSuccess() {

        }

        override fun onLikeSuccess() {

        }

        override fun onUnFavoriteSuccess() {

        }

        override fun onUnlikeSuccess() {

        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<ExploreItem>() {
            override fun areContentsTheSame(oldItem: ExploreItem, newItem: ExploreItem): Boolean {
                return false
            }

            override fun areItemsTheSame(oldItem: ExploreItem, newItem: ExploreItem): Boolean {
                return false
            }
        }
    }

    class ExploreItemImageAdapter(private val onItemImageOpenTransition: ((view: View, item: String, intent: Intent) -> Unit)? = null, private val list: List<DynamicPicture>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        private val urlList: Array<String> = Array(list.size) {
//            list[it].dyPictureTwoLink
//        }
init {
    Log.e("adapteImage",list.toString())
}
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.run {
                list[position].apply {
                    Glide.with(context)
                            .load(dyPictureTwoLink)
                            .centerCrop()
                            .into(itemSelectedImage)
                    cancelImage.isVisible = false
                    setOnClickListener {
                        val intent = Intent(context, ImageViewActivity::class.java)
                        intent.putExtra("list", Array(list.size) {
                            list[it].dyPictureTwoLink
                        })
                        intent.putExtra("position", position)
                        onItemImageOpenTransition?.invoke(itemSelectedImage, dyPictureTwoLink, intent)
                    }
                    itemSelectedImage.transitionName = dyPictureTwoLink
                }
                updatePadding(right = 0)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_select, parent, false)) {}
        }
    }
}