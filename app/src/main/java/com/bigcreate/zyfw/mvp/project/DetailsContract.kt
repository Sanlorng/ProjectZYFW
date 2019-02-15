package com.bigcreate.zyfw.mvp.project

import com.bigcreate.zyfw.models.GetProjectRequest
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView
import com.google.gson.JsonObject

interface DetailsContract {
    interface Presenter:BasePresenter{
        fun doGetDetails(getProjectRequest: GetProjectRequest)
    }

    interface View: BaseView{
        fun onGetDetailsSuccess(jsonObject: JsonObject)
        fun onGetDetailsFailed(jsonObject: JsonObject)
    }

}