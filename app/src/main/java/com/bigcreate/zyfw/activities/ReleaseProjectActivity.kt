package com.bigcreate.zyfw.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import kotlinx.android.synthetic.main.activity_release_project.*

class ReleaseProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_project)
        setSupportActionBar(toolbar_release_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_release_project.setNavigationOnClickListener {
            finish()
        }
        window.transucentSystemUI(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_release_project,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return true
    }
}

