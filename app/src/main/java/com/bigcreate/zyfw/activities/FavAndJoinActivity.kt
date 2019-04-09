package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.FragmentAdapter
import com.bigcreate.zyfw.fragments.MyFavoriteFragment
import com.bigcreate.zyfw.fragments.MyJoinFragment
import kotlinx.android.synthetic.main.activity_fav_and_join.*

class FavAndJoinActivity : AuthLoginActivity() {

    override fun afterCheckLoginSuccess() {
        val position = intent.getIntExtra("favOrJoin",0)
        viewpagerFavAndJoin.adapter = FragmentAdapter(supportFragmentManager, listOf(
                MyFavoriteFragment.newInstance("",""),MyJoinFragment.newInstance("","")
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
