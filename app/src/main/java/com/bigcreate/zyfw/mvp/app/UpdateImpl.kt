package com.bigcreate.zyfw.mvp.app

import android.util.Log
import com.bigcreate.library.WebKit
import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.base.UpdateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class UpdateImpl(var mView: UpdateContract.NetworkView?) : UpdateContract.Presenter {
    var job: Job? = null
    override fun doUpdateCheck(packageName: String) {
        if (mView == null)
            throw Throwable("Please bind view")
        val view = mView!!
        view.run {
            if (!getViewContext().isNetworkActive) {
                onNetworkFailed()
                return
            }
            onRequesting()
            job = GlobalScope.launch {
                try {
                    UpdateService.getAppUpdateVersion(packageName).execute().body()?.apply {
                        if (BuildConfig.DEBUG) {
                            Log.e("packageName", packageName)
                            Log.e("response", WebKit.gson.toJson(this))
                        }
                        launch(Dispatchers.Main) {
                            onRequestFinished()
                            onUpdateCheckSuccess(this@apply)
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    onRequestFinished()
                    onNetworkFailed()
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