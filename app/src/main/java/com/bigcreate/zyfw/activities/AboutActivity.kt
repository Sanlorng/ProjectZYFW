package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        window.translucentSystemUI(true)
        setSupportActionBar(toolbarAbout)
        toolbarAbout.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textUpdateHistory.setOnClickListener {
            startActivity(AppUpdateHistoryActivity::class.java)
        }
    }
}
