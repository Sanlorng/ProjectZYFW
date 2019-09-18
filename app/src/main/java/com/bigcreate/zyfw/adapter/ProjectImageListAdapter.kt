package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project_image.view.*
import kotlinx.android.synthetic.main.item_project_videos.view.*

class ProjectImageListAdapter(val images:List<String>): RecyclerView.Adapter<ProjectImageListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            images[position].apply {
                Glide.with(context)
                        .load(this)
                        .into(itemProjectImages)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_project_image,parent,false))
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}