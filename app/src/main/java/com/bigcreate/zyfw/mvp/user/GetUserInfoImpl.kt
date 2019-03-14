package com.bigcreate.zyfw.mvp.user

import com.bigcreate.library.fromJson
import com.bigcreate.library.toJson
import com.bigcreate.zyfw.base.*
import com.bigcreate.zyfw.models.SimpleRequest
import com.bigcreate.zyfw.models.UserInfo
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class GetUserInfoImpl(mView: View):BasePresenterImpl<SimpleRequest,JsonObject,GetUserInfoImpl.View>(mView) {

    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.run {
            data?.apply {
                if (isJsonNull)
                    onUserInfoIsEmpty()
                else
                when(code) {
                    200 -> onGetUserInfoSuccess(jsonContentFromData.toJson().fromJson<UserInfo>().apply {
                        Attributes.token = jsonContentFromData.getAsString("token")
                        Attributes.userInfo = this
                    })
                    404 -> onUserInfoIsEmpty()
                    else -> onGetUserInfoFailed()
                }
            }
        }
    }

    override fun backgroundRequest(request: SimpleRequest): JsonObject? {
        return RemoteService.instance.getUserInfoBySelf(request).execute().body()
    }
    interface View: BaseNetworkView {
        fun onGetUserInfoSuccess(userInfo: UserInfo)
        fun onGetUserInfoFailed()
        fun onUserInfoIsEmpty()
    }
}

