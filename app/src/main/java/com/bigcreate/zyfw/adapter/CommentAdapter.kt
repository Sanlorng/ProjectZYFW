package com.bigcreate.zyfw.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.models.Comment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter : PagedListAdapter<Comment, CommentAdapter.ViewHolder>(diff) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.run {
            Log.e("item", comment)
            holder.itemView.findViewById<TextView>(R.id.textNickCommentItem).text = userNick
            holder.itemView.findViewById<TextView>(R.id.textTimeCommentItem).text = commentTime
            holder.itemView.findViewById<TextView>(R.id.textContentCommentItem).text = comment
            Glide.with(holder.itemView.context)
                    .load(headPictureLink)
                    .circleCrop()
                    .into(holder.itemView.avatarComment)
            holder.itemView.setOnClickListener {
                it.context.startActivity<MyDetailsActivity> {
                    putExtra("userId",userId)
                }
            }
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<Comment>() {
            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.comment == newItem.comment
            }

            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.userNick == newItem.userNick && oldItem.commentTime == newItem.commentTime
            }
        }
    }
}