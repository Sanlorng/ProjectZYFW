package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.UpdateProjectRequest
import com.bigcreate.zyfw.mvp.app.LocationContract
import com.bigcreate.zyfw.mvp.app.LocationImpl
import com.bigcreate.zyfw.mvp.project.CreateImpl
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import kotlinx.android.synthetic.main.activity_release_project.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class ReleaseProjectActivity : AuthLoginActivity(), CreateImpl.View {
    //    val photoResult = 3
//    val VIDEORESULT = 2
//    val imageType = "image/*"
//    val VIDEO_UNSPECIFIED = "video/*"
//    var isGetting = false
//    var selectType = 0
    var tencentLocation: TencentLocation? = null
    //    var imageString = ""
//    var videoString = ""
    private var editMode = false
    var projectId = -1
    var file: File? = null
    private val createImpl = CreateImpl(this)
    private val locationImpl = LocationImpl(object : LocationContract.View {
        override fun getViewContext(): Context {
            return this@ReleaseProjectActivity
        }

        override fun onLocationPermissionDenied() {
            toast("未授予定位权限")
        }

        override fun onLocationRequestFailed() {
            chip_location.text = "定位失败"
        }

        override fun onLocationRequestSuccess(location: TencentLocation) {
            location.apply {
                val text = "$city·$district$town$village$street$streetNo".replace("Unknown", "")
                if (text.length > 12)
                    chip_location.text = text.subSequence(0, 12)
                else
                    chip_location.text = text
                tencentLocation = this
            }
        }

        override fun onRequesting() {
            chip_location.text = "正在请求位置"
        }

        override fun onRequestFinished() {

        }
    }).apply { alwaysCall = true }
    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_project)
        setSupportActionBar(toolbar_release_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_release_project.setNavigationOnClickListener {
            dialog("提示", "你确认退出项目编辑页面吗，这将丢失你已写好的内容",
                    "确认", DialogInterface.OnClickListener { _, _ -> finish() },
                    "取消", DialogInterface.OnClickListener { _, _ -> })
        }
        editMode = intent.getBooleanExtra("editMode", false)
        if (editMode) {
            supportActionBar?.title = "编辑项目信息"
            projectId = intent.getIntExtra("projectId", -1)
            textProjectTypeRelease.isVisible = false
            try {
                intent.getStringExtra("projectInfo").fromJson<Project>().apply {
                    edit_topic.append(projectTopic)
                    edit_content.append(projectContent)
                    edit_contact_phone.append(projectPrincipalPhone)
                    edit_contact.append(projectPrincipalName)
                    edit_people.append(projectPeopleNumbers)
                    this@ReleaseProjectActivity.projectId = projectId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Attributes.userInfo?.apply {
                edit_contact_phone.append(userPhone)
                edit_contact.append(userNick)
            }
            val string = resources.getStringArray(R.array.project_type_id)
            for (i in 0 until string.size) {
                chipGroupProjectType.addView(
                        Chip(this).apply {
                            text = string[i]
                            id = i + 1
                            isCheckable = true
                            isCheckedIconVisible = false
                        }
                )
            }
        }
        dialog = AlertDialog.Builder(this@ReleaseProjectActivity)
                .setView(R.layout.process_wait)
                .setCancelable(false)
                .create()
        locationImpl.start()
        chip_location.isChecked = false
        chip_location.isCheckable = false
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            if (editMode) findItem(R.id.releaseCreateProject).isVisible = false
            else findItem(R.id.releaseEditProjectDone).isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_release_project, menu)
        menu?.findItem(R.id.releaseEditProjectDone)?.apply {
            icon = getDrawable(R.drawable.ic_done_black_24dp)!!.apply {
                DrawableCompat.setTint(this, getColor(R.color.colorAccent))
            }
        }
        menu?.findItem(R.id.releaseCreateProject)?.setIconTint(getColor(R.color.colorAccent))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.run {
            when (itemId) {
                R.id.releaseCreateProject -> {
                    if (checkInfoEmpty()) {
                        showProgress(true)
                        createImpl.doCreateProject(CreateProjectRequest(
                                projectTopic = edit_topic.text.toString(),
                                projectContent = edit_content.text.toString(),
                                latitude = tencentLocation!!.latitude,
                                longitude = tencentLocation!!.longitude,
                                projectPrincipalPhone = edit_contact_phone.text.toString(),
                                projectPrincipalName = edit_contact.text.toString(),
                                projectAddress = chip_location.text.toString(),
                                projectPeopleNumbers = edit_people.text.toString(),
                                projectRegion = tencentLocation!!.city,
                                projectTypeId = chipGroupProjectType.checkedChipId,
                                token = Attributes.loginUserInfo!!.token,
                                username = Attributes.loginUserInfo!!.username
                        ))
                    }
                }
                R.id.releaseEditProjectDone -> {
                    if (checkInfoEmpty()) {
                        showProgress(true)
                        createImpl.doUpdateProject(UpdateProjectRequest(
                                projectTopic = edit_topic.text.toString(),
                                projectContent = edit_content.text.toString(),
                                projectPrincipalPhone = edit_contact_phone.text.toString(),
                                projectPrincipalName = edit_contact.text.toString(),
                                projectAddress = chip_location.text.toString(),
                                projectRegion = tencentLocation!!.city,
                                projectPeopleNumbers = edit_people.text.toString(),
                                token = Attributes.loginUserInfo!!.token,
                                username = Attributes.loginUserInfo!!.username,
                                projectId = projectId.toString()
                        ))
                    }
                }
                else -> {
                }
            }
        }
        return true
    }

    private fun checkInfoEmpty(): Boolean {
        when {
            edit_topic.text.toString().isEmpty() -> {
                toast("未填写标题")
            }
            edit_content.text.toString().isEmpty() -> {
                toast("未填写内容")
            }
            edit_contact.text.toString().isEmpty() -> {
                toast("未填写联系人")
            }
            edit_contact_phone.text.toString().isEmpty() -> {
                toast("未填写手机号")
            }
            edit_people.text.toString().isEmpty() -> {
                toast("未填写人数")
            }
            chipGroupProjectType.checkedChipId == -1 && editMode.not() -> {
                toast("您未选择分类")
            }
            tencentLocation == null -> {
                toast("定位失败，无法发布")
            }

            else -> return true
        }
        return false
    }

    override fun onBackPressed() {
        dialog("提示", "你确认退出项目编辑页面吗，这将丢失你已写好的内容",
                "确认", DialogInterface.OnClickListener { _, _ -> finish() },
                "取消", DialogInterface.OnClickListener { _, _ -> })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.run {
            data.data?.run {
                Log.d("url", this.path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateProjectFailed(jsonObject: JsonObject) {
        showProgress(false)
    }

    override fun onCreateProjectSuccess(jsonObject: JsonObject) {
        showProgress(false)
        jsonObject.get("data").asJsonObject.apply {
            GlobalScope.launch {
                launch(Dispatchers.Main) {
                    startActivity(Intent(this@ReleaseProjectActivity, ProjectDetailsActivity::class.java).apply {
                        putExtra("projectId", get("projectId").asInt)
                        putExtra("projectTopic", edit_topic.text.toString())
                    })
                }
                launch(Dispatchers.Main) {
                    finish()
                }
            }

        }
    }

    override fun onUpdateProjectFailed(jsonObject: JsonObject) {
        showProgress(false)
        finish()
    }

    override fun onUpdateProjectSuccess(jsonObject: JsonObject) {
        showProgress(false)
        setResult(ResultCode.OK)
        GlobalScope.launch(Dispatchers.Main) {
            finish()
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onNetworkFailed() {
        showProgress(false)
        toast("网络连接失败")
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onRequestFinished() {

    }

    private fun showProgress(progressing: Boolean) {
        if (progressing)
            dialog.show()
        else
            dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.cancel()
        createImpl.detachView()
        locationImpl.detachView()
    }

    override fun onResume() {
        window.transucentSystemUI(true)
        super.onResume()
    }
}

