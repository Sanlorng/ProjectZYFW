package com.bigcreate.zyfw.mvp.app

import android.util.Log
import com.bigcreate.library.WebKit
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.base.UpdateService
import com.bigcreate.zyfw.base.isNetworkActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdateImpl(var mView: UpdateContract.View?):UpdateContract.Presenter {
    var job: Job? =null
    override fun doUpdateCheck(packageName: String) {
        if (mView == null)
            throw Throwable("Please bind view")
        val view = mView!!
        if (!view.getViewContext().isNetworkActive) {
            view.onNetworkFailed()
            return
        }
        job = GlobalScope.launch {
            UpdateService.getAppUpdateVersion(packageName).execute().body()?.apply {
                if (BuildConfig.DEBUG){
                    Log.e("packageName",packageName)
                    Log.e("response",WebKit.gson.toJson(this))
                }
                launch(Dispatchers.Main) {

                        view.onUpdateCheckSuccess(this@apply)
                }
            }
        }
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }

    override fun cancelJob() {
        job?.cancel()
    }
}