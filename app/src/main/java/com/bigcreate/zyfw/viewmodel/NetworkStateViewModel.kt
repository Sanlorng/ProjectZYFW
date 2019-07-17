package com.bigcreate.zyfw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bigcreate.zyfw.base.NetworkState

class NetworkStateViewModel:ViewModel() {
    val state = MutableLiveData<NetworkState>().also {
        it.postValue(NetworkState.LOADED)
    }
}