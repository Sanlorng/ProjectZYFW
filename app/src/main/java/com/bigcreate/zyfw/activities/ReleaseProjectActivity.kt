package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.amap.api.location.AMapLocation
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.UpdateProjectRequest
import com.bigcreate.zyfw.mvp.app.AMapLocationImpl
import com.bigcreate.zyfw.mvp.project.CreateImpl
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_release_project.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class ReleaseProjectActivity : AuthLoginActivity(), CreateImpl.View {

    private var editMode = false
    private var amapLocation: AMapLocation? = null
    private lateinit var amapLocationImpl: AMapLocationImpl
    var projectId = -1
    var file: File? = null
    private val createImpl = CreateImpl(this)

    private lateinit var dialog: AlertDialog
    override fun setContentView() {
        setContentView(R.layout.activity_release_project)
    }
    override fun afterCheckLoginSuccess() {
        setSupportActionBar(toolbarReleaseProject)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarReleaseProject.setNavigationOnClickListener {
            dialog("提示", "你确认退出项目编辑页面吗，这将丢失你已写好的内容",
                    "确认", DialogInterface.OnClickListener { _, _ -> finish() },
                    "取消", DialogInterface.OnClickListener { _, _ -> })
        }
        editMode = intent.getBooleanExtra("editMode", false)
        if (editMode) {
            supportActionBar?.title = "编辑项目信息"
            projectId = intent.getIntExtra("projectId", -1)
            textTypeRelease.isVisible = false
            try {
                intent.getStringExtra("projectInfo").fromJson<Project>().apply {
                    inputTopicRelease.append(projectTopic)
                    inputContentRelease.append(projectContent)
                    inputContactPhoneRelease.append(projectPrincipalPhone)
                    inputContactRelease.append(projectPrincipalName)
                    inputNumbersRelease.append(projectPeopleNumbers)
                    this@ReleaseProjectActivity.projectId = projectId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Attributes.userInfo?.apply {
                inputContactPhoneRelease.append(userPhone)
                inputContactRelease.append(userNick)
            }
            val string = resources.getStringArray(R.array.project_type_id)
            for (i in 0 until string.size) {
                chipGroupTypeRelease.addView(
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
                .setView(R.layout.layout_process_upload)
                .setCancelable(false)
                .create()
//        locationImpl.start()
        amapLocationImpl = AMapLocationImpl(object :AMapLocationImpl.View {
            override val onceLocation: Boolean
                get() = false
            override fun onRequestFailed(location: AMapLocation?) {

            }

            override fun onRequestSuccess(location: AMapLocation) {
                amapLocation = location.apply {
                    val text = "$city·$poiName".replace("Unknown", "")
                    if (text.length > 12)
                        chipLocationRelease.text = text.subSequence(0, 12)
                    else
                        chipLocationRelease.text = text
                }
            }

            override fun getViewContext(): Context {
                return this@ReleaseProjectActivity
            }
        })
        amapLocationImpl.startLocation()
        chipLocationRelease.isChecked = false
        chipLocationRelease.isCheckable = false
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
                                projectTopic = inputTopicRelease.text.toString(),
                                projectContent = inputContentRelease.text.toString(),
                                latitude = amapLocation!!.latitude,
                                longitude = amapLocation!!.longitude,
                                projectPrincipalPhone = inputContactPhoneRelease.text.toString(),
                                projectPrincipalName = inputContactRelease.text.toString(),
                                projectAddress = chipLocationRelease.text.toString(),
                                projectPeopleNumbers = inputNumbersRelease.text.toString(),
                                projectRegion = amapLocation!!.city,
                                projectTypeId = chipGroupTypeRelease.checkedChipId,
                                token = Attributes.token,
                                username = Attributes.username
                        ))
                    }
                }
                R.id.releaseEditProjectDone -> {
                    if (checkInfoEmpty()) {
                        showProgress(true)
                        createImpl.doUpdateProject(UpdateProjectRequest(
                                projectTopic = inputTopicRelease.text.toString(),
                                projectContent = inputContentRelease.text.toString(),
                                projectPrincipalPhone = inputContactPhoneRelease.text.toString(),
                                projectPrincipalName = inputContactRelease.text.toString(),
                                projectAddress = chipLocationRelease.text.toString(),
                                projectRegion = amapLocation!!.city,
                                projectPeopleNumbers = inputNumbersRelease.text.toString(),
                                token = Attributes.token,
                                username = Attributes.username,
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
            inputTopicRelease.text.toString().isEmpty() -> {
                toast("未填写标题")
            }
            inputContentRelease.text.toString().isEmpty() -> {
                toast("未填写内容")
            }
            inputContactRelease.text.toString().isEmpty() -> {
                toast("未填写联系人")
            }
            inputContactPhoneRelease.text.toString().isEmpty() -> {
                toast("未填写手机号")
            }
            inputNumbersRelease.text.toString().isEmpty() -> {
                toast("未填写人数")
            }
            chipGroupTypeRelease.checkedChipId == -1 && editMode.not() -> {
                toast("您未选择分类")
            }
            amapLocation == null -> {
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
                        putExtra("projectTopic", inputTopicRelease.text.toString())
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
        amapLocationImpl.detachView()
//        locationImpl.detachView()
    }

    override fun onResume() {
        window.translucentSystemUI(true)
        super.onResume()
    }
}

