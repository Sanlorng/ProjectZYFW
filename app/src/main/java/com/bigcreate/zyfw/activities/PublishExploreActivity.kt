package com.bigcreate.zyfw.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.setIconTint
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.SelectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.base.paddingStatusBar
import com.bigcreate.zyfw.models.ExploreEditRequest
import com.bigcreate.zyfw.models.PublishExploreRequest
import com.bigcreate.zyfw.mvp.explore.UpdateExploreImpl
import com.bigcreate.zyfw.mvp.user.PublishExploreImpl
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.BoxingMediaLoader
import com.bilibili.boxing.loader.IBoxingCallback
import com.bilibili.boxing.loader.IBoxingMediaLoader
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_publish_explore.*
import java.io.File

class PublishExploreActivity : AuthLoginActivity(), PublishExploreImpl.View,UpdateExploreImpl.View {

    private var action = SelectListAdapter.Action("")
    private val list = ArrayList<SelectListAdapter.Model>().apply {
        add(action)
    }
    private lateinit var dialog: AlertDialog
    lateinit var imageConfig: BoxingConfig
    lateinit var videoConfig: BoxingConfig
    private var editMode = false
    private var dynamicId = 0
    private var publishExploreImpl = PublishExploreImpl(this)
    private var editExploreImpl = UpdateExploreImpl(this)
    private val boxImpl = object : IBoxingMediaLoader {
        override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
            Glide.with(img)
                    .load(absPath)
                    .into(img)
        }

        override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
            Glide.with(img)
                    .load(absPath)
                    .into(img)
        }
    }

    override fun afterCheckLoginSuccess() {
        dialog = MaterialAlertDialogBuilder(this@PublishExploreActivity)
                .setView(R.layout.layout_process_upload)
                .setCancelable(false)
                .create()
        setSupportActionBar(toolbarExplorePublish)
        toolbarExplorePublish.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (editMode.not())"发表动态" else "编辑动态"
        }
        listExplorePublish.layoutManager = GridLayoutManager(this, 4)
        BoxingMediaLoader.getInstance().init(boxImpl)
        imageConfig = BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
        videoConfig = BoxingConfig(BoxingConfig.Mode.VIDEO)
        listExplorePublish.adapter = SelectListAdapter(list) {

            Boxing.of(imageConfig).withIntent(this@PublishExploreActivity, BoxingActivity::class.java)
                    .start(this@PublishExploreActivity, RequestCode.PUBLISH_EXPLORE)

        }
        toolbarExplorePublish.paddingStatusBar()
        toolbarExplorePublish.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        toolbarExplorePublish.layoutParams.height = toolbarExplorePublish.measuredHeight
        listExplorePublish.itemAnimator = DefaultItemAnimator()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_publish_explore)
        editMode = intent.getBooleanExtra("editType",false)
        dynamicId = intent.getIntExtra("dynamicId",0)
        if (editMode) {
            listExplorePublish.isVisible = false
            inputExplorePublish.text?.append(intent.getStringExtra("dynamicContent"))

        }
    }

    private fun showProgress(progressing: Boolean) {
        if (progressing)
            dialog.show()
        else
            dialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Boxing.getResult(data)?.apply {
            when {
                resultCode != Activity.RESULT_OK -> {
                }
                requestCode == RequestCode.PUBLISH_EXPLORE -> {
                    list.add(SelectListAdapter.Image(get(0).path))
                    list.remove(action)
                    list.add(action)
                    listExplorePublish.adapter?.notifyItemInserted(list.lastIndex - 1)
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun explorePublishFailed() {
        toast("发布失败")
    }

    override fun explorePublishSuccess() {
        setResult(ResultCode.OK)
        toast("发布成功")
        finish()
    }

    override fun onUpdateFailed(jsonObject: JsonObject) {
        toast("更新失败")
    }

    override fun onUpdateSuccess() {
        setResult(ResultCode.OK,Intent().apply {
            putExtra("position",intent.getIntExtra("position",0))
        })
        toast("更新成功")
        finish()
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onRequesting() {
        showProgress(true)
    }

    override fun onRequestFinished() {
        showProgress(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_release_project, menu)
        menu?.findItem(R.id.releaseEditProjectDone)?.isVisible = false
        menu?.findItem(R.id.releaseCreateProject)?.setIconTint(getColor(R.color.colorAccent))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        intent.getStringExtra("projectId").run {
            Attributes.loginUserInfo?.apply {
                when (item?.itemId) {
                    null -> {
                    }
                    R.id.releaseCreateProject -> {
                        if (editMode.not()) {
                            publishExploreImpl.doRequest(
                                    PublishExploreRequest(list.run {
                                        val listFile = ArrayList<File>()
                                        forEach {
                                            if (it !is SelectListAdapter.Action)
                                                listFile.add(File(it.path))
                                        }
                                        listFile
                                    }, token, inputExplorePublish.text?.trim()?.toString() ?: ""))
                        }else {
                            editExploreImpl.doRequest(ExploreEditRequest(Attributes.token,dynamicId,inputExplorePublish.text?.toString()?:""))
                        }
                    }
                }
            }
        }
        return true
    }


}
