package com.bigcreate.zyfw.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R

class MyDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)
        window.transucentSystemUI(true)
    }
}
