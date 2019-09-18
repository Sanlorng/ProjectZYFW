package com.bigcreate.zyfw.base

import androidx.lifecycle.ViewModelProvider

object ViewModelHelper {
    fun getAppViewModelProvider(application: MyApplication):ViewModelProvider {
        return ViewModelProvider(application,ViewModelProvider.AndroidViewModelFactory(application))
    }
}