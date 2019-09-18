package com.bigcreate.zyfw.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf

import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ProjectImageListAdapter
import com.bigcreate.zyfw.adapter.ProjectVideoListAdapter
import kotlinx.android.synthetic.main.fragment_project_image.*

/**
 * A simple [Fragment] subclass.
 */
class ProjectImageFragment : Fragment() {
    init {
        arguments = bundleOf("title" to "图片")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    fun refreshImages(images: List<String>) {
        listProjectImages.adapter = ProjectImageListAdapter(images)
    }
}
