package com.bigcreate.zyfw.base

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object AppConfig : ViewModelStoreOwner {
    private val viewModelStore = ViewModelStore()
    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }
}