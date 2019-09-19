package com.bigcreate.zyfw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ChatActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.callback.enqueue
import com.bigcreate.zyfw.models.ChatUser
import com.bigcreate.zyfw.models.UserInfoByPart
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user_avatar.view.*

class AvatarListAdapter(val avatarList:ArrayList<UserInfoByPart>,private val listener:(UserInfoByPart.() -> Unit)? = null):RecyclerView.Adapter<AvatarListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            avatarList[position].apply {
                setOnClickListener {
                    listener?.invoke(this)
                }
                if (Attributes.userTemp.containsKey(userId).not()) {
                    RemoteService.getHeadLinkAndNick(userId).enqueue {
                        response {
                            val info = body()
                            if (info != null) {
                                Attributes.userTemp[userId] = info
                                if (info.userHeadPictureLink.isNullOrEmpty().not()) {
                                    Glide.with(context)
                                            .load(info.userHeadPictureLink)
                                            .circleCrop()
                                            .into(avatar)
                                }

                                if (info.userNick.isNullOrEmpty().not()) {
                                    avatar_nick.text = info.userNick
                                }

                            }
                        }
                    }
                }else {
                    val info = Attributes.userTemp[userId]
                    if (info.userHeadPictureLink.isNullOrEmpty().not()) {
                        Glide.with(context)
                                .load(info.userHeadPictureLink)
                                .circleCrop()
                                .into(avatar)
                    }

                    if (info.userNick.isNullOrEmpty().not()) {
                        avatar_nick.text = info.userNick
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_avatar,parent,false))
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}