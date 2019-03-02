package com.bigcreate.zyfw.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bigcreate.library.getPath
import com.bigcreate.library.startActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.RequestCode
import com.bigcreate.zyfw.models.SimpleRequest
import kotlinx.android.synthetic.main.activity_test_interface.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class TestInterfaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_interface)
        setSupportActionBar(testInterfaceToolbar)
        supportActionBar?.title = "接口测试"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.transucentSystemUI(true)
        testInterfaceToolbar.setNavigationOnClickListener {
            finish()
        }
        navigation_test.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.testAvatar -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    startActivityForResult(intent, RequestCode.AVATAR)
                }
                R.id.testRecommend -> {
                    GlobalScope.launch {
                        Attributes.loginUserInfo!!.run {
                            RemoteService.getRecommendData(SimpleRequest(token, username)).execute()
                        }
                    }
                }
                R.id.testDownloadAvatar -> {
                    GlobalScope.launch {
                        Attributes.loginUserInfo!!.run {
                            RemoteService.instance.getUserAvatar(SimpleRequest(token, username)).execute().body()?.byteStream()?.apply {
                                val file = File(applicationContext.externalCacheDir!!.absolutePath + "me_avatar.jpg")
                                val buffer = ByteArray(1024)
                                val out = file.outputStream()
                                var len = read(buffer)
                                while (len > 0) {
                                    out.write(buffer, 0, len)
                                    len = read(buffer)
                                }
                                close()
                                launch(Dispatchers.Main) {
                                    startActivity(Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(file.absolutePath.toUri(), "image/*")
                                    })
                                }
                            }
                        }
                    }
                }
                R.id.testRegister -> {
                    startActivity(SignUpActivity::class.java)
                }
                R.id.testAuthLogin -> {
                    Attributes.loginUserInfo = null
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.AVATAR -> if (resultCode == Activity.RESULT_OK) GlobalScope.launch {
                data?.data?.apply {
                    val file = File(getPath(this))
                    val type = MediaType.parse("multipart/form-data")
                    val loginUser = Attributes.loginUserInfo!!
                    val part = MultipartBody.Part.createFormData("file", file.name,
                            RequestBody.create(type, file))
                    RemoteService.setupUserAvatar(
                            part,
                            RequestBody.create(type, loginUser.token),
                            RequestBody.create(type, loginUser.username)).execute()
                }
            }
        }
    }
}
