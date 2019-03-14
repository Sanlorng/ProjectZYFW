package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.Comment

class CommentAdapter(private val CommentList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = CommentList[position]
        item.run {
            holder.itemView.findViewById<TextView>(R.id.textNickCommentItem).text = userNick
            holder.itemView.findViewById<TextView>(R.id.textTimeCommentItem).text = commentTime
            holder.itemView.findViewById<TextView>(R.id.textContentCommentItem).text = comment
        }
    }

    override fun getItemCount(): Int {
        return CommentList.size
    }
}