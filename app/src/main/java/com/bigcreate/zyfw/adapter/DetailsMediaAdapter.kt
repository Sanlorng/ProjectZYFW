package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.Project
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project_details_header.view.*
import kotlinx.android.synthetic.main.item_project_details_image.view.*
import kotlinx.android.synthetic.main.item_project_details_video.view.*

class DetailsMediaAdapter(private val listMedia: ArrayList<Model>) : RecyclerView.Adapter<DetailsMediaAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return listMedia.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listMedia[position].run {
            holder.itemView.apply {
                when (this@run) {
                    is Image -> {
                        Glide.with(context)
                                .load(path)
                                .into(imageProjectDetailsItem)
                    }

                    is Video -> {
                        videoProjectMedia.setVideoURI(path.toUri())
                        videoProjectMedia.setMediaController(MediaController(context))
                    }

                    is Header -> project.run {
                        context.apply {
                            locationDetails.text = getString(R.string.localeProjectVar, projectAddress)
                            typeDetails.text = getString(R.string.typeProjectVar, resources.getStringArray(R.array.project_type_id)[projectTypeId - 1])
                            textNameDetails.text = getString(R.string.contactNameVar, projectPrincipalName)
                            textDescriptionDetails.text = projectContent
                            textNumberDetails.text = getString(R.string.needPeoleNumVar, projectPeopleNumbers)
                            textPhoneDetails.text = projectPrincipalPhone
                            textTimeDetails.text = projectIssueTime
                            testShowInfo.text = projectTopic
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            MediaType.IMAGE -> R.layout.item_project_details_image
            MediaType.VIDEO -> R.layout.item_project_details_video
            else -> R.layout.item_project_details_header
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return when (listMedia[position]) {
            is Header -> MediaType.HEADER
            is Image -> MediaType.IMAGE
            is Video -> MediaType.VIDEO
            else -> MediaType.UNKNOWN
        }
    }

    abstract class Model(val path: String)
    class Image(path: String) : Model(path)
    class Video(path: String) : Model(path)
    class Header(path: String) : Model(path) {
        lateinit var project: Project
    }

    object MediaType {
        const val HEADER = 0
        const val IMAGE = 1
        const val VIDEO = 2
        const val UNKNOWN = 3
    }
}