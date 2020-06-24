package com.bigcreate.zyfw.mvp.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.core.net.toUri
import com.bigcreate.library.dialog
import com.bigcreate.library.exceptionDialog
import com.bigcreate.library.isNetworkActive
import com.bigcreate.zyfw.BuildConfig
import com.bigcreate.zyfw.base.defaultSharedPreferences
import kotlinx.coroutines.*
import java.io.PrintWriter
import java.io.StringWriter
import java.net.ConnectException
import java.net.SocketTimeoutException

abstract class BasePresenterImpl<R, D, V : BaseView>(var mView: V?) : BasePresenter, PresenterInter<R, D> {
    private var job: Job? = null

    override fun cancelJob() {
        job?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }

    fun doRequest(request: R) {
        cancelJob()
        job = doRequest(mView, request)
    }

}

interface PresenterInter<R, D> {

    fun doRequest(mViewImpl: BaseView?, request: R) = mViewImpl?.run {
        if (this is BaseNetworkView) {
            run {
                if (!getViewContext().isNetworkActive) {
                    onNetworkFailed()
                    return null
                }
                onRequesting()
                GlobalScope.launch {
                    try {
                        backgroundRequest(request).also {
                            withContext(Dispatchers.Main) {
                                try {
                                    onRequestFinished()
                                    afterRequestSuccess(it)
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        onRequestFinished()
                                        if (BuildConfig.DEBUG)
                                            e.printStackTrace()
                                        else
                                            onException(getViewContext(), e)
                                    }
                                } catch (e: java.lang.Exception) {
                                    withContext(Dispatchers.Main) {
                                        onRequestFinished()
                                        if (BuildConfig.DEBUG)
                                            e.printStackTrace()
                                        else
                                            onException(getViewContext(), e)
                                    }
                                }
                            }
                        }
                    } catch (e: SocketTimeoutException) {
                        withContext(Dispatchers.Main) {
                            onNetworkFailed()
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                getViewContext().exceptionDialog(e)
                        }
                    } catch (e: ConnectException) {
                        withContext(Dispatchers.Main) {
                            onNetworkFailed()
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                getViewContext().exceptionDialog(e)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                onException(getViewContext(), e)

                        }
                    } catch (e: java.lang.Exception) {
                        withContext(Dispatchers.Main) {
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                onException(getViewContext(), e)
                        }
                    }
                }
            }
            return null
        }
        onRequesting()
        GlobalScope.launch {
            try {
                backgroundRequest(request).apply {
                    withContext(Dispatchers.Main) {
                        try {
                            onRequestFinished()
                            afterRequestSuccess(this@apply)
                        } catch (e: java.lang.Exception) {
                            withContext(Dispatchers.Main) {
                                onRequestFinished()
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace()
                                else
                                    onException(getViewContext(), e)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onRequestFinished()
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace()
                                else
                                    onException(getViewContext(), e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onRequestFinished()
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                    else
                        onException(getViewContext(), e)
                }
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    onRequestFinished()
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                    else
                        onException(getViewContext(), e)
                }
            }
        }
    }

    private fun onException(context: Context, e: Exception) {
        e.printStackTrace()
        throw e
    }

    fun backgroundRequest(request: R): D?
    fun afterRequestSuccess(data: D?)
}