package com.bigcreate.zyfw.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.models.ExploreCommentItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_comment.view.*

class ExploreDetailsCommentListAdapter() : PagedListAdapter<ExploreCommentItem,RecyclerView.ViewHolder>(diff) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("onBindViewHolder","")
        getItem(position)?.run {
            holder.itemView.apply {
                Log.e("item",dyCommentContent)
                textNickCommentItem.text = dyCommentUserNick
                textTimeCommentItem.text = dyCommentTime
                textContentCommentItem.text = dyCommentContent
                Glide.with(context)
                        .load(headPictureLink)
                        .circleCrop()
                        .into(avatarComment)
                avatarComment.setOnClickListener {
                    context.startActivity<MyDetailsActivity> {
                        putExtra("userId",dyCommentUserId)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
        ) {}
    }
    companion object {
        private val diff = object : DiffUtil.ItemCallback<ExploreCommentItem>() {
            override fun areItemsTheSame(oldItem: ExploreCommentItem, newItem: ExploreCommentItem): Boolean {
                return oldItem.dyCommentId == newItem.dyCommentId
            }

            override fun areContentsTheSame(oldItem: ExploreCommentItem, newItem: ExploreCommentItem): Boolean {
                return oldItem.dyCommentContent == newItem.dyCommentContent
            }
        }
    }
}