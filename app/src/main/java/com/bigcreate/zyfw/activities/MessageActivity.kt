package com.bigcreate.zyfw.activities

import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AuthLoginActivity() {

    override fun setContentView() {
        setContentView(R.layout.activity_message)
    }

    override fun afterCheckLoginSuccess() {
        setSupportActionBar(toolbarMessage)
        toolbarMessage.setNavigationOnClickListener {
            finish()
        }
        window.translucentSystemUI(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "消息"
    }
}
