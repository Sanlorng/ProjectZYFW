package com.bigcreate.zyfw.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AuthLoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar_settings)
        toolbar_settings.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}
