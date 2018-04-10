package com.bigcreate.library

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.support.design.widget.BottomNavigationView
import com.bigcreate.library.R
import java.lang.Exception
import java.lang.reflect.Field

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

    fun exceptionDialog(context: Context, exception: Exception) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(exception.javaClass.name)
        dialog.setMessage(exception.message)
        dialog.setCancelable(true)
        dialog.setPositiveButton("OK", DialogInterface.OnClickListener{
            dialog, which ->
        })
        dialog.show()
    }

    fun bottomNavigationShiftModeSwithc(bottomNavigationView: BottomNavigationView, type: Boolean){
        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView,false)
            shiftingMode.isAccessible = false
            for (index in 0 until menuView.childCount){
                val itemView = menuView.getChildAt(index) as BottomNavigationItemView
                itemView.setShiftingMode(false)
                itemView.setChecked(itemView.itemData.isChecked)
            }

    }
}