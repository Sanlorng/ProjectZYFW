package com.bigcreate.zyfw.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bigcreate.library.setIconTint
import com.bigcreate.library.startActivity
import com.bigcreate.library.toast
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.adapter.SelectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.Glide4Engine
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.base.ResultCode
import com.bigcreate.zyfw.models.FilesUploadRequest
import com.bigcreate.zyfw.mvp.project.UploadProjectMediaImpl
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.BoxingMediaLoader
import com.bilibili.boxing.loader.IBoxingCallback
import com.bilibili.boxing.loader.IBoxingMediaLoader
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.engine.impl.PicassoEngine
import kotlinx.android.synthetic.main.activity_select_image_and_video.*
import java.io.File

class SelectImageAndVideoActivity : AuthLoginActivity(), UploadProjectMediaImpl.View {
    private var action = SelectListAdapter.Action("")
    private val list = ArrayList<SelectListAdapter.Model>().apply {
        add(action)
    }
    private val uploadProjectMediaImpl = UploadProjectMediaImpl(this)
    private lateinit var dialog: AlertDialog
    lateinit var imageConfig: BoxingConfig
    lateinit var videoConfig: BoxingConfig
    private val boxImpl = object : IBoxingMediaLoader {
        override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
            Glide.with(img.context)
                    .load(absPath)
                    .into(img)
        }

        override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
            Glide.with(img.context)
                    .load(absPath)
                    .into(img)
        }
    }

    override fun setContentView() {
        setContentView(R.layout.activity_select_image_and_video)
    }

    override fun afterCheckLoginSuccess() {
        dialog = MaterialAlertDialogBuilder(this@SelectImageAndVideoActivity)
                .setView(R.layout.layout_process_upload)
                .setCancelable(false)
                .create()
        setSupportActionBar(toolbarSelect)
        toolbarSelect.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (intent.type == "image") "选择图片" else "选择视频"
        }
        listSelectItem.layoutManager = GridLayoutManager(this, 5)
        BoxingMediaLoader.getInstance().init(boxImpl)
        imageConfig = BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
        videoConfig = BoxingConfig(BoxingConfig.Mode.VIDEO)
        listSelectItem.adapter = SelectListAdapter(list) {
            if (intent.type == "image") {
//                startActivity<MediaPickerActivity>()
//                Matisse.from(this)
//                        .choose(MimeType.ofImage())
//                        .countable(true)
//                        .maxSelectable(9)
//                        .imageEngine(Glide4Engine())
//                        .forResult(RequestCode.SELECT_IMAGE)
                Boxing.of(imageConfig).withIntent(this@SelectImageAndVideoActivity, BoxingActivity::class.java)
                        .start(this@SelectImageAndVideoActivity, RequestCode.SELECT_IMAGE)
            }

            else {
                Boxing.of(videoConfig).withIntent(this@SelectImageAndVideoActivity, BoxingActivity::class.java)
                        .start(this@SelectImageAndVideoActivity, RequestCode.SELECT_VIDEO)
            }

        }
        listSelectItem.itemAnimator = DefaultItemAnimator()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Boxing.getResult(data)?.apply {
            when {
                resultCode != Activity.RESULT_OK -> {
                }
                requestCode == RequestCode.SELECT_IMAGE -> {
//                    val array = Matisse.obtainPathResult(data)
//                    list.addAll(List(array.size) {
//                        SelectListAdapter.Image(array[it])
//                    })
                    list.add(SelectListAdapter.Image(get(0).path))
                    list.remove(action)
                    list.add(action)
                    listSelectItem.adapter?.notifyDataSetChanged()
                }

                requestCode == RequestCode.SELECT_VIDEO -> {
                    list.add(SelectListAdapter.Video(get(0).path))
                    list.remove(action)
                    list.add(action)
                    listSelectItem.adapter?.notifyDataSetChanged()
                }
                else -> {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_select_image, menu)
        menu?.findItem(R.id.uploadSelectedItem)?.setIconTint(getColor(R.color.colorAccent))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        intent.getStringExtra("projectId").run {
            Attributes.loginUserInfo?.apply {
                when (item?.itemId) {
                    null -> {
                    }
                    R.id.uploadSelectedItem -> {
                        intent.getStringExtra("projectId").apply {
                            if (intent.type == "image")
                                uploadProjectMediaImpl.doUploadImage(
                                        FilesUploadRequest(list.run {
                                            val listFile = ArrayList<File>()
                                            forEach {
                                                if (it !is SelectListAdapter.Action)
                                                    listFile.add(File(it.path))
                                            }
                                            listFile
                                        }, token, username, this@run))
                            else
                                uploadProjectMediaImpl.doUploadVideo(
                                        FilesUploadRequest(list.run {
                                            val listFile = ArrayList<File>()
                                            forEach {
                                                if (it !is SelectListAdapter.Action)
                                                    listFile.add(File(it.path))
                                            }
                                            listFile
                                        }, token, username, this@run))
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onUploadImageFailed() {
        toast("上传失败")
    }

    override fun onUploadImageSuccess() {
        toast("上传成功")
        setResult(ResultCode.OK)
        finish()
    }

    override fun onUploadVideoFailed() {
        toast("上传失败")
    }

    override fun onUploadVideoSuccess() {
        toast("上传成功")
        setResult(ResultCode.OK)
    }

    override fun onRequesting() {
        super.onRequesting()
        showProgress(true)
    }

    override fun onRequestFinished() {
        super.onRequestFinished()
        showProgress(false)
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onDestroy() {
        uploadProjectMediaImpl.detachView()
        super.onDestroy()
    }

    private fun showProgress(progressing: Boolean) {
        if (progressing)
            dialog.show()
        else
            dialog.dismiss()
    }
}
