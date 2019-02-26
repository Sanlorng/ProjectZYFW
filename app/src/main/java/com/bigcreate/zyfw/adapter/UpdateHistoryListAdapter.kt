package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.UpdateInfo
import kotlinx.android.synthetic.main.version_item.view.*

class UpdateHistoryListAdapter(val list: List<UpdateInfo>) : RecyclerView.Adapter<UpdateHistoryListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].apply {
            holder.itemView.run {
                versionName.text = this@apply.versionName
                versionLog.text = this@apply.changelog
                versionLayout.setOnClickListener {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.version_item,parent,false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}