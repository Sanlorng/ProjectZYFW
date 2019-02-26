package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.models.UpdateProjectRequest
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface CreateContract {
    interface Presenter : BasePresenter {
        fun doCreateProject(projectRequest: CreateProjectRequest)
        fun doUpdateProject(updateProjectRequest: UpdateProjectRequest)
    }

    interface NetworkView : BaseNetworkView {
        fun onCreateProjectSuccess(jsonObject: JsonObject)
        fun onCreateProjectFailed(jsonObject: JsonObject)
        fun onUpdateProjectSuccess(jsonObject: JsonObject)
        fun onUpdateProjectFailed(jsonObject: JsonObject)
    }
}