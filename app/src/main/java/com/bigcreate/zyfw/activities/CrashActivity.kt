package com.bigcreate.zyfw.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.MyApplication
import com.bigcreate.zyfw.base.UpdateService
import com.bigcreate.zyfw.models.CrashLog
import kotlinx.android.synthetic.main.activity_crash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        setSupportActionBar(crashToolbar)
        window.translucentSystemUI(true)
        var errorString = ""
        MyApplication.EXCEPTIONS.forEach {
            errorString += StringWriter().apply {
                it.printStackTrace(PrintWriter(this))
            }.toString()
        }
        errorStringText.text = errorString
        MyApplication.EXCEPTIONS.clear()
        restartAppButton.setOnClickListener {
            startActivity<LaunchActivity>{
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        sendLogAndRestartAppButton.setOnClickListener {
            GlobalScope.launch {
                UpdateService.uploadCrashLog(CrashLog(
                        BuildConfig.VERSION_NAME,
                        BuildConfig.APPLICATION_ID,
                        BuildConfig.VERSION_CODE.toString(),
                        errorString
                ))
                restartAppButton.callOnClick()
            }
        }
    }

    override fun onBackPressed() {

    }
}
