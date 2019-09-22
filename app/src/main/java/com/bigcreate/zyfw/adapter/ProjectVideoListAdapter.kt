package com.bigcreate.zyfw.adapter

import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.view.ProportionView
import com.bumptech.glide.Glide
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import kotlinx.android.synthetic.main.item_project_videos.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectVideoListAdapter(val videos:List<String>):RecyclerView.Adapter<ProjectVideoListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            itemProjectVideoPreview.setUp(videos[position],JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,"宣传视频")
//            GlobalScope.launch(Dispatchers.Default) {
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(videos[position],HashMap<String,String>())
//                val bitmap = retriever.frameAtTime
//                retriever.release()
//                if (bitmap != null) {
//                    launch(Dispatchers.Main) {
//                        layoutItemProjectVideos?.widthWeight = bitmap.width
//                        layoutItemProjectVideos?.heightWeight = bitmap.height
//                        itemProjectVideoPreview?.setVideoPath(videos[position])
//                    }
//                    Log.e("videoWidth",bitmap.width.toString())
//                    Log.e("videoHeight",bitmap.height.toString())
//                }
//            }

//            Glide.with(context)
//                    .load(videos[position])
//                    .into(itemProjectVideoPreview)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        JCVideoPlayer.releaseAllVideos()
    }
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_project_videos,parent,false))
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}