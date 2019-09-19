package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.ProvinceProject
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project_search.view.*

class ProvinceProjectAdapter(private val onClick:(ProvinceProject.() -> Unit)? = null):PagedListAdapter<ProvinceProject,ProvinceProjectAdapter.ViewHolder>(diff) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.itemView.run {
                val addressText = "$contacts·$projectPlace"
                val peopleText = "$serviceType·$releaseDate"
                topicProjectSearchItem.text = projectTitle
                contentProjectSearchItem.text = serviceTime
                addressProjectSearchItem.text = addressText
                peopleProjectSearchItem.text = peopleText
                Glide.with(context)
                        .load(imgSrc)
                        .into(imageProjectSearchItem)
                setOnClickListener {
                    onClick?.invoke(this@apply)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_project_search,parent,false))
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    companion object {
        private val diff = object : DiffUtil.ItemCallback<ProvinceProject>() {
            override fun areContentsTheSame(oldItem: ProvinceProject, newItem: ProvinceProject): Boolean {
                return oldItem.nearId == newItem.nearId
            }

            override fun areItemsTheSame(oldItem: ProvinceProject, newItem: ProvinceProject): Boolean {
                return oldItem.nearId == newItem.nearId
            }
        }
    }
}