package com.bigcreate.zyfw.mvp.app

import com.bigcreate.zyfw.models.RestResult
import com.bigcreate.zyfw.models.UpdateInfo
import com.bigcreate.zyfw.mvp.base.BasePresenter
import com.bigcreate.zyfw.mvp.base.BaseView

interface UpdateContract {
    interface Presenter: BasePresenter{
        fun doUpdateCheck(packageName: String)
    }

    interface View: BaseView{
        fun onUpdateCheckSuccess(updateInfo: UpdateInfo)
        fun onUpdateCheckFailed(response:RestResult<UpdateInfo>)
    }
}