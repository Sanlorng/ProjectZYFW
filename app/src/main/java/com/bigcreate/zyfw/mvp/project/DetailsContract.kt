package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.mvp.base.BaseNetworkView
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.google.gson.JsonObject

interface DetailsContract {
    interface Presenter : BasePresenter {
        fun doGetDetails(getProjectRequest: GetProjectRequest)
    }

    interface NetworkView : BaseNetworkView {
        fun onGetDetailsSuccess(project: Project)
        fun onGetDetailsFailed(jsonObject: JsonObject)
    }

}