package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.base.RemoteService
import com.bigcreate.zyfw.base.code
import com.bigcreate.zyfw.base.newTokenFromData
import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.UpdateProjectRequest
import com.bigcreate.zyfw.mvp.base.BaseMultiPresenterImpl
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.PresenterInter
import com.google.gson.JsonObject

class CreateImpl(mView: View?) :
        BaseMultiPresenterImpl<CreateImpl.View>(mView) {
    private val createInter = object : PresenterInter<CreateProjectRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
                            Attributes.token = newTokenFromData
                            onCreateProjectSuccess(this@apply)
                        }
                        else -> onCreateProjectFailed(this@apply)
                    }
                }
            }
        }

        override fun backgroundRequest(request: CreateProjectRequest): JsonObject? {
            return RemoteService.createProject(request).execute().body()
        }
    }

    private val updateInter = object : PresenterInter<UpdateProjectRequest, JsonObject> {
        override fun afterRequestSuccess(data: JsonObject?) {
            mView?.run {
                data?.apply {
                    when (code) {
                        200 -> {
                            Attributes.token = newTokenFromData
                            onUpdateProjectSuccess(this@apply)
                        }
                        else -> onUpdateProjectFailed(this@apply)
                    }
                }
            }
        }

        override fun backgroundRequest(request: UpdateProjectRequest): JsonObject? {
            return RemoteService.updateProject(request).execute().body()
        }
    }

    fun doCreateProject(projectRequest: CreateProjectRequest) {
        addJob(createInter.doRequest(mView, projectRequest))
    }

    fun doUpdateProject(updateProjectRequest: UpdateProjectRequest) {
        addJob(updateInter.doRequest(mView, updateProjectRequest))
    }

    interface View : BaseNetworkView {
        fun onCreateProjectSuccess(jsonObject: JsonObject)
        fun onCreateProjectFailed(jsonObject: JsonObject)
        fun onUpdateProjectSuccess(jsonObject: JsonObject)
        fun onUpdateProjectFailed(jsonObject: JsonObject)
    }
}