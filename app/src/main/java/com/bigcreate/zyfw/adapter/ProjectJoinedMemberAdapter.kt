package com.bigcreate.zyfw.adapter

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.models.JoinedMember
import com.bigcreate.zyfw.models.UserInfoByPart
import com.bigcreate.zyfw.view.ListItemView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_list_item_view.view.*

class ProjectJoinedMemberAdapter(private val list: List<JoinedMember>):RecyclerView.Adapter<ProjectJoinedMemberAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as ListItemView).apply {
            list[position].run {
                getLogoView().isVisible = true
                Glide.with(context)
                        .load(userInfoByPart.userHeadPictureLink)
                        .circleCrop()
                        .into(logoView)
                setTitleText(userInfoByPart.userNick)
                setSubTitleText(joinedTime)
                setActionIcon(R.drawable.ic_outline_comment_24px)
                setActionText("聊天")
                setOnItemClick(View.OnClickListener {
                    context.startActivity<MyDetailsActivity> {
                        putExtra("userId",joinedUserId)
                    }
                })
                setOnActionClick(View.OnClickListener {
                    context.startActivity<ChatActivity> {
                        putExtra("chatId",joinedUserId)
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemView(parent.context).apply {
            setCardBackgroundColor(TypedValue().run {
                context.theme.resolveAttribute(R.attr.colorSurface,this,true)
                data
            })
            elevation = 0f
            radius = 0f
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        })
    }
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)
}