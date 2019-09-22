package com.bigcreate.zyfw.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.preference.*
import com.bigcreate.library.startActivity
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.MyDetailsActivity
import com.bigcreate.zyfw.activities.UpdateManagerActivity
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.MyApplication
import com.bigcreate.zyfw.base.ViewModelHelper
import com.bigcreate.zyfw.base.defaultSharedPreferences
import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.app.UpdateImpl
import com.bigcreate.zyfw.viewmodel.LoginViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class SettingsFragment : PreferenceFragmentCompat(), UpdateImpl.View {
    private val updateImpl = UpdateImpl(this)
    private lateinit var loginViewModel:LoginViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

    }

    override fun getViewContext(): Context {
        return context!!
    }

    override fun onNetworkFailed() {
        findPreference<Preference>("appUpdate")?.summary = "网络连接失败"
    }

    override fun onRequesting() {
        findPreference<Preference>("appUpdate")?.summary = "正在检查更新"
    }

    override fun onRequestFinished() {

    }

    override fun onUpdateCheckFailed(response: RestResult<UpdateInfo>) {
        findPreference<Preference>("appUpdate")?.summary = "检查更新失败"
    }

    override fun onUpdateCheckSuccess(updateInfo: UpdateInfo) {
        var currentCode = 0L
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            currentCode = BuildConfig.VERSION_CODE.toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            currentCode = context!!.packageManager.getPackageInfo(context!!.packageName, PackageManager.GET_CONFIGURATIONS).longVersionCode
        if (updateInfo.versionCode.toLong() > currentCode) {
            findPreference<Preference>("appUpdate")?.summary = "检查到新版本：" + updateInfo.versionName
        } else {
            findPreference<Preference>("appUpdate")?.summary = "当前已是最新版本"
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = ViewModelHelper.getAppViewModelProvider(activity!!.application as MyApplication)[LoginViewModel::class.java]
        findPreference<Preference>("about")?.summary = "版本号：" + try {
            context!!.run {
                packageManager.getPackageInfo(applicationInfo.packageName, 0).versionName
            }
        } catch (e: Exception) {
            "null"
        }
        findPreference<Preference>("about")?.setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + activity!!.packageName)
            startActivity(intent)
            true
        }
        findPreference<SwitchPreference>("status_bar_mask")?.setOnPreferenceClickListener {
            activity?.window?.translucentSystemUI(true)
            true
        }
        findPreference<Preference>("Notification")?.setOnPreferenceClickListener {
            val intent = Intent()
            //SDK 26

            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context!!.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context!!.applicationInfo.uid)

            intent.putExtra("app_package", context!!.packageName)
            intent.putExtra("app_uid", context!!.applicationInfo.uid)
            startActivity(intent)
            true
        }
        val accountSetting = findPreference<Preference>("account_settings")
        accountSetting?.setOnPreferenceClickListener {
            context?.startActivity<MyDetailsActivity>()
            true
        }
        findPreference<Preference>("category_account")?.isVisible = false
        findPreference<Preference>("appUpdate")?.setOnPreferenceClickListener {
            context?.startActivity<UpdateManagerActivity>()
            true
        }
        findPreference<SwitchPreference>("switch_night_mode")?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
        findPreference<ListPreference>("nightModeSwitch")?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                //Log.e("newValue",newValue.toString())
                AppCompatDelegate.setDefaultNightMode((newValue as String).toInt())
                true
            }

            setSummaryProvider {
                if (value != null) {
                    setNightSummary(value.toInt())
                }else {
                    setNightSummary(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }


        }
        if (Attributes.loginUserInfo!=null) {
            Attributes.loginUserInfo?.run {
                accountSetting?.summary = username
                accountSetting?.title = Attributes.userInfo?.userNick
                Glide.with(this@SettingsFragment)
                        .load(Attributes.userImg)
                        .circleCrop()
                        .into(object : CustomTarget<Drawable>() {
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                accountSetting?.icon = resource
                            }
                        })
                findPreference<PreferenceCategory>("category_account")?.isVisible = true
                findPreference<Preference>("exit_account")?.setOnPreferenceClickListener {
                    Attributes.loginUserInfo = null

                    context!!.defaultSharedPreferences.edit {
                        putString("username", "")
                        putString("password", "")
                        putBoolean("saved_account", false)
                    }
                    activity?.finish()
                    true
                }
                findPreference<Preference>("exit_account")
            }
        }else {
            findPreference<PreferenceCategory>("category_account")?.isVisible = false
        }

        updateImpl.doRequest(context!!.packageName)
    }

    override fun onPause() {
        super.onPause()
        updateImpl.detachView()
    }
    private fun setNightSummary(value: Int):String {
        return when(value) {
            AppCompatDelegate.MODE_NIGHT_NO -> resources.getStringArray(R.array.night_mode_switch)[0]
            AppCompatDelegate.MODE_NIGHT_YES -> resources.getStringArray(R.array.night_mode_switch)[1]
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> resources.getStringArray(R.array.night_mode_switch)[2]
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> resources.getStringArray(R.array.night_mode_switch)[3]
            else -> ""
        }
    }
}