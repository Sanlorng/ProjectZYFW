package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R

class MenuListAdapter(val list: List<MenuItem>, private val listener: (it: MenuListAdapter.MenuItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position].run {
            (holder.itemView as TextView).apply {
                this.text = context.getString(this@run.text)
                this.id = this@run.id
                this.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(drawable), null, null)
                setOnClickListener {
                    listener.invoke(this@run)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_account_menu, parent, false)) {}
    }

    data class MenuItem(val id: Int, val drawable: Int, val text: Int)
    interface MenuListItemClick
}

