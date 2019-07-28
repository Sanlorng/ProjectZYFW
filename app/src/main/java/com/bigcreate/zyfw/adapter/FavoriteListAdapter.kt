package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.Project
import kotlinx.android.synthetic.main.item_project_search.view.*

class FavoriteListAdapter : PagedListAdapter<Project, FavoriteListAdapter.ViewHolder>(diff) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.itemView.run {
                layoutProjectSearchItem.setOnClickListener {

                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_project_search, parent, false))
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<Project>() {
            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.projectContent == newItem.projectContent
            }

            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.projectId == newItem.projectId
            }
        }
    }
}