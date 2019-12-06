package com.bigcreate.zyfw.adapter

import android.net.Uri
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.core.util.getOrDefault
import androidx.core.util.remove
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_item_media_picker.view.*

class PickerAdapter(private val list: List<String>,private val listener:(PickerAdapter.(uri:String,view: View, position: Int,isChecked:Boolean) -> Unit)? = null):RecyclerView.Adapter<PickerAdapter.ViewHolder>() {
    val listChecked = SparseBooleanArray()
    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            list[position].run {
                Glide.with(context)
                        .load(this)
                        .centerCrop()
                        .into(imagePreviewPicker)
                choiceMedia.isChecked = listChecked.getOrDefault(position,false)
                imagePreviewPicker.setOnClickListener {
                    choiceMedia.isChecked = choiceMedia.isChecked.not()
                }
                choiceMedia.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked.not()) {
                        if (listChecked.containsKey(position)) {
                            listChecked.delete(position)
                        }
                    }else {
                        listChecked[position] = isChecked
                    }
                    listener?.invoke(this@PickerAdapter,this@run,this@apply,position,isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_media_picker,parent,false))
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}