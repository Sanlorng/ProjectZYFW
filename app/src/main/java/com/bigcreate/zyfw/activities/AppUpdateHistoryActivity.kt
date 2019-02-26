package com.bigcreate.zyfw.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.UpdateHistoryListAdapter
import com.bigcreate.zyfw.base.UpdateService
import kotlinx.android.synthetic.main.activity_app_update_history.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AppUpdateHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_update_history)
        setSupportActionBar(toolbarAppUpdateHistory)
        toolbarAppUpdateHistory.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        GlobalScope.launch {
            try {
                UpdateService.getAppUpdateHistory(packageName).execute().body()?.apply {
                    launch(Dispatchers.Main) {
                        listUpdateHistory.layoutManager = LinearLayoutManager(this@AppUpdateHistoryActivity)
                        listUpdateHistory.adapter = UpdateHistoryListAdapter(this@apply)
                    }
                }
            }catch (e: Exception) {

            }
        }
        window.transucentSystemUI(true)
    }
}
