package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.UpdateInfo
import kotlinx.android.synthetic.main.item_version_history.view.*

class UpdateHistoryListAdapter(val list: List<UpdateInfo>) : RecyclerView.Adapter<UpdateHistoryListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].apply {
            holder.itemView.run {
                nameVersionHistoryItem.text = this@apply.versionName
                contentVersionHistoryItem.text = this@apply.changelog
                layoutVersionHistory.setOnClickListener {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_version_history, parent, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}