package com.bigcreate.zyfw.callback

interface DownloadCallback {
    fun onDownloading(totalLen: Long, currentLen: Long)
    fun onDownloadSuccess(path: String)
    fun onDownloadFailed(msg: String)
    fun onPreDownload()
}