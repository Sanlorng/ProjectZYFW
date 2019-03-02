package com.bigcreate.zyfw.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DetailsMediaAdapter(val listMedia:ArrayList<Model>): RecyclerView.Adapter<DetailsMediaAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return listMedia.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as ImageView).run {
            Glide.with(context)
                    .load(listMedia[position].path)
                    .into(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImageView(parent.context))
    }
    abstract class Model(val path: String)
    class Image(path: String):Model(path)
    class Video(path: String):Model(path)
}