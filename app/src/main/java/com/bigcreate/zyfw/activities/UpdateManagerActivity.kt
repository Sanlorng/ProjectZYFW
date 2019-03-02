package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bigcreate.library.setIconTint
import com.bigcreate.library.startActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.app.UpdateImpl
import kotlinx.android.synthetic.main.activity_update_manager.*

class UpdateManagerActivity : AppCompatActivity(), UpdateImpl.View {
    var path = ""
    private val updateImpl = UpdateImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_manager)
        window.transucentSystemUI(true)
        updateImpl.doRequest(packageName)
        setSupportActionBar(toolbarAppbarManager)
        toolbarAppbarManager.setNavigationOnClickListener {
            finish()
        }
        toolbarAppbarManager.inflateMenu(R.menu.toolbar_app_manager)
        toolbarAppbarManager.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.appUpdateHistory -> startActivity(AppUpdateHistoryActivity::class.java)
            }
            true
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateImpl.doRequest(packageName)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_app_manager, menu)
        menu?.apply {
            findItem(R.id.appUpdateHistory).setIconTint(getColor(R.color.colorAccent))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.appUpdateHistory -> startActivity(AppUpdateHistoryActivity::class.java)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onNetworkFailed() {
        textChangelog.text = "网络简介失败"
    }

    override fun onRequesting() {
        textChangelog.text = "正在检查更新"
    }

    override fun onRequestFinished() {

    }

    override fun onUpdateCheckFailed(response: RestResult<UpdateInfo>) {
        textChangelog.text = "检查更新失败"
    }

    override fun onUpdateCheckSuccess(updateInfo: UpdateInfo) {
        path = updateInfo.path
        textChangelog.text = "版本号：" + updateInfo.versionName + "\n更新日志：\n" + updateInfo.changelog
        materialButtonDownload.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, updateInfo.path.toUri()))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        updateImpl.detachView()
    }
}
