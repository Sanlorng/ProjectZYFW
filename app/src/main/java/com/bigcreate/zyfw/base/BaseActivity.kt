package com.bigcreate.zyfw.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

val Fragment.appCompactActivity: AppCompatActivity?
    get() {
        return try {
            activity as AppCompatActivity
        } catch (e: Exception) {
            null
        }
    }
val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

@RequiresApi(api = Build.VERSION_CODES.O)
fun Activity.startInstallPermissionSettingActivity() {
    startActivityForResult(Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
    ), RequestCode.INSTALL_PERMISSION)
}