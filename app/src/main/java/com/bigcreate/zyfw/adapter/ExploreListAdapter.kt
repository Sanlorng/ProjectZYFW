package com.bigcreate.zyfw.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ImageViewActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.DynamicPicture
import com.bigcreate.zyfw.models.ExploreItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user_share_content.view.*
import kotlinx.android.synthetic.main.layout_item_select.view.*

class ExploreListAdapter(private val onItemClick: ((view: View, item: ExploreItem, position: Int) -> Unit)? = null,
                         private val onItemImageOpenTransition: ((view: View, item: String, intent: Intent) -> Unit)? = null) : PagedListAdapter<ExploreItem, ExploreListAdapter.ViewHolder>(diff) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            getItem(position)?.apply {
                exploreItemUserNick.text = userInfoByPart.userNick
                exploreItemContent.text = dyContent
                exploreItemCreateTime.text = dyReleaseTime.split(" ").first()
                Glide.with(context)
                        .load(userInfoByPart.userHeadPictureLink)
                        .circleCrop()
                        .into(exploreItemUserAvatar)
                if (listImageExploreItem.adapter == null) {
                    listImageExploreItem.layoutManager = GridLayoutManager(context, when {
                        dynamicPicture.size % 5 == 0 -> 5
                        dynamicPicture.size % 4 == 0 -> 4
                        else -> 3
                    })
                    listImageExploreItem.adapter = ExploreItemImageAdapter(onItemImageOpenTransition, dynamicPicture)
                }
                if (Attributes.userId == userInfoByPart.userId) {
                    exploreItemUserAction.isVisible = true
                    exploreItemUserAction.setOnClickListener {
                    }
                }

                setOnClickListener {
                    onItemClick?.invoke(this@run, this, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(R.layout.item_user_share_content, parent)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        constructor(resId: Int, parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(resId, parent, false))
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
        private val urlList: Array<String> = Array(list.size) {
            list[it].dyPictureTwoLink
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.run {
                urlList[position].apply {
                    Glide.with(context)
                            .load(this)
                            .centerCrop()
                            .into(itemSelectedImage)
                    cancelImage.isVisible = false
                    setOnClickListener {
                        val intent = Intent(context, ImageViewActivity::class.java)
                        intent.putExtra("list", urlList)
                        intent.putExtra("position", position)
                        itemSelectedImage.transitionName = this
                        onItemImageOpenTransition?.invoke(itemSelectedImage, this, intent)
                    }
                }
                updatePadding(right = 0)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_select, parent, false)) {}
        }
    }
}