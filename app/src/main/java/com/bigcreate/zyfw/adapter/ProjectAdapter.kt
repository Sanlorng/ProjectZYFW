package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.SearchModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project_search.view.*

class ProjectAdapter(val list: ArrayList<SearchModel>,private val listener:(SearchModel.() -> Unit)? = null):RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {
        list[position].apply {
            holder.itemView.run {
                topicProjectSearchItem.text = projectTopic
                addressProjectSearchItem.text = context.getString(R.string.secondContentSearchVar,
                        projectPrincipalName, projectAddress, "")
                contentProjectSearchItem.text = projectContent
                peopleProjectSearchItem.text = context.getString(R.string.needPeoleNumVar, projectPeopleNumbers) + "·" + projectIssueTime
                projectPictureLinkTwo.apply {
                    if (this != null && size > 0)
                        Glide.with(context)
                                .load(get(0))
                                .centerInside()
                                .into(imageProjectSearchItem)
                    else
                        imageProjectSearchItem.isVisible = false
                }
                setOnClickListener {
                    listener?.invoke(this@apply)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)
}