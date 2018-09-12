package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.fragments.SignUpFragment
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    var fragmentTransaction: FragmentTransaction ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction?.run {
            replace(R.id.container_sign_up, SignUpFragment())

            commit()
        }

    }
}
