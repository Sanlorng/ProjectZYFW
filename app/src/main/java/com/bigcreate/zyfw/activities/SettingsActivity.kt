package com.bigcreate.zyfw.activities

import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AuthLoginActivity() {

    override fun setContentView() {
        setContentView(R.layout.activity_settings)
    }
    override fun afterCheckLoginSuccess() {
        setSupportActionBar(toolbarSettings)
        toolbarSettings.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
