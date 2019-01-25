package com.bigcreate.zyfw.base

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

val FragmentActivity.myApplication:MyApplication?
    get() {
        return try {

        application as MyApplication
        }catch (e:Exception){
            null
        }
    }
val Fragment.appCompactActivity: AppCompatActivity?
get() {
    return try {
        activity as AppCompatActivity
    }catch (e:Exception){
        null
    }
}
val Context.defaultSharedPreferences
get() = PreferenceManager.getDefaultSharedPreferences(this)