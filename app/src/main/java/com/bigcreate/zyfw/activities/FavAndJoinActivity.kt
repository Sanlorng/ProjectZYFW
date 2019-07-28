package com.bigcreate.zyfw.activities

import android.os.Bundle
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.fragments.MyFavoriteFragment
import com.bigcreate.zyfw.fragments.MyJoinFragment
import com.bigcreate.zyfw.fragments.MyReleasedFragment
import kotlinx.android.synthetic.main.activity_fav_and_join.*

class FavAndJoinActivity : AuthLoginActivity() {

    override fun afterCheckLoginSuccess() {
        val position = intent.getIntExtra("favOrJoin", 0)
        viewpagerFavAndJoin.offscreenPageLimit = 3
        viewpagerFavAndJoin.adapter = FragmentAdapter(supportFragmentManager, listOf(
                MyReleasedFragment.newInstance("", "")
                , MyJoinFragment.newInstance("", ""), MyFavoriteFragment.newInstance("", "")
        ))
        tabLayoutFavAndJoin.setupWithViewPager(viewpagerFavAndJoin)
        viewpagerFavAndJoin.currentItem = position
    }

    override fun setContentView() {
        setContentView(R.layout.activity_fav_and_join)
        setSupportActionBar(toolbarFavAndJoin)
        toolbarFavAndJoin.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
