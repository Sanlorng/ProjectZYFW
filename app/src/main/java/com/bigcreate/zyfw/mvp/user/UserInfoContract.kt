package com.bigcreate.zyfw.mvp.user

import com.bigcreate.zyfw.models.InitPersonInfoRequest
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UpdateInfoRequest
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface UserInfoContract {
    interface Presenter: BasePresenter{
        fun doInitUserInfo(initPersonInfoRequest: InitPersonInfoRequest)
        fun doUpdateUserInfo(updateInfoRequest: UpdateInfoRequest)
        fun doSetupAvatar(file: MultipartBody.Part, body: Map<String,String>)
    }

    interface View: BaseView{
        fun onInitUserInfoSuccess(jsonObject: JsonObject)
        fun onInitUserInfoFailed(jsonObject: JsonObject)
        fun onUpdateUserInfoSuccess(jsonObject: JsonObject)
        fun onUpdateUserInfoFailed(jsonObject: JsonObject)
        fun onSetupAvatarSuccess()
    }
}