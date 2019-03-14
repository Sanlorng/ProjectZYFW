package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.models.MessageHeader

/**
 * Create by Sanlorng on 2018/4/16
 */
class MessageListAdapter(private val messageMap: HashMap<String, MessageHeader>) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_header, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            it.context.startActivity(ChatActivity::class.java)
        }
    }

    override fun getItemCount(): Int {
        return messageMap.size
    }
}