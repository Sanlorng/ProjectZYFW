package com.bigcreate.zyfw.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.viewmodel.MediaViewModel
import kotlinx.android.synthetic.main.item_folder_picker.view.*

class MediaFolderAdapter(private val context: Context,private val list: SparseArray<MediaViewModel.MediaList>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: View.inflate(context, R.layout.item_folder_picker,null)
        view.textFolderMedia.text = list.valueAt(position).bucketName
        return view
    }

    override fun getCount(): Int {
        return list.size()
    }

    override fun getItem(position: Int): Any {
        return list.valueAt(position)
    }

    override fun getItemId(position: Int): Long {
        return list.keyAt(position).toLong()
    }

    class ViewHolder(val view: View) {

        init {
            view.tag = this
        }
    }
}