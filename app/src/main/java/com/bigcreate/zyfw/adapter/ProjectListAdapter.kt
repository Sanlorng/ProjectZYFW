package com.bigcreate.zyfw.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.base.SearchModel

class ProjectListAdapter(val listProject: List<SearchModel>) : RecyclerView.Adapter<ProjectListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

    override fun getItemCount(): Int {
        return listProject.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listProject[position]
        holder.itemView.run {
            findViewById<TextView>(R.id.title_project_item).text = item.projectTopic
            findViewById<TextView>(R.id.address_project_item).text = item.projectAddress
            findViewById<TextView>(R.id.number_project_item).text = item.projectPeopleNumbers
            if (position == 0){
                val mLayoutParams = layoutParams as RecyclerView.LayoutParams
                mLayoutParams.topMargin = 20
            }
            setOnClickListener {
                val intent = Intent(context,ProjectDetailsActivity::class.java)
                intent.putExtra("projectId",item.projectId)
                intent.putExtra("projectTopic",item.projectTopic)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item,parent,false)
        return ViewHolder(view)
    }
}