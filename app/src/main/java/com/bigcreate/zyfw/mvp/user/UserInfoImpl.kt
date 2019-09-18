package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.FileUploadRequest
import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject
import java.io.File

class UserInfoImpl(mView: View?) : BaseMultiPresenterImpl<UserInfoImpl.View>(mView) {
    private val initInter = object : PresenterInter<InitPersonInfoRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> onInitUserInfoSuccess(this@apply).apply {
                            Attributes.token = newTokenFromData
                        }
                        else -> onInitUserInfoFailed(this@apply)
                    }
                }
            }
        }

        override fun backgroundRequest(request: InitPersonInfoRequest): JsonObject? {
            return RemoteService.initPersonInfo(request).execute().body()
        }
    }

    private val updateInter = object : PresenterInter<UpdateInfoRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> onUpdateUserInfoSuccess(this@apply).apply {
                            Attributes.token = newTokenFromData
                        }
                        else -> onUpdateUserInfoFailed(this@apply)
                    }
                }
            }
        }

        override fun backgroundRequest(request: UpdateInfoRequest): JsonObject? {
            return RemoteService.updatePersonInfo(request).execute().body()
        }
    }

    private val setupAvatarInter = object : PresenterInter<FileUploadRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> onSetupAvatarSuccess().apply {
                            Attributes.token = newTokenFromData
                        }
                        else -> onSetupAvatarFailed()
                    }
                }
            }
        }

        override fun backgroundRequest(request: FileUploadRequest): JsonObject? {
            return request.run {
                RemoteService.setupUserAvatar(part, token, username).execute().body()
            }
        }
    }

    fun doInitUserInfo(initPersonInfoRequest: InitPersonInfoRequest) {
        addJob(initInter.doRequest(mView, initPersonInfoRequest))
    }

    fun doUpdateUserInfo(updateInfoRequest: UpdateInfoRequest) {
        addJob(updateInter.doRequest(mView, updateInfoRequest))
    }

    fun doSetupAvatar(file: File, token: String, userId: Int) {
        addJob(setupAvatarInter.doRequest(mView, FileUploadRequest(
                file, token, userId
        )))

    }

    interface View : BaseNetworkView {
        fun onInitUserInfoSuccess(jsonObject: JsonObject)
        fun onInitUserInfoFailed(jsonObject: JsonObject)
        fun onUpdateUserInfoSuccess(jsonObject: JsonObject)
        fun onUpdateUserInfoFailed(jsonObject: JsonObject)
        fun onSetupAvatarSuccess()
        fun onSetupAvatarFailed()
    }
}