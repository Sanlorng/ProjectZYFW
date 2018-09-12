package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.base.myApplication
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (myApplication?.loginToken == null)
            account_exit_button.visibility = View.GONE
        else
            account_exit_button.visibility = View.VISIBLE
        account_exit_button.setOnClickListener {
            myApplication?.loginToken = null
            defaultSharedPreferences.edit()
                    .putString("user_name",null)
                    .putString("user_pass",null)
                    .putString("user_token",null)
                    .apply()
            finish()
        }
    }
}
