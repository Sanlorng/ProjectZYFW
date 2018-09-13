package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.R

class ProjectListAdapter(val listProject: List<Project>) : RecyclerView.Adapter<ProjectListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

    }

    override fun getItemCount(): Int {
        return listProject.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.title_project_item).text = listProject[position].project_topic
        holder.itemView.findViewById<TextView>(R.id.address_project_item).text = listProject[position].project_address
        holder.itemView.findViewById<TextView>(R.id.number_project_item).text = listProject[position].project_people_numbers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item,parent,false)
        return ViewHolder(view)
    }
}