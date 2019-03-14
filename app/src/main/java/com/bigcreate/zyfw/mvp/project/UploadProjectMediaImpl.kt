package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.FilesUploadRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class UploadProjectMediaImpl(view: View):BaseMultiPresenterImpl<UploadProjectMediaImpl.View>(view) {
    private val imageInter = object :PresenterInter<FilesUploadRequest,JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when(code) {
                        200 ->  {
                            Attributes.token = newTokenFromData
                            onUploadImageSuccess()
                        }
                        else -> onUploadImageFailed()
                    }
                }
            }
        }
        override fun backgroundRequest(request: FilesUploadRequest): JsonObject? {
            return request.run {
                RemoteService.addProjectPicture(parts,token,username,projectId).execute().body()
            }
        }
    }

    private val videoInter = object : PresenterInter<FilesUploadRequest,JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when(code) {
                        200 -> {
                            Attributes.token = newTokenFromData
                            onUploadVideoSuccess()
                        }
                        else -> onUploadVideoFailed()
                    }
                }
            }
        }

        override fun backgroundRequest(request: FilesUploadRequest): JsonObject? {
            return request.run {
                RemoteService.addProjectVideo(parts,token,username,projectId).execute().body()
            }
        }
    }
    fun doUploadImage(request: FilesUploadRequest){
        addJob(imageInter.doRequest(mView,request))
    }

    fun doUploadVideo(request: FilesUploadRequest) {
        addJob(videoInter.doRequest(mView,request))
    }
    interface View:BaseNetworkView {
        fun onUploadImageSuccess()
        fun onUploadImageFailed()
        fun onUploadVideoSuccess()
        fun onUploadVideoFailed()
    }
}