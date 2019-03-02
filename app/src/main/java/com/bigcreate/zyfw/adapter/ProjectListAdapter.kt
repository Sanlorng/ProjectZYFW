package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.SearchModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.project_item.view.*
import java.text.SimpleDateFormat

class ProjectListAdapter(val listProject: ArrayList<SearchModel>) : RecyclerView.Adapter<ProjectListAdapter.ViewHolder>() {
    var mListener: ProjectItemClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return listProject.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listProject[position].apply {
            holder.itemView.run {
                title_project_item.text = projectTopic
                address_project_item.text =
                        "$projectPrincipalName·$projectAddress / $projectIssueTime"
                content_project_item.text = projectContent
                number_project_item.text = "$projectPeopleNumbers 人"
                projectPictureLinkTwo.apply {
                    if (size >0)
                        Glide.with(context)
                                .load(get(0))
                                .centerInside()
                                .into(image_project_item)
                    else
                        image_project_item.isVisible = false
                }
                        if (position == 0) {
                            val mLayoutParams = layoutParams as RecyclerView.LayoutParams
                            mLayoutParams.topMargin = 20
                        }
                setOnClickListener {
                    mListener?.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ViewHolder(view)
    }

    interface ProjectItemClickListener {
        fun onItemClick(position: Int)
    }
}