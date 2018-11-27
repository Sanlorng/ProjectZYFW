package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.Comment

class CommentAdapter(val CommentList:List<Comment>):RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = CommentList[position]
        item.run {
            holder.itemView.findViewById<TextView>(R.id.textView_nick_comment).text = userNick
            holder.itemView.findViewById<TextView>(R.id.textView_time_comment).text = commentTime
            holder.itemView.findViewById<TextView>(R.id.textView_content_comment).text = comment
        }
    }

    override fun getItemCount(): Int {
        return CommentList.size
    }
}