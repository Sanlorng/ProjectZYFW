package com.bigcreate.zyfw.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AuthLoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbarMessage)
        toolbarMessage.setNavigationOnClickListener {
            finish()
        }
        window.transucentSystemUI(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "消息"
    }
}
