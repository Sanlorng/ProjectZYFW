package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.CreateProjectRequest
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.google.gson.JsonObject

interface CreateContract {
    interface Presenter:BasePresenter{
        fun doCreateProject(projectRequest: CreateProjectRequest)
    }

    interface View:BaseView{
        fun onCreateProjectSuccess(jsonObject: JsonObject)
        fun onCreateProjectFailed(jsonObject: JsonObject)
    }
}