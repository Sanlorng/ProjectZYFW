package com.bigcreate.library

import android.view.View
import android.view.Window
import android.view.WindowManager
import com.bigcreate.library.R

/**
 * Create by Sanlorng on 2018/4/9
 */
object UI {
    fun statuBarTransucent(window: Window){
        window.statusBarColor = window.context.getColor(R.color.statusbarColor)
        fitStatuBar(window)
    }

    fun fitStatuBar(window: Window){
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    }

    fun statusBarIconColor(window: Window,light: Boolean){
        var ui = window.decorView.systemUiVisibility
        if (light)
            ui = ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else
            ui = ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        window.decorView.systemUiVisibility = ui
    }

    fun statuBarIconHide(window: Window,hide: Boolean){
        var ui = window.decorView.systemUiVisibility
        if (hide)
            ui = ui or View.SYSTEM_UI_FLAG_LOW_PROFILE
        else
            ui = ui and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
        window.decorView.systemUiVisibility = ui
    }
}