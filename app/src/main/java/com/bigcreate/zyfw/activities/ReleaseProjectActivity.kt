package com.bigcreate.zyfw.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.bigcreate.library.*
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.WebInterface
import com.bigcreate.zyfw.base.myApplication
import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.ReleaseResponse
import com.bigcreate.zyfw.mvp.app.LocationContract
import com.bigcreate.zyfw.mvp.app.LocationImpl
import com.bigcreate.zyfw.mvp.project.CreateContract
import com.bigcreate.zyfw.mvp.project.CreateImpl
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import kotlinx.android.synthetic.main.activity_release_project.*
import kotlinx.android.synthetic.main.fragment_setup_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class ReleaseProjectActivity : AppCompatActivity(),CreateContract.View {
    val PHOTORESULT = 3
    val VIDEORESULT = 2
    val IMAGE_UNSPECIFIED = "image/*"
    val VIDEO_UNSPECIFIED = "video/*"
    var isGetting = false
    var selectType = 0
    var tencentLocation:TencentLocation? = null
    var imageString = ""
    var videoString = ""
    var file:File? = null
    val createImpl = CreateImpl(this)
    val locationImpl =  LocationImpl(object :LocationContract.View{
        override fun getViewContext(): Context {
            return  this@ReleaseProjectActivity
        }

        override fun onLocationPermissionDenied() {
            toast("未授予定位权限")
        }

        override fun onLocationRequestFailed() {
            chip_location.text = "定位失败"
        }

        override fun onLocationRequestSuccess(location: TencentLocation) {
            location.apply {
                val text = "$city·$district$town$village$street$streetNo".replace("Unknown","")
                if (text.length > 12)
                    chip_location.text = text.subSequence(0, 12)
                else
                    chip_location.text = text
                tencentLocation = this
            }
        }

        override fun onNetworkFailed() {
            chip_location.text = "请求位置信息失败"
        }

        override fun onRequesting() {
            chip_location.text = "正在请求位置"
        }
    }).apply { alwaysCall = true }
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_project)
        setSupportActionBar(toolbar_release_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_release_project.setNavigationOnClickListener {
            dialog("提示","你确认退出项目编辑页面吗，这将丢失你已写好的内容",
                    "确认", DialogInterface.OnClickListener { dialog, which ->  finish()},
                    "取消", DialogInterface.OnClickListener { dialog, which ->  })
        }
//        card_view_photo.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_UNSPECIFIED)
//            startActivityForResult(intent,PHOTORESULT)
//        }
//        card_view_video.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,VIDEO_UNSPECIFIED)
//            startActivityForResult(intent,VIDEORESULT)
//        }
        val string = resources.getStringArray(R.array.project_type_id)
        for (i in 0 until string.size) {
            chipGroupProjectType.addView(
                    Chip(this).apply {
                        text = string[i]
                        id = i+1
                        isCheckable = true
                        isCheckedIconVisible = false
                    }
            )
        }
        chipGroupProjectType.setOnCheckedChangeListener { chipGroup, i ->
            toast("你点击了$i")
        }
            dialog = AlertDialog.Builder(this@ReleaseProjectActivity)
                    .setView(R.layout.process_wait)
                    .create()
            locationImpl.start()
//        val tencentLocation = TencentLocationManager.getInstance(this)
//        val request = TencentLocationRequest.create()
//        request?.run {
//            requestLevel = TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA
//            isAllowCache = true
//            interval = 1500
//            isAllowGPS =true
//            isAllowDirection = true
//        }
//        tencentLocation.requestLocationUpdates(request,this)
        chip_location.isChecked = false
        chip_location.isCheckable = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_release_project,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.run {
            when(itemId){
                R.id.release_project_menu -> {
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
                        chipGroupProjectType.checkedChipId == -1 -> {
                            toast("您未选择分类")
                        }
//                        textView_add_video.visibility != View.GONE -> {
//
//                        }
//                        textView_add_photo.visibility != View.GONE -> {
//
//                        }
                        tencentLocation == null -> {
                            toast("定位失败，无法发布")
                        }
                        else -> {
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
//                            Thread{
//                                try {
//
////TODO
////                                    val data = CreateProjectRequest(edit_topic.string(), edit_content.string(),
////                                            tencentLocation!!.city, tencentLocation!!.address, tencentLocation!!.latitude, tencentLocation!!.longitude,
////                                            edit_contact.string(), edit_contact_phone.string(), edit_people.string(), myApplication!!.loginUser!!.name,
////                                            "", imageString,
////                                            myApplication!!.loginUser!!.userId, (spinner_type_project.selectedItemPosition + 1).toString())
//                                    val data = ""
//                                    try {
//                                        val dataString = WebKit.gson.toJson(data)
//                                        dataString.replace("deoBase64\":\"\"", "deoBase64\":\"${file?.toBase64()}\"")
//
//                                        val response = WebKit.okClient.postRequest(WebInterface.PROJECTISSUE_URL, WebKit.mediaJson, dataString)?.string()
//                                        response?.run {
//                                            Log.d("response", this)
//                                            val responseModel = WebKit.gson.fromJson(this, ReleaseResponse::class.java)
//                                            runOnUiThread {
//                                                dialog?.cancel()
//                                            }
//                                            responseModel?.run {
//                                                when (stateCode) {
//                                                    200 -> runOnUiThread {
//                                                        Toast.makeText(this@ReleaseProjectActivity, "项目已发布,两秒后进入项目详情页", Toast.LENGTH_SHORT).show()
//                                                        Thread {
//                                                            Thread.sleep(2000)
//                                                            runOnUiThread {
//                                                                val intent = Intent(this@ReleaseProjectActivity, ProjectDetailsActivity::class.java)
//                                                                intent.putExtra("project_id", responseModel.projectId.toString())
//                                                                startActivity(intent)
//                                                                finish()
//                                                            }
//                                                        }.start()
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }catch (e: Exception){
//                                        when(e){
//                                            is OutOfMemoryError -> {
//                                                runOnUiThread {
//                                                    Toast.makeText(this@ReleaseProjectActivity,"你选择的视频或图片文件过大，请重新选择",Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//                                            else -> {
//                                                e.printStackTrace()
//                                            }
//                                        }
//                                        return@Thread
//                                    }
//                                }catch (e: Exception) {
//                                    if (dialog?.isShowing != null && dialog?.isShowing == true)
//                                        runOnUiThread { dialog?.cancel() }
//                                }
//                            }.start()
                        }
                    }
                }
                else -> {}
            }
        }
        return true
    }

    override fun onBackPressed() {
        dialog("提示","你确认退出项目编辑页面吗，这将丢失你已写好的内容",
                "确认", DialogInterface.OnClickListener { dialog, which ->  finish()},
                "取消", DialogInterface.OnClickListener { dialog, which ->  })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.run {
            data.data?.run {
                Log.d("url",this.path)
                when(requestCode){
//                    PHOTORESULT ->{
//                        imageView_photo.setImageBitmap(getBitmapFromUri(this))
//                        getBitmapFromUri(this).toBase64()?.run {
//                            imageString = this
//                            Log.d("bitMap",this )
//                        }
//                        imageView_photo.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                        textView_add_photo.visibility = View.GONE
//                    }
//                    VIDEORESULT ->{
//                        val projection = arrayOf(MediaStore.Video.Media.DATA)
//                        val cursorLoader =CursorLoader(this@ReleaseProjectActivity,this,projection,null,null,null)
//                        val cursor = cursorLoader.loadInBackground()
//                        cursor?.moveToFirst()
//                        this@ReleaseProjectActivity.getPath(this).run {
//                            Log.d("pathsss",this)
//                            val file = File(this)
//                            file.run {
//                                this@ReleaseProjectActivity.file = this
//                            }
//                        }
//                        //val path = cursor?.getString(column_index!!)
//
//                        val mediaData = MediaMetadataRetriever()
//                        mediaData.setDataSource(this@ReleaseProjectActivity,this)
//                        imageView_video.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                        imageView_video.setImageBitmap(mediaData.frameAtTime)
//                        mediaData.release()
//                        textView_add_video.visibility = View.GONE
//                    }
                }
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
            Attributes.loginUserInfo?.token = get("newToken").asString
            GlobalScope.launch{
                for (i in 0..1)
                    delay(1000)
                launch(Dispatchers.Main){
                    startActivity(Intent(this@ReleaseProjectActivity,ProjectDetailsActivity::class.java).apply {
                        putExtra("projectId",get("projectId").asString)
                        putExtra("projectTopic",edit_topic.text.toString())
                    })
                }
                launch(Dispatchers.Main){
                    finish()
                }
            }

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
    private fun showProgress(progressing: Boolean){
        if (progressing)
            dialog.show()
        else
            dialog.dismiss()
    }

//        override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
//
//        }
//
//        override fun onLocationChanged(p0: TencentLocation?, p1: Int, p2: String?) {
//            p0?.run {
//                if (isGetting.not()) {
//                    val text = "$city·$name"
//                    if (text.length>12)
//                    chip_location.text = text.subSequence(0, 12)
//                    else
//                        chip_location.text = text
//                    tencentLocation = p0
//                }
//            }
//        }

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

