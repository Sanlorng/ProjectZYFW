package com.bigcreate.zyfw.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ExploreDetailsPageAdapter(dynamicId: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
       return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(RecyclerView(parent.context)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position) {
            0 -> {

            }

            1 -> {

            }

            2 -> {

            }
        }
    }
}