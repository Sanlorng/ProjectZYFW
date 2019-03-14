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
import kotlinx.android.synthetic.main.fragment_project_details_action.*
import kotlinx.android.synthetic.main.item_project_details_action.view.*

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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project_details_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        listActionDetails.menu.forEach {
//            it.apply {
//
//            }
//        }
//        listActionDetails.setCheckedItem(R.id.deleteProjectDetails)
        actionList.clear()
        when (arguments?.getInt(ARG_ITEM_COUNT)) {
            null -> {
            }
            0 -> actionList.apply {
                add(ActionItem(R.drawable.ic_outline_video_call_24px, "添加视频"))
                add(ActionItem(R.drawable.ic_outline_add_photo_alternate_24px, "添加图片"))
                add(ActionItem(R.drawable.ic_outline_edit_24px, "编辑项目"))
                add(ActionItem(R.drawable.ic_outline_delete_outline_24px, "删除项目"))
            }
        }
        listActionDetails.layoutManager = LinearLayoutManager(context)
        listActionDetails.adapter = ProjectActionItemAdapter()
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

    interface Listener {
        fun onProjectActionItemClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_project_details_action, parent, false)) {

        internal val text: MaterialButton = itemView.buttonActionDetails

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
                    if (iconId == R.drawable.ic_outline_delete_outline_24px) {
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

        fun newInstance(type: Int): ProjectActionItemListDialogFragment =
                ProjectActionItemListDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, type)
                    }
                }

    }

    inner class ActionItem(@DrawableRes val iconId: Int, val actionText: String)
}
