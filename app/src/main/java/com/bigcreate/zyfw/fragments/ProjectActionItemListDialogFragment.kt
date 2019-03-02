package com.bigcreate.zyfw.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_projectactionitem_list_dialog.*
import kotlinx.android.synthetic.main.fragment_projectactionitem_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ProjectActionItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [ProjectActionItemListDialogFragment.Listener].
 */
class ProjectActionItemListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null
    private val actionList = ArrayList<ActionItem>()
    private var type: Int? = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projectactionitem_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        actionList.clear()
        when (arguments?.getInt(ARG_ITEM_COUNT)) {
            null -> {
            }
            0 -> actionList.apply {
                add(ActionItem(R.drawable.ic_video_call_black_24dp, "添加视频"))
                add(ActionItem(R.drawable.ic_add_a_photo_black_24dp, "添加图片"))
                add(ActionItem(R.drawable.ic_mode_edit_black_24dp, "编辑项目"))
                add(ActionItem(R.drawable.ic_delete_black_24dp, "删除项目"))
            }
            1 -> actionList.apply {
                if (arguments!!.getBoolean("isFavorite"))
                    add(ActionItem(R.drawable.ic_star_black_24dp, "收藏项目"))
                else
                    add(ActionItem(R.drawable.ic_star_black_24dp, "取消收藏"))
            }
        }
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = ProjectActionItemAdapter()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    interface Listener {
        fun onProjectActionItemClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_projectactionitem_list_dialog_item, parent, false)) {

        internal val text: MaterialButton = itemView.text

        init {
            text.setOnClickListener {
                mListener?.let {
                    it.onProjectActionItemClicked(actionList[adapterPosition].iconId)
                    dismiss()
                }
            }
        }
    }

    private inner class ProjectActionItemAdapter internal constructor() : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            actionList[position].run {
                holder.text.apply {
                    text = actionText
                    icon = context!!.getDrawable(iconId)
                    if (iconId == R.drawable.ic_delete_black_24dp) {
                        iconTint = ContextCompat.getColorStateList(context, R.color.colorAccent)
                        setTextColor(context.getColor(R.color.colorAccent))
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return actionList.size
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(type: Int): ProjectActionItemListDialogFragment =
                ProjectActionItemListDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, type)
                    }
                }

    }

    inner class ActionItem(@DrawableRes val iconId: Int, val actionText: String)
}
