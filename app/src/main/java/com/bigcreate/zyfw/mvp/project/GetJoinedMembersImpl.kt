package com.bigcreate.zyfw.mvp.project

import com.bigcreate.library.fromJson
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.jsonContentFromData
import com.bigcreate.zyfw.base.jsonData
import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.JoinedMember
import com.bigcreate.zyfw.models.UserInfoByPart
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenterImpl
import com.google.gson.JsonObject

class GetJoinedMembersImpl(view: View): BasePresenterImpl<GetProjectRequest,JsonObject,GetJoinedMembersImpl.View>(view) {
    override fun afterRequestSuccess(data: JsonObject?) {
        mView?.apply {
            data?.run {
                if (code == 200) {
                    onGetJoinedMemberSuccess(jsonData.get("content").toString().fromJson())
                    return
                }
                onGetJoinedMemberFailed(this)
            }
        }
    }

    override fun backgroundRequest(request: GetProjectRequest): JsonObject? {
        return RemoteService.getProjectJoinedMembers(request).execute().body()
    }
    interface View : BaseNetworkView {
        fun onGetJoinedMemberSuccess(userList:List<JoinedMember>)
        fun onGetJoinedMemberFailed(jsonObject: JsonObject)
    }
}