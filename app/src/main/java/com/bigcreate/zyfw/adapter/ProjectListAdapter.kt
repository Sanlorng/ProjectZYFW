package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.SearchModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project_search.view.*

class ProjectListAdapter(private val listener: ((Int, SearchModel) -> Unit)? = null) : PagedListAdapter<SearchModel, ProjectListAdapter.ViewHolder>(diff) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.itemView.run {
                topicProjectSearchItem.text = projectTopic
                addressProjectSearchItem.text = context.getString(R.string.secondContentSearchVar,
                        projectPrincipalName, projectAddress, projectIssueTime)
                contentProjectSearchItem.text = projectContent
                numbersProjectSearchItem.text = context.getString(R.string.numberProjectSearchVar, projectPeopleNumbers)
                projectPictureLinkTwo.apply {
                    if (size > 0)
                        Glide.with(context)
                                .load(get(0))
                                .centerInside()
                                .into(imageProjectSearchItem)
                    else
                        imageProjectSearchItem.isVisible = false
                }
                setOnClickListener {
                    listener?.invoke(position, this@apply)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_search, parent, false)
        return ViewHolder(view)
    }


    companion object {
        private val diff = object : DiffUtil.ItemCallback<SearchModel>() {
            override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem.projectContent == newItem.projectContent
            }

            override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem.projectId == newItem.projectId
            }
        }
    }
}