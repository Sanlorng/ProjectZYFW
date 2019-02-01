package com.bigcreate.zyfw.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.app.UpdateContract
import com.bigcreate.zyfw.mvp.app.UpdateImpl
import kotlinx.android.synthetic.main.activity_update_manager.*

class UpdateManagerActivity : AppCompatActivity(),UpdateContract.View {
    var path = ""
    val updateImpl = UpdateImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_manager)
        window.transucentSystemUI(true)
        updateImpl.doUpdateCheck(packageName)
        setSupportActionBar(toolbarAppbarManager)
        toolbarAppbarManager.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateImpl.doUpdateCheck(packageName)
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

    fun downloadUpdate(updateInfo: UpdateInfo){
        val downloadManager = getSystemService(DownloadManager::class.java)
//        downloadManager.enqueue(DownloadManager.Request(updateInfo.path.toUri()).apply {
//            setNotificationVisibility(DownloadManager.)
//        })

    }

    override fun onDestroy() {
        super.onDestroy()
        updateImpl.detachView()
    }
}
