package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject
import java.io.File

interface UserInfoContract {
    interface Presenter : BasePresenter {
        fun doInitUserInfo(initPersonInfoRequest: InitPersonInfoRequest)
        fun doUpdateUserInfo(updateInfoRequest: UpdateInfoRequest)
        fun doSetupAvatar(file: File, token: String, username: String)
    }

    interface NetworkView : BaseNetworkView {
        fun onInitUserInfoSuccess(jsonObject: JsonObject)
        fun onInitUserInfoFailed(jsonObject: JsonObject)
        fun onUpdateUserInfoSuccess(jsonObject: JsonObject)
        fun onUpdateUserInfoFailed(jsonObject: JsonObject)
        fun onSetupAvatarSuccess()
        fun onSetupAvatarFailed()
    }
}