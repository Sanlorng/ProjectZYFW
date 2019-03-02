package com.bigcreate.zyfw.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.bigcreate.library.startActivity
import com.bigcreate.library.transucentSystemUI
import com.bigcreate.zyfw.base.Attributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


abstract class AuthLoginActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (Attributes.loginUserInfo == null){
            GlobalScope.launch(Dispatchers.Main) {
                startActivity(LoginActivity::class.java)
            }
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        window.transucentSystemUI(true)
        if (Attributes.loginUserInfo == null){
            GlobalScope.launch(Dispatchers.Main) {
                startActivity(LoginActivity::class.java)
            }
            finish()
        }
    }
}