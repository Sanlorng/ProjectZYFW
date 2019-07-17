package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.fromJson
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.ExploreListAdapter
import com.bigcreate.zyfw.models.ExploreItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_explore_details.*
import kotlinx.android.synthetic.main.item_user_share_content.*
import kotlinx.android.synthetic.main.item_user_share_content.view.*

class ExploreDetailsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_explore_details)
		window.translucentSystemUI(true)
		setSupportActionBar(toolbarExploreDetails)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		toolbarExploreDetails.setNavigationOnClickListener {
			finishAfterTransition()
		}
		val data = intent.getStringExtra("shareItem")?:""
		val item = data.fromJson<ExploreItem>()
		item.apply {
			exploreItemUserNick.text = userInfoByPart.userNick
			exploreItemContent.text = dyContent
			exploreItemCreateTime.text = dyReleaseTime.split(" ").first()
			Glide.with(this@ExploreDetailsActivity)
					.load(userInfoByPart.userHeadPictureLink)
					.circleCrop()
					.into(exploreItemUserAvatar)
			if (listImageExploreItem.adapter == null) {
				listImageExploreItem.layoutManager = GridLayoutManager(this@ExploreDetailsActivity,when {
					dynamicPicture.size %5 == 0 -> 5
					dynamicPicture.size %4 == 0 -> 4
					else -> 3
				})
				listImageExploreItem.adapter = ExploreListAdapter.ExploreItemImageAdapter({ view, item, intent ->

				},dynamicPicture)
			}
		}

	}
}
