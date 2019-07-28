package com.bigcreate.zyfw.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.dialog
import com.bigcreate.library.setIconTint
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.startInstallPermissionSettingActivity
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.app.UpdateImpl
import com.bigcreate.zyfw.service.DownloadService
import kotlinx.android.synthetic.main.activity_update_manager.*
import java.io.File

class UpdateManagerActivity : AppCompatActivity(), UpdateImpl.View {
    var path = ""
    private val updateImpl = UpdateImpl(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_manager)
        window.translucentSystemUI(true)
        updateImpl.doRequest(packageName)
        setSupportActionBar(toolbarUpdate)
        toolbarUpdate.setNavigationOnClickListener {
            finish()
        }
        toolbarUpdate.inflateMenu(R.menu.toolbar_app_manager)
        toolbarUpdate.setOnMenuItemClickListener {
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
        textChangelogUpdate.text = "网络简介失败"
    }

    override fun onRequesting() {
        textChangelogUpdate.text = "正在检查更新"
    }

    override fun onRequestFinished() {

    }

    override fun onUpdateCheckFailed(response: RestResult<UpdateInfo>) {
        textChangelogUpdate.text = "检查更新失败"
    }

    override fun onUpdateCheckSuccess(updateInfo: UpdateInfo) {
        path = updateInfo.path
        textChangelogUpdate.text = getString(R.string.textChangelogVar, updateInfo.versionName, updateInfo.changelog)
        buttonDownloadUpdate.setOnClickListener {
            //            startActivity(Intent(Intent.ACTION_VIEW, updateInfo.path.toUri()))
            if (packageManager.canRequestPackageInstalls())
                Intent(this, DownloadService::class.java).apply {
                    File(externalCacheDir?.absoluteFile!!, "update").apply {
                        if (!exists())
                            mkdir()
                    }
                    putExtra("downloadType", "update")
                    putExtra("savePath", externalCacheDir?.absolutePath + "/update/appUpdate.apk")
                    putExtra("downloadUrl", path)
                    startService(this)
                } else {
                dialog("请求安装权限",
                        "Android O以上版本需要额外的权限才能安装，请授予权限",
                        "跳转到权限授予界面",
                        DialogInterface.OnClickListener { _, _ ->
                            startInstallPermissionSettingActivity()
                        })
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        updateImpl.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.INSTALL_PERMISSION && resultCode == Activity.RESULT_OK)
            Intent(this, DownloadService::class.java).apply {
                File(externalCacheDir?.absoluteFile, "update").apply {
                    if (!exists())
                        mkdir()
                }
                putExtra("downloadType", "update")
                putExtra("savePath", externalCacheDir!!.absolutePath + "/update/appUpdate.apk")
                putExtra("downloadUrl", path)
                startService(this)
            }
    }
}
