package com.bigcreate.zyfw.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.models.MessageHeader
import kotlinx.android.synthetic.main.item_message_header.view.*

/**
 * Create by Sanlorng on 2018/4/16
 */
class MessageListAdapter(private val messageMap: ArrayList<MessageHeader>) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_header, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            messageMap[position].apply {
                hintMessageItem.text = message
                setOnClickListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("chatId", this.id)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return messageMap.size
    }
}